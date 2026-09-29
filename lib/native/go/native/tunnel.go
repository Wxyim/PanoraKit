/*
 * This file is part of MonadBox - A customized edition of YumeBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package main

//#include "bridge.h"
import "C"

import (
	"sync"
	"time"
	"unsafe"

	"cfa/native/app"
	"cfa/native/config"
	"cfa/native/tunnel"
)

const proxyGroupCacheTTL = 250 * time.Millisecond

var proxyGroupCache struct {
	sync.Mutex
	createdAt            time.Time
	excludeNotSelectable bool
	sortMode             tunnel.SortMode
	subtitlePattern      string
	groups               []*tunnel.ProxyGroup
}

func invalidateProxyGroupCache() {
	proxyGroupCache.Lock()
	proxyGroupCache.groups = nil
	proxyGroupCache.createdAt = time.Time{}
	proxyGroupCache.Unlock()
}

//export queryTunnelState
func queryTunnelState() *C.char {
	mode := tunnel.QueryMode()

	response := &struct {
		Mode string `json:"mode"`
	}{mode}

	return marshalJson(response)
}

//export queryNow
func queryNow(upload, download *C.uint64_t) {
	up, down := tunnel.Now()

	*upload = C.uint64_t(up)
	*download = C.uint64_t(down)
}

//export queryTotal
func queryTotal(upload, download *C.uint64_t) {
	up, down := tunnel.Total()

	*upload = C.uint64_t(up)
	*download = C.uint64_t(down)
}

//export queryTrafficSnapshot
func queryTrafficSnapshot(nowUpload, nowDownload, totalUpload, totalDownload *C.uint64_t) {
	nowUp, nowDown := tunnel.Now()
	totalUp, totalDown := tunnel.Total()

	*nowUpload = C.uint64_t(nowUp)
	*nowDownload = C.uint64_t(nowDown)
	*totalUpload = C.uint64_t(totalUp)
	*totalDownload = C.uint64_t(totalDown)
}

//export queryRuntimeSnapshot
func queryRuntimeSnapshot() *C.char {
	// Traffic is deliberately not part of this payload: it changes on every call, which would stop
	// the (otherwise stable) proxy-group payload from being memoized end to end. The client reads
	// traffic through the dedicated queryTrafficSnapshot call instead.
	return marshalJson(&struct {
		Configuration config.RuntimeUiConfiguration `json:"configuration"`
		Providers     []*tunnel.Provider            `json:"providers"`
		ProxyGroups   []*tunnel.ProxyGroup          `json:"proxyGroups"`
	}{
		Configuration: config.QueryUiConfiguration(),
		Providers:     tunnel.QueryProviders(),
		ProxyGroups:   queryProxyGroups(false, tunnel.Default),
	})
}

//export queryRuntimeSnapshotStamp
func queryRuntimeSnapshotStamp() C.uint64_t {
	return C.uint64_t(runtimePayloadRevision())
}

// runtimePayloadRevision hashes exactly what queryRuntimeSnapshot serializes.
//
// The client polls that payload every couple of seconds and, in local (in-process) mode, used to
// pay for a full Go-side marshal, a UTF-8 -> UTF-16 conversion across the JNI boundary and a full
// Kotlin deserialization on every tick, all of it linear in the number of proxies. The payload is
// dominated by state that only moves when the user acts or when the core finishes a latency test,
// so the client compares this hash first and keeps its decoded copy while the hash stands.
//
// An equal hash therefore has to mean an equal payload: every field of every structure that
// queryRuntimeSnapshot marshals is hashed below. When a field is added to RuntimeUiConfiguration
// (lib/native/go/native/config/load.go), Provider (tunnel/providers.go), ProxyGroup or Proxy
// (tunnel/proxies.go), it must be hashed here as well. The client additionally expires its copy on
// a short TTL (see Clash.queryRuntimeSnapshot), so a field missed here can only delay an update
// instead of freezing it.
func runtimePayloadRevision() uint64 {
	ui := config.QueryUiConfiguration()

	h := uint64(fnvOffset64)
	h = hashString(h, ui.ExternalController)
	h = hashString(h, ui.ExternalControllerTLS)
	h = hashString(h, ui.Secret)
	h = hashString(h, ui.ConfigSource)
	h = hashString(h, ui.ConfigPath)

	providers := tunnel.QueryProviders()
	h = hashUint64(h, uint64(len(providers)))
	for _, p := range providers {
		h = hashString(h, p.Name)
		h = hashString(h, p.VehicleType)
		h = hashString(h, p.Type)
		h = hashUint64(h, uint64(p.UpdatedAt))
		h = hashString(h, p.Path)
		h = hashUint64(h, uint64(p.Count))
	}

	groups := queryProxyGroups(false, tunnel.Default)
	h = hashUint64(h, uint64(len(groups)))
	for _, g := range groups {
		h = hashString(h, g.Name)
		h = hashString(h, g.Type)
		h = hashString(h, g.Now)
		h = hashBool(h, g.Hidden)
		h = hashString(h, g.Icon)
		h = hashUint64(h, uint64(len(g.Proxies)))
		for _, p := range g.Proxies {
			h = hashString(h, p.Name)
			h = hashString(h, p.Title)
			h = hashString(h, p.Subtitle)
			h = hashString(h, p.Type)
			h = hashUint64(h, uint64(int64(p.Delay)))
			h = hashBool(h, p.Hidden)
			h = hashString(h, p.Icon)
		}
	}

	return h
}

// fnvOffset64/fnvPrime64 are the FNV-1a constants. The hashing helpers are hand-rolled over
// strings and integers so that a revision pass allocates nothing and never builds an intermediate
// representation of the payload it is stamping.
const (
	fnvOffset64 = 14695981039346656037
	fnvPrime64  = 1099511628211
)

func hashString(h uint64, value string) uint64 {
	// Length-prefix each field so that neighbouring fields cannot be shifted into each other.
	h = hashUint64(h, uint64(len(value)))
	for i := 0; i < len(value); i++ {
		h ^= uint64(value[i])
		h *= fnvPrime64
	}
	return h
}

func hashBool(h uint64, value bool) uint64 {
	if value {
		return hashUint64(h, 1)
	}
	return hashUint64(h, 0)
}

func hashUint64(h uint64, value uint64) uint64 {
	for i := 0; i < 8; i++ {
		h ^= value & 0xff
		h *= fnvPrime64
		value >>= 8
	}
	return h
}

//export queryConnections
func queryConnections() *C.char {
	return marshalJson(tunnel.QueryConnections())
}

//export closeConnection
func closeConnection(id C.c_string) C.int {
	if tunnel.CloseConnection(C.GoString(id)) {
		return 1
	}

	return 0
}

//export closeAllConnections
func closeAllConnections() {
	tunnel.CloseAllConnections()
}

//export queryGroupNames
func queryGroupNames(excludeNotSelectable C.int) *C.char {
	return marshalJson(tunnel.QueryProxyGroupNames(excludeNotSelectable != 0))
}

//export queryGroup
func queryGroup(name C.c_string, sortMode C.c_string) *C.char {
	n := C.GoString(name)
	s := C.GoString(sortMode)

	mode := tunnel.Default

	switch s {
	case "Title":
		mode = tunnel.Title
	case "Delay":
		mode = tunnel.Delay
	}

	response := tunnel.QueryProxyGroup(n, mode, app.SubtitlePattern())

	if response == nil {
		return nil
	}

	return marshalJson(response)
}

//export queryGroups
func queryGroups(excludeNotSelectable C.int, sortMode C.c_string) *C.char {
	mode := tunnel.Default
	switch C.GoString(sortMode) {
	case "Title":
		mode = tunnel.Title
	case "Delay":
		mode = tunnel.Delay
	}

	return marshalJson(queryProxyGroups(excludeNotSelectable != 0, mode))
}

func queryProxyGroups(excludeNotSelectable bool, mode tunnel.SortMode) []*tunnel.ProxyGroup {
	pattern := app.SubtitlePattern()
	patternText := ""
	if pattern != nil {
		patternText = pattern.String()
	}
	now := time.Now()
	proxyGroupCache.Lock()
	if proxyGroupCache.groups != nil &&
		proxyGroupCache.excludeNotSelectable == excludeNotSelectable &&
		proxyGroupCache.sortMode == mode &&
		proxyGroupCache.subtitlePattern == patternText &&
		now.Sub(proxyGroupCache.createdAt) < proxyGroupCacheTTL {
		groups := proxyGroupCache.groups
		proxyGroupCache.Unlock()
		return groups
	}
	proxyGroupCache.Unlock()

	names := tunnel.QueryProxyGroupNames(excludeNotSelectable)
	groups := make([]*tunnel.ProxyGroup, 0, len(names))
	for _, name := range names {
		if group := tunnel.QueryProxyGroup(name, mode, pattern); group != nil {
			groups = append(groups, group)
		}
	}

	proxyGroupCache.Lock()
	proxyGroupCache.createdAt = now
	proxyGroupCache.excludeNotSelectable = excludeNotSelectable
	proxyGroupCache.sortMode = mode
	proxyGroupCache.subtitlePattern = patternText
	proxyGroupCache.groups = groups
	proxyGroupCache.Unlock()
	return groups
}

//export healthCheck
func healthCheck(completable unsafe.Pointer, name C.c_string) {
	nameStr := C.GoString(name)

	completeAsync(completable, func() error {
		tunnel.HealthCheck(nameStr)
		invalidateProxyGroupCache()
		return nil
	})
}

//export healthCheckAll
func healthCheckAll() {
	tunnel.HealthCheckAll()
	invalidateProxyGroupCache()
}

//export healthCheckProxy
func healthCheckProxy(completable unsafe.Pointer, proxyName C.c_string) {
	proxyNameStr := C.GoString(proxyName)

	completeJsonAsync(completable, func() any {
		delay := tunnel.HealthCheckProxy(proxyNameStr)
		invalidateProxyGroupCache()
		return &struct {
			Delay int `json:"delay"`
		}{delay}
	})
}

//export patchSelector
func patchSelector(selector, name C.c_string) C.int {
	s := C.GoString(selector)
	n := C.GoString(name)

	if tunnel.PatchSelector(s, n) {
		invalidateProxyGroupCache()
		return 1
	}

	return 0
}

//export patchMode
func patchMode(mode C.c_string) C.int {
	modeStr := C.GoString(mode)

	if err := tunnel.SetMode(modeStr); err != nil {
		return 0
	}
	invalidateProxyGroupCache()
	return 1
}

//export queryProviders
func queryProviders() *C.char {
	return marshalJson(tunnel.QueryProviders())
}

//export updateProvider
func updateProvider(completable unsafe.Pointer, pType C.c_string, name C.c_string) {
	pTypeStr := C.GoString(pType)
	nameStr := C.GoString(name)

	completeAsync(completable, func() error {
		err := tunnel.UpdateProvider(pTypeStr, nameStr)
		if err == nil {
			invalidateProxyGroupCache()
		}
		return err
	})
}

//export suspend
func suspend(suspended C.int) {
	tunnel.Suspend(suspended != 0)
}
