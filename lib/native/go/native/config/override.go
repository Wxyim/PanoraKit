package config

import (
	"crypto/sha256"
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"strings"

	"gopkg.in/yaml.v3"
)

// CompileRequest matches the Kotlin CompileRequest JSON contract.
type CompileRequest struct {
	ProfileUUID   string   `json:"profileUuid"`
	ProfileDir    string   `json:"profileDir"`
	ProfilePath   string   `json:"profilePath"`
	OverridePaths []string `json:"overridePaths"`
	OutputPath    string   `json:"outputPath"`
}

// CompileResult matches the Kotlin CompileResult JSON contract.
type CompileResult struct {
	Success     bool     `json:"success"`
	Fingerprint string   `json:"fingerprint"`
	FinalYaml   string   `json:"finalYaml"`
	Warnings    []string `json:"warnings"`
	Error       *string  `json:"error"`
}

func errorResult(message string) CompileResult {
	return CompileResult{
		Success:  false,
		Warnings: []string{},
		Error:    &message,
	}
}

func successResult(fingerprint, finalYaml string) CompileResult {
	return CompileResult{
		Success:     true,
		Fingerprint: fingerprint,
		FinalYaml:   finalYaml,
		Warnings:    []string{},
	}
}

// CompileOverride is the main entry point that replaces the Rust override compiler.
// If writeOutput is true, it writes the final YAML to the output path.
func CompileOverride(requestJSON string, writeOutput bool) string {
	var request CompileRequest
	if err := json.Unmarshal([]byte(requestJSON), &request); err != nil {
		return mustMarshal(errorResult(fmt.Sprintf("decode override request: %s", err)))
	}

	sourceYaml, err := os.ReadFile(request.ProfilePath)
	if err != nil {
		return mustMarshal(errorResult(fmt.Sprintf("read profile yaml: %s", err)))
	}

	// No overrides → pass through with fingerprint
	if len(request.OverridePaths) == 0 {
		fingerprint := sha256Hex(request.ProfileUUID, string(sourceYaml))

		if writeOutput {
			if err := writeAtomic(request.OutputPath, sourceYaml); err != nil {
				return mustMarshal(errorResult(fmt.Sprintf("write runtime yaml: %s", err)))
			}
		}

		return mustMarshal(successResult(fingerprint, string(sourceYaml)))
	}

	// Parse YAML into generic map for manipulation
	var root map[string]interface{}
	if err := yaml.Unmarshal(sourceYaml, &root); err != nil {
		return mustMarshal(errorResult(fmt.Sprintf("parse source yaml: %s", err)))
	}
	if root == nil {
		root = make(map[string]interface{})
	}

	// Apply static runtime patches (same as the Rust patch_static_runtime)
	patchStaticRuntime(root, request.ProfileDir)

	// Load and merge override JSON files, then apply
	combinedPatch, hasPatch, err := loadCombinedOverridePatch(request.OverridePaths)
	if err != nil {
		return mustMarshal(errorResult(err.Error()))
	}
	if hasPatch {
		applyOverrideDocument(root, combinedPatch)
	}

	// Marshal back to YAML
	finalYamlBytes, err := yaml.Marshal(root)
	if err != nil {
		return mustMarshal(errorResult(fmt.Sprintf("encode final yaml: %s", err)))
	}
	finalYaml := string(finalYamlBytes)

	fingerprint := sha256Hex(request.ProfileUUID, finalYaml)

	if writeOutput {
		if err := writeAtomic(request.OutputPath, finalYamlBytes); err != nil {
			return mustMarshal(errorResult(fmt.Sprintf("write runtime yaml: %s", err)))
		}
	}

	return mustMarshal(successResult(fingerprint, finalYaml))
}

func patchStaticRuntime(root map[string]interface{}, profileDir string) {
	root["interface-name"] = ""
	root["routing-mark"] = 0

	// Set external-ui if external-controller is present
	ec, _ := root["external-controller"].(string)
	ecTLS, _ := root["external-controller-tls"].(string)
	if strings.TrimSpace(ec) != "" || strings.TrimSpace(ecTLS) != "" {
		root["external-ui"] = "./ui"
	}

	// Patch profile section
	profile := ensureMapField(root, "profile")
	profile["store-selected"] = false
	profile["store-fake-ip"] = true

	// Check append-system-dns from clash-for-android
	appendSystemDNS := false
	if cfa, ok := root["clash-for-android"].(map[string]interface{}); ok {
		if v, ok := cfa["append-system-dns"].(bool); ok {
			appendSystemDNS = v
		}
	}

	// Patch DNS if not enabled
	dnsEnabled := false
	if dns, ok := root["dns"].(map[string]interface{}); ok {
		if v, ok := dns["enable"].(bool); ok {
			dnsEnabled = v
		}
	}
	if !dnsEnabled {
		dns := ensureMapField(root, "dns")
		dns["enable"] = true
		dns["use-hosts"] = true
		dns["default-nameserver"] = toInterfaceSlice(defaultNameServers)
		dns["nameserver"] = toInterfaceSlice(defaultNameServers)
		dns["enhanced-mode"] = "fake-ip"
		dns["fake-ip-range"] = defaultFakeIPRange
		dns["fake-ip-filter"] = toInterfaceSlice(defaultFakeIPFilter)
		cfa := ensureMapField(root, "clash-for-android")
		cfa["append-system-dns"] = true
		appendSystemDNS = true
	}

	// Re-check append-system-dns (may have been set by the block above)
	if cfa, ok := root["clash-for-android"].(map[string]interface{}); ok {
		if v, ok := cfa["append-system-dns"].(bool); ok && v {
			appendSystemDNS = true
		}
	}

	if appendSystemDNS {
		dns := ensureMapField(root, "dns")
		nameservers := ensureSliceField(dns, "nameserver")
		hasSystem := false
		for _, ns := range nameservers {
			if s, ok := ns.(string); ok && s == "system://" {
				hasSystem = true
				break
			}
		}
		if !hasSystem {
			dns["nameserver"] = append(nameservers, "system://")
		}
	}

	// Patch TUN
	tun := ensureMapField(root, "tun")
	tun["enable"] = false
	tun["auto-route"] = false
	tun["auto-detect-interface"] = false

	// Patch listeners — remove tproxy, redir, tun
	overridePatchListeners(root)

	// Patch providers — normalize paths
	overridePatchProviders(root, profileDir)
}

func overridePatchListeners(root map[string]interface{}) {
	listeners, ok := root["listeners"].([]interface{})
	if !ok {
		return
	}
	newListeners := make([]interface{}, 0, len(listeners))
	for _, listener := range listeners {
		m, ok := listener.(map[string]interface{})
		if !ok {
			newListeners = append(newListeners, listener)
			continue
		}
		if kind, ok := m["type"].(string); ok {
			switch kind {
			case "tproxy", "redir", "tun":
				continue
			}
		}
		newListeners = append(newListeners, listener)
	}
	root["listeners"] = newListeners
}

func overridePatchProviders(root map[string]interface{}, profileDir string) {
	for _, field := range []struct {
		key    string
		prefix string
	}{
		{"proxy-providers", "proxies"},
		{"rule-providers", "rules"},
	} {
		providers, ok := root[field.key].(map[string]interface{})
		if !ok {
			continue
		}
		for _, provider := range providers {
			p, ok := provider.(map[string]interface{})
			if !ok {
				continue
			}
			if path, ok := p["path"].(string); ok && strings.TrimSpace(path) != "" {
				p["path"] = normalizeProviderPath(path, profileDir)
				continue
			}
			if url, ok := p["url"].(string); ok && url != "" {
				hash := sha256HexSingle(url)
				p["path"] = fmt.Sprintf("./providers/%s/%s.yaml", field.prefix, hash)
			}
		}
	}
}

func normalizeProviderPath(path, profileDir string) string {
	path = strings.ReplaceAll(path, "\\", "/")
	if filepath.IsAbs(path) {
		rel, err := filepath.Rel(profileDir, path)
		if err == nil {
			rel = strings.ReplaceAll(rel, "\\", "/")
			if !strings.HasPrefix(rel, "./") && !strings.HasPrefix(rel, "../") {
				return "./" + rel
			}
			return rel
		}
	}
	return path
}

func loadCombinedOverridePatch(overridePaths []string) (map[string]interface{}, bool, error) {
	combined := make(map[string]interface{})
	hasPatch := false

	for _, overridePath := range overridePaths {
		content, err := os.ReadFile(overridePath)
		if err != nil {
			return nil, false, fmt.Errorf("read override file %s: %s", overridePath, err)
		}
		if len(strings.TrimSpace(string(content))) == 0 {
			continue
		}
		var patch map[string]interface{}
		if err := json.Unmarshal(content, &patch); err != nil {
			return nil, false, fmt.Errorf("parse override json %s: %s", overridePath, err)
		}
		mergeOverrideDocument(combined, patch)
		hasPatch = true
	}

	return combined, hasPatch, nil
}

// mergeOverrideDocument merges a patch into the target at the top level.
func mergeOverrideDocument(target, patch map[string]interface{}) {
	for key, value := range patch {
		parsed := parseModifierKey(key)
		switch parsed.modifier {
		case modStart, modEnd:
			existing, _ := target[key]
			target[key] = appendPatchArray(existing, value)
		case modMerge:
			existing, _ := target[key]
			target[key] = mergePatchValue(existing, value)
		case modForce:
			target[key] = value
		case modReplace:
			if existingMap, ok := target[key].(map[string]interface{}); ok {
				if patchMap, ok := value.(map[string]interface{}); ok {
					mergeOverrideDocument(existingMap, patchMap)
					continue
				}
			}
			target[key] = value
		}
	}
}

func applyOverrideDocument(target, patch map[string]interface{}) {
	grouped := groupPatchKeys(patch)
	for _, entry := range grouped {
		baseKey := entry.key
		ops := entry.ops

		// Handle force
		if ops.force != nil {
			target[baseKey] = ops.force
			continue
		}

		// Handle replace (deep merge for objects)
		if ops.replace != nil {
			if existingMap, ok := target[baseKey].(map[string]interface{}); ok {
				if replaceMap, ok := ops.replace.(map[string]interface{}); ok {
					applyOverrideDocument(existingMap, replaceMap)
				} else {
					target[baseKey] = ops.replace
				}
			} else {
				target[baseKey] = ops.replace
			}
		}

		// Handle merge
		if ops.merge != nil {
			existing, _ := target[baseKey]
			target[baseKey] = mergePatchValue(existing, ops.merge)
		}

		// Handle start/end list operations
		if ops.start != nil || ops.end != nil {
			var items []interface{}
			if ops.start != nil {
				items = append(items, collectArrayItems(ops.start)...)
			}
			if existing, ok := target[baseKey].([]interface{}); ok && ops.replace == nil {
				items = append(items, existing...)
			}
			if ops.end != nil {
				items = append(items, collectArrayItems(ops.end)...)
			}
			target[baseKey] = items
		}
	}
}

const (
	modReplace = iota
	modStart
	modEnd
	modMerge
	modForce
)

type parsedKey struct {
	base     string
	modifier int
}

type patchOps struct {
	replace interface{}
	start   interface{}
	end     interface{}
	merge   interface{}
	force   interface{}
}

type groupedEntry struct {
	key string
	ops patchOps
}

func parseModifierKey(key string) parsedKey {
	// Literal key: <keyname>
	if strings.HasPrefix(key, "<") && strings.HasSuffix(key, ">") {
		return parsedKey{base: key[1 : len(key)-1], modifier: modReplace}
	}
	for _, entry := range []struct {
		suffix   string
		modifier int
	}{
		{"-start", modStart},
		{"-end", modEnd},
		{"-merge", modMerge},
		{"-force", modForce},
	} {
		if strings.HasSuffix(key, entry.suffix) {
			return parsedKey{
				base:     key[:len(key)-len(entry.suffix)],
				modifier: entry.modifier,
			}
		}
	}
	return parsedKey{base: key, modifier: modReplace}
}

func groupPatchKeys(patch map[string]interface{}) []groupedEntry {
	var grouped []groupedEntry
	index := map[string]int{}

	for key, value := range patch {
		parsed := parseModifierKey(key)
		idx, exists := index[parsed.base]
		if !exists {
			idx = len(grouped)
			grouped = append(grouped, groupedEntry{key: parsed.base})
			index[parsed.base] = idx
		}

		ops := &grouped[idx].ops
		switch parsed.modifier {
		case modReplace:
			ops.replace = value
		case modStart:
			ops.start = value
		case modEnd:
			ops.end = value
		case modMerge:
			ops.merge = value
		case modForce:
			ops.force = value
		}
	}

	return grouped
}

func appendPatchArray(existing, incoming interface{}) interface{} {
	var items []interface{}
	if arr, ok := existing.([]interface{}); ok {
		items = append(items, arr...)
	}
	if arr, ok := incoming.([]interface{}); ok {
		items = append(items, arr...)
	} else if incoming != nil {
		items = append(items, incoming)
	}
	return items
}

func mergePatchValue(target, patch interface{}) interface{} {
	targetMap, targetOk := target.(map[string]interface{})
	patchMap, patchOk := patch.(map[string]interface{})
	if targetOk && patchOk {
		for key, value := range patchMap {
			existing, _ := targetMap[key]
			targetMap[key] = mergePatchValue(existing, value)
		}
		return targetMap
	}
	return patch
}

func collectArrayItems(value interface{}) []interface{} {
	if value == nil {
		return nil
	}
	if arr, ok := value.([]interface{}); ok {
		return arr
	}
	return []interface{}{value}
}

// Helpers

func ensureMapField(root map[string]interface{}, key string) map[string]interface{} {
	if existing, ok := root[key].(map[string]interface{}); ok {
		return existing
	}
	m := make(map[string]interface{})
	root[key] = m
	return m
}

func ensureSliceField(root map[string]interface{}, key string) []interface{} {
	if existing, ok := root[key].([]interface{}); ok {
		return existing
	}
	return []interface{}{}
}

func toInterfaceSlice(ss []string) []interface{} {
	result := make([]interface{}, len(ss))
	for i, s := range ss {
		result[i] = s
	}
	return result
}

func sha256Hex(profileUUID, content string) string {
	h := sha256.New()
	h.Write([]byte(profileUUID))
	h.Write([]byte(content))
	return fmt.Sprintf("%x", h.Sum(nil))
}

func sha256HexSingle(content string) string {
	h := sha256.New()
	h.Write([]byte(content))
	return fmt.Sprintf("%x", h.Sum(nil))
}

func writeAtomic(path string, data []byte) error {
	dir := filepath.Dir(path)
	if err := os.MkdirAll(dir, 0755); err != nil {
		return err
	}
	tmp := path + ".tmp"
	if err := os.WriteFile(tmp, data, 0644); err != nil {
		return err
	}
	return os.Rename(tmp, path)
}

func mustMarshal(v interface{}) string {
	b, err := json.Marshal(v)
	if err != nil {
		return `{"success":false,"fingerprint":"","finalYaml":"","warnings":[],"error":"override result encode failed"}`
	}
	return string(b)
}
