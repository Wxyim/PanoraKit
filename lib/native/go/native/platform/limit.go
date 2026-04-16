/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
//go:build linux
// +build linux

package platform

import "syscall"

var nullFd int
var maxFdCount int

func init() {
	fd, err := syscall.Open("/dev/null", syscall.O_WRONLY, 0644)
	if err != nil {
		panic(err.Error())
	}

	nullFd = fd

	var limit syscall.Rlimit

	if err := syscall.Getrlimit(syscall.RLIMIT_NOFILE, &limit); err != nil {
		maxFdCount = 1024
	} else {
		maxFdCount = int(limit.Cur)
	}

	maxFdCount = maxFdCount / 4 * 3
}

func ShouldBlockConnection() bool {
	fd, err := syscall.Dup(nullFd)
	if err != nil {
		return true
	}

	_ = syscall.Close(fd)

	if fd > maxFdCount {
		return true
	}

	return false
}
