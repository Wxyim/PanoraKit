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
package app

import (
	"errors"
	"os"
	"syscall"
)

var openContentImpl = func(url string) (int, error) {
	return -1, errors.New("not implement")
}

func OpenContent(url string) (*os.File, error) {
	fd, err := openContentImpl(url)

	if err != nil {
		return nil, err
	}

	_ = syscall.SetNonblock(fd, true)

	return os.NewFile(uintptr(fd), "fd"), nil
}

func ApplyContentContext(openContent func(string) (int, error)) {
	openContentImpl = openContent
}
