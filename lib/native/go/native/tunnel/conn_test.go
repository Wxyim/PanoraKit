package tunnel

import (
	"reflect"
	"testing"
	"time"

	C "github.com/metacubex/mihomo/constant"
	"github.com/metacubex/mihomo/tunnel/statistic"
)

func TestConnectionNeedsUidOnlyForUnattributedUserConnections(t *testing.T) {
	info := &statistic.TrackerInfo{Metadata: &C.Metadata{Type: C.TUN}}
	if !connectionNeedsUid(info) {
		t.Fatal("a TUN connection without a UID must be enrichable")
	}

	info.Metadata.Uid = 10_123
	if connectionNeedsUid(info) {
		t.Fatal("a connection with a UID must not be queried again")
	}

	info.Metadata.Uid = 0
	info.Metadata.Type = C.INNER
	if connectionNeedsUid(info) {
		t.Fatal("inner connections have no owning app")
	}

	if connectionNeedsUid(nil) || connectionNeedsUid(&statistic.TrackerInfo{}) {
		t.Fatal("missing metadata must not be treated as an enrichable connection")
	}
}

func TestRetainRecentClosedKeepsUnresolvedDeliveredEntryForLateUid(t *testing.T) {
	now := time.Now()
	deliveredCutoff := now.Add(-6 * time.Second)
	undeliveredCutoff := now.Add(-recentClosedUndeliveredRetention)
	info := &statistic.TrackerInfo{Metadata: &C.Metadata{Type: C.TUN}}

	entry := recentClosedEntry{
		info:        info,
		closedAt:    now.Add(-time.Minute),
		deliveredAt: now.Add(-10 * time.Second),
	}
	if !retainRecentClosed(&entry, true, deliveredCutoff, undeliveredCutoff) {
		t.Fatal("an unresolved delivered entry must survive its short window while retries remain")
	}

	entry.enrichAttempts = enrichUidMaxAttempts
	if retainRecentClosed(&entry, true, deliveredCutoff, undeliveredCutoff) {
		t.Fatal("an entry that exhausted its retries must use the normal delivered window")
	}

	entry.enrichAttempts = 0
	info.Metadata.Uid = 10_123
	if retainRecentClosed(&entry, true, deliveredCutoff, undeliveredCutoff) {
		t.Fatal("a resolved entry must not be retained by the UID grace path")
	}
	entry.redeliver = true
	if !retainRecentClosed(&entry, true, deliveredCutoff, undeliveredCutoff) {
		t.Fatal("a pending redelivery must survive until the app polls again")
	}

	entry.redeliver = false
	info.Metadata.Uid = 0
	if retainRecentClosed(&entry, false, deliveredCutoff, undeliveredCutoff) {
		t.Fatal("find-process-mode=off must not extend retention")
	}
}

func TestSelectRecentClosedIndicesPrioritizesRedelivery(t *testing.T) {
	buffer := make([]recentClosedEntry, 250)
	for i := range buffer {
		buffer[i].closedAt = time.Unix(int64(i), 0)
		if i < 200 {
			buffer[i].deliveredAt = time.Unix(1, 0)
		}
	}
	buffer[0].redeliver = true

	selected := selectRecentClosedIndices(buffer)
	if len(selected) != recentClosedMaxPerResponse {
		t.Fatalf("selected %d entries, want %d", len(selected), recentClosedMaxPerResponse)
	}
	if selected[0] != 0 {
		t.Fatalf("first selected index = %d, want redelivered index 0", selected[0])
	}
	if got := len(uniqueInts(selected)); got != len(selected) {
		t.Fatalf("redelivery index was duplicated: selected=%v", selected)
	}

	undelivered := make([]recentClosedEntry, 250)
	for i := range undelivered {
		undelivered[i].closedAt = time.Unix(int64(i), 0)
	}
	selected = selectRecentClosedIndices(undelivered)
	if !reflect.DeepEqual(selected, intRange(0, recentClosedMaxPerResponse)) {
		t.Fatalf("backlog selection = %v, want oldest %d entries", selected, recentClosedMaxPerResponse)
	}
}

func uniqueInts(values []int) map[int]struct{} {
	result := make(map[int]struct{}, len(values))
	for _, value := range values {
		result[value] = struct{}{}
	}
	return result
}

func intRange(start, end int) []int {
	result := make([]int, 0, end-start)
	for i := start; i < end; i++ {
		result = append(result, i)
	}
	return result
}
