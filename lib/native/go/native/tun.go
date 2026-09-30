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
	"context"
	"io"
	"sync"
	"time"
	"unsafe"

	"golang.org/x/sys/unix"

	"golang.org/x/sync/semaphore"

	"cfa/native/app"
	"cfa/native/tun"
)

var rTunLock sync.Mutex
var rTun *remoteTun
var rootTun io.Closer

const tunCloseAcquireTimeout = 5 * time.Second

type remoteTun struct {
	closer   io.Closer
	callback unsafe.Pointer

	closed bool
	limit  *semaphore.Weighted
}

func (t *remoteTun) markSocket(fd int) {
	_ = t.limit.Acquire(context.Background(), 1)
	defer t.limit.Release(1)

	if t.closed {
		return
	}

	C.mark_socket(t.callback, C.int(fd))
}

func (t *remoteTun) querySocketUid(protocol int, source, target string, force bool) int {
	_ = t.limit.Acquire(context.Background(), 1)
	defer t.limit.Release(1)

	if t.closed {
		return -1
	}

	forceFlag := C.int(0)
	if force {
		forceFlag = 1
	}

	return int(
		C.query_socket_uid(
			t.callback,
			C.int(protocol),
			C.CString(source),
			C.CString(target),
			forceFlag,
		),
	)
}

func (t *remoteTun) queryPackageName(uid int) string {
	_ = t.limit.Acquire(context.Background(), 1)
	defer t.limit.Release(1)

	if t.closed {
		return ""
	}

	result := C.query_package_name(t.callback, C.int(uid))
	if result == nil {
		return ""
	}
	defer C.free(unsafe.Pointer(result))
	return C.GoString(result)
}

func (t *remoteTun) close() {
	t.closed = true
	ctx, cancel := context.WithTimeout(context.Background(), tunCloseAcquireTimeout)
	defer cancel()

	if err := t.limit.Acquire(ctx, 4); err != nil {
		if t.closer != nil {
			_ = t.closer.Close()
		}
		app.ApplyTunContext(nil, nil)
		app.ApplyPackageNameResolver(nil)
		return
	}
	defer t.limit.Release(4)

	if t.closer != nil {
		_ = t.closer.Close()
	}

	app.ApplyTunContext(nil, nil)
	app.ApplyPackageNameResolver(nil)

	C.release_object(t.callback)
}

func closeCurrentTunLocked() {
	if rTun != nil {
		rTun.close()
		rTun = nil
	}

	if rootTun != nil {
		_ = rootTun.Close()
		rootTun = nil
	}

	app.ApplyTunContext(nil, nil)
	app.ApplyPackageNameResolver(nil)
}

// startTun hands the established TUN fd to the mihomo stack and returns 0 on success. On
// failure the fd is closed (F_GETFD-guarded so it is never double-closed) and 1 is returned,
// letting the Kotlin layer roll the runtime back instead of leaving a dead session marked Running.
//
//export startTun
func startTun(fd C.int, stack, gateway, portal, dns C.c_string, callback unsafe.Pointer) C.int {
	rTunLock.Lock()
	defer rTunLock.Unlock()

	closeCurrentTunLocked()

	f := int(fd)
	s := C.GoString(stack)
	g := C.GoString(gateway)
	p := C.GoString(portal)
	d := C.GoString(dns)

	remote := &remoteTun{callback: callback, closed: false, limit: semaphore.NewWeighted(4)}

	app.ApplyTunContext(remote.markSocket, remote.querySocketUid)
	app.ApplyPackageNameResolver(remote.queryPackageName)

	closer, err := tun.Start(f, s, g, p, d)
	if err != nil {
		remote.close()
		// The fd was established (the Android VPN session is live) but the stack failed to
		// take it over. sing-tun wraps the fd via os.NewFile, so depending on where its
		// teardown stopped the descriptor may already be closed. Release it exactly once:
		// F_GETFD fails on a closed descriptor, so this never double-closes (which could
		// otherwise close an unrelated, reused descriptor number).
		if _, ferr := unix.FcntlInt(uintptr(f), unix.F_GETFD, 0); ferr == nil {
			_ = unix.Close(f)
		}

		return 1
	}

	remote.closer = closer

	rTun = remote

	return 0
}

//export stopTun
func stopTun() {
	rTunLock.Lock()
	defer rTunLock.Unlock()

	closeCurrentTunLocked()
}

//export startRootTun
func startRootTun(configJSON C.c_string) *C.char {
	rTunLock.Lock()
	defer rTunLock.Unlock()

	closeCurrentTunLocked()

	closer, err := tun.StartRoot(C.GoString(configJSON))
	if err != nil {
		closeCurrentTunLocked()
		return C.CString(err.Error())
	}

	rootTun = closer
	return nil
}

//export stopRootTun
func stopRootTun() {
	rTunLock.Lock()
	defer rTunLock.Unlock()

	closeCurrentTunLocked()
}
