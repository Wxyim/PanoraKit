/*
 * This file is part of YumeBox.
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
 */

package main

/*
#cgo LDFLAGS: -llog

#include "bridge.h"
*/
import "C"

import (
	"os"
	"runtime"
	"runtime/debug"
	"strconv"
	"strings"

	"cfa/native/config"
	"cfa/native/delegate"
	"cfa/native/tunnel"

	"github.com/metacubex/mihomo/log"
)

func main() {
	panic("Stub!")
}

//export coreInit
func coreInit(home, versionName, gitVersion C.c_string, sdkVersion C.int) {
	h := C.GoString(home)
	v := C.GoString(versionName)
	g := C.GoString(gitVersion)
	s := int(sdkVersion)

	delegate.Init(h, v, g, s)
	applyMemoryLimit()

	reset()
}

// applyMemoryLimit sets a soft cap on the Go runtime heap so the process does not
// rely solely on manual forceGc() calls to keep a sane footprint on low-RAM devices.
// The cap is derived from total physical RAM with a floor and a ceiling.
func applyMemoryLimit() {
	total := totalSystemMemory()
	if total <= 0 {
		log.Warnln("[APP] unable to read /proc/meminfo, skip memory limit")
		return
	}

	const (
		minLimit = 256 << 20 // 256 MiB
		maxLimit = 1 << 30   // 1 GiB
	)

	limit := total / 4
	if limit < minLimit {
		limit = minLimit
	}
	if limit > maxLimit {
		limit = maxLimit
	}

	debug.SetMemoryLimit(int64(limit))
	log.Infoln("[APP] Go runtime memory limit:", limit>>20, "MiB")
}

// totalSystemMemory returns the physical RAM in bytes reported by /proc/meminfo,
// or 0 when it cannot be determined.
func totalSystemMemory() uint64 {
	data, err := os.ReadFile("/proc/meminfo")
	if err != nil {
		return 0
	}
	for _, line := range strings.Split(string(data), "\n") {
		if !strings.HasPrefix(line, "MemTotal:") {
			continue
		}
		fields := strings.Fields(line)
		if len(fields) < 2 {
			return 0
		}
		kb, err := strconv.ParseUint(fields[1], 10, 64)
		if err != nil {
			return 0
		}
		return kb * 1024
	}
	return 0
}

//export reset
func reset() {
	config.LoadDefault()
	invalidateProxyGroupCache()
	tunnel.ResetStatistic()
	tunnel.CloseAllConnections()
}

//export forceGc
func forceGc() {
	go func() {
		log.Infoln("[APP] request force GC")

		runtime.GC()
		debug.FreeOSMemory()
	}()
}

//export setCustomUserAgent
func setCustomUserAgent(userAgent C.c_string) {
	ua := C.GoString(userAgent)
	config.SetCustomUserAgent(ua)
	log.Infoln("[APP] custom User-Agent set:", ua)
}
