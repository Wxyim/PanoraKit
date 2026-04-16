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
package all

import (
	_ "cfa/native/app"
	_ "cfa/native/common"
	_ "cfa/native/config"
	_ "cfa/native/delegate"
	_ "cfa/native/platform"
	_ "cfa/native/proxy"
	_ "cfa/native/tun"
	_ "cfa/native/tunnel"

	_ "golang.org/x/sync/semaphore"

	_ "github.com/metacubex/mihomo/log"
)
