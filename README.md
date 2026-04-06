# Project Overview

<div align="center">

**English** | [简体中文](docs/README_ZH_HANS.md)

<img src="docs/logo.webp" width="96" alt="MonadBox logo">

# MonadBox

A Customized Edition of YumeBox

[![Latest release](https://img.shields.io/github/v/release/NomadBoxLab/NomadBox?label=Release&logo=github)](https://github.com/NomadBoxLab/NomadBox/releases/latest)
[![GitHub License](https://img.shields.io/github/license/NomadBoxLab/NomadBox?logo=gnu)](LICENSE)
[![Upstream](https://img.shields.io/badge/Upstream-YumeBox-informational)](https://github.com/YumeLira/YumeBox)

An open-source Android client based on [mihomo](https://github.com/MetaCubeX/mihomo), customized from [YumeBox](https://github.com/YumeLira/YumeBox).

</div>

## Use

MonadBox currently supports **Android 8.0 (API 26) and above**.

- Releases: [NomadBoxLab/NomadBox Releases](https://github.com/NomadBoxLab/NomadBox/releases)
- Issues: [NomadBoxLab/NomadBox Issues](https://github.com/NomadBoxLab/NomadBox/issues)
- Privacy Policy: [PRIVACY_POLICY](PRIVACY_POLICY.md)
- Contributing: [CONTRIBUTING](docs/CONTRIBUTING.md)
- Third-party libraries: [ThirdParty](docs/ThirdParty.md)

## About This Fork

MonadBox is maintained as a branch-maintained customized edition, with emphasis on:

- Branding and package identity updates
- Improved localization experience (including Chinese)
- Privacy-oriented defaults and documentation cleanup

This project is maintained in its dedicated repository at `NomadBoxLab/NomadBox`.

For upstream development and original roadmap, see [YumeBox](https://github.com/YumeLira/YumeBox).

## Upstream Release Timeline

Reference: [YumeBox Update History](https://yumebox.oom-wg.dev/update/history)

- `v0.5.0` (2026-03-29): new override system and templates, Root Tun support, onboarding/interaction polish, config editor and preview improvements, GeoX download, logging page refactor.
- `v0.4.0` (2026-02-27): global color/theme and page refactors, packaging size optimization, stability fixes.
- `v0.3.x` (2025-12 to 2026-02): incremental UX, provider/proxy controls, startup/runtime stability fixes, feature hardening.
- `v0.2.0` (2025-11-30): multilingual support, traffic page, scheme/clipboard/QR import, access control improvements.
- `v0.1.0` (2025-11-27): first public milestone.

## Fork Change Log (Source-Derived)

Fork base: `68ff390`.

The following changelog is reconstructed from actual source changes in this branch (not only commit titles).

### Architecture & Build

- Migrated to a clear multi-module layout under `modules/*`, with explicit project mapping in Gradle settings.
- Centralized build output behavior at root level and aligned native output handling.
- Added configurable startup-gate local template and signing template files for local/release workflows.

### Runtime & Startup Reliability

- Reworked startup verification flow to use BuildConfig-driven checks and configurable expectations.
- Added stricter startup/runtime recovery paths for Tun and RootTun to reduce stale startup-state issues.
- Improved runtime startup log persistence and retention behavior.

### Override System & Remote Resources

- Replaced Rust override processing dependency with Go-native processing pipeline.
- Added remote override fetching/parsing pipeline (JSON + plugin-style rule import paths).
- Added remote override metadata, periodic refresh support, and provider page integration for manual/auto update.
- Added HTTP remote resource safety switch (default secure behavior, explicit opt-in for non-localhost HTTP).

### Storage, Logs & Maintenance

- Added `StorageCleanupManager` with auto cleanup policy, threshold/interval settings, and manual cleanup trigger.
- Added startup/history log browsing, export, and deletion support in log UI.
- Added readable log snapshot/archive behavior for cleanup operations and improved log retention pruning.

### OEM Adaptation & Device Compatibility

- Added OEM permission settings navigator and fallback strategy for manufacturer-specific settings pages.
- Added OEM jump result statistics logging for diagnostics (attempt/success/failure tracking).
- Added display cutout behavior updates for onboarding/main activities.

### UI/UX, Accessibility & Localization

- Expanded localization coverage and reduced hard-coded strings in settings/about/navigation/editor actions.
- Improved accessibility metadata (for example, flag icon descriptions).
- Updated onboarding/about/settings flows and copy to match MonadBox positioning.

### Docs & Style Baseline

- Refined app-facing copy and metadata expression for a consistent fork narrative.
- Updated root README and Chinese docs structure; cleaned legacy website docs bundle tracked in repository.
- Added project-wide formatting baselines (`.editorconfig`, `.clang-format`) and executed style alignment on native/Go/Kotlin sources.
