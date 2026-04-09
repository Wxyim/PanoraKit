# Licensing Policy

[English](LICENSING.md) | [简体中文](LICENSING_ZH_HANS.md)

## 1. Repository Source License

Repository-owned and fork-derived MonadBox source files are licensed under AGPL-3.0-only.

## 2. Scope Of The Root LICENSE

The root [LICENSE](../LICENSE) applies to repository-owned and fork-derived source files unless a file or sub-tree carries a different upstream notice.

It does not relicense:

- synced upstream source trees such as Mihomo
- third-party Gradle dependencies
- icons, flags, and other external assets under their original terms
- generated outputs under `build/`

## 3. Dependency Policy

Distribution rules:

- Keep upstream notices and attribution when redistributing source or binaries.
- For copyleft or reciprocal licenses (for example LGPL/EPL), satisfy their additional distribution obligations when applicable.
- Do not add restricted dependencies or components back into the public release path.

## 4. Release Rules

- GitHub source publication stays AGPL-3.0-only for MonadBox-owned and fork-derived code.
- Public GitHub release binaries must pass `./gradlew checkGithubOssLicensePolicy`.
- `checkGithubOssLicensePolicy` prevents restricted components from re-entering the public release path; if it fails, the release workflow must stop instead of publishing binaries.

## 5. Enforcement

- Policy document: [LICENSING.md](../docs/LICENSING.md)
- Gradle guard: [build.gradle.kts](../build.gradle.kts)
- Release workflow guard: [.github/workflows/release-build.yml](../.github/workflows/release-build.yml)

## 6. Third-Party Inventory

### 6.1 Repository Baseline And Synced Source

- Historical YumeBox fork baseline: AGPL-3.0
- [Mihomo](https://github.com/MetaCubeX/mihomo): GPL-3.0 for the currently configured `Meta` / tagged source synced during native builds

### 6.2 Regular Open-Source Dependencies

- [libsu](https://github.com/topjohnwu/libsu): Apache-2.0
- [miuix](https://github.com/compose-miuix-ui/miuix): Apache-2.0
- [mmkv](https://github.com/Tencent/mmkv): BSD-3-Clause
- [liquid](https://github.com/FletchMcKee/liquid): Apache-2.0
- [sketch](https://github.com/panpf/sketch): Apache-2.0
- [Lucide](https://github.com/lucide-icons/lucide): ISC
- [Circle Flags](https://github.com/HatScripts/circle-flags): MIT, currently referenced as remote SVG assets
- [PanguText](https://github.com/BetterAndroid/PanguText): Apache-2.0
- [KavaRef](https://github.com/HighCapable/KavaRef): Apache-2.0
- [ZXing](https://github.com/zxing/zxing): Apache-2.0

### 6.3 Dependencies With Additional Distribution Obligations

- [sora-editor](https://github.com/Rosemoe/sora-editor): LGPL-2.1, retain notices and satisfy LGPL obligations when distributing modified copies
- [android-tree-sitter](https://github.com/itsaky/android-tree-sitter): LGPL-2.1, retain notices and satisfy LGPL obligations when distributing modified copies
- LSP4J and `org.eclipse.jdt.annotation`: EPL-2.0, retain notices when redistributing
