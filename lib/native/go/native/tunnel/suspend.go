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
package tunnel

func Suspend(s bool) {
	// cause by ACTION_SCREEN_OFF/ACTION_SCREEN_ON,
	// but we don't know what should do so just ignored.
	//
	// WARNING: don't call core's Tunnel.OnSuspend/OnRunning at here,
	// this will cause the core to stop processing new incoming connections when the screen is locked.
}
