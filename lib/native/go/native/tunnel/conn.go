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

	C "github.com/metacubex/mihomo/constant"
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
// entries do not keep the fine cadence armed: they only need to expire, which
// the coarse tick is enough for.
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
	// many entries.
	recentClosedUndeliveredRetention = 30 * time.Minute
	// [recentClosedMax] bounds the buffer itself, which is what actually caps
	// the connection payload. On a device sustaining more than ~80 closes per
	// second the oldest entries can therefore be evicted before the app polls;
	// that trades a bounded slice of history for a bounded payload.
	recentClosedMax = 500
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

type recentClosedEntry struct {
	info     *statistic.TrackerInfo
	closedAt time.Time
	// deliveredAt marks the poll that first returned this entry. Zero means no
	// poll has seen it yet, which is what keeps a background close around until
	// the app comes back (see the retention notes above).
	deliveredAt time.Time
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

var (
	recentClosedOnce   sync.Once
	recentClosedMu     sync.Mutex
	recentClosedSeen   = make(map[string]*statistic.TrackerInfo)
	recentClosedBuffer []recentClosedEntry
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
	defer recentClosedMu.Unlock()

	// Nothing live and nothing pending: only retained entries can have work
	// left, and that work is expiring them, which the coarse cadence covers.
	// Retained entries deliberately do not keep the fine cadence armed.
	idle := connectionCount == 0 && len(recentClosedSeen) == 0
	if !idle {
		// Detect connections that left the manager since the previous sample and
		// retain their final tracker info so a later poll can still observe them.
		for id, info := range recentClosedSeen {
			if alive[id] == nil {
				recentClosedBuffer = append(recentClosedBuffer, recentClosedEntry{info: info, closedAt: now})
				delete(recentClosedSeen, id)
			}
		}
		for id, info := range alive {
			if _, ok := recentClosedSeen[id]; !ok {
				recentClosedSeen[id] = info
			}
		}
	}

	// Expire retained entries. Entries no poll has seen yet are kept for the
	// longer window so a backgrounded app still gets them on its next poll;
	// delivered ones expire as before, measured from the poll that delivered
	// them. Entries are appended in close order and delivered in batches, so
	// both timestamps are non-decreasing and this stops at the first live entry.
	deliveredCutoff := now.Add(-recentClosedRetentionWindow())
	undeliveredCutoff := now.Add(-recentClosedUndeliveredRetention)
	keep := 0
	for keep < len(recentClosedBuffer) {
		entry := recentClosedBuffer[keep]
		if entry.deliveredAt.IsZero() {
			if entry.closedAt.After(undeliveredCutoff) {
				break
			}
		} else if entry.deliveredAt.After(deliveredCutoff) {
			break
		}
		keep++
	}
	recentClosedBuffer = recentClosedBuffer[keep:]

	// Bound the buffer on a busy device.
	if len(recentClosedBuffer) > recentClosedMax {
		recentClosedBuffer = recentClosedBuffer[len(recentClosedBuffer)-recentClosedMax:]
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
	// Stamp the poll on every entry it returns: an entry that has been seen once
	// expires on the short window from here on, so retained history cannot keep
	// growing once the app is back and polling again.
	deliveredAt := time.Now()
	for i := range recentClosedBuffer {
		entry := &recentClosedBuffer[i]
		if entry.deliveredAt.IsZero() {
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
