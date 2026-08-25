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
	"os"
	"strings"
	"time"

	"cfa/native/app"

	"github.com/metacubex/mihomo/config"
	"github.com/metacubex/mihomo/hub"
	"github.com/metacubex/mihomo/log"
	"gopkg.in/yaml.v3"
)

func LoadCompiled(path string) error {
	startedAt := time.Now()
	configData, err := os.ReadFile(path)
	if err != nil {
		log.Errorln("Load compiled %s: %s", path, err.Error())
		return err
	}
	log.Infoln("[APP] compiled load: read done cost=%dms size=%d", time.Since(startedAt).Milliseconds(), len(configData))

	parseStartedAt := time.Now()
	rawCfg, err := config.UnmarshalRawConfig(configData)
	if err != nil {
		log.Errorln("Load compiled %s: %s", path, err.Error())
		return err
	}
	log.Infoln("[APP] compiled load: raw parse done cost=%dms", time.Since(parseStartedAt).Milliseconds())

	configMu.Lock()
	currentUiConfiguration = RuntimeUiConfiguration{
		ExternalController:    rawCfg.ExternalController,
		ExternalControllerTLS: rawCfg.ExternalControllerTLS,
		Secret:                rawCfg.Secret,
		ConfigSource:          "compiled",
		ConfigPath:            path,
	}
	configMu.Unlock()

	subtitlePattern := rawCfg.ClashForAndroid.UiSubtitlePattern
	configStartedAt := time.Now()
	cfg, err := config.ParseRawConfig(rawCfg)
	if err != nil {
		log.Errorln("Load compiled %s: %s", path, err.Error())
		return err
	}
	log.Infoln("[APP] compiled load: typed config done cost=%dms", time.Since(configStartedAt).Milliseconds())

	applyStartedAt := time.Now()
	hub.ApplyConfig(cfg)
	log.Infoln("[APP] compiled load: apply config done cost=%dms", time.Since(applyStartedAt).Milliseconds())
	app.ApplySubtitlePattern(subtitlePattern)
	log.Infoln("[APP] compiled load: total done cost=%dms", time.Since(startedAt).Milliseconds())
	return nil
}

func QueryProxyGroupsFromCompiledYaml(yamlText string, profileDir string, excludeNotSelectable bool) ([]*ProxyGroup, error) {
	_ = profileDir
	configData := []byte(yamlText)

	rawCfg, err := config.UnmarshalRawConfig(configData)
	if err != nil {
		return nil, err
	}

	groupNames := make([]string, 0, len(rawCfg.ProxyGroup))
	seen := make(map[string]struct{}, len(rawCfg.ProxyGroup))
	for _, mapping := range rawCfg.ProxyGroup {
		name, _ := mapping["name"].(string)
		name = strings.TrimSpace(name)
		if name == "" {
			continue
		}
		if _, ok := seen[name]; ok {
			continue
		}
		seen[name] = struct{}{}
		groupNames = append(groupNames, name)
	}

	subtitlePattern := rawCfg.ClashForAndroid.UiSubtitlePattern
	cfg, err := config.ParseRawConfig(rawCfg)
	if err != nil {
		return nil, err
	}

	app.ApplySubtitlePattern(subtitlePattern)

	return buildProxyGroupsFromParsed(cfg, groupNames, excludeNotSelectable), nil
}

func QueryConfigFromCompiledYaml(yamlText string) (map[string]any, error) {
	var root map[string]any
	if err := yaml.Unmarshal([]byte(yamlText), &root); err != nil {
		return nil, err
	}
	return root, nil
}
