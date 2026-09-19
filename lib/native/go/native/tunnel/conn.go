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
	"time"

	C "github.com/metacubex/mihomo/constant"
	"github.com/metacubex/mihomo/tunnel/statistic"
)

const closeAllTimeout = 2 * time.Second

// Recently-closed connection retention.
//
// The app polls connection snapshots on a fixed cadence (1s) and only records a
// connection as "closed" after observing it in at least one snapshot. A
// short-lived request that opens and closes between two polls would therefore
// never surface in the recent-request history. To make such connections
// observable, keep the final tracker info of recently-closed connections around
// for [recentClosedRetention] and include them in [QueryConnections] snapshots,
// so the next poll is guaranteed to see them and record them as closed.
const (
	recentClosedRetention = 2 * time.Second
	recentClosedSampler   = 50 * time.Millisecond
	recentClosedMax       = 500
)

type recentClosedEntry struct {
	info     *statistic.TrackerInfo
	closedAt time.Time
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
			ticker := time.NewTicker(recentClosedSampler)
			defer ticker.Stop()
			for range ticker.C {
				observeRecentClosed()
			}
		}()
	})
}

func observeRecentClosed() {
	snap := statistic.DefaultManager.Snapshot()
	now := time.Now()

	alive := make(map[string]bool, len(snap.Connections))
	for _, c := range snap.Connections {
		alive[c.UUID.String()] = true
	}

	recentClosedMu.Lock()
	defer recentClosedMu.Unlock()

	// Detect connections that left the manager since the previous sample and
	// retain their final tracker info so a later poll can still observe them.
	for id, info := range recentClosedSeen {
		if !alive[id] {
			recentClosedBuffer = append(recentClosedBuffer, recentClosedEntry{info: info, closedAt: now})
			delete(recentClosedSeen, id)
		}
	}
	for _, c := range snap.Connections {
		id := c.UUID.String()
		if _, ok := recentClosedSeen[id]; !ok {
			recentClosedSeen[id] = c
		}
	}

	// Prune retained entries whose retention window has elapsed.
	cutoff := now.Add(-recentClosedRetention)
	keep := 0
	for keep < len(recentClosedBuffer) && !recentClosedBuffer[keep].closedAt.After(cutoff) {
		keep++
	}
	recentClosedBuffer = recentClosedBuffer[keep:]

	// Bound the buffer on a busy device.
	if len(recentClosedBuffer) > recentClosedMax {
		recentClosedBuffer = recentClosedBuffer[len(recentClosedBuffer)-recentClosedMax:]
	}
}

func QueryConnections() *connectionSnapshot {
	ensureRecentClosedSampler()
	snap := statistic.DefaultManager.Snapshot()

	recentClosedMu.Lock()
	defer recentClosedMu.Unlock()

	conns := make([]*connectionInfo, 0, len(snap.Connections)+len(recentClosedBuffer))
	for _, c := range snap.Connections {
		conns = append(conns, &connectionInfo{TrackerInfo: c})
	}
	for _, e := range recentClosedBuffer {
		conns = append(conns, &connectionInfo{TrackerInfo: e.info, Closed: true})
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
