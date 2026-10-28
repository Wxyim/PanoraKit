package tunnel

import (
	"testing"
	"time"

	"github.com/metacubex/mihomo/common/utils"
	C "github.com/metacubex/mihomo/constant"
	"github.com/metacubex/mihomo/tunnel/statistic"
)

func resetRecentClosedTrackingForTest() {
	recentClosedMu.Lock()
	recentClosedSeen = make(map[string]*statistic.TrackerInfo)
	recentClosedEnriched = make(map[string]enrichState)
	recentClosedBuffer = nil
	recentClosedBuffered = make(map[string]struct{})
	recentClosedMu.Unlock()
}

func TestAppendRecentClosedLockedDeduplicatesAndPrunesIDs(t *testing.T) {
	resetRecentClosedTrackingForTest()
	info := &statistic.TrackerInfo{
		UUID:     utils.NewUUIDV4(),
		Metadata: &C.Metadata{Type: C.TUN},
	}

	recentClosedMu.Lock()
	appendRecentClosedLocked(info, time.Now(), false)
	appendRecentClosedLocked(info, time.Now(), false)
	if got := len(recentClosedBuffer); got != 1 {
		t.Fatalf("buffer length = %d, want 1 after duplicate close", got)
	}
	dropRecentClosedPrefixLocked(1)
	if got := len(recentClosedBuffer); got != 0 {
		t.Fatalf("buffer length = %d, want 0 after pruning", got)
	}
	if _, exists := recentClosedBuffered[info.UUID.String()]; exists {
		t.Fatal("pruned entry still has a dedupe marker")
	}
	recentClosedMu.Unlock()
}

func TestTrackerCloseCallbackRecordsImmediately(t *testing.T) {
	resetRecentClosedTrackingForTest()
	info := &statistic.TrackerInfo{
		UUID:     utils.NewUUIDV4(),
		Metadata: &C.Metadata{Type: C.TUN},
	}

	onRecentTrackerClosed(info)
	onRecentTrackerClosed(info)

	recentClosedMu.Lock()
	defer recentClosedMu.Unlock()
	if got := len(recentClosedBuffer); got != 1 {
		t.Fatalf("buffer length = %d, want 1 after duplicate callbacks", got)
	}
	entry := recentClosedBuffer[0]
	if entry.info != info {
		t.Fatal("retained entry does not reference the closed tracker info")
	}
	if entry.closedAt.IsZero() {
		t.Fatal("close callback did not stamp closedAt")
	}
	if _, exists := recentClosedSeen[info.UUID.String()]; exists {
		t.Fatal("closed tracker is still present in the live seen map")
	}
}
