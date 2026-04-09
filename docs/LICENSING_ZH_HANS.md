# 许可策略

[简体中文](LICENSING_ZH_HANS.md) | [English](LICENSING.md)

## 1. 仓库源码许可证

MonadBox 自有代码和 fork 衍生代码采用 AGPL-3.0-only 许可。

## 2. 根 LICENSE 的适用范围

根目录 [LICENSE](../LICENSE) 适用于仓库自有代码和 fork 衍生代码，除非某个文件或子目录明确保留了不同的上游声明。

它不重授权以下内容：

- Mihomo 这类同步进来的上游源码树
- Gradle 解析得到的第三方依赖
- 图标、旗帜和其他外部素材
- `build/` 下的生成产物

## 3. 依赖策略

分发规则：

- 分发源码或二进制时，保留上游声明和 attribution。
- 对 LGPL/EPL 等带有附加义务的许可证，在适用场景下按条款履行分发义务。
- 受限组件不得重新进入公开发布路径。

## 4. 发布规则

- GitHub 源码仓库继续按 AGPL-3.0-only 描述 MonadBox 自有和 fork 衍生代码。
- GitHub 公开二进制发布必须通过 `./gradlew checkGithubOssLicensePolicy`。
- `checkGithubOssLicensePolicy` 用于防止受限组件重新进入公开发布路径；一旦命中，release workflow 必须失败，而不是继续发布 APK。

## 5. 执行位置

- 策略文档：[LICENSING_ZH_HANS.md](../docs/LICENSING_ZH_HANS.md)
- Gradle 校验：[build.gradle.kts](../build.gradle.kts)
- Release workflow 校验：[.github/workflows/release-build.yml](../.github/workflows/release-build.yml)

## 6. 第三方依赖清单

### 6.1 仓库基线与同步源码

- 历史 YumeBox fork 基线：AGPL-3.0
- [Mihomo](https://github.com/MetaCubeX/mihomo)：当前配置的 `Meta` / 对应 tag 同步源码为 GPL-3.0

### 6.2 常规开源依赖

- [libsu](https://github.com/topjohnwu/libsu)：Apache-2.0
- [miuix](https://github.com/compose-miuix-ui/miuix)：Apache-2.0
- [mmkv](https://github.com/Tencent/mmkv)：BSD-3-Clause
- [liquid](https://github.com/FletchMcKee/liquid)：Apache-2.0
- [sketch](https://github.com/panpf/sketch)：Apache-2.0
- [Lucide](https://github.com/lucide-icons/lucide)：ISC
- [Circle Flags](https://github.com/HatScripts/circle-flags)：MIT，当前通过远程 SVG 资源引用
- [PanguText](https://github.com/BetterAndroid/PanguText)：Apache-2.0
- [KavaRef](https://github.com/HighCapable/KavaRef)：Apache-2.0
- [ZXing](https://github.com/zxing/zxing)：Apache-2.0

### 6.3 附带分发义务的依赖

- [sora-editor](https://github.com/Rosemoe/sora-editor)：LGPL-2.1，分发时保留上游声明；如分发修改版副本，需履行 LGPL 对应义务
- [android-tree-sitter](https://github.com/itsaky/android-tree-sitter)：LGPL-2.1，分发时保留上游声明；如分发修改版副本，需履行 LGPL 对应义务
- LSP4J 与 `org.eclipse.jdt.annotation`：EPL-2.0，分发时保留 EPL 声明
