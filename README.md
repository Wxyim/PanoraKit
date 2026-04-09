# MonadBox

[English](README.md) | [简体中文](docs/README_ZH_HANS.md)

MonadBox is a customized Android client based on [mihomo](https://github.com/MetaCubeX/mihomo), maintained as a fork of [YumeBox](https://github.com/YumeLira/YumeBox).

## Overview

- Minimum Android version: `Android 8.0 / API 26`
- Upstream project: [YumeBox](https://github.com/YumeLira/YumeBox)
- Releases: [MonadBox Releases](https://github.com/NomadBoxLab/NomadBox/releases)
- Issues: [MonadBox Issues](https://github.com/NomadBoxLab/NomadBox/issues)

### Documentation

- Development guide: [docs/DEVELOP.md](docs/DEVELOP.md)
- Licensing policy: [docs/LICENSING.md](docs/LICENSING.md)
- Privacy notice (EN): [docs/PRIVACY_POLICY.md](docs/PRIVACY_POLICY.md)

## Quick Start

1. Copy local templates:
   - [local.properties.example](local.properties.example)
   - [startup-gate.local.properties.example](startup-gate.local.properties.example)
   - [signing.properties.example](signing.properties.example)
2. Build native artifacts.
3. Run Gradle checks.

Common commands:

```bash
./gradlew spotlessApply
./gradlew test
./gradlew assembleDebug
```

For the complete environment and native build sequence, see [docs/DEVELOP.md](docs/DEVELOP.md).

## Fork Focus

This fork currently emphasizes:

- privacy and safer defaults
- localization and UI polish

## License Status

- Repository-owned and fork-derived MonadBox source files are licensed under AGPL-3.0-only.
- Third-party dependencies, synced upstream source, and remote assets keep their own upstream licenses.
- Public GitHub release binaries must pass the policy defined in [docs/LICENSING.md](docs/LICENSING.md).

## Notes

- Authoritative build/toolchain values live in [gradle.properties](gradle.properties).
- UI capability registration is tracked in [config/ui-capability-registry.txt](config/ui-capability-registry.txt).
