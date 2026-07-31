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
	nowUpload, nowDownload := tunnel.Now()
	totalUpload, totalDownload := tunnel.Total()

	return marshalJson(&struct {
		Configuration config.RuntimeUiConfiguration `json:"configuration"`
		Providers     []*tunnel.Provider             `json:"providers"`
		ProxyGroups   []*tunnel.ProxyGroup           `json:"proxyGroups"`
		TrafficNow    int64                          `json:"trafficNow"`
		TrafficTotal  int64                          `json:"trafficTotal"`
	}{
		Configuration: config.QueryUiConfiguration(),
		Providers:     tunnel.QueryProviders(),
		ProxyGroups:   queryProxyGroups(false, tunnel.Default),
		TrafficNow:    packTraffic(nowUpload, nowDownload),
		TrafficTotal:  packTraffic(totalUpload, totalDownload),
	})
}

func packTraffic(upload, download uint64) int64 {
	return int64(downScaleTraffic(upload)<<32 | downScaleTraffic(download))
}

func downScaleTraffic(value uint64) uint64 {
	const (
		gbThresh  = 1024 * 1024 * 1024
		mbThresh  = 1024 * 1024
		kbThresh  = 1024
		scale     = 100
		valueMask = 0x3FFFFFFF
	)

	switch {
	case value > gbThresh:
		return ((value * scale / 1024 / 1024 / 1024) & valueMask) | (3 << 30)
	case value > mbThresh:
		return ((value * scale / 1024 / 1024) & valueMask) | (2 << 30)
	case value > kbThresh:
		return ((value * scale / 1024) & valueMask) | (1 << 30)
	default:
		return value & valueMask
	}
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
