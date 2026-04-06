package main

//#include "bridge.h"
import "C"

import (
	"unsafe"

	"cfa/native/config"
)

type remoteValidCallback struct {
	callback unsafe.Pointer
}

func (r *remoteValidCallback) reportStatus(json string) {
	C.fetch_report(r.callback, marshalString(json))
}

//export fetchAndValid
func fetchAndValid(callback unsafe.Pointer, path, url C.c_string, force C.int) {
	pathStr := C.GoString(path)
	urlStr := C.GoString(url)
	forceBool := force != 0

	runAsyncWithRelease(callback, func() {
		cb := &remoteValidCallback{callback: callback}
		err := config.FetchAndValid(pathStr, urlStr, forceBool, cb.reportStatus)
		C.fetch_complete(callback, marshalError(err))
	})
}

//export loadCompiledConfig
func loadCompiledConfig(completable unsafe.Pointer, path C.c_string) {
	pathStr := C.GoString(path)

	completeAsync(completable, func() error {
		return config.LoadCompiled(pathStr)
	})
}

//export inspectCompiledConfig
func inspectCompiledConfig(yamlText C.c_string) *C.char {
	cfg, err := config.QueryConfigFromCompiledYaml(C.GoString(yamlText))
	if err != nil {
		return nil
	}
	return marshalJson(cfg)
}

//export inspectCompiledGroups
func inspectCompiledGroups(yamlText C.c_string, profileDir C.c_string, excludeNotSelectable C.int) *C.char {
	groups, err := config.QueryProxyGroupsFromCompiledYaml(
		C.GoString(yamlText),
		C.GoString(profileDir),
		excludeNotSelectable != 0,
	)
	if err != nil {
		return nil
	}
	return marshalJson(groups)
}

//export compilePreview
func compilePreview(requestJson C.c_string) *C.char {
	result := config.CompileOverride(C.GoString(requestJson), false)
	return C.CString(result)
}

//export compileToFile
func compileToFile(requestJson C.c_string) *C.char {
	result := config.CompileOverride(C.GoString(requestJson), true)
	return C.CString(result)
}
