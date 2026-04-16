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
	"strings"
	"time"
	"unsafe"

	"github.com/metacubex/mihomo/log"
)

type message struct {
	Level   string `json:"level"`
	Message string `json:"message"`
	Time    int64  `json:"time"`
}

func init() {
	go func() {
		sub := log.Subscribe()
		defer log.UnSubscribe(sub)

		for msg := range sub {
			cPayload := cString(msg.Payload)

			switch msg.LogLevel {
			case log.INFO:
				C.log_info(cPayload)
			case log.ERROR:
				C.log_error(cPayload)
			case log.WARNING:
				C.log_warn(cPayload)
			case log.DEBUG:
				C.log_debug(cPayload)
			case log.SILENT:
				C.log_verbose(cPayload)
			}
		}
	}()
}

//export subscribeLogcat
func subscribeLogcat(remote unsafe.Pointer) {
	go func(remote unsafe.Pointer) {
		sub := log.Subscribe()
		defer log.UnSubscribe(sub)

		for msg := range sub {
			if msg.LogLevel < log.Level() && !strings.HasPrefix(msg.Payload, "[APP]") {
				continue
			}

			rMsg := &message{
				Level:   msg.LogLevel.String(),
				Message: msg.Payload,
				Time:    time.Now().UnixNano() / 1000 / 1000,
			}

			if C.logcat_received(remote, marshalJson(rMsg)) != 0 {
				C.release_object(remote)

				log.Debugln("Logcat subscriber closed")

				break
			}
		}
	}(remote)

	log.Infoln("[APP] Logcat level: %s", log.Level().String())
}
