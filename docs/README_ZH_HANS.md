# MonadBox

[简体中文](README_ZH_HANS.md) | [English](../README.md)

MonadBox 是一个基于 [mihomo](https://github.com/MetaCubeX/mihomo) 的定制化 Android 客户端，维护自 [YumeBox](https://github.com/YumeLira/YumeBox) fork。

## 概览

- 最低 Android 版本：`Android 8.0 / API 26`
- 上游项目：[YumeBox](https://github.com/YumeLira/YumeBox)
- 发行页：[MonadBox Releases](https://github.com/NomadBoxLab/NomadBox/releases)
- 问题反馈：[MonadBox Issues](https://github.com/NomadBoxLab/NomadBox/issues)

### 文档

- 开发指南：[DEVELOP_ZH_HANS.md](DEVELOP_ZH_HANS.md)
- 许可策略：[LICENSING_ZH_HANS.md](LICENSING_ZH_HANS.md)
- 隐私说明：[PRIVACY_POLICY_ZH_HANS.md](PRIVACY_POLICY_ZH_HANS.md)

## 快速开始

1. 复制本地配置模板：
   - [local.properties.example](../local.properties.example)
   - [startup-gate.local.properties.example](../startup-gate.local.properties.example)
   - [signing.properties.example](../signing.properties.example)
2. 构建 native 产物。
3. 运行 Gradle 校验。

常用命令：

```bash
./gradlew spotlessApply
./gradlew test
./gradlew assembleDebug
```

完整环境准备与 native 构建顺序请查看 [DEVELOP_ZH_HANS.md](DEVELOP_ZH_HANS.md)。

## Fork 重点

当前 fork 重点包括：

- 隐私与更安全的默认行为
- 本地化与 UI 体验优化

## 项目规范声明

本项目遵循 [2026 现代 Android 应用设计与工程规范](SPEC_ZH_HANS.md)。

核心规则：

- 应用采用分层架构、单向数据流、UI state、state holder 与 Repository 边界。
- UI 采用 adaptive-first、edge-to-edge、information-first 的 Compose 设计系统。
- 产品能力按初级 / 中级 / 高级三层暴露。
- 核心对象需要结构化建模；高影响变更必须具备确认、预览、回滚或恢复路径。
- 自定义组件必须提供 semantics、无障碍支持与稳定测试钩子。
- Android 平台约束，包括前台服务、窗口变化、本地网络访问与系统返回行为，均视为产品约束。

合并门禁：

1. 稳定对象模型
2. 单一 UI 真相源
3. Adaptive 布局验收
4. Edge-to-edge 验收
5. 关键路径测试
6. 高风险操作保护
7. 组件语义
8. 最小性能基线

## 许可状态

- MonadBox 自有代码与 fork 衍生代码采用 AGPL-3.0-only。
- 第三方依赖、同步上游源码和外部资源继续遵循各自上游许可。
- GitHub 公开二进制发布必须通过 [LICENSING_ZH_HANS.md](LICENSING_ZH_HANS.md) 中定义的策略校验。

## 备注

- 构建与工具链权威配置以 [gradle.properties](../gradle.properties) 为准。
- UI 能力注册表见 [config/ui-capability-registry.txt](../config/ui-capability-registry.txt)。
