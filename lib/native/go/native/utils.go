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
