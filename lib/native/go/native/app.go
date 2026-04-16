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
	"errors"
	"unsafe"

	"cfa/native/app"
	"cfa/native/config"

	"github.com/metacubex/mihomo/log"
)

func openRemoteContent(url string) (int, error) {
	u := C.CString(url)
	e := (*C.char)(C.malloc(1024))

	log.Debugln("Open remote url: %s", url)

	defer C.free(unsafe.Pointer(e))

	fd := C.open_content(u, e, 1024)

	if fd < 0 {
		return -1, errors.New(C.GoString(e))
	}

	return int(fd), nil
}

//export notifyDnsChanged
func notifyDnsChanged(dnsList C.c_string) {
	d := C.GoString(dnsList)

	app.NotifyDnsChanged(d)
}

//export notifyInstalledAppsChanged
func notifyInstalledAppsChanged(uids C.c_string) {
	u := C.GoString(uids)

	app.NotifyInstallAppsChanged(u)
}

//export notifyTimeZoneChanged
func notifyTimeZoneChanged(name C.c_string, offset C.int) {
	app.NotifyTimeZoneChanged(C.GoString(name), int(offset))
}

//export queryConfiguration
func queryConfiguration() *C.char {
	response := config.QueryUiConfiguration()
	return marshalJson(&response)
}

func init() {
	app.ApplyContentContext(openRemoteContent)
}
