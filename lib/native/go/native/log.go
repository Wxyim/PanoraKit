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
	"sync"
	"time"
	"unsafe"

	"github.com/metacubex/mihomo/log"
)

type message struct {
	Level   string `json:"level"`
	Message string `json:"message"`
	Time    int64  `json:"time"`
}

// logcatSubscribers tracks the live JNI log subscribers. The count decides
// whether the always-on drainer below still has to forward each line to
// logcat: with no subscriber nothing in the process can render those lines, so
// the per-line cgo call (and the logcat write behind it) is skipped while the
// mihomo channel itself keeps being drained.
var logcatSubscribers = struct {
	sync.Mutex
	signals map[chan struct{}]struct{}
}{signals: make(map[chan struct{}]struct{})}

func registerLogcatSubscriber() chan struct{} {
	signal := make(chan struct{})
	logcatSubscribers.Lock()
	logcatSubscribers.signals[signal] = struct{}{}
	logcatSubscribers.Unlock()
	return signal
}

func releaseLogcatSubscriber(signal chan struct{}) {
	logcatSubscribers.Lock()
	delete(logcatSubscribers.signals, signal)
	logcatSubscribers.Unlock()
}

func hasLogcatSubscribers() bool {
	logcatSubscribers.Lock()
	defer logcatSubscribers.Unlock()
	return len(logcatSubscribers.signals) > 0
}

// unsubscribeLogcat detaches every JNI log subscriber.
//
// Without it a closed Kotlin channel left the Go goroutine (and its JNI global
// reference) alive for the whole process lifetime, so each abandoned
// subscription kept marshalling and decoding every core log line and handed a
// duplicate copy of each line to the channels that were still open.
//
//export unsubscribeLogcat
func unsubscribeLogcat() {
	logcatSubscribers.Lock()
	signals := make([]chan struct{}, 0, len(logcatSubscribers.signals))
	for signal := range logcatSubscribers.signals {
		signals = append(signals, signal)
	}
	logcatSubscribers.signals = make(map[chan struct{}]struct{})
	logcatSubscribers.Unlock()

	for _, signal := range signals {
		close(signal)
	}
}

func init() {
	go func() {
		sub := log.Subscribe()
		defer log.UnSubscribe(sub)

		for msg := range sub {
			// Always-on drainer keeps the unbuffered mihomo log channel from
			// blocking data-plane goroutines. Only forward lines at or above
			// the configured level to logcat so per-packet debug output (DNS
			// hijack, process lookup) never pays for a logcat write when it is
			// not requested. [APP] lines always stay visible for lifecycle
			// diagnostics. Mirrors the level gate in subscribeLogcat.
			appLine := strings.HasPrefix(msg.Payload, "[APP]")
			if msg.LogLevel < log.Level() && !appLine {
				continue
			}
			// Errors and warnings are low-volume and are kept on logcat as the
			// fastest way to diagnose a failure from `adb logcat`. Info/debug lines
			// are the high-volume ones, so they are only forwarded while a
			// subscriber (log page / recording) can actually render them.
			if !appLine &&
				!hasLogcatSubscribers() &&
				msg.LogLevel != log.ERROR &&
				msg.LogLevel != log.WARNING {
				continue
			}

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
	signal := registerLogcatSubscriber()

	go func(remote unsafe.Pointer, signal chan struct{}) {
		sub := log.Subscribe()
		defer log.UnSubscribe(sub)
		defer releaseLogcatSubscriber(signal)

		for {
			select {
			case <-signal:
				C.release_object(remote)

				log.Debugln("Logcat subscriber closed")

				return
			case msg, ok := <-sub:
				if !ok {
					return
				}
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

					return
				}
			}
		}
	}(remote, signal)

	log.Infoln("[APP] Logcat level: %s", log.Level().String())
}
