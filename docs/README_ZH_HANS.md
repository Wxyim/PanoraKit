# 项目概览

<div align="center">

**简体中文** | [English](../README.md)

<img src="logo.webp" style="width: 96px;" alt="logo">

## MonadBox

YumeBox 的定制化版本

[![Latest release](https://img.shields.io/github/v/release/NomadBoxLab/NomadBox?label=Release&logo=github)](https://github.com/NomadBoxLab/NomadBox/releases/latest)
[![GitHub License](https://img.shields.io/github/license/NomadBoxLab/NomadBox?logo=gnu)](/LICENSE)
[![Upstream](https://img.shields.io/badge/Upstream-YumeBox-informational)](https://github.com/YumeLira/YumeBox)

**一个基于 [mihomo](https://github.com/MetaCubeX/mihomo) 内核的开源 Android 客户端，从 [YumeBox](https://github.com/YumeLira/YumeBox) 定制而来**

</div>

## 使用

MonadBox（基于 YumeBox 定制）目前仅支持 **Android 8.0（API 26）及以上系统**。

请前往 Release 页面下载对应架构的安装包：[NomadBoxLab/NomadBox Releases](https://github.com/NomadBoxLab/NomadBox/releases)
问题反馈请前往：[Issues](https://github.com/NomadBoxLab/NomadBox/issues)
隐私政策请参考：[PRIVACY_POLICY](../PRIVACY_POLICY.md)
参与贡献请参考：[CONTRIBUTING](CONTRIBUTING.md)
第三方依赖清单请参考：[ThirdParty](ThirdParty.md)
如果这个项目对你有帮助，请点下 Star，这是持续更新的动力

### 关于定制版

MonadBox 是 YumeBox 的定制化版本，主要聚焦于：

- 增强隐私和本地化支持
- 简化品牌与维护策略
- 自定义配置和功能管理

当前项目已迁移到独立仓库 `NomadBoxLab/NomadBox` 维护。

原始 YumeBox 项目请访问：[YumeBox](https://github.com/YumeLira/YumeBox)

## 上游版本时间线

参考页面：[YumeBox 更新日志](https://yumebox.oom-wg.dev/update/history)

- `v0.5.0`（2026-03-29）：引入全新覆写系统与模板、Root Tun 支持、初始化引导与交互优化、配置编辑/预览增强、GeoX 下载、日志页面重构。
- `v0.4.0`（2026-02-27）：全局取色与页面重构、打包体积优化、稳定性修复。
- `v0.3.x`（2025-12 至 2026-02）：持续增强代理控制体验、启动与运行时稳定性、配置管理与 UI 细节。
- `v0.2.0`（2025-11-30）：多语言支持、流量页面、Scheme/剪贴板/扫码导入、访问控制能力增强。
- `v0.1.0`（2025-11-27）：首个公开里程碑版本。

## Fork 变更日志（基于源码反推）

Fork 起点：`68ff390`。

以下内容根据本分支的实际源码改动归纳，不是简单复述 commit 标题。

### 架构与构建

- 迁移为 `modules/*` 多模块目录结构，并在 Gradle 中显式映射模块边界。
- 将构建产物策略统一到根目录 `build` 下，补充 native 产物路径协同。
- 新增本地签名模板与 startup-gate 本地配置模板，完善本地/发布工作流。

### 运行时与启动稳定性

- 启动校验改为 BuildConfig 驱动与可配置化期望值。
- 完善 Tun/RootTun 启动恢复路径，降低“启动中状态残留”导致的异常。
- 强化运行时启动日志的持久化与保留策略。

### 覆写系统与远程资源

- 移除 Rust 覆写处理依赖，转为 Go 原生处理链路。
- 增加远程覆写拉取/解析流程（JSON + 插件规则导入路径）。
- 增加远程覆写元数据、周期更新能力，并接入 Providers 页面手动/自动更新。
- 增加远程资源 HTTP 安全开关（默认安全，仅显式开启后允许非 localhost HTTP）。

### 存储、日志与维护能力

- 新增 `StorageCleanupManager`，支持自动清理策略、阈值/间隔配置与手动触发。
- 日志页新增历史日志/启动日志浏览、导出与删除能力。
- 清理流程增加可读日志归档，日志保留与裁剪策略更完善。

### OEM 适配与设备兼容性

- 新增 OEM 权限页导航器与厂商页面跳转回退策略。
- 增加 OEM 跳转统计日志（尝试/成功/失败）用于诊断。
- 完善主页面与引导页异形屏/刘海屏布局行为。

### 交互、无障碍与本地化

- 扩充多语言词条覆盖，减少设置/关于/导航/编辑场景中的硬编码文案。
- 提升无障碍描述（如旗帜图标语义）。
- 调整引导、关于、设置等流程文案以匹配 MonadBox 定位。

### 文档与样式基线

- 优化应用内关键文案与元数据表达，统一分支叙事语气。
- 更新根 README 与中文文档结构，并清理仓库内遗留的网站文档包。
- 新增 `.editorconfig` 与 `.clang-format` 样式基线，并执行 native/Go/Kotlin 风格对齐。

### 反馈与建议

如果遇到 Bug，请在 Issues 页面提交：
[Issues](https://github.com/NomadBoxLab/NomadBox/issues)

有想法或改进建议也欢迎直接在仓库中提出。

### 参与贡献

如果您希望改进 MonadBox，请参阅 [CONTRIBUTING](CONTRIBUTING.md)。
如果希望改进多语言翻译，请在 `locale/lang` 目录下创建或更新对应的翻译文件。

### 特别

~~作者对这个项目中的代码一无所知。代码处于可用或不可用状态，没有第三种情况。~~

以及该项目中使用的 [第三方](ThirdParty.md) 库。
