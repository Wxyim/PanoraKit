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
package config

import (
	"strings"

	"github.com/dlclark/regexp2"

	"cfa/native/app"

	"github.com/metacubex/mihomo/adapter/outboundgroup"
	mihomoConfig "github.com/metacubex/mihomo/config"
	C "github.com/metacubex/mihomo/constant"
)

// Proxy 代理结构体（本地定义，避免依赖 tunnel 包）
type Proxy struct {
	Name     string `json:"name"`
	Title    string `json:"title"`
	Subtitle string `json:"subtitle"`
	Type     string `json:"type"`
	Delay    int    `json:"delay"`
	Hidden   bool   `json:"hidden"`
	Icon     string `json:"icon"`
}

// ProxyGroup 代理组结构体（本地定义，避免依赖 tunnel 包）
type ProxyGroup struct {
	Name    string   `json:"name"`
	Type    string   `json:"type"`
	Now     string   `json:"now"`
	Hidden  bool     `json:"hidden"`
	Icon    string   `json:"icon"`
	Proxies []*Proxy `json:"proxies"`
}

func buildProxyGroupsFromParsed(
	cfg *mihomoConfig.Config,
	orderedNames []string,
	excludeNotSelectable bool,
) []*ProxyGroup {
	if cfg == nil || len(orderedNames) == 0 {
		return []*ProxyGroup{}
	}

	result := make([]*ProxyGroup, 0, len(orderedNames))
	pattern := app.SubtitlePattern()

	for _, name := range orderedNames {
		proxy := cfg.Proxies[name]
		if proxy == nil {
			continue
		}
		if excludeNotSelectable && proxy.Type() != C.Selector {
			continue
		}

		group, ok := proxy.Adapter().(outboundgroup.ProxyGroup)
		if !ok {
			continue
		}

		result = append(result, &ProxyGroup{
			Name:    name,
			Type:    proxy.Type().String(),
			Now:     group.Now(),
			Hidden:  group.Hidden(),
			Icon:    group.Icon(),
			Proxies: convertPreviewProxies(group.Proxies(), pattern),
		})
	}

	return result
}

func convertPreviewProxies(
	proxies []C.Proxy,
	uiSubtitlePattern *regexp2.Regexp,
) []*Proxy {
	result := make([]*Proxy, 0, len(proxies))
	for _, proxy := range proxies {
		result = append(result, buildPreviewProxy(proxy, uiSubtitlePattern))
	}
	return result
}

func buildPreviewProxy(
	proxy C.Proxy,
	uiSubtitlePattern *regexp2.Regexp,
) *Proxy {
	name := proxy.Name()
	title := name
	subtitle := proxy.Type().String()
	hidden := false
	icon := ""

	if uiSubtitlePattern != nil {
		if _, ok := proxy.Adapter().(outboundgroup.ProxyGroup); !ok {
			runes := []rune(name)
			match, err := uiSubtitlePattern.FindRunesMatch(runes)
			if err == nil && match != nil {
				title = string(runes[:match.Index]) + string(runes[match.Index+match.Length:])
				subtitle = string(runes[match.Index : match.Index+match.Length])
			}
		}
	}

	if group, ok := proxy.Adapter().(outboundgroup.ProxyGroup); ok {
		hidden = group.Hidden()
		icon = group.Icon()
	}

	return &Proxy{
		Name:     name,
		Title:    strings.TrimSpace(title),
		Subtitle: strings.TrimSpace(subtitle),
		Type:     proxy.Type().String(),
		Delay:    int(proxy.LastDelayForTestUrl(getPreviewTestURL(proxy))),
		Hidden:   hidden,
		Icon:     strings.TrimSpace(icon),
	}
}

func getPreviewTestURL(proxy C.Proxy) string {
	for key := range proxy.ExtraDelayHistories() {
		if len(key) > 0 {
			return key
		}
	}
	return "https://www.gstatic.com/generate_204"
}
