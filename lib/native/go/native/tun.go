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

func (t *remoteTun) querySocketUid(protocol int, source, target string) int {
	_ = t.limit.Acquire(context.Background(), 1)
	defer t.limit.Release(1)

	if t.closed {
		return -1
	}

	return int(C.query_socket_uid(t.callback, C.int(protocol), C.CString(source), C.CString(target)))
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
		return
	}
	defer t.limit.Release(4)

	if t.closer != nil {
		_ = t.closer.Close()
	}

	app.ApplyTunContext(nil, nil)

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
}

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

	closer, err := tun.Start(f, s, g, p, d)
	if err != nil {
		remote.close()

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
