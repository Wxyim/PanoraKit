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
package config

var (
	defaultNameServers = []string{
		"223.5.5.5",
		"119.29.29.29",
		"8.8.4.4",
		"1.0.0.1",
	}
	defaultFakeIPFilter = []string{
		// Stun Services
		"+.stun.*.*",
		"+.stun.*.*.*",
		"+.stun.*.*.*.*",
		"+.stun.*.*.*.*.*",

		// Google Voices
		"lens.l.google.com",

		// Nintendo Switch STUN
		"*.n.n.srv.nintendo.net",

		// PlayStation STUN
		"+.stun.playstation.net",

		// XBox
		"xbox.*.*.microsoft.com",
		"*.*.xboxlive.com",

		// Microsoft Captive Portal
		"*.msftncsi.com",
		"*.msftconnecttest.com",

		// Bilibili CDN
		"*.mcdn.bilivideo.cn",

		// Windows Default LAN WorkGroup
		"WORKGROUP",
	}
	defaultFakeIPRange = "28.0.0.0/8"
)
