# MonadBox

[English](README.md) | [简体中文](docs/README_ZH_HANS.md)

MonadBox is a customized Android client based on [mihomo](https://github.com/MetaCubeX/mihomo), maintained as a fork of [YumeBox](https://github.com/YumeLira/YumeBox).

## Overview

- Minimum Android version: `Android 8.0 / API 26`
- Upstream project: [YumeBox](https://github.com/YumeLira/YumeBox)
- Releases: [MonadBox Releases](https://github.com/NomadBoxLab/NomadBox/releases)
- Issues: [MonadBox Issues](https://github.com/NomadBoxLab/NomadBox/issues)
- Privacy notice: [PRIVACY_POLICY.md](/D:/Projects/Playground/MonadBox/PRIVACY_POLICY.md)
- 隐私说明（简体中文）: [docs/PRIVACY_ZH_HANS.md](/D:/Projects/Playground/MonadBox/docs/PRIVACY_ZH_HANS.md)

## Documentation

- Documentation hub: [docs/README.md](/D:/Projects/Playground/MonadBox/docs/README.md)
- Development guide: [docs/DEVELOP.md](/D:/Projects/Playground/MonadBox/docs/DEVELOP.md)
- Contributing guide: [docs/CONTRIBUTING.md](/D:/Projects/Playground/MonadBox/docs/CONTRIBUTING.md)
- Performance guide: [docs/PERFORMANCE.md](/D:/Projects/Playground/MonadBox/docs/PERFORMANCE.md)
- Third-party libraries: [docs/ThirdParty.md](/D:/Projects/Playground/MonadBox/docs/ThirdParty.md)

## Quick Start

1. Copy local templates:
   - [`local.properties.example`](/D:/Projects/Playground/MonadBox/local.properties.example)
   - [`startup-gate.local.properties.example`](/D:/Projects/Playground/MonadBox/startup-gate.local.properties.example)
   - [`signing.properties.example`](/D:/Projects/Playground/MonadBox/signing.properties.example)
2. Build native artifacts.
3. Run Gradle checks.

Common commands:

```bash
./gradlew spotlessApply
./gradlew test
./gradlew assembleDebug
```

For the complete environment and native build sequence, see [docs/DEVELOP.md](/D:/Projects/Playground/MonadBox/docs/DEVELOP.md).

## Fork Focus

This fork currently emphasizes:

- privacy and safer defaults
- runtime stability and startup recovery
- localization and UI polish
- modularized Android project structure

## Notes

- Authoritative build/toolchain values live in [`gradle.properties`](/D:/Projects/Playground/MonadBox/gradle.properties).
- UI capability registration is tracked in [`config/ui-capability-registry.txt`](/D:/Projects/Playground/MonadBox/config/ui-capability-registry.txt).
