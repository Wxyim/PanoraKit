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
 * Copyright (c) MonadBox Contributors 2026 - Present
 */
package config

import "testing"

func TestQueryProxyGroupsFromCompiledYamlIncludesGlobalInGlobalMode(t *testing.T) {
	groups, err := QueryProxyGroupsFromCompiledYaml(`
mode: global
proxy-groups:
  - name: AUTO
    type: select
    proxies:
      - DIRECT
`, "", true)
	if err != nil {
		t.Fatalf("query preview groups: %v", err)
	}

	if len(groups) != 2 {
		t.Fatalf("group count = %d, want 2", len(groups))
	}
	if groups[0].Name != "GLOBAL" {
		t.Fatalf("first group = %q, want GLOBAL", groups[0].Name)
	}
	if groups[1].Name != "AUTO" {
		t.Fatalf("second group = %q, want AUTO", groups[1].Name)
	}
}
