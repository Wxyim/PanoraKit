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

/*
#include "bridge.h"
*/
import "C"

import (
	"encoding/json"
	"reflect"
	"unsafe"

	"github.com/metacubex/mihomo/log"
)

func cString(value string) *C.char {
	return C.CString(value)
}

func releaseObject(obj unsafe.Pointer) {
	C.release_object(obj)
}

func runAsyncWithRelease(obj unsafe.Pointer, work func()) {
	go func() {
		defer releaseObject(obj)
		work()
	}()
}

func completeAsyncWith[T any](
	obj unsafe.Pointer,
	work func() T,
	marshal func(T) *C.char,
	complete func(unsafe.Pointer, *C.char),
) {
	runAsyncWithRelease(obj, func() {
		complete(obj, marshal(work()))
	})
}

func completeWithError(obj unsafe.Pointer, payload *C.char) {
	C.complete(obj, payload)
}

func completeWithJson(obj unsafe.Pointer, payload *C.char) {
	C.complete_with_string(obj, payload)
}

func completeAsync(obj unsafe.Pointer, work func() error) {
	completeAsyncWith(obj, work, marshalError, completeWithError)
}

func completeJsonAsync(obj unsafe.Pointer, work func() any) {
	completeAsyncWith(obj, work, marshalJson, completeWithJson)
}

func marshalJson(obj any) *C.char {
	res, err := json.Marshal(obj)
	if err != nil {
		log.Errorln("marshalJson: %v", err)
		return nil
	}

	return cString(string(res))
}

func marshalError(err error) *C.char {
	if err == nil {
		return nil
	}

	return cString(err.Error())
}

func marshalString(obj any) *C.char {
	if obj == nil {
		return nil
	}

	switch o := obj.(type) {
	case error:
		return cString(o.Error())
	case string:
		return cString(o)
	}

	log.Errorln("marshalString: invalid type %s", reflect.TypeOf(obj).Name())
	return nil
}
