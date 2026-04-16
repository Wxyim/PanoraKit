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

import (
	"io"

	"github.com/metacubex/mihomo/config"
)

const (
	PROXIES = "proxies"
	RULES   = "rules"
)

func forEachProviders(rawCfg *config.RawConfig, fun func(index int, total int, key string, provider map[string]any, prefix string)) {
	total := len(rawCfg.ProxyProvider) + len(rawCfg.RuleProvider)
	index := 0

	for k, v := range rawCfg.ProxyProvider {
		fun(index, total, k, v, PROXIES)

		index++
	}

	for k, v := range rawCfg.RuleProvider {
		fun(index, total, k, v, RULES)

		index++
	}
}

func destroyProviders(cfg *config.Config) {
	for _, p := range cfg.Providers {
		if p, ok := p.(io.Closer); ok {
			_ = p.Close()
		}
	}

	for _, p := range cfg.RuleProviders {
		if p, ok := p.(io.Closer); ok {
			_ = p.Close()
		}
	}
}
