package config

import (
	"os"
	P "path"
	"strings"
	"sync"

	"gopkg.in/yaml.v3"

	"github.com/metacubex/mihomo/config"
	"github.com/metacubex/mihomo/hub"
	"github.com/metacubex/mihomo/log"
)

type RuntimeUiConfiguration struct {
	ExternalController    string `json:"externalController,omitempty"`
	ExternalControllerTLS string `json:"externalControllerTls,omitempty"`
	Secret                string `json:"secret,omitempty"`
	ConfigSource          string `json:"configSource,omitempty"`
	ConfigPath            string `json:"configPath,omitempty"`
}

var (
	configMu               sync.RWMutex
	currentUiConfiguration RuntimeUiConfiguration
)

func logDns(cfg *config.RawConfig) {
	bytes, err := yaml.Marshal(&cfg.DNS)
	if err != nil {
		log.Warnln("Marshal dns: %s", err.Error())

		return
	}

	log.Infoln("dns:")

	for _, line := range strings.Split(string(bytes), "\n") {
		log.Infoln("  %s", line)
	}
}

func UnmarshalAndPatch(profilePath string) (*config.RawConfig, error) {
	configPath := P.Join(profilePath, "config.yaml")

	configData, err := os.ReadFile(configPath)
	if err != nil {
		return nil, err
	}

	rawConfig, err := config.UnmarshalRawConfig(configData)
	if err != nil {
		return nil, err
	}

	if err := process(rawConfig, profilePath); err != nil {
		return nil, err
	}

	return rawConfig, nil
}

func Parse(rawConfig *config.RawConfig) (*config.Config, error) {
	cfg, err := config.ParseRawConfig(rawConfig)
	if err != nil {
		return nil, err
	}

	return cfg, nil
}

func Load(path string) error {
	rawCfg, err := UnmarshalAndPatch(path)
	if err != nil {
		log.Errorln("Load %s: %s", path, err.Error())

		return err
	}

	logDns(rawCfg)
	configMu.Lock()
	currentUiConfiguration = RuntimeUiConfiguration{
		ExternalController:    rawCfg.ExternalController,
		ExternalControllerTLS: rawCfg.ExternalControllerTLS,
		Secret:                rawCfg.Secret,
		ConfigSource:          "profile",
		ConfigPath:            P.Join(path, "config.yaml"),
	}
	configMu.Unlock()

	cfg, err := Parse(rawCfg)
	if err != nil {
		log.Errorln("Load %s: %s", path, err.Error())

		return err
	}

	// like hub.Parse()
	hub.ApplyConfig(cfg)

	return nil
}

func LoadDefault() {
	cfg, err := config.Parse([]byte{})
	if err != nil {
		panic(err.Error())
	}

	configMu.Lock()
	currentUiConfiguration = RuntimeUiConfiguration{}
	configMu.Unlock()
	hub.ApplyConfig(cfg)
}

func QueryUiConfiguration() RuntimeUiConfiguration {
	configMu.RLock()
	defer configMu.RUnlock()
	return currentUiConfiguration
}
