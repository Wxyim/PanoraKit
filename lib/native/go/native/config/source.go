/*
 * This file is part of MonadBox.
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 */
package config

import (
	"os"
	"path/filepath"
	"strings"

	"gopkg.in/yaml.v3"
)

// QueryProxyGroupsFromSourceYaml builds a stopped-runtime preview directly from the
// profile's source config.yaml. It only performs a generic YAML parse instead of a
// full mihomo typed parse, so mode switches stay fast even for very large configs.
//
// Inline proxy-provider payloads and providers whose files are already cached on
// disk are expanded; remote providers without a local cache are represented by
// their provider name so the config structure stays visible.
func QueryProxyGroupsFromSourceYaml(
	yamlText string,
	profileDir string,
	excludeNotSelectable bool,
	includeGlobal bool,
) ([]*ProxyGroup, error) {
	var root map[string]any
	if err := yaml.Unmarshal([]byte(yamlText), &root); err != nil {
		return nil, err
	}

	directTypes := map[string]string{}
	for _, raw := range asStringSlice(root["proxies"]) {
		node, ok := asStringMap(raw)
		if !ok {
			continue
		}
		name := strings.TrimSpace(asString(node["name"]))
		if name == "" {
			continue
		}
		directTypes[name] = asString(node["type"])
	}

	groupTypes := map[string]string{}
	for _, raw := range asStringSlice(root["proxy-groups"]) {
		group, ok := asStringMap(raw)
		if !ok {
			continue
		}
		name := strings.TrimSpace(asString(group["name"]))
		if name == "" {
			continue
		}
		groupTypes[name] = normalizeProxyType(asString(group["type"]))
	}

	providers := map[string][]sourceNode{}
	for name, raw := range asMap(root["proxy-providers"]) {
		provider, ok := asStringMap(raw)
		if !ok {
			continue
		}
		nodes := parseSourceProviderNodes(provider, profileDir)
		if len(nodes) > 0 {
			providers[name] = nodes
		}
	}

	groups := make([]*ProxyGroup, 0, len(groupTypes)+1)
	if includeGlobal {
		groups = append(groups, &ProxyGroup{
			Name:    "GLOBAL",
			Type:    "Selector",
			Now:     "",
			Proxies: sourceTopLevelProxies(root, directTypes, groupTypes),
		})
	}

	for _, raw := range asStringSlice(root["proxy-groups"]) {
		group, ok := asStringMap(raw)
		if !ok {
			continue
		}
		name := strings.TrimSpace(asString(group["name"]))
		if name == "" || strings.EqualFold(name, "GLOBAL") {
			continue
		}
		groupType := groupTypes[name]
		if excludeNotSelectable && groupType != "Selector" {
			continue
		}
		groups = append(groups, &ProxyGroup{
			Name:    name,
			Type:    groupType,
			Now:     asString(group["now"]),
			Hidden:  asBool(group["hidden"]),
			Icon:    asString(group["icon"]),
			Proxies: buildSourceGroupProxies(group["proxies"], directTypes, groupTypes, providers),
		})
	}

	return groups, nil
}

type sourceNode struct {
	name    string
	rawType string
}

func parseSourceProviderNodes(provider map[string]any, profileDir string) []sourceNode {
	var rawNodes []any
	if payload, ok := provider["payload"].([]any); ok {
		rawNodes = payload
	} else if path := strings.TrimSpace(asString(provider["path"])); path != "" {
		resolved := path
		if !filepath.IsAbs(resolved) {
			resolved = filepath.Join(profileDir, resolved)
		}
		if content, err := os.ReadFile(resolved); err == nil {
			var doc map[string]any
			if err := yaml.Unmarshal(content, &doc); err == nil {
				rawNodes = asStringSlice(doc["proxies"])
			}
		}
	}
	return parseSourceNodes(rawNodes)
}

func parseSourceNodes(rawNodes []any) []sourceNode {
	nodes := make([]sourceNode, 0, len(rawNodes))
	for _, raw := range rawNodes {
		node, ok := asStringMap(raw)
		if !ok {
			continue
		}
		name := strings.TrimSpace(asString(node["name"]))
		if name == "" {
			continue
		}
		nodes = append(nodes, sourceNode{name: name, rawType: asString(node["type"])})
	}
	return nodes
}

func buildSourceGroupProxies(
	raw any,
	directTypes map[string]string,
	groupTypes map[string]string,
	providers map[string][]sourceNode,
) []*Proxy {
	result := make([]*Proxy, 0, 64)
	for _, rawEntry := range asStringSlice(raw) {
		name := ""
		rawType := ""
		switch entry := rawEntry.(type) {
		case string:
			name = strings.TrimSpace(entry)
		case map[string]any:
			name = strings.TrimSpace(asString(entry["name"]))
			rawType = asString(entry["type"])
		}
		if name == "" {
			continue
		}
		if nodes, ok := providers[name]; ok {
			for _, node := range nodes {
				result = append(result, sourceProxy(node.name, node.rawType))
			}
			continue
		}
		if groupType, ok := groupTypes[name]; ok {
			result = append(result, sourceGroupProxy(name, groupType))
			continue
		}
		if rawType == "" {
			rawType = directTypes[name]
		}
		result = append(result, sourceProxy(name, rawType))
	}
	return result
}

func sourceTopLevelProxies(
	root map[string]any,
	directTypes map[string]string,
	groupTypes map[string]string,
) []*Proxy {
	result := make([]*Proxy, 0, len(directTypes)+len(groupTypes))
	for _, raw := range asStringSlice(root["proxies"]) {
		node, ok := asStringMap(raw)
		if !ok {
			continue
		}
		name := strings.TrimSpace(asString(node["name"]))
		if name == "" {
			continue
		}
		result = append(result, sourceProxy(name, asString(node["type"])))
	}
	for _, raw := range asStringSlice(root["proxy-groups"]) {
		group, ok := asStringMap(raw)
		if !ok {
			continue
		}
		name := strings.TrimSpace(asString(group["name"]))
		if name == "" {
			continue
		}
		groupType := groupTypes[name]
		result = append(result, sourceGroupProxy(name, groupType))
	}
	return result
}

func sourceProxy(name, rawType string) *Proxy {
	rawType = strings.TrimSpace(rawType)
	normalized := normalizeProxyType(rawType)
	if normalized == "Unknown" {
		// Builtin proxies (DIRECT/REJECT/...) are not listed in the proxies
		// section, so fall back to mapping the node name itself.
		normalized = normalizeProxyType(name)
	}
	subtitle := normalized
	if normalized == "Unknown" && rawType != "" {
		subtitle = rawType
	}
	return &Proxy{
		Name:     name,
		Title:    name,
		Subtitle: subtitle,
		Type:     normalized,
	}
}

func sourceGroupProxy(name, groupType string) *Proxy {
	return &Proxy{
		Name:     name,
		Title:    name,
		Subtitle: groupType,
		Type:     groupType,
	}
}

// normalizeProxyType maps a raw clash YAML type to the UI enum name used by the
// native runtime (Proxy.Type in the Android app). Unknown types map to "Unknown".
func normalizeProxyType(raw string) string {
	switch strings.ToLower(strings.TrimSpace(raw)) {
	case "direct":
		return "Direct"
	case "reject":
		return "Reject"
	case "reject-drop":
		return "RejectDrop"
	case "compatible":
		return "Compatible"
	case "pass":
		return "Pass"
	case "ss":
		return "Shadowsocks"
	case "ssr":
		return "ShadowsocksR"
	case "snell":
		return "Snell"
	case "socks5":
		return "Socks5"
	case "http":
		return "Http"
	case "vmess":
		return "Vmess"
	case "vless":
		return "Vless"
	case "trojan":
		return "Trojan"
	case "hysteria":
		return "Hysteria"
	case "hysteria2", "hy2":
		return "Hysteria2"
	case "tuic":
		return "Tuic"
	case "wireguard":
		return "WireGuard"
	case "dns":
		return "Dns"
	case "ssh":
		return "Ssh"
	case "mieru":
		return "Mieru"
	case "anytls":
		return "AnyTLS"
	case "sudoku":
		return "Sudoku"
	case "masque":
		return "Masque"
	case "trust-tunnel":
		return "TrustTunnel"
	case "shadow-quic":
		return "ShadowQuic"
	case "select":
		return "Selector"
	case "url-test":
		return "URLTest"
	case "fallback":
		return "Fallback"
	case "load-balance":
		return "LoadBalance"
	case "relay":
		return "Relay"
	case "smart":
		return "Smart"
	default:
		return "Unknown"
	}
}

func asMap(value any) map[string]any {
	m, _ := value.(map[string]any)
	return m
}

func asStringMap(value any) (map[string]any, bool) {
	m, ok := value.(map[string]any)
	return m, ok
}

func asStringSlice(value any) []any {
	s, _ := value.([]any)
	return s
}

func asString(value any) string {
	s, _ := value.(string)
	return s
}

func asBool(value any) bool {
	b, _ := value.(bool)
	return b
}
