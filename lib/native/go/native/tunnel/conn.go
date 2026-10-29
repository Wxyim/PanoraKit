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
// backgrounded — the manager close callback below already records it — and
// needs no extra polling, no push channel and no binder traffic.
//
// A delivered close whose owner is still unknown is another special case: its
// first copy may already have reached the app, but a later UID lookup can still
// repair it. Such entries keep the long replay window until their retry budget
// is exhausted, and a successful late lookup marks them for one redelivery.
//
// A normal close is recorded synchronously by the Manager.Leave callback
// installed below, so the 20ms close-diff sampler is no longer needed. While
// live trackers exist, the fallback sampler keeps a 250ms cadence to reconcile
// unexpected removals and switches to 20ms only while a live connection still
// needs UID enrichment and its next retry is due. With no live trackers, it
// checks retained UID work only while the app is polling, then parks on the
// lifecycle/query channel. Expired retained entries are pruned lazily when the
// next snapshot is queried, so an idle process does not keep a timer running.
const (
	recentClosedRetention = 6 * time.Second
	// [recentClosedRetentionCap] bounds the adaptive window so a stalled app (or
	// a single manual refresh) cannot pin every closed connection in the
	// snapshot: the buffer is also bounded by [recentClosedMax], but a bound on
	// the window keeps the steady-state payload predictable as well.
	recentClosedRetentionCap    = 8 * time.Second
	recentUidEnrichmentSampler  = 20 * time.Millisecond
	recentClosedFallbackSampler = 250 * time.Millisecond
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
		!entry.system &&
		entry.enrichAttempts < enrichUidMaxAttempts &&
		connectionNeedsUid(entry.info) {
		return entry.closedAt.After(undeliveredCutoff)
	}
	return entry.deliveredAt.After(deliveredCutoff)
}

type recentClosedEntry struct {
	info       *statistic.TrackerInfo
	closedAt   time.Time
	durationMs int64
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
	// system marks a retained close whose owner was authoritatively resolved to UID 0. It is a
	// terminal identity, so the entry must not consume more enrichment attempts; the flag is
	// carried into every snapshot so the app can render "System" instead of "Unknown App".
	system bool
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
	// System marks a connection whose owner is the kernel/root rather than an ordinary app. It is
	// only set after an authoritative UID 0 lookup (see [enrichUid]); an unresolved lookup (-1) is
	// deliberately left false so it still renders as Unknown App.
	System bool `json:"system,omitempty"`
	// ClosedAt is when the core observed the connection leaving, in wall-clock
	// milliseconds. The app stamps its recent-request history with it, which
	// matters for a batch of closes that happened while the app was
	// backgrounded: those must not all look like they closed at poll time.
	// Omitted for live connections.
	ClosedAt int64 `json:"closedAt,omitempty"`
	// DurationMs is measured with Go's monotonic clock at close time. It is authoritative for the
	// detail-sheet duration and is unaffected by wall-clock adjustments between Start and ClosedAt.
	// Omitted for live connections and when the duration rounds to zero.
	DurationMs int64 `json:"durationMs,omitempty"`
}

// enrichState tracks one live connection's UID enrichment attempts so a miss is retried a couple of
// times (Android publishes a socket's owner a few ms after the socket first sends) without burning a
// binder call on every sampler tick for the rest of the connection's life.
type enrichState struct {
	attempts int
	lastAt   int64 // UnixNano of the last attempt
	// system is set once the owner was authoritatively resolved to UID 0; further lookups are
	// unnecessary and would only waste binder calls.
	system bool
}

// uidEnrichmentRequest keeps the retained buffer entry that a lookup belongs to, when there is
// one. Live connections have no buffer entry; a successful lookup for a retained close must also
// mark that entry for redelivery to the app.
type uidEnrichmentRequest struct {
	info  *statistic.TrackerInfo
	entry *recentClosedEntry
}

// uidEnrichmentOutcome distinguishes a lookup that found an app from one that authoritatively
// found the kernel/root owner. Both are terminal answers; only a negative result stays retryable.
type uidEnrichmentOutcome int

const (
	uidEnrichmentUnresolved uidEnrichmentOutcome = iota
	uidEnrichmentAttributed
	uidEnrichmentSystem
)

type uidEnrichmentResult struct {
	request uidEnrichmentRequest
	outcome uidEnrichmentOutcome
}

var (
	recentClosedOnce     sync.Once
	recentClosedMu       sync.Mutex
	recentClosedSeen     = make(map[string]*statistic.TrackerInfo)
	recentClosedEnriched = make(map[string]enrichState)
	recentClosedBuffer   []recentClosedEntry
	recentClosedBuffered = make(map[string]struct{})
	recentClosedWake     = make(chan struct{}, 1)
)

// ensureRecentClosedSampler installs the manager lifecycle callbacks and starts
// the fallback sampler. Manager.Leave records a normal close immediately; the
// sampler only reconciles unexpected removals and performs bounded UID
// enrichment. Retained entries are pruned during active scans and before a
// snapshot is returned.
func ensureRecentClosedSampler() {
	recentClosedOnce.Do(func() {
		statistic.DefaultManager.SetTrackerCallbacks(onRecentTrackerJoined, onRecentTrackerClosed)
		go func() {
			// Reuse both the timer and the live map across ticks. The fallback
			// cadence is 250ms while live trackers exist. With no live trackers
			// and no pending UID work, the loop blocks until a join, leave, or
			// connection query arrives.
			alive := make(map[string]statistic.Tracker, 16)
			timer := time.NewTimer(0)
			defer timer.Stop()
			timerC := timer.C
			for {
				if timerC == nil {
					<-recentClosedWake
				} else {
					select {
					case <-timerC:
						timerC = nil
					case <-recentClosedWake:
						stopAndDrainTimer(timer)
						timerC = nil
					}
				}

				needFast := observeRecentClosed(alive)
				interval, keepSampling := recentClosedSamplerInterval(alive, needFast, time.Now())
				if !keepSampling {
					timerC = nil
					continue
				}
				stopAndDrainTimer(timer)
				timer.Reset(interval)
				timerC = timer.C
			}
		}()
	})
}

func onRecentTrackerJoined(_ statistic.Tracker) {
	wakeRecentClosedSampler()
}

func wakeRecentClosedSampler() {
	select {
	case recentClosedWake <- struct{}{}:
	default:
	}
}

func stopAndDrainTimer(timer *time.Timer) {
	if timer.Stop() {
		return
	}
	select {
	case <-timer.C:
	default:
	}
}

func recentClosedSamplerInterval(
	alive map[string]statistic.Tracker,
	needFast bool,
	now time.Time,
) (time.Duration, bool) {
	if needFast {
		return recentUidEnrichmentSampler, true
	}
	if len(alive) > 0 {
		return recentClosedFallbackSampler, true
	}
	if hasPendingRecentClosedUidEnrichment(now) {
		return recentClosedFallbackSampler, true
	}
	return 0, false
}

func hasPendingRecentClosedUidEnrichment(now time.Time) bool {
	if !uidEnrichmentPossible() || !uidEnrichmentActive(now) {
		return false
	}

	recentClosedMu.Lock()
	defer recentClosedMu.Unlock()
	for i := range recentClosedBuffer {
		entry := &recentClosedBuffer[i]
		if !entry.system &&
			entry.enrichAttempts < enrichUidMaxAttempts &&
			connectionNeedsUid(entry.info) {
			return true
		}
	}
	return false
}

// onRecentTrackerClosed records the final tracker info exactly when the tracker
// is removed. Manager.Leave runs before the underlying connection is closed, so
// the counters and metadata are still valid. This replaces the 20ms close-diff
// sampler and gives closedAt the actual close-call time rather than the next
// sample time.
func onRecentTrackerClosed(info *statistic.TrackerInfo) {
	if info == nil {
		return
	}
	now := time.Now()
	id := info.UUID.String()

	recentClosedMu.Lock()
	state := recentClosedEnriched[id]
	system := state.system || app.IsSystemMetadata(info.Metadata)
	delete(recentClosedSeen, id)
	delete(recentClosedEnriched, id)
	appendRecentClosedLocked(info, now, system)
	recentClosedMu.Unlock()

	app.UnmarkSystemMetadata(info.Metadata)
	wakeRecentClosedSampler()
}

func appendRecentClosedLocked(info *statistic.TrackerInfo, closedAt time.Time, system bool) {
	if info == nil {
		return
	}
	durationMs := recentClosedDurationMs(info, closedAt)
	// Keep the wall-clock field monotonic as a data invariant. The monotonic
	// duration above remains authoritative if the wall clock moved backwards.
	if closedAt.UnixMilli() < info.Start.UnixMilli() {
		closedAt = info.Start
	}
	id := info.UUID.String()
	if _, exists := recentClosedBuffered[id]; exists {
		return
	}
	recentClosedBuffered[id] = struct{}{}
	recentClosedBuffer = append(recentClosedBuffer, recentClosedEntry{
		info:       info,
		closedAt:   closedAt,
		durationMs: durationMs,
		system:     system,
	})
}

func recentClosedDurationMs(info *statistic.TrackerInfo, closedAt time.Time) int64 {
	if info == nil {
		return 0
	}
	// time.Time.Sub uses the monotonic reading when both values came from this
	// process, so an NTP/user wall-clock adjustment cannot make the duration
	// negative or jump.
	elapsed := closedAt.Sub(info.Start)
	if elapsed < 0 {
		return 0
	}
	return elapsed.Milliseconds()
}

func dropRecentClosedPrefixLocked(count int) {
	if count <= 0 {
		return
	}
	if count > len(recentClosedBuffer) {
		count = len(recentClosedBuffer)
	}
	for i := 0; i < count; i++ {
		if info := recentClosedBuffer[i].info; info != nil {
			delete(recentClosedBuffered, info.UUID.String())
		}
	}
	recentClosedBuffer = recentClosedBuffer[count:]
}

// observeRecentClosed samples the live connection set, moves connections that
// closed since the previous sample into the retention buffer and prunes
// expired entries. It reports whether the fast UID-enrichment cadence is
// needed; close detection itself is handled by onRecentTrackerClosed. A false
// return means the caller may use the coarse fallback cadence.
func observeRecentClosed(alive map[string]statistic.Tracker) bool {
	now := time.Now()

	// Snapshot() also reads /proc/<pid>/statm for the memory field, which is
	// not needed on this watcher. Iterate the manager directly so ticks avoid a
	// per-tick /proc read and the slice allocation Snapshot() performs.
	clear(alive)
	statistic.DefaultManager.Range(func(c statistic.Tracker) bool {
		alive[c.ID()] = c
		return true
	})
	connectionCount := len(alive)

	recentClosedMu.Lock()
	enrichPossible := uidEnrichmentPossible()
	polling := uidEnrichmentActive(now)
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
			if _, ok := alive[id]; !ok {
				state := recentClosedEnriched[id]
				system := state.system
				if info != nil && app.IsSystemMetadata(info.Metadata) {
					system = true
				}
				appendRecentClosedLocked(info, now, system)
				if info != nil {
					app.UnmarkSystemMetadata(info.Metadata)
				}
				delete(recentClosedSeen, id)
				delete(recentClosedEnriched, id)
			}
		}
		for id, tracker := range alive {
			if _, alreadyClosed := recentClosedBuffered[id]; alreadyClosed {
				continue
			}
			info, ok := recentClosedSeen[id]
			if !ok {
				// TrackerInfo is stable for the tracker lifetime: its counters are
				// atomics. Keep the first pointer instead of rebuilding the whole
				// live map through Info() on repeated samples.
				info = tracker.Info()
				recentClosedSeen[id] = info
			}
			if info != nil && app.IsSystemMetadata(info.Metadata) {
				state := recentClosedEnriched[id]
				if !state.system {
					state.system = true
					recentClosedEnriched[id] = state
				}
			}
		}
	}

	pruneRecentClosedLocked(now, enrichPossible)

	// Bound the buffer on a busy device.
	if len(recentClosedBuffer) > recentClosedMax {
		dropRecentClosedPrefixLocked(len(recentClosedBuffer) - recentClosedMax)
	}

	// Collect the connections whose owner the core left unresolved (see [enrichUid]). The scan is
	// budgeted per tick and skips connections it already gave up on, so opening the list over a long
	// backlog cannot turn one tick into one binder round trip per connection.
	//
	// Live connections are attributed even when the app is not polling: the recent-request history
	// retains background closes for later replay, but their sockets are gone by the time the user
	// returns, so a lookup then can only miss. A bounded attempt per live connection while the app
	// is in the background is the minimum needed to keep that replayed history attributable. The
	// remaining retry budget and the retained-close tiers stay gated on the app polling the list:
	//
	//   1. live connections: their socket is still alive, so this is the only tier where a
	//      late lookup can still succeed. Prioritising a visible backlog over them would create a
	//      fresh Unknown App row every time a short-lived connection waits behind it;
	//   2. retained closes a poll has already delivered (the row is visible and may still be
	//      recoverable from procfs);
	//   3. retained closes no poll has delivered yet (they have the long replay window);
	//   4. any leftover budget goes back to delivered unresolved closes, including a large batch
	//      delivered after the app returned from the background.
	if enrichPossible {
		budget := enrichUidBudgetPerTick
		// Leave a small slice for the already-visible close tier. Otherwise a steady stream of
		// new live connections could consume every tick and keep a row that is already showing
		// Unknown App from ever being retried.
		liveBudget := enrichUidBudgetPerTick - 2
		if !polling {
			// The app is not looking at the list. Only the bounded background attempt budget is
			// paid for, so this is a burst limiter rather than the full polling budget.
			budget = enrichUidBackgroundBudgetPerTick
			liveBudget = budget
		}
		scheduleEntry := func(entry *recentClosedEntry) bool {
			attempts, lastAt, scheduled := tryScheduleUidEnrichment(
				entry.info,
				entry.system,
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
		for id, info := range recentClosedSeen {
			if budget <= 0 || liveBudget <= 0 {
				break
			}
			state := recentClosedEnriched[id]
			// With no poller there is nobody to show a retry result to, and the background cost
			// must stay bounded. Two attempts cover a delayed owner publication without letting a
			// long-lived unresolved connection keep paying for binder calls forever.
			if !polling && state.attempts >= enrichUidBackgroundMaxAttempts {
				continue
			}
			attempts, lastAt, scheduled :=
				tryScheduleUidEnrichment(info, state.system, state.attempts, state.lastAt, now)
			if scheduled {
				state.attempts = attempts
				state.lastAt = lastAt
				recentClosedEnriched[id] = state
				pending = append(pending, uidEnrichmentRequest{info: info})
				budget--
				liveBudget--
			}
		}
		if polling {
			for i := range recentClosedBuffer {
				if budget <= 0 {
					break
				}
				entry := &recentClosedBuffer[i]
				if !entry.deliveredAt.IsZero() {
					scheduleEntry(entry)
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
	}

	needFast := false
	if enrichPossible {
		maxAttempts := enrichUidMaxAttempts
		if !polling {
			maxAttempts = enrichUidBackgroundMaxAttempts
		}
		for id, info := range recentClosedSeen {
			if !connectionNeedsUid(info) {
				continue
			}
			state := recentClosedEnriched[id]
			if state.system || state.attempts >= maxAttempts {
				continue
			}
			if state.attempts == 0 ||
				now.Sub(time.Unix(0, state.lastAt)) >= enrichUidRetryInterval {
				needFast = true
				break
			}
		}
	}
	recentClosedMu.Unlock()

	// Attribute the connections outside the lock: the lookup is a JNI/binder round trip and must not
	// block a poll waiting for the same lock.
	var results []uidEnrichmentResult
	for _, request := range pending {
		results = append(results, uidEnrichmentResult{
			request: request,
			outcome: enrichUid(request.info),
		})
	}
	if len(results) > 0 {
		recentClosedMu.Lock()
		var redeliver []*recentClosedEntry
		for _, result := range results {
			switch result.outcome {
			case uidEnrichmentAttributed:
				entry := result.request.entry
				if entry == nil {
					// The live tracker may have closed while the lookup was in
					// flight; the event callback has already moved its info into
					// the retained buffer.
					entry = findRecentClosedLocked(result.request.info.UUID.String())
				}
				if entry != nil {
					redeliver = append(redeliver, entry)
				}
			case uidEnrichmentSystem:
				id := result.request.info.UUID.String()
				if state, ok := recentClosedEnriched[id]; ok {
					state.system = true
					recentClosedEnriched[id] = state
				}
				// The live entry can have moved to the retained buffer while the lookup was in
				// flight. Locate it by UUID in that case so the system flag is not lost.
				entry := result.request.entry
				if entry == nil {
					entry = findRecentClosedLocked(id)
				}
				if entry != nil && !entry.system {
					entry.system = true
					redeliver = append(redeliver, entry)
				}
			}
		}
		for _, entry := range redeliver {
			if entry.deliveredAt.IsZero() {
				// Enrichment won the race against first delivery. The poll that delivers this
				// entry will already carry the upgraded identity, so no redelivery is needed.
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

	return needFast
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

// pruneRecentClosedLocked removes entries that have outlived their delivery or
// replay window. It is called by the sampler when it is already active and by
// QueryConnections before returning a snapshot, allowing the sampler to sleep
// indefinitely when no live connection or UID retry needs attention.
func pruneRecentClosedLocked(now time.Time, enrichPossible bool) {
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
	dropRecentClosedPrefixLocked(keep)
}

// UID enrichment for connections the core never attributed.
//
// mihomo resolves a connection's owner UID (and with it the package name) either when
// find-process-mode is `always` or, with `strict` (the default), only once the connection's rule
// evaluation reaches a PROCESS/UID rule. Under `strict` a connection matched by an earlier domain
// rule keeps an empty uid and process, and the app can only render it as "Unknown App".
//
// The app owns the very lookup the core would have run (VpnTunTransport.queryUid, with its
// positive/negative caches), so it fills the gap itself. Live connections get bounded lookups while
// their socket still exists even when the app is in the background; retries and the retained-close
// tiers only run while the app polls the list. `off` is left alone, since it is an explicit opt-out
// of process resolution.
const (
	// enrichUidPollWindow is how long after the last connection poll the app is still assumed to
	// be rendering the list. The repository polls every second while the traffic screen is open
	// and every five seconds while it only keeps the recent-request history warm.
	enrichUidPollWindow = 10 * time.Second

	// enrichUidBudgetPerTick bounds how many lookups one sampler tick performs. Opening the screen
	// over a long list of already-established connections would otherwise stall that tick behind one
	// binder round trip per connection; the rest are picked up by the following ticks.
	enrichUidBudgetPerTick = 8

	// enrichUidBackgroundBudgetPerTick bounds the one-shot live lookups while the app is not
	// polling. The sampler runs at 20ms while a live connection needs UID enrichment (and at
	// 250ms otherwise), so this still drains a burst quickly without spending an unbounded number
	// of binder calls in the background.
	enrichUidBackgroundBudgetPerTick = 4

	// enrichUidBackgroundMaxAttempts is the per-connection cap while the app is not polling.
	enrichUidBackgroundMaxAttempts = 2

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
// A positive UID is stored in the metadata for the existing app-identity fallback. UID 0 is not a
// miss: Android reports it for kernel/root-owned sockets, and the caller records it as a terminal
// system identity so the UI can label the row as System. Only a negative result remains retryable.
func enrichUid(info *statistic.TrackerInfo) uidEnrichmentOutcome {
	if !connectionNeedsUid(info) {
		return uidEnrichmentUnresolved
	}
	metadata := info.Metadata
	if app.IsSystemMetadata(metadata) {
		return uidEnrichmentSystem
	}
	source, target := app.MetadataSocketAddrs(metadata)
	if source == nil || target == nil {
		return uidEnrichmentUnresolved
	}
	switch uid := app.QuerySocketUid(source, target, true); {
	case uid > 0:
		metadata.Uid = uint32(uid)
		return uidEnrichmentAttributed
	case uid == 0:
		return uidEnrichmentSystem
	default:
		return uidEnrichmentUnresolved
	}
}

// tryScheduleUidEnrichment books one enrichment attempt for [info] when it is still unattributed
// and has attempts left, returning the updated attempt bookkeeping. The rate limiting lives here so
// the live scan and the retained-closed scan share the same retry policy.
func tryScheduleUidEnrichment(
	info *statistic.TrackerInfo,
	system bool,
	attempts int,
	lastAt int64,
	now time.Time,
) (newAttempts int, newLastAt int64, scheduled bool) {
	if system || !connectionNeedsUid(info) {
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

// findRecentClosedLocked returns the retained entry for id, if any. The caller must hold
// recentClosedMu. It covers the race where a live connection moved into the retention buffer
// while its UID lookup was in flight.
func findRecentClosedLocked(id string) *recentClosedEntry {
	for i := range recentClosedBuffer {
		info := recentClosedBuffer[i].info
		if info != nil && info.UUID.String() == id {
			return &recentClosedBuffer[i]
		}
	}
	return nil
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

// QueryConnections prunes expired history before returning it, and wakes the
// sampler after the snapshot so any retained UID enrichment remains bounded
// and prompt without a periodic idle scan.
func QueryConnections() *connectionSnapshot {
	ensureRecentClosedSampler()
	noteQueryCadence()
	snap := statistic.DefaultManager.Snapshot()

	recentClosedMu.Lock()
	defer func() {
		recentClosedMu.Unlock()
		wakeRecentClosedSampler()
	}()
	pruneRecentClosedLocked(time.Now(), uidEnrichmentPossible())

	conns := make([]*connectionInfo, 0, len(snap.Connections)+len(recentClosedBuffer))
	for _, c := range snap.Connections {
		conns = append(conns, &connectionInfo{
			TrackerInfo: c,
			System: recentClosedEnriched[c.UUID.String()].system ||
				app.IsSystemMetadata(c.Metadata),
		})
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
			DurationMs:  entry.durationMs,
			System: entry.system ||
				(entry.info != nil && app.IsSystemMetadata(entry.info.Metadata)),
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
