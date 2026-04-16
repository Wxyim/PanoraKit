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
	"encoding/json"
	"os"
	"path/filepath"
	"strings"
	"testing"

	"gopkg.in/yaml.v3"
)

func TestCompileOverrideAppliesStaticRuntimePatchesWithoutOverrides(t *testing.T) {
	tmpDir := t.TempDir()
	profileDir := filepath.Join(tmpDir, "profile")
	if err := os.MkdirAll(profileDir, 0755); err != nil {
		t.Fatalf("mkdir profile dir: %v", err)
	}

	profilePath := filepath.Join(profileDir, "config.yaml")
	outputPath := filepath.Join(profileDir, "runtime.yaml")
	source := `mixed-port: 7890
external-controller: 0.0.0.0:9090
clash-for-android:
  append-system-dns: false
tun:
  enable: true
  auto-route: true
  auto-detect-interface: true
listeners:
  - name: redir-in
    type: redir
    port: 7892
  - name: http-in
    type: http
    port: 7893
proxy-providers:
  remote:
    type: http
    url: https://example.com/proxies.yaml
    interval: 3600
  local:
    type: file
    path: ./LinkCube.yaml
rule-providers:
  remote-rules:
    type: http
    url: https://example.com/rules.yaml
    interval: 3600
  local-rules:
    type: file
    path: ./ruleset/Bing.yaml
proxies:
  - name: test
    type: ss
    server: example.com
    port: 443
    cipher: aes-128-gcm
    password: password
proxy-groups:
  - name: PROXY
    type: select
    proxies:
      - test
rules:
  - MATCH,PROXY
`
	if err := os.WriteFile(profilePath, []byte(source), 0644); err != nil {
		t.Fatalf("write profile: %v", err)
	}

	request := CompileRequest{
		ProfileUUID: "local-config2",
		ProfileDir:  profileDir,
		ProfilePath: profilePath,
		OutputPath:  outputPath,
	}
	requestJSON, err := json.Marshal(request)
	if err != nil {
		t.Fatalf("marshal request: %v", err)
	}

	resultJSON := CompileOverride(string(requestJSON), true)
	var result CompileResult
	if err := json.Unmarshal([]byte(resultJSON), &result); err != nil {
		t.Fatalf("decode result %s: %v", resultJSON, err)
	}
	if !result.Success {
		if result.Error == nil {
			t.Fatalf("compile failed without error message")
		}
		t.Fatalf("compile failed: %s", *result.Error)
	}

	runtimeBytes, err := os.ReadFile(outputPath)
	if err != nil {
		t.Fatalf("read runtime yaml: %v", err)
	}
	var root map[string]interface{}
	if err := yaml.Unmarshal(runtimeBytes, &root); err != nil {
		t.Fatalf("parse runtime yaml: %v", err)
	}

	dns := requireMap(t, root, "dns")
	if dns["enable"] != true {
		t.Fatalf("dns.enable = %#v, want true", dns["enable"])
	}
	if dns["enhanced-mode"] != "fake-ip" {
		t.Fatalf("dns.enhanced-mode = %#v, want fake-ip", dns["enhanced-mode"])
	}
	if !containsString(requireSlice(t, dns, "nameserver"), "system://") {
		t.Fatalf("dns.nameserver missing system://: %#v", dns["nameserver"])
	}
	cfa := requireMap(t, root, "clash-for-android")
	if cfa["append-system-dns"] != true {
		t.Fatalf("clash-for-android.append-system-dns = %#v, want true", cfa["append-system-dns"])
	}
	if root["external-ui"] != "./ui" {
		t.Fatalf("external-ui = %#v, want ./ui", root["external-ui"])
	}

	tun := requireMap(t, root, "tun")
	for _, key := range []string{"enable", "auto-route", "auto-detect-interface"} {
		if tun[key] != false {
			t.Fatalf("tun.%s = %#v, want false", key, tun[key])
		}
	}

	listeners := requireSlice(t, root, "listeners")
	if len(listeners) != 1 {
		t.Fatalf("listeners len = %d, want 1: %#v", len(listeners), listeners)
	}
	listener, ok := listeners[0].(map[string]interface{})
	if !ok || listener["type"] != "http" {
		t.Fatalf("remaining listener = %#v, want http listener", listeners[0])
	}

	proxyProviders := requireMap(t, root, "proxy-providers")
	proxyProvider := requireMap(t, proxyProviders, "remote")
	assertProviderPathPrefix(t, proxyProvider, providerPath(profileDir, "proxies/"))
	localProxyProvider := requireMap(t, proxyProviders, "local")
	assertProviderPathEquals(t, localProxyProvider, providerPath(profileDir, "LinkCube.yaml"))

	ruleProviders := requireMap(t, root, "rule-providers")
	ruleProvider := requireMap(t, ruleProviders, "remote-rules")
	assertProviderPathPrefix(t, ruleProvider, providerPath(profileDir, "rules/"))
	localRuleProvider := requireMap(t, ruleProviders, "local-rules")
	assertProviderPathEquals(t, localRuleProvider, providerPath(profileDir, "ruleset/Bing.yaml"))
}

func requireMap(t *testing.T, root map[string]interface{}, key string) map[string]interface{} {
	t.Helper()
	value, ok := root[key].(map[string]interface{})
	if !ok {
		t.Fatalf("%s = %#v, want map", key, root[key])
	}
	return value
}

func requireSlice(t *testing.T, root map[string]interface{}, key string) []interface{} {
	t.Helper()
	value, ok := root[key].([]interface{})
	if !ok {
		t.Fatalf("%s = %#v, want slice", key, root[key])
	}
	return value
}

func containsString(values []interface{}, expected string) bool {
	for _, value := range values {
		if value == expected {
			return true
		}
	}
	return false
}

func assertProviderPathPrefix(t *testing.T, provider map[string]interface{}, prefix string) {
	t.Helper()
	path, ok := provider["path"].(string)
	if !ok {
		t.Fatalf("provider path = %#v, want string", provider["path"])
	}
	if !strings.HasPrefix(path, prefix) {
		t.Fatalf("provider path = %q, want prefix %s", path, prefix)
	}
}

func assertProviderPathEquals(t *testing.T, provider map[string]interface{}, expected string) {
	t.Helper()
	path, ok := provider["path"].(string)
	if !ok {
		t.Fatalf("provider path = %#v, want string", provider["path"])
	}
	if path != expected {
		t.Fatalf("provider path = %q, want %s", path, expected)
	}
}
