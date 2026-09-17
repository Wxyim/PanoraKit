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
	"fmt"
	"os"
	"path/filepath"
	"testing"
)

func TestQueryProxyGroupsFromSourceYamlLiteralGroups(t *testing.T) {
	groups, err := QueryProxyGroupsFromSourceYaml(`
proxies:
  - name: US-01
    type: ss
  - name: JP-01
    type: vmess
proxy-groups:
  - name: AUTO
    type: url-test
    proxies:
      - US-01
      - JP-01
  - name: MANUAL
    type: select
    proxies:
      - AUTO
      - US-01
      - DIRECT
`, "", false, false)
	if err != nil {
		t.Fatalf("query source groups: %v", err)
	}
	if len(groups) != 2 {
		t.Fatalf("group count = %d, want 2", len(groups))
	}
	auto := groups[0]
	if auto.Name != "AUTO" || auto.Type != "URLTest" {
		t.Fatalf("first group = %q/%q, want AUTO/URLTest", auto.Name, auto.Type)
	}
	if len(auto.Proxies) != 2 {
		t.Fatalf("AUTO proxies = %d, want 2", len(auto.Proxies))
	}
	if auto.Proxies[0].Name != "US-01" || auto.Proxies[0].Type != "Shadowsocks" {
		t.Fatalf("AUTO[0] = %q/%q, want US-01/Shadowsocks", auto.Proxies[0].Name, auto.Proxies[0].Type)
	}
	manual := groups[1]
	if len(manual.Proxies) != 3 {
		t.Fatalf("MANUAL proxies = %d, want 2", len(manual.Proxies))
	}
	if manual.Proxies[2].Name != "DIRECT" || manual.Proxies[2].Type != "Direct" {
		t.Fatalf("MANUAL[2] = %q/%q, want DIRECT/Direct (builtin fallback)", manual.Proxies[2].Name, manual.Proxies[2].Type)
	}

	if manual.Proxies[0].Type != "URLTest" {
		t.Fatalf("MANUAL[0] type = %q, want URLTest (group reference)", manual.Proxies[0].Type)
	}
}

func TestQueryProxyGroupsFromSourceYamlIncludesGlobalInGlobalMode(t *testing.T) {
	groups, err := QueryProxyGroupsFromSourceYaml(`
proxies:
  - name: US-01
    type: ss
proxy-groups:
  - name: AUTO
    type: select
    proxies:
      - US-01
`, "", false, true)
	if err != nil {
		t.Fatalf("query source groups: %v", err)
	}
	if len(groups) != 2 {
		t.Fatalf("group count = %d, want 2", len(groups))
	}
	if groups[0].Name != "GLOBAL" {
		t.Fatalf("first group = %q, want GLOBAL", groups[0].Name)
	}
	if len(groups[0].Proxies) != 4 {
		t.Fatalf("GLOBAL proxies = %d, want 4", len(groups[0].Proxies))
	}
	if groups[0].Proxies[0].Name != "DIRECT" || groups[0].Proxies[0].Type != "Direct" {
		t.Fatalf("GLOBAL[0] = %q/%q, want DIRECT/Direct", groups[0].Proxies[0].Name, groups[0].Proxies[0].Type)
	}
	if groups[0].Proxies[1].Name != "REJECT" || groups[0].Proxies[1].Type != "Reject" {
		t.Fatalf("GLOBAL[1] = %q/%q, want REJECT/Reject", groups[0].Proxies[1].Name, groups[0].Proxies[1].Type)
	}
}

func TestQueryProxyGroupsFromSourceYamlInlineProviderExpansion(t *testing.T) {
	groups, err := QueryProxyGroupsFromSourceYaml(`
proxy-providers:
  cn:
    type: file
    payload:
      - name: CN-01
        type: trojan
      - name: CN-02
        type: hysteria2
proxy-groups:
  - name: CN
    type: select
    proxies:
      - cn
`, "", false, false)
	if err != nil {
		t.Fatalf("query source groups: %v", err)
	}
	if len(groups) != 1 {
		t.Fatalf("group count = %d, want 1", len(groups))
	}
	group := groups[0]
	if len(group.Proxies) != 2 {
		t.Fatalf("group proxies = %d, want 2 (provider expanded)", len(group.Proxies))
	}
	if group.Proxies[0].Name != "CN-01" || group.Proxies[0].Type != "Trojan" {
		t.Fatalf("proxies[0] = %q/%q, want CN-01/Trojan", group.Proxies[0].Name, group.Proxies[0].Type)
	}
	if group.Proxies[1].Name != "CN-02" || group.Proxies[1].Type != "Hysteria2" {
		t.Fatalf("proxies[1] = %q/%q, want CN-02/Hysteria2", group.Proxies[1].Name, group.Proxies[1].Type)
	}
}

func TestQueryProxyGroupsFromSourceYamlExcludeNotSelectable(t *testing.T) {
	groups, err := QueryProxyGroupsFromSourceYaml(`
proxy-groups:
  - name: AUTO
    type: url-test
    proxies: []
  - name: MANUAL
    type: select
    proxies: []
`, "", true, false)
	if err != nil {
		t.Fatalf("query source groups: %v", err)
	}
	if len(groups) != 1 {
		t.Fatalf("group count = %d, want 1", len(groups))
	}
	if groups[0].Name != "MANUAL" {
		t.Fatalf("group = %q, want MANUAL", groups[0].Name)
	}
}

func TestQueryProxyGroupsFromSourceYamlRemoteProviderCacheExpansion(t *testing.T) {
	profileDir := t.TempDir()
	url := "https://example.com/sub.yaml"
	cacheDir := filepath.Join(profileDir, "providers", "proxies")
	if err := os.MkdirAll(cacheDir, 0o700); err != nil {
		t.Fatalf("mkdir cache dir: %v", err)
	}
	cacheFile := filepath.Join(cacheDir, md5Hex(url))
	if err := os.WriteFile(cacheFile, []byte("proxies:\n  - name: US-01\n    type: ss\n"), 0o600); err != nil {
		t.Fatalf("write provider cache: %v", err)
	}

	groups, err := QueryProxyGroupsFromSourceYaml(
		fmt.Sprintf(`
proxy-providers:
  sub:
    type: http
    url: %s
proxy-groups:
  - name: MANUAL
    type: select
    proxies:
      - sub
`, url),
		profileDir,
		false,
		false,
	)
	if err != nil {
		t.Fatalf("query source groups: %v", err)
	}
	if len(groups) != 1 {
		t.Fatalf("group count = %d, want 1", len(groups))
	}
	proxies := groups[0].Proxies
	if len(proxies) != 1 {
		t.Fatalf("group proxies = %d, want 1 (cached provider expanded)", len(proxies))
	}
	if proxies[0].Name != "US-01" || proxies[0].Type != "Shadowsocks" {
		t.Fatalf("proxies[0] = %q/%q, want US-01/Shadowsocks", proxies[0].Name, proxies[0].Type)
	}
}

func TestQueryProxyGroupsFromSourceYamlShadowQuicAndTrustTunnelTypes(t *testing.T) {
	groups, err := QueryProxyGroupsFromSourceYaml(`
proxies:
  - name: SQ-01
    type: shadowquic
  - name: TT-01
    type: trusttunnel
proxy-groups:
  - name: MANUAL
    type: select
    proxies:
      - SQ-01
      - TT-01
`, "", false, false)
	if err != nil {
		t.Fatalf("query source groups: %v", err)
	}
	if len(groups) != 1 {
		t.Fatalf("group count = %d, want 1", len(groups))
	}
	proxies := groups[0].Proxies
	if len(proxies) != 2 {
		t.Fatalf("group proxies = %d, want 2", len(proxies))
	}
	if proxies[0].Name != "SQ-01" || proxies[0].Type != "ShadowQuic" {
		t.Fatalf("proxies[0] = %q/%q, want SQ-01/ShadowQuic", proxies[0].Name, proxies[0].Type)
	}
	if proxies[1].Name != "TT-01" || proxies[1].Type != "TrustTunnel" {
		t.Fatalf("proxies[1] = %q/%q, want TT-01/TrustTunnel", proxies[1].Name, proxies[1].Type)
	}
}

func TestQueryProxyGroupsFromSourceYamlNewProtocolTypes(t *testing.T) {
	groups, err := QueryProxyGroupsFromSourceYaml(`
proxies:
  - name: OV-01
    type: openvpn
  - name: TS-01
    type: tailscale
  - name: ZT-01
    type: zerotier
  - name: ET-01
    type: easytier
  - name: GR-01
    type: gost-relay
  - name: RM-01
    type: rematch
proxy-groups:
  - name: MANUAL
    type: select
    proxies:
      - OV-01
      - TS-01
      - ZT-01
      - ET-01
      - GR-01
      - RM-01
`, "", false, false)
	if err != nil {
		t.Fatalf("query source groups: %v", err)
	}
	if len(groups) != 1 {
		t.Fatalf("group count = %d, want 1", len(groups))
	}
	proxies := groups[0].Proxies
	if len(proxies) != 6 {
		t.Fatalf("group proxies = %d, want 6", len(proxies))
	}
	want := map[string]string{
		"OV-01": "OpenVPN",
		"TS-01": "Tailscale",
		"ZT-01": "ZeroTier",
		"ET-01": "EasyTier",
		"GR-01": "GostRelay",
		"RM-01": "Rematch",
	}
	for _, proxy := range proxies {
		if proxy.Type != want[proxy.Name] {
			t.Fatalf("proxy %q type = %q, want %q", proxy.Name, proxy.Type, want[proxy.Name])
		}
	}
}
