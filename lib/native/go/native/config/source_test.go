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

import "testing"

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
	if len(groups[0].Proxies) != 2 {
		t.Fatalf("GLOBAL proxies = %d, want 2", len(groups[0].Proxies))
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
