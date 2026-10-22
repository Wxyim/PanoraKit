/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) YumeLira 2025 - 2026
 */
package tunnel

import (
	"sync"
	"sync/atomic"
	"time"

	"cfa/native/app"

	"github.com/metacubex/mihomo/component/process"
	C "github.com/metacubex/mihomo/constant"
	mihomoTunnel "github.com/metacubex/mihomo/tunnel"
	"github.com/metacubex/mihomo/tunnel/statistic"
)

const closeAllTimeout = 2 * time.Second

// Recently-closed connection retention.
//
// The app polls connection snapshots on a cadence it varies with what is
// actually on screen (1s while the traffic screen renders the list, ~5s while
// the poll only keeps the recent-request history warm) and only records a
// connection as "closed" after observing it in at least one snapshot. A
// short-lived request that opens and closes between two polls would therefore
// never surface in the recent-request history. To make such connections
// observable, keep the final tracker info of recently-closed connections around
// for the retention window and include them in [QueryConnections] snapshots, so
// the next poll is guaranteed to see them and record them as closed.
//
// [recentClosedRetention] is the floor for that window. It covers the app's
// slowest cadence (5s, while only the recent-request history is being kept warm)
// with a second of slack, so a close is observed whichever cadence is in effect
// when it happens. The window then also follows the measured gap between polls
// (see [recentClosedRetentionWindow]) so a future cadence change cannot silently
// start dropping closes.
//
// A close that happens while nobody polls is a different case: the app suspends
// its connection polling whenever it is backgrounded or the screen is off (the
// recent-request list is the only consumer and it cannot be on screen then), so
// honouring only [recentClosedRetention] would drop every close of that period.
// Entries are therefore retained until they have been delivered to a poll once
// ([recentClosedEntry.deliveredAt]); undelivered entries live for
// [recentClosedUndeliveredRetention] instead, so returning to the foreground
// replays what happened while the app was away. This costs nothing while
// backgrounded — the sampler below already runs — and needs no extra polling,
// no push channel and no binder traffic.
//
// A delivered close whose owner is still unknown is another special case: its
// first copy may already have reached the app, but a later UID lookup can still
// repair it. Such entries keep the long replay window until their retry budget
// is exhausted, and a successful late lookup marks them for one redelivery.
//
// [recentClosedSampler] is the active cadence: it must be finer than the app's
// 1s poll to catch short-lived connections, and the finer it is the shorter a
// connection has to live to be guaranteed observable. 20ms means any connection
// alive for >=20ms spans at least one sample and is captured with certainty;
// shorter ones are captured probabilistically (~lifetime/20ms).
//
// [recentClosedIdleSampler] is the backoff cadence used while the manager is
// idle (no live connections, none pending). Idle ticks are already nearly free
// CPU-wise, but keeping a 20ms timer armed prevents the CPU from idling deep
// between ticks on a quiet device, so the sampler backs off to 250ms when there
// is nothing to track and returns to 20ms as soon as activity resumes. Retained
// entries do not keep the fine cadence armed: the coarse tick is enough to expire
// them and still gives an unresolved close a couple of UID-enrichment attempts
// before the next poll (see [tryScheduleUidEnrichment]).
// The only tradeoff: a connection that opens and closes entirely inside a
// single idle window (the first connection of a new burst) is not observed.
const (
	recentClosedRetention = 6 * time.Second
	// [recentClosedRetentionCap] bounds the adaptive window so a stalled app (or
	// a single manual refresh) cannot pin every closed connection in the
	// snapshot: the buffer is also bounded by [recentClosedMax], but a bound on
	// the window keeps the steady-state payload predictable as well.
	recentClosedRetentionCap = 8 * time.Second
	recentClosedSampler      = 20 * time.Millisecond
	recentClosedIdleSampler  = 250 * time.Millisecond
	// [recentClosedUndeliveredRetention] is how long a close survives while no
	// poll has seen it yet. It only has to outlast a backgrounded app; the list
	// it feeds is a "recent requests" view, so a bound that spans hours would
	// only replay stale history. [recentClosedMax] additionally caps how much
	// can accumulate, so one poll after a long background delivers at most that
	// many entries. The same bound applies to an already-delivered entry whose
	// owner is still unresolved or which is waiting to be redelivered.
	recentClosedUndeliveredRetention = 30 * time.Minute
	// [recentClosedMax] bounds the buffer itself, which is what actually caps
	// the connection payload. On a device sustaining more than ~80 closes per
	// second the oldest entries can therefore be evicted before the app polls;
	// that trades a bounded slice of history for a bounded payload.
	recentClosedMax = 500
	// [recentClosedMaxPerResponse] bounds how many retained closes a single
	// [QueryConnections] response carries. [recentClosedMax] bounds the buffer,
	// but the RootTun path serialises the whole snapshot to a JSON string that
	// crosses a binder transaction (~1 MiB budget, and binder strings are UTF-16
	// so roughly half that many characters), so a saturated buffer would risk a
	// TransactionTooLargeException. Undelivered entries are served first so a
	// backlog drains over consecutive polls instead of being dropped.
	recentClosedMaxPerResponse = 200
)

// recentClosedLastQueryNanos / recentClosedPollGapNanos hold the cadence of
// [QueryConnections] calls (nanoseconds since the epoch). They are written by
// whichever goroutine serves the app's queries and read by the sampler, hence
// the atomics.
var (
	recentClosedLastQueryNanos int64
	recentClosedPollGapNanos   int64
)

// recentClosedRetentionWindow returns how long a closed connection must stay in
// the snapshot for the app's next poll to observe it.
//
// The window tracks the poll cadence the app actually uses: 1s while the traffic
// screen is open, ~5s while the app is merely keeping the recent-request history
// warm. 1.5x the measured gap absorbs scheduling jitter; both ends are clamped
// so the value stays sane before the second poll is seen and if the app stalls,
// and the floor keeps the guaranteed slice of history from shrinking back to
// something shorter than the slowest cadence.
func recentClosedRetentionWindow() time.Duration {
	gap := time.Duration(atomic.LoadInt64(&recentClosedPollGapNanos))
	if gap < recentClosedRetention {
		return recentClosedRetention
	}
	window := gap + gap/2
	if window > recentClosedRetentionCap {
		return recentClosedRetentionCap
	}
	return window
}

// retainRecentClosed reports whether [entry] should stay in the retention buffer.
//
// Delivered entries normally expire on the short poll-following window. An unresolved entry that
// can still be enriched is the exception: keep it for the same replay window as an undelivered
// close so a late UID can be handed to the app even after the first (incomplete) copy was rendered.
// Once the bounded retry budget is exhausted, the normal short window applies again.
func retainRecentClosed(
	entry *recentClosedEntry,
	enrichmentPossible bool,
	deliveredCutoff time.Time,
	undeliveredCutoff time.Time,
) bool {
	if entry == nil {
		return false
	}
	if entry.deliveredAt.IsZero() {
		return entry.closedAt.After(undeliveredCutoff)
	}
	if entry.redeliver {
		return entry.closedAt.After(undeliveredCutoff)
	}
	if enrichmentPossible &&
		entry.enrichAttempts < enrichUidMaxAttempts &&
		connectionNeedsUid(entry.info) {
		return entry.closedAt.After(undeliveredCutoff)
	}
	return entry.deliveredAt.After(deliveredCutoff)
}

type recentClosedEntry struct {
	info     *statistic.TrackerInfo
	closedAt time.Time
	// deliveredAt marks the poll that first returned this entry. Zero means no
	// poll has seen it yet, which is what keeps a background close around until
	// the app comes back (see the retention notes above).
	deliveredAt time.Time
	// enrichAttempts/enrichLastAt give the entry its own bounded UID enrichment
	// budget (see [tryScheduleUidEnrichment]). A connection that closed before the
	// live scan attributed it would otherwise reach the app with an empty owner,
	// which is what renders a short "0 s / 0 B" request as "Unknown App".
	enrichAttempts int
	enrichLastAt   int64
	// redeliver marks an entry whose metadata was upgraded after it had already been returned to
	// the app. The next poll must include it once more so the app can replace the incomplete copy
	// it recorded on first delivery.
	redeliver bool
}

// connectionSnapshot mirrors statistic.Snapshot but tags every connection with
// whether it has already closed. Closed connections are retained briefly by
// [observeRecentClosed] so short-lived requests remain observable by the app's
// periodic poll; the flag lets the app render them as closed immediately.
type connectionSnapshot struct {
	DownloadTotal int64             `json:"downloadTotal"`
	UploadTotal   int64             `json:"uploadTotal"`
	Connections   []*connectionInfo `json:"connections"`
	Memory        uint64            `json:"memory"`
}

type connectionInfo struct {
	*statistic.TrackerInfo
	Closed bool `json:"closed"`
	// ClosedAt is when the core observed the connection leaving, in wall-clock
	// milliseconds. The app stamps its recent-request history with it, which
	// matters for a batch of closes that happened while the app was
	// backgrounded: those must not all look like they closed at poll time.
	// Omitted for live connections.
	ClosedAt int64 `json:"closedAt,omitempty"`
}

// enrichState tracks one live connection's UID enrichment attempts so a miss is retried a couple of
// times (Android publishes a socket's owner a few ms after the socket first sends) without burning a
// binder call on every sampler tick for the rest of the connection's life.
type enrichState struct {
	attempts int
	lastAt   int64 // UnixNano of the last attempt
}

// uidEnrichmentRequest keeps the retained buffer entry that a lookup belongs to, when there is
// one. Live connections have no buffer entry; a successful lookup for a retained close must also
// mark that entry for redelivery to the app.
type uidEnrichmentRequest struct {
	info  *statistic.TrackerInfo
	entry *recentClosedEntry
}

var (
	recentClosedOnce     sync.Once
	recentClosedMu       sync.Mutex
	recentClosedSeen     = make(map[string]*statistic.TrackerInfo)
	recentClosedEnriched = make(map[string]enrichState)
	recentClosedBuffer   []recentClosedEntry
)

// ensureRecentClosedSampler starts the background sampler that watches the
// manager for connections leaving and retains their final tracker info.
func ensureRecentClosedSampler() {
	recentClosedOnce.Do(func() {
		go func() {
			interval := recentClosedSampler
			for {
				timer := time.NewTimer(interval)
				<-timer.C
				if observeRecentClosed() {
					interval = recentClosedSampler
				} else {
					interval = recentClosedIdleSampler
				}
			}
		}()
	})
}

// observeRecentClosed samples the live connection set, moves connections that
// closed since the previous sample into the retention buffer and prunes
// expired entries. It reports whether the fine sampling cadence is still
// needed (live connections or pending seen entries); a false return means the
// manager is idle and the caller may back off the sampling cadence. Retained
// entries alone do not request the fine cadence — they only need to expire.
func observeRecentClosed() bool {
	now := time.Now()

	// Snapshot() also reads /proc/<pid>/statm for the memory field, which is
	// not needed on this watcher. Iterate the manager directly so ticks avoid a
	// per-tick /proc read and the slice allocation Snapshot() performs.
	var alive map[string]*statistic.TrackerInfo
	connectionCount := 0
	statistic.DefaultManager.Range(func(c statistic.Tracker) bool {
		if alive == nil {
			alive = make(map[string]*statistic.TrackerInfo, 8)
		}
		alive[c.ID()] = c.Info()
		connectionCount++
		return true
	})

	recentClosedMu.Lock()
	enrich := uidEnrichmentActive(now)
	enrichPossible := uidEnrichmentPossible()
	var pending []uidEnrichmentRequest

	// Nothing live and nothing pending: only retained entries can have work
	// left, and that work is expiring them plus the handful of UID-enrichment
	// attempts the coarse cadence already covers. Retained entries deliberately
	// do not keep the fine cadence armed.
	idle := connectionCount == 0 && len(recentClosedSeen) == 0
	if !idle {
		// Detect connections that left the manager since the previous sample and
		// retain their final tracker info so a later poll can still observe them.
		for id, info := range recentClosedSeen {
			if alive[id] == nil {
				recentClosedBuffer = append(recentClosedBuffer, recentClosedEntry{info: info, closedAt: now})
				delete(recentClosedSeen, id)
				delete(recentClosedEnriched, id)
			}
		}
		for id, info := range alive {
			if _, ok := recentClosedSeen[id]; !ok {
				recentClosedSeen[id] = info
			}
		}
	}

	// Expire retained entries. Entries no poll has seen yet are kept for the
	// longer window so a backgrounded app still gets them on its next poll.
	// Delivered entries normally expire on the short window, but an unresolved
	// entry that still has enrichment attempts left (or an entry waiting to be
	// redelivered) keeps the long replay window. The loop stops at the first
	// retained entry; later entries are newer, so they cannot be expired yet.
	deliveredCutoff := now.Add(-recentClosedRetentionWindow())
	undeliveredCutoff := now.Add(-recentClosedUndeliveredRetention)
	keep := 0
	for keep < len(recentClosedBuffer) {
		entry := recentClosedBuffer[keep]
		if retainRecentClosed(&entry, enrichPossible, deliveredCutoff, undeliveredCutoff) {
			break
		}
		keep++
	}
	recentClosedBuffer = recentClosedBuffer[keep:]

	// Bound the buffer on a busy device.
	if len(recentClosedBuffer) > recentClosedMax {
		recentClosedBuffer = recentClosedBuffer[len(recentClosedBuffer)-recentClosedMax:]
	}

	// Collect the connections whose owner the core left unresolved (see [enrichUid]). The scan is
	// budgeted per tick and skips connections it already gave up on, so opening the list over a long
	// backlog cannot turn one tick into one binder round trip per connection. The budget is spent in
	// priority order; the buffer tiers run oldest-first and the live tier rides Go's randomised map
	// iteration, so nothing starves:
	//
	//   1. delivered closes that are still unresolved get first refusal of a small share of the
	//      budget: they are already visible and their short retention window is running;
	//   2. live connections, whose owner must be queried while the socket still exists;
	//   3. retained closes no poll has delivered yet (they have the long replay window);
	//   4. any leftover budget goes back to delivered unresolved closes, including a large batch
	//      delivered after the app returned from the background.
	if enrich {
		budget := enrichUidBudgetPerTick
		urgentBudget := enrichUidBudgetPerTick / 2
		scheduleEntry := func(entry *recentClosedEntry) bool {
			attempts, lastAt, scheduled := tryScheduleUidEnrichment(
				entry.info,
				entry.enrichAttempts,
				entry.enrichLastAt,
				now,
			)
			entry.enrichAttempts, entry.enrichLastAt = attempts, lastAt
			if scheduled {
				pending = append(pending, uidEnrichmentRequest{info: entry.info, entry: entry})
				budget--
			}
			return scheduled
		}
		for i := range recentClosedBuffer {
			if budget <= 0 || urgentBudget <= 0 {
				break
			}
			entry := &recentClosedBuffer[i]
			if !entry.deliveredAt.IsZero() && scheduleEntry(entry) {
				urgentBudget--
			}
		}
		for id, info := range recentClosedSeen {
			if budget <= 0 {
				break
			}
			state := recentClosedEnriched[id]
			attempts, lastAt, scheduled :=
				tryScheduleUidEnrichment(info, state.attempts, state.lastAt, now)
			if scheduled {
				recentClosedEnriched[id] = enrichState{attempts: attempts, lastAt: lastAt}
				pending = append(pending, uidEnrichmentRequest{info: info})
				budget--
			}
		}
		for i := range recentClosedBuffer {
			if budget <= 0 {
				break
			}
			entry := &recentClosedBuffer[i]
			if entry.deliveredAt.IsZero() {
				scheduleEntry(entry)
			}
		}
		for i := range recentClosedBuffer {
			if budget <= 0 {
				break
			}
			entry := &recentClosedBuffer[i]
			if !entry.deliveredAt.IsZero() {
				scheduleEntry(entry)
			}
		}
	}
	recentClosedMu.Unlock()

	// Attribute the connections outside the lock: the lookup is a JNI/binder round trip and must not
	// block a poll waiting for the same lock.
	var redeliver []*recentClosedEntry
	for _, request := range pending {
		if enrichUid(request.info) && request.entry != nil {
			redeliver = append(redeliver, request.entry)
		}
	}
	if len(redeliver) > 0 {
		recentClosedMu.Lock()
		for _, entry := range redeliver {
			if entry.deliveredAt.IsZero() {
				// Enrichment won the race against first delivery. The poll that delivers this
				// entry will already carry the UID, so no redelivery is needed.
				continue
			}
			entry.redeliver = true
			// An entry can have been delivered and then enriched after its short retention window
			// had already started. Refresh that window so the next poll can hand the upgraded
			// metadata to the app before the entry expires.
			if !entry.deliveredAt.IsZero() {
				entry.deliveredAt = time.Now()
			}
		}
		recentClosedMu.Unlock()
	}

	return !idle
}

// noteQueryCadence records the gap between consecutive [QueryConnections] calls
// so [recentClosedRetentionWindow] can follow the app's actual poll cadence.
func noteQueryCadence() {
	now := time.Now().UnixNano()
	previous := atomic.SwapInt64(&recentClosedLastQueryNanos, now)
	if previous != 0 {
		atomic.StoreInt64(&recentClosedPollGapNanos, now-previous)
	}
}

// UID enrichment for connections the core never attributed.
//
// mihomo resolves a connection's owner UID (and with it the package name) either when
// find-process-mode is `always` or, with `strict` (the default), only once the connection's rule
// evaluation reaches a PROCESS/UID rule. Under `strict` a connection matched by an earlier domain
// rule keeps an empty uid and process, and the app can only render it as "Unknown App".
//
// The app owns the very lookup the core would have run (VpnTunTransport.queryUid, with its
// positive/negative caches), so it fills the gap itself. That work is deferred to the moment the
// connection list is actually rendered: enrichment only runs while the app polls connections, so a
// user who never opens the list never pays for it. `off` is left alone, since it is an explicit
// opt-out of process resolution.
const (
	// enrichUidPollWindow is how long after the last connection poll the app is still assumed to
	// be rendering the list. The repository polls every second while the traffic screen is open
	// and every five seconds while it only keeps the recent-request history warm.
	enrichUidPollWindow = 10 * time.Second

	// enrichUidBudgetPerTick bounds how many lookups one sampler tick performs. Opening the screen
	// over a long list of already-established connections would otherwise stall that tick behind one
	// binder round trip per connection; the rest are picked up by the following ticks.
	enrichUidBudgetPerTick = 8

	// A connection is attributed at most enrichUidMaxAttempts times, spaced by at least
	// enrichUidRetryInterval. Android publishes the socket owner a few ms after the socket first
	// sends, so the first tick can legitimately miss; after a couple of retries the socket is either
	// published or gone, and a connection that cannot be attributed must not keep paying for binder
	// calls and procfs reads for as long as it lives.
	enrichUidMaxAttempts   = 3
	enrichUidRetryInterval = 500 * time.Millisecond
)

// uidEnrichmentPossible reports whether the configured process mode permits owner lookups at all.
func uidEnrichmentPossible() bool {
	return mihomoTunnel.FindProcessMode() != process.FindProcessOff
}

// uidEnrichmentActive reports whether the app is currently rendering the connection list and the
// core still leaves attribution to it.
func uidEnrichmentActive(now time.Time) bool {
	if !uidEnrichmentPossible() {
		return false
	}
	last := atomic.LoadInt64(&recentClosedLastQueryNanos)
	return last != 0 && now.Sub(time.Unix(0, last)) < enrichUidPollWindow
}

// connectionNeedsUid reports whether [info] is a user connection that the core did not attribute.
// Inner connections have no owning app, and a non-zero UID is already enough for the UI fallback.
func connectionNeedsUid(info *statistic.TrackerInfo) bool {
	return info != nil &&
		info.Metadata != nil &&
		info.Metadata.Type != C.INNER &&
		info.Metadata.Uid == 0
}

// enrichUid resolves the owner UID of a connection the core skipped and stores it in the metadata
// the app's snapshot renders. The lookup is forced past the app-side negative cache: this path runs
// precisely for the connections that missed while they were live, and it is rate limited by
// [tryScheduleUidEnrichment], so a cached -1 would only mask the retry that can still attribute it.
//
// Connections the core already attributed, inner (core-originated) connections, and connections
// whose 4-tuple cannot be rebuilt are left untouched: the first two need no help and the third has
// nothing to query with.
//
// It returns true only when this call added a UID, which tells the caller that a retained closed
// entry must be redelivered to the app.
func enrichUid(info *statistic.TrackerInfo) bool {
	if !connectionNeedsUid(info) {
		return false
	}
	metadata := info.Metadata
	source, target := app.MetadataSocketAddrs(metadata)
	if source == nil || target == nil {
		return false
	}
	if uid := app.QuerySocketUid(source, target, true); uid > 0 {
		metadata.Uid = uint32(uid)
		return true
	}
	return false
}

// tryScheduleUidEnrichment books one enrichment attempt for [info] when it is still unattributed
// and has attempts left, returning the updated attempt bookkeeping. The rate limiting lives here so
// the live scan and the retained-closed scan share the same retry policy.
func tryScheduleUidEnrichment(
	info *statistic.TrackerInfo,
	attempts int,
	lastAt int64,
	now time.Time,
) (newAttempts int, newLastAt int64, scheduled bool) {
	if !connectionNeedsUid(info) {
		return attempts, lastAt, false
	}
	if attempts >= enrichUidMaxAttempts {
		return attempts, lastAt, false
	}
	if attempts > 0 && now.Sub(time.Unix(0, lastAt)) < enrichUidRetryInterval {
		return attempts, lastAt, false
	}
	return attempts + 1, now.UnixNano(), true
}

// selectRecentClosedIndices chooses the retained closes to include in one response.
//
// Redelivery entries come first because they carry metadata the app has not seen. The remaining
// budget preserves the original backlog behavior: serve the oldest undelivered entries first,
// then top the response up with the newest delivered entries.
func selectRecentClosedIndices(buffer []recentClosedEntry) []int {
	numDelivered := 0
	for numDelivered < len(buffer) && !buffer[numDelivered].deliveredAt.IsZero() {
		numDelivered++
	}
	start, end := 0, len(buffer)
	if len(buffer)-numDelivered >= recentClosedMaxPerResponse {
		start, end = numDelivered, numDelivered+recentClosedMaxPerResponse
	} else if end > recentClosedMaxPerResponse {
		start = end - recentClosedMaxPerResponse
	}

	selected := make([]bool, len(buffer))
	selectedIndices := make([]int, 0, recentClosedMaxPerResponse)
	addIndex := func(index int) {
		if selected[index] || len(selectedIndices) >= recentClosedMaxPerResponse {
			return
		}
		selected[index] = true
		selectedIndices = append(selectedIndices, index)
	}
	for i := range buffer {
		if len(selectedIndices) >= recentClosedMaxPerResponse {
			break
		}
		if buffer[i].redeliver {
			addIndex(i)
		}
	}
	for i := start; i < end; i++ {
		if len(selectedIndices) >= recentClosedMaxPerResponse {
			break
		}
		addIndex(i)
	}
	return selectedIndices
}

func QueryConnections() *connectionSnapshot {
	ensureRecentClosedSampler()
	noteQueryCadence()
	snap := statistic.DefaultManager.Snapshot()

	recentClosedMu.Lock()
	defer recentClosedMu.Unlock()

	conns := make([]*connectionInfo, 0, len(snap.Connections)+len(recentClosedBuffer))
	for _, c := range snap.Connections {
		conns = append(conns, &connectionInfo{TrackerInfo: c})
	}
	// Cap how much retained history one response carries (see [recentClosedMaxPerResponse]).
	selectedIndices := selectRecentClosedIndices(recentClosedBuffer)

	// Stamp the poll on every entry it returns: an entry that has been seen once
	// expires on the short window from here on, so retained history cannot keep
	// growing once the app is back and polling again. A redelivery refreshes that
	// window because the app only now receives the upgraded metadata.
	deliveredAt := time.Now()
	for _, i := range selectedIndices {
		entry := &recentClosedBuffer[i]
		if entry.redeliver {
			entry.redeliver = false
			entry.deliveredAt = deliveredAt
		} else if entry.deliveredAt.IsZero() {
			entry.deliveredAt = deliveredAt
		}
		conns = append(conns, &connectionInfo{
			TrackerInfo: entry.info,
			Closed:      true,
			ClosedAt:    entry.closedAt.UnixMilli(),
		})
	}

	return &connectionSnapshot{
		DownloadTotal: snap.DownloadTotal,
		UploadTotal:   snap.UploadTotal,
		Connections:   conns,
		Memory:        snap.Memory,
	}
}

func CloseConnection(id string) bool {
	conn := statistic.DefaultManager.Get(id)
	if conn == nil {
		return false
	}

	return conn.Close() == nil
}

func CloseAllConnections() {
	done := make(chan struct{})
	go func() {
		statistic.DefaultManager.Range(func(c statistic.Tracker) bool {
			_ = c.Close()
			return true
		})
		close(done)
	}()
	select {
	case <-done:
	case <-time.After(closeAllTimeout):
	}
}

func closeMatch(filter func(conn C.Connection) bool) {
	statistic.DefaultManager.Range(func(c statistic.Tracker) bool {
		if filter(c) {
			_ = c.Close()
		}
		return true
	})
}

func closeConnByGroup(name string) {
	closeMatch(func(conn C.Connection) bool {
		for _, c := range conn.Chains() {
			if c == name {
				return true
			}
		}

		return false
	})
}
