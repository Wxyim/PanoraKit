@file:Suppress(
    "PackageDirectoryMismatch",
    "PackageName",
    "ClassName",
    "ObjectPropertyName",
    "PropertyName",
    "FunctionName",
    "NonAsciiCharacters",
    "RemoveRedundantBackticks",
    "REDUNDANT_ELSE_IN_WHEN",
    "UnusedExpression",
    "unused",
)

package dev.oom_wg.purejoy.mlang

import androidx.compose.runtime.Composable
import dev.oom_wg.purejoy.fyl.fytxt.FYTxtConfig
import dev.oom_wg.purejoy.fyl.fytxt.FYTxtGroup
import dev.oom_wg.purejoy.fyl.fytxt.FYTxtTag
import dev.oom_wg.purejoy.fyl.fytxt.compose.observe
import dev.oom_wg.purejoy.fyl.fytxt.strfmt.fmt

object `MLang` {
    init {
        `MLangGroups`
    }

    enum class `MLangGroups` : FYTxtGroup {
        `lang` {
            override val stats = mapOf(`MLangTags`.EN to 1.0, `MLangTags`.ZH to 1.0)
        };

        companion object {
            init {
                FYTxtConfig.init(`lang`, `MLangTags`.entries)
            }
        }
    }

    enum class `MLangTags` : FYTxtTag {
        EN {
            override val pattern = null
        },
        ZH {
            override val pattern = null
        },
    }

    object `About` {
        init {
            `MLangGroups`
        }

        /** 关于 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """About"""
                        `MLangTags`.ZH -> """关于"""
                        else -> null
                    }
                } ?: """关于"""

        /** 关于 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `App` {
            init {
                `MLangGroups`
            }

            /** 一个基于 mihomo 的定制化 Android 客户端 */
            val `Description`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """A customized Android client based on mihomo"""
                            `MLangTags`.ZH -> """一个基于 mihomo 的定制化 Android 客户端"""
                            else -> null
                        }
                    } ?: """一个基于 mihomo 的定制化 Android 客户端"""

            /** 一个基于 mihomo 的定制化 Android 客户端 */
            @Composable
            fun `Description`(vararg args: Any?) = FYTxtConfig.observe { `Description`.fmt(args) }

            /** 加载中... */
            val `VersionLoading`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Loading..."""
                            `MLangTags`.ZH -> """加载中..."""
                            else -> null
                        }
                    } ?: """加载中..."""

            /** 加载中... */
            @Composable
            fun `VersionLoading`(vararg args: Any?) =
                FYTxtConfig.observe { `VersionLoading`.fmt(args) }

            /** 加载失败 */
            val `VersionFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to load"""
                            `MLangTags`.ZH -> """加载失败"""
                            else -> null
                        }
                    } ?: """加载失败"""

            /** 加载失败 */
            @Composable
            fun `VersionFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `VersionFailed`.fmt(args) }

            /** %s · mihomo %s */
            val `VersionWithMihomo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%s · mihomo %s"""
                            `MLangTags`.ZH -> """%s · mihomo %s"""
                            else -> null
                        }
                    } ?: """%s · mihomo %s"""

            /** %s · mihomo %s */
            @Composable
            fun `VersionWithMihomo`(vararg args: Any?) =
                FYTxtConfig.observe { `VersionWithMihomo`.fmt(args) }

            /** MonadBox */
            val `Name`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """MonadBox"""
                            `MLangTags`.ZH -> """MonadBox"""
                            else -> null
                        }
                    } ?: """MonadBox"""

            /** MonadBox */
            @Composable fun `Name`(vararg args: Any?) = FYTxtConfig.observe { `Name`.fmt(args) }
        }

        object `Section` {
            init {
                `MLangGroups`
            }

            /** 项目链接 */
            val `ProjectLinks`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Project Links"""
                            `MLangTags`.ZH -> """项目链接"""
                            else -> null
                        }
                    } ?: """项目链接"""

            /** 项目链接 */
            @Composable
            fun `ProjectLinks`(vararg args: Any?) = FYTxtConfig.observe { `ProjectLinks`.fmt(args) }

            /** 鸣谢 */
            val `Credits`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Credits"""
                            `MLangTags`.ZH -> """鸣谢"""
                            else -> null
                        }
                    } ?: """鸣谢"""

            /** 鸣谢 */
            @Composable
            fun `Credits`(vararg args: Any?) = FYTxtConfig.observe { `Credits`.fmt(args) }

            /** 更多 */
            val `More`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """More"""
                            `MLangTags`.ZH -> """更多"""
                            else -> null
                        }
                    } ?: """更多"""

            /** 更多 */
            @Composable fun `More`(vararg args: Any?) = FYTxtConfig.observe { `More`.fmt(args) }

            /** 许可证 */
            val `License`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """License"""
                            `MLangTags`.ZH -> """许可证"""
                            else -> null
                        }
                    } ?: """许可证"""

            /** 许可证 */
            @Composable
            fun `License`(vararg args: Any?) = FYTxtConfig.observe { `License`.fmt(args) }
        }

        object `Link` {
            init {
                `MLangGroups`
            }

            /** MonadBox（GitHub） */
            val `Repository`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """MonadBox (GitHub)"""
                            `MLangTags`.ZH -> """MonadBox（GitHub）"""
                            else -> null
                        }
                    } ?: """MonadBox（GitHub）"""

            /** MonadBox（GitHub） */
            @Composable
            fun `Repository`(vararg args: Any?) = FYTxtConfig.observe { `Repository`.fmt(args) }

            /** MonadBox Releases（更新源） */
            val `Releases`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """MonadBox Releases (Update Source)"""
                            `MLangTags`.ZH -> """MonadBox Releases（更新源）"""
                            else -> null
                        }
                    } ?: """MonadBox Releases（更新源）"""

            /** MonadBox Releases（更新源） */
            @Composable
            fun `Releases`(vararg args: Any?) = FYTxtConfig.observe { `Releases`.fmt(args) }

            /** mihomo */
            val `Mihomo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """mihomo"""
                            `MLangTags`.ZH -> """mihomo"""
                            else -> null
                        }
                    } ?: """mihomo"""

            /** mihomo */
            @Composable fun `Mihomo`(vararg args: Any?) = FYTxtConfig.observe { `Mihomo`.fmt(args) }

            /** YumeBox（上游） */
            val `Upstream`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """YumeBox (Upstream)"""
                            `MLangTags`.ZH -> """YumeBox（上游）"""
                            else -> null
                        }
                    } ?: """YumeBox（上游）"""

            /** YumeBox（上游） */
            @Composable
            fun `Upstream`(vararg args: Any?) = FYTxtConfig.observe { `Upstream`.fmt(args) }

            /** Telegram 群组 */
            val `TelegramGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Telegram Group"""
                            `MLangTags`.ZH -> """Telegram 群组"""
                            else -> null
                        }
                    } ?: """Telegram 群组"""

            /** Telegram 群组 */
            @Composable
            fun `TelegramGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `TelegramGroup`.fmt(args) }

            /** Telegram 频道 */
            val `TelegramChannel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Telegram Channel"""
                            `MLangTags`.ZH -> """Telegram 频道"""
                            else -> null
                        }
                    } ?: """Telegram 频道"""

            /** Telegram 频道 */
            @Composable
            fun `TelegramChannel`(vararg args: Any?) =
                FYTxtConfig.observe { `TelegramChannel`.fmt(args) }
        }

        object `License` {
            init {
                `MLangGroups`
            }

            /** 检查更新 */
            val `CheckUpdate`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Check for Updates"""
                            `MLangTags`.ZH -> """检查更新"""
                            else -> null
                        }
                    } ?: """检查更新"""

            /** 检查更新 */
            @Composable
            fun `CheckUpdate`(vararg args: Any?) = FYTxtConfig.observe { `CheckUpdate`.fmt(args) }

            /** 打开 GitHub Releases 页面查看新版本 */
            val `CheckUpdateSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Open the GitHub releases page"""
                            `MLangTags`.ZH -> """打开 GitHub Releases 页面查看新版本"""
                            else -> null
                        }
                    } ?: """打开 GitHub Releases 页面查看新版本"""

            /** 打开 GitHub Releases 页面查看新版本 */
            @Composable
            fun `CheckUpdateSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `CheckUpdateSummary`.fmt(args) }

            /** 引用库 */
            val `Libraries`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Libraries"""
                            `MLangTags`.ZH -> """引用库"""
                            else -> null
                        }
                    } ?: """引用库"""

            /** 引用库 */
            @Composable
            fun `Libraries`(vararg args: Any?) = FYTxtConfig.observe { `Libraries`.fmt(args) }

            /** 查看本应用使用的第三方库 */
            val `LibrariesSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """View all third-party libraries used in this app"""
                            `MLangTags`.ZH -> """查看本应用使用的第三方库"""
                            else -> null
                        }
                    } ?: """查看本应用使用的第三方库"""

            /** 查看本应用使用的第三方库 */
            @Composable
            fun `LibrariesSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `LibrariesSummary`.fmt(args) }

            /** GNU Affero General Public License v3.0 */
            val `AgplName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GNU Affero General Public License v3.0"""
                            `MLangTags`.ZH -> """GNU Affero General Public License v3.0"""
                            else -> null
                        }
                    } ?: """GNU Affero General Public License v3.0"""

            /** GNU Affero General Public License v3.0 */
            @Composable
            fun `AgplName`(vararg args: Any?) = FYTxtConfig.observe { `AgplName`.fmt(args) }

            /** 本程序是自由软件；您可以在 GNU Affero General Public License 的条款下重新分发和修改它。 */
            val `AgplDescription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License."""
                            `MLangTags`.ZH ->
                                """本程序是自由软件；您可以在 GNU Affero General Public License 的条款下重新分发和修改它。"""
                            else -> null
                        }
                    } ?: """本程序是自由软件；您可以在 GNU Affero General Public License 的条款下重新分发和修改它。"""

            /** 本程序是自由软件；您可以在 GNU Affero General Public License 的条款下重新分发和修改它。 */
            @Composable
            fun `AgplDescription`(vararg args: Any?) =
                FYTxtConfig.observe { `AgplDescription`.fmt(args) }
        }

        /** © 2025 YumeLira，2026 – 至今 MonadBox */
        val `Copyright`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """© 2025 YumeLira, 2026 – Present MonadBox"""
                        `MLangTags`.ZH -> """© 2025 YumeLira，2026 – 至今 MonadBox"""
                        else -> null
                    }
                } ?: """© 2025 YumeLira，2026 – 至今 MonadBox"""

        /** © 2025 YumeLira，2026 – 至今 MonadBox */
        @Composable
        fun `Copyright`(vararg args: Any?) = FYTxtConfig.observe { `Copyright`.fmt(args) }
    }

    object `AccessControl` {
        init {
            `MLangGroups`
        }

        /** 访问控制 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Access Control"""
                        `MLangTags`.ZH -> """访问控制"""
                        else -> null
                    }
                } ?: """访问控制"""

        /** 访问控制 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Search` {
            init {
                `MLangGroups`
            }

            /** 搜索应用... */
            val `Placeholder`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Search apps..."""
                            `MLangTags`.ZH -> """搜索应用..."""
                            else -> null
                        }
                    } ?: """搜索应用..."""

            /** 搜索应用... */
            @Composable
            fun `Placeholder`(vararg args: Any?) = FYTxtConfig.observe { `Placeholder`.fmt(args) }

            /** 没有找到匹配的应用 */
            val `EmptyResults`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No matching apps found"""
                            `MLangTags`.ZH -> """没有找到匹配的应用"""
                            else -> null
                        }
                    } ?: """没有找到匹配的应用"""

            /** 没有找到匹配的应用 */
            @Composable
            fun `EmptyResults`(vararg args: Any?) = FYTxtConfig.observe { `EmptyResults`.fmt(args) }
        }

        object `AppList` {
            init {
                `MLangGroups`
            }

            /** 应用列表 (%d 已选择) */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """App List (%d selected)"""
                            `MLangTags`.ZH -> """应用列表 (%d 已选择)"""
                            else -> null
                        }
                    } ?: """应用列表 (%d 已选择)"""

            /** 应用列表 (%d 已选择) */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 加载中... */
            val `Loading`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Loading..."""
                            `MLangTags`.ZH -> """加载中..."""
                            else -> null
                        }
                    } ?: """加载中..."""

            /** 加载中... */
            @Composable
            fun `Loading`(vararg args: Any?) = FYTxtConfig.observe { `Loading`.fmt(args) }

            /** 在授予设备权限前，暂时无法浏览已安装应用。 */
            val `BrowseUnavailablePermission`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Installed-app browsing is unavailable until the device permission is granted."""
                            `MLangTags`.ZH -> """在授予设备权限前，暂时无法浏览已安装应用。"""
                            else -> null
                        }
                    } ?: """在授予设备权限前，暂时无法浏览已安装应用。"""

            /** 在授予设备权限前，暂时无法浏览已安装应用。 */
            @Composable
            fun `BrowseUnavailablePermission`(vararg args: Any?) =
                FYTxtConfig.observe { `BrowseUnavailablePermission`.fmt(args) }

            /** 当前设备构建不支持浏览已安装应用。您仍然可以通过导入或手动添加包名来管理访问控制。 */
            val `BrowseUnavailableManual`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Installed-app browsing is unavailable on this device build. You can still manage access control by importing or manually adding package names."""
                            `MLangTags`.ZH -> """当前设备构建不支持浏览已安装应用。您仍然可以通过导入或手动添加包名来管理访问控制。"""
                            else -> null
                        }
                    } ?: """当前设备构建不支持浏览已安装应用。您仍然可以通过导入或手动添加包名来管理访问控制。"""

            /** 当前设备构建不支持浏览已安装应用。您仍然可以通过导入或手动添加包名来管理访问控制。 */
            @Composable
            fun `BrowseUnavailableManual`(vararg args: Any?) =
                FYTxtConfig.observe { `BrowseUnavailableManual`.fmt(args) }

            /** 打开权限设置 */
            val `OpenPermissionSettings`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Open permission settings"""
                            `MLangTags`.ZH -> """打开权限设置"""
                            else -> null
                        }
                    } ?: """打开权限设置"""

            /** 打开权限设置 */
            @Composable
            fun `OpenPermissionSettings`(vararg args: Any?) =
                FYTxtConfig.observe { `OpenPermissionSettings`.fmt(args) }

            /** 无法打开权限设置页 */
            val `PermissionSettingsUnavailable`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unable to open the permission settings page"""
                            `MLangTags`.ZH -> """无法打开权限设置页"""
                            else -> null
                        }
                    } ?: """无法打开权限设置页"""

            /** 无法打开权限设置页 */
            @Composable
            fun `PermissionSettingsUnavailable`(vararg args: Any?) =
                FYTxtConfig.observe { `PermissionSettingsUnavailable`.fmt(args) }

            /** 手动添加包名 */
            val `ManualAddTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add package name manually"""
                            `MLangTags`.ZH -> """手动添加包名"""
                            else -> null
                        }
                    } ?: """手动添加包名"""

            /** 手动添加包名 */
            @Composable
            fun `ManualAddTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `ManualAddTitle`.fmt(args) }

            /** com.example.app */
            val `ManualAddPlaceholder`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """com.example.app"""
                            `MLangTags`.ZH -> """com.example.app"""
                            else -> null
                        }
                    } ?: """com.example.app"""

            /** com.example.app */
            @Composable
            fun `ManualAddPlaceholder`(vararg args: Any?) =
                FYTxtConfig.observe { `ManualAddPlaceholder`.fmt(args) }

            /** 添加包名 */
            val `AddPackage`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add package"""
                            `MLangTags`.ZH -> """添加包名"""
                            else -> null
                        }
                    } ?: """添加包名"""

            /** 添加包名 */
            @Composable
            fun `AddPackage`(vararg args: Any?) = FYTxtConfig.observe { `AddPackage`.fmt(args) }

            /** 包名无效 */
            val `InvalidPackage`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Invalid package name"""
                            `MLangTags`.ZH -> """包名无效"""
                            else -> null
                        }
                    } ?: """包名无效"""

            /** 包名无效 */
            @Composable
            fun `InvalidPackage`(vararg args: Any?) =
                FYTxtConfig.observe { `InvalidPackage`.fmt(args) }

            /** 已选包名 (%d) */
            val `SelectedPackagesTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Selected packages (%d)"""
                            `MLangTags`.ZH -> """已选包名 (%d)"""
                            else -> null
                        }
                    } ?: """已选包名 (%d)"""

            /** 已选包名 (%d) */
            @Composable
            fun `SelectedPackagesTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `SelectedPackagesTitle`.fmt(args) }

            /** 当前还没有已选包名 */
            val `NoSelectedPackages`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No package names selected yet"""
                            `MLangTags`.ZH -> """当前还没有已选包名"""
                            else -> null
                        }
                    } ?: """当前还没有已选包名"""

            /** 当前还没有已选包名 */
            @Composable
            fun `NoSelectedPackages`(vararg args: Any?) =
                FYTxtConfig.observe { `NoSelectedPackages`.fmt(args) }
        }

        object `Settings` {
            init {
                `MLangGroups`
            }

            /** 访问控制设置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Access Control Settings"""
                            `MLangTags`.ZH -> """访问控制设置"""
                            else -> null
                        }
                    } ?: """访问控制设置"""

            /** 访问控制设置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 显示系统应用 */
            val `ShowSystemApps`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Show System Apps"""
                            `MLangTags`.ZH -> """显示系统应用"""
                            else -> null
                        }
                    } ?: """显示系统应用"""

            /** 显示系统应用 */
            @Composable
            fun `ShowSystemApps`(vararg args: Any?) =
                FYTxtConfig.observe { `ShowSystemApps`.fmt(args) }

            /** 倒序排列 */
            val `DescendingOrder`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Descending Order"""
                            `MLangTags`.ZH -> """倒序排列"""
                            else -> null
                        }
                    } ?: """倒序排列"""

            /** 倒序排列 */
            @Composable
            fun `DescendingOrder`(vararg args: Any?) =
                FYTxtConfig.observe { `DescendingOrder`.fmt(args) }

            /** 已选应用优先 */
            val `SelectedFirst`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Selected Apps First"""
                            `MLangTags`.ZH -> """已选应用优先"""
                            else -> null
                        }
                    } ?: """已选应用优先"""

            /** 已选应用优先 */
            @Composable
            fun `SelectedFirst`(vararg args: Any?) =
                FYTxtConfig.observe { `SelectedFirst`.fmt(args) }

            /** 排序方式 */
            val `SortMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Sort Mode"""
                            `MLangTags`.ZH -> """排序方式"""
                            else -> null
                        }
                    } ?: """排序方式"""

            /** 排序方式 */
            @Composable
            fun `SortMode`(vararg args: Any?) = FYTxtConfig.observe { `SortMode`.fmt(args) }

            /** 当前：%s */
            val `SortModeCurrent`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Current: %s"""
                            `MLangTags`.ZH -> """当前：%s"""
                            else -> null
                        }
                    } ?: """当前：%s"""

            /** 当前：%s */
            @Composable
            fun `SortModeCurrent`(vararg args: Any?) =
                FYTxtConfig.observe { `SortModeCurrent`.fmt(args) }

            /** 批量操作 */
            val `BatchOperation`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Batch Operation"""
                            `MLangTags`.ZH -> """批量操作"""
                            else -> null
                        }
                    } ?: """批量操作"""

            /** 批量操作 */
            @Composable
            fun `BatchOperation`(vararg args: Any?) =
                FYTxtConfig.observe { `BatchOperation`.fmt(args) }

            /** 对当前筛选结果中的应用批量操作 */
            val `BatchOperationSummaryBrowse`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Apply bulk actions to apps in the current filtered list"""
                            `MLangTags`.ZH -> """对当前筛选结果中的应用批量操作"""
                            else -> null
                        }
                    } ?: """对当前筛选结果中的应用批量操作"""

            /** 对当前筛选结果中的应用批量操作 */
            @Composable
            fun `BatchOperationSummaryBrowse`(vararg args: Any?) =
                FYTxtConfig.observe { `BatchOperationSummaryBrowse`.fmt(args) }

            /** 对当前已选包名列表执行批量操作 */
            val `BatchOperationSummaryManual`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Run batch actions against the selected package list"""
                            `MLangTags`.ZH -> """对当前已选包名列表执行批量操作"""
                            else -> null
                        }
                    } ?: """对当前已选包名列表执行批量操作"""

            /** 对当前已选包名列表执行批量操作 */
            @Composable
            fun `BatchOperationSummaryManual`(vararg args: Any?) =
                FYTxtConfig.observe { `BatchOperationSummaryManual`.fmt(args) }

            /** 全选 */
            val `SelectAll`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Select All"""
                            `MLangTags`.ZH -> """全选"""
                            else -> null
                        }
                    } ?: """全选"""

            /** 全选 */
            @Composable
            fun `SelectAll`(vararg args: Any?) = FYTxtConfig.observe { `SelectAll`.fmt(args) }

            /** 全不选 */
            val `DeselectAll`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Deselect All"""
                            `MLangTags`.ZH -> """全不选"""
                            else -> null
                        }
                    } ?: """全不选"""

            /** 全不选 */
            @Composable
            fun `DeselectAll`(vararg args: Any?) = FYTxtConfig.observe { `DeselectAll`.fmt(args) }

            /** 反选 */
            val `Invert`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Invert"""
                            `MLangTags`.ZH -> """反选"""
                            else -> null
                        }
                    } ?: """反选"""

            /** 反选 */
            @Composable fun `Invert`(vararg args: Any?) = FYTxtConfig.observe { `Invert`.fmt(args) }

            /** 复制已选 */
            val `CopySelected`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Copy Selected"""
                            `MLangTags`.ZH -> """复制已选"""
                            else -> null
                        }
                    } ?: """复制已选"""

            /** 复制已选 */
            @Composable
            fun `CopySelected`(vararg args: Any?) = FYTxtConfig.observe { `CopySelected`.fmt(args) }

            /** 清空已选 */
            val `ClearSelected`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Clear Selected"""
                            `MLangTags`.ZH -> """清空已选"""
                            else -> null
                        }
                    } ?: """清空已选"""

            /** 清空已选 */
            @Composable
            fun `ClearSelected`(vararg args: Any?) =
                FYTxtConfig.observe { `ClearSelected`.fmt(args) }

            /** 已清空 %d 个包名 */
            val `ClearSelectedResult`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cleared %d packages"""
                            `MLangTags`.ZH -> """已清空 %d 个包名"""
                            else -> null
                        }
                    } ?: """已清空 %d 个包名"""

            /** 已清空 %d 个包名 */
            @Composable
            fun `ClearSelectedResult`(vararg args: Any?) =
                FYTxtConfig.observe { `ClearSelectedResult`.fmt(args) }

            /** 导入/导出 */
            val `ImportExport`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import/Export"""
                            `MLangTags`.ZH -> """导入/导出"""
                            else -> null
                        }
                    } ?: """导入/导出"""

            /** 导入/导出 */
            @Composable
            fun `ImportExport`(vararg args: Any?) = FYTxtConfig.observe { `ImportExport`.fmt(args) }

            /** 导入时仅保留当前设备可识别的包名 */
            val `ImportExportSummaryBrowse`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Import keeps only package names recognized on this device"""
                            `MLangTags`.ZH -> """导入时仅保留当前设备可识别的包名"""
                            else -> null
                        }
                    } ?: """导入时仅保留当前设备可识别的包名"""

            /** 导入时仅保留当前设备可识别的包名 */
            @Composable
            fun `ImportExportSummaryBrowse`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportExportSummaryBrowse`.fmt(args) }

            /** 导入时保留格式有效的包名，导出当前已选包名 */
            val `ImportExportSummaryManual`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Import keeps package names with valid format and exports the current selected list"""
                            `MLangTags`.ZH -> """导入时保留格式有效的包名，导出当前已选包名"""
                            else -> null
                        }
                    } ?: """导入时保留格式有效的包名，导出当前已选包名"""

            /** 导入时保留格式有效的包名，导出当前已选包名 */
            @Composable
            fun `ImportExportSummaryManual`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportExportSummaryManual`.fmt(args) }

            /** 从剪贴板导入 */
            val `Import`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import from Clipboard"""
                            `MLangTags`.ZH -> """从剪贴板导入"""
                            else -> null
                        }
                    } ?: """从剪贴板导入"""

            /** 从剪贴板导入 */
            @Composable fun `Import`(vararg args: Any?) = FYTxtConfig.observe { `Import`.fmt(args) }

            /** 导出到剪贴板 */
            val `Export`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Export to Clipboard"""
                            `MLangTags`.ZH -> """导出到剪贴板"""
                            else -> null
                        }
                    } ?: """导出到剪贴板"""

            /** 导出到剪贴板 */
            @Composable fun `Export`(vararg args: Any?) = FYTxtConfig.observe { `Export`.fmt(args) }

            /** 导入成功：%d 个包名 */
            val `ImportSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Imported: %d package names"""
                            `MLangTags`.ZH -> """导入成功：%d 个包名"""
                            else -> null
                        }
                    } ?: """导入成功：%d 个包名"""

            /** 导入成功：%d 个包名 */
            @Composable
            fun `ImportSuccess`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportSuccess`.fmt(args) }

            /** 导入 %d 行，新增 %d 个，忽略 %d 个 */
            val `ImportPartial`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Processed %d lines, added %d, ignored %d"""
                            `MLangTags`.ZH -> """导入 %d 行，新增 %d 个，忽略 %d 个"""
                            else -> null
                        }
                    } ?: """导入 %d 行，新增 %d 个，忽略 %d 个"""

            /** 导入 %d 行，新增 %d 个，忽略 %d 个 */
            @Composable
            fun `ImportPartial`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportPartial`.fmt(args) }

            /** 已复制 %d 个包名到剪贴板 */
            val `ExportSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Copied %d package names to clipboard"""
                            `MLangTags`.ZH -> """已复制 %d 个包名到剪贴板"""
                            else -> null
                        }
                    } ?: """已复制 %d 个包名到剪贴板"""

            /** 已复制 %d 个包名到剪贴板 */
            @Composable
            fun `ExportSuccess`(vararg args: Any?) =
                FYTxtConfig.observe { `ExportSuccess`.fmt(args) }

            /** 导入失败 */
            val `ImportFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import failed"""
                            `MLangTags`.ZH -> """导入失败"""
                            else -> null
                        }
                    } ?: """导入失败"""

            /** 导入失败 */
            @Composable
            fun `ImportFailed`(vararg args: Any?) = FYTxtConfig.observe { `ImportFailed`.fmt(args) }

            /** 地区快捷选择 */
            val `RegionQuickSelect`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Region Quick Select"""
                            `MLangTags`.ZH -> """地区快捷选择"""
                            else -> null
                        }
                    } ?: """地区快捷选择"""

            /** 地区快捷选择 */
            @Composable
            fun `RegionQuickSelect`(vararg args: Any?) =
                FYTxtConfig.observe { `RegionQuickSelect`.fmt(args) }

            /** 对当前筛选结果中的应用按地区快速选择 */
            val `RegionQuickSelectSummaryBrowse`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Quick-select apps in the current filtered list by region"""
                            `MLangTags`.ZH -> """对当前筛选结果中的应用按地区快速选择"""
                            else -> null
                        }
                    } ?: """对当前筛选结果中的应用按地区快速选择"""

            /** 对当前筛选结果中的应用按地区快速选择 */
            @Composable
            fun `RegionQuickSelectSummaryBrowse`(vararg args: Any?) =
                FYTxtConfig.observe { `RegionQuickSelectSummaryBrowse`.fmt(args) }

            /** 仅保留当前已选包名中的地区匹配项 */
            val `RegionQuickSelectSummaryManual`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Keep only region-matched packages from the selected list"""
                            `MLangTags`.ZH -> """仅保留当前已选包名中的地区匹配项"""
                            else -> null
                        }
                    } ?: """仅保留当前已选包名中的地区匹配项"""

            /** 仅保留当前已选包名中的地区匹配项 */
            @Composable
            fun `RegionQuickSelectSummaryManual`(vararg args: Any?) =
                FYTxtConfig.observe { `RegionQuickSelectSummaryManual`.fmt(args) }

            /** 中国应用 */
            val `ChinaApps`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """China Apps"""
                            `MLangTags`.ZH -> """中国应用"""
                            else -> null
                        }
                    } ?: """中国应用"""

            /** 中国应用 */
            @Composable
            fun `ChinaApps`(vararg args: Any?) = FYTxtConfig.observe { `ChinaApps`.fmt(args) }

            /** 非中国应用 */
            val `OverseasApps`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Non-China Apps"""
                            `MLangTags`.ZH -> """非中国应用"""
                            else -> null
                        }
                    } ?: """非中国应用"""

            /** 非中国应用 */
            @Composable
            fun `OverseasApps`(vararg args: Any?) = FYTxtConfig.observe { `OverseasApps`.fmt(args) }

            /** 已按「%s」快捷选择，共 %d 个 */
            val `RegionSelectResult`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Quick selected by "%s", total %d"""
                            `MLangTags`.ZH -> """已按「%s」快捷选择，共 %d 个"""
                            else -> null
                        }
                    } ?: """已按「%s」快捷选择，共 %d 个"""

            /** 已按「%s」快捷选择，共 %d 个 */
            @Composable
            fun `RegionSelectResult`(vararg args: Any?) =
                FYTxtConfig.observe { `RegionSelectResult`.fmt(args) }
        }

        object `SortMode` {
            init {
                `MLangGroups`
            }

            /** 包名 */
            val `PackageName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Package Name"""
                            `MLangTags`.ZH -> """包名"""
                            else -> null
                        }
                    } ?: """包名"""

            /** 包名 */
            @Composable
            fun `PackageName`(vararg args: Any?) = FYTxtConfig.observe { `PackageName`.fmt(args) }

            /** 应用名称 */
            val `Label`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """App Name"""
                            `MLangTags`.ZH -> """应用名称"""
                            else -> null
                        }
                    } ?: """应用名称"""

            /** 应用名称 */
            @Composable fun `Label`(vararg args: Any?) = FYTxtConfig.observe { `Label`.fmt(args) }

            /** 安装时间 */
            val `InstallTime`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Install Time"""
                            `MLangTags`.ZH -> """安装时间"""
                            else -> null
                        }
                    } ?: """安装时间"""

            /** 安装时间 */
            @Composable
            fun `InstallTime`(vararg args: Any?) = FYTxtConfig.observe { `InstallTime`.fmt(args) }

            /** 更新时间 */
            val `UpdateTime`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update Time"""
                            `MLangTags`.ZH -> """更新时间"""
                            else -> null
                        }
                    } ?: """更新时间"""

            /** 更新时间 */
            @Composable
            fun `UpdateTime`(vararg args: Any?) = FYTxtConfig.observe { `UpdateTime`.fmt(args) }
        }

        object `Button` {
            init {
                `MLangGroups`
            }

            /** 取消 */
            val `Cancel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cancel"""
                            `MLangTags`.ZH -> """取消"""
                            else -> null
                        }
                    } ?: """取消"""

            /** 取消 */
            @Composable fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

            /** 确定 */
            val `Confirm`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Confirm"""
                            `MLangTags`.ZH -> """确定"""
                            else -> null
                        }
                    } ?: """确定"""

            /** 确定 */
            @Composable
            fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }
        }
    }

    object `AppSettings` {
        init {
            `MLangGroups`
        }

        /** 应用设置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """App Settings"""
                        `MLangTags`.ZH -> """应用设置"""
                        else -> null
                    }
                } ?: """应用设置"""

        /** 应用设置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Section` {
            init {
                `MLangGroups`
            }

            /** 行为 */
            val `Behavior`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Behavior"""
                            `MLangTags`.ZH -> """行为"""
                            else -> null
                        }
                    } ?: """行为"""

            /** 行为 */
            @Composable
            fun `Behavior`(vararg args: Any?) = FYTxtConfig.observe { `Behavior`.fmt(args) }

            /** 界面 */
            val `Interface`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Interface"""
                            `MLangTags`.ZH -> """界面"""
                            else -> null
                        }
                    } ?: """界面"""

            /** 界面 */
            @Composable
            fun `Interface`(vararg args: Any?) = FYTxtConfig.observe { `Interface`.fmt(args) }

            /** 服务 */
            val `Service`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Service"""
                            `MLangTags`.ZH -> """服务"""
                            else -> null
                        }
                    } ?: """服务"""

            /** 服务 */
            @Composable
            fun `Service`(vararg args: Any?) = FYTxtConfig.observe { `Service`.fmt(args) }

            /** 网络 */
            val `Network`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Network"""
                            `MLangTags`.ZH -> """网络"""
                            else -> null
                        }
                    } ?: """网络"""

            /** 网络 */
            @Composable
            fun `Network`(vararg args: Any?) = FYTxtConfig.observe { `Network`.fmt(args) }

            /** 清理 */
            val `Cleanup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cleanup"""
                            `MLangTags`.ZH -> """清理"""
                            else -> null
                        }
                    } ?: """清理"""

            /** 清理 */
            @Composable
            fun `Cleanup`(vararg args: Any?) = FYTxtConfig.observe { `Cleanup`.fmt(args) }
        }

        object `Behavior` {
            init {
                `MLangGroups`
            }

            /** 自动启动 */
            val `AutoStartTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Start"""
                            `MLangTags`.ZH -> """自动启动"""
                            else -> null
                        }
                    } ?: """自动启动"""

            /** 自动启动 */
            @Composable
            fun `AutoStartTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoStartTitle`.fmt(args) }

            /** 应用启动和开机时自动启动代理服务 */
            val `AutoStartSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Automatically start proxy service on app launch and boot"""
                            `MLangTags`.ZH -> """应用启动和开机时自动启动代理服务"""
                            else -> null
                        }
                    } ?: """应用启动和开机时自动启动代理服务"""

            /** 应用启动和开机时自动启动代理服务 */
            @Composable
            fun `AutoStartSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoStartSummary`.fmt(args) }

            /** 启动更新配置 */
            val `AutoUpdateOnStartTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update on Start"""
                            `MLangTags`.ZH -> """启动更新配置"""
                            else -> null
                        }
                    } ?: """启动更新配置"""

            /** 启动更新配置 */
            @Composable
            fun `AutoUpdateOnStartTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoUpdateOnStartTitle`.fmt(args) }

            /** 启动时自动更新当前订阅配置 */
            val `AutoUpdateOnStartSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Auto-update the active subscription profile on start"""
                            `MLangTags`.ZH -> """启动时自动更新当前订阅配置"""
                            else -> null
                        }
                    } ?: """启动时自动更新当前订阅配置"""

            /** 启动时自动更新当前订阅配置 */
            @Composable
            fun `AutoUpdateOnStartSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoUpdateOnStartSummary`.fmt(args) }

            /** 坚持一个中国原则 */
            val `OneChinaTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """One-China Policy"""
                            `MLangTags`.ZH -> """坚持一个中国原则"""
                            else -> null
                        }
                    } ?: """坚持一个中国原则"""

            /** 坚持一个中国原则 */
            @Composable
            fun `OneChinaTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `OneChinaTitle`.fmt(args) }

            /** 自动将台湾地区旗帜及区域码显示为中国 */
            val `OneChinaSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Automatically display TW region flags and codes as China"""
                            `MLangTags`.ZH -> """自动将台湾地区旗帜及区域码显示为中国"""
                            else -> null
                        }
                    } ?: """自动将台湾地区旗帜及区域码显示为中国"""

            /** 自动将台湾地区旗帜及区域码显示为中国 */
            @Composable
            fun `OneChinaSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `OneChinaSummary`.fmt(args) }
        }

        object `Interface` {
            init {
                `MLangGroups`
            }

            /** 界面语言 */
            val `LanguageTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """App Language"""
                            `MLangTags`.ZH -> """界面语言"""
                            else -> null
                        }
                    } ?: """界面语言"""

            /** 界面语言 */
            @Composable
            fun `LanguageTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `LanguageTitle`.fmt(args) }

            /** 选择应用界面的显示语言 */
            val `LanguageSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Choose the display language for the app UI"""
                            `MLangTags`.ZH -> """选择应用界面的显示语言"""
                            else -> null
                        }
                    } ?: """选择应用界面的显示语言"""

            /** 选择应用界面的显示语言 */
            @Composable
            fun `LanguageSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `LanguageSummary`.fmt(args) }

            /** 跟随系统 */
            val `LanguageSystem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Follow System"""
                            `MLangTags`.ZH -> """跟随系统"""
                            else -> null
                        }
                    } ?: """跟随系统"""

            /** 跟随系统 */
            @Composable
            fun `LanguageSystem`(vararg args: Any?) =
                FYTxtConfig.observe { `LanguageSystem`.fmt(args) }

            /** 简体中文 */
            val `LanguageChinese`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """简体中文"""
                            `MLangTags`.ZH -> """简体中文"""
                            else -> null
                        }
                    } ?: """简体中文"""

            /** 简体中文 */
            @Composable
            fun `LanguageChinese`(vararg args: Any?) =
                FYTxtConfig.observe { `LanguageChinese`.fmt(args) }

            /** English */
            val `LanguageEnglish`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """English"""
                            `MLangTags`.ZH -> """English"""
                            else -> null
                        }
                    } ?: """English"""

            /** English */
            @Composable
            fun `LanguageEnglish`(vararg args: Any?) =
                FYTxtConfig.observe { `LanguageEnglish`.fmt(args) }

            /** 主题模式 */
            val `ThemeModeTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Theme Mode"""
                            `MLangTags`.ZH -> """主题模式"""
                            else -> null
                        }
                    } ?: """主题模式"""

            /** 主题模式 */
            @Composable
            fun `ThemeModeTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `ThemeModeTitle`.fmt(args) }

            /** 选择应用的主题样式 */
            val `ThemeModeSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Select the app's theme style"""
                            `MLangTags`.ZH -> """选择应用的主题样式"""
                            else -> null
                        }
                    } ?: """选择应用的主题样式"""

            /** 选择应用的主题样式 */
            @Composable
            fun `ThemeModeSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `ThemeModeSummary`.fmt(args) }

            /** 跟随系统 */
            val `ThemeModeSystem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Follow System"""
                            `MLangTags`.ZH -> """跟随系统"""
                            else -> null
                        }
                    } ?: """跟随系统"""

            /** 跟随系统 */
            @Composable
            fun `ThemeModeSystem`(vararg args: Any?) =
                FYTxtConfig.observe { `ThemeModeSystem`.fmt(args) }

            /** 浅色 */
            val `ThemeModeLight`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Light"""
                            `MLangTags`.ZH -> """浅色"""
                            else -> null
                        }
                    } ?: """浅色"""

            /** 浅色 */
            @Composable
            fun `ThemeModeLight`(vararg args: Any?) =
                FYTxtConfig.observe { `ThemeModeLight`.fmt(args) }

            /** 深色 */
            val `ThemeModeDark`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Dark"""
                            `MLangTags`.ZH -> """深色"""
                            else -> null
                        }
                    } ?: """深色"""

            /** 深色 */
            @Composable
            fun `ThemeModeDark`(vararg args: Any?) =
                FYTxtConfig.observe { `ThemeModeDark`.fmt(args) }

            /** 主题配色 */
            val `ColorThemeTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Theme Palette"""
                            `MLangTags`.ZH -> """主题配色"""
                            else -> null
                        }
                    } ?: """主题配色"""

            /** 主题配色 */
            @Composable
            fun `ColorThemeTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `ColorThemeTitle`.fmt(args) }

            /** 点击选择主题色，并自动推导黑白主题配色 */
            val `ColorThemeSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Pick a seed color and auto-derive monochrome theme colors"""
                            `MLangTags`.ZH -> """点击选择主题色，并自动推导黑白主题配色"""
                            else -> null
                        }
                    } ?: """点击选择主题色，并自动推导黑白主题配色"""

            /** 点击选择主题色，并自动推导黑白主题配色 */
            @Composable
            fun `ColorThemeSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `ColorThemeSummary`.fmt(args) }

            /** 选择主题色 */
            val `ColorThemePickerTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Pick Theme Color"""
                            `MLangTags`.ZH -> """选择主题色"""
                            else -> null
                        }
                    } ?: """选择主题色"""

            /** 选择主题色 */
            @Composable
            fun `ColorThemePickerTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `ColorThemePickerTitle`.fmt(args) }

            /** 主题色代码（#RRGGBB） */
            val `ColorThemeCodeLabel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Theme Color Code (#RRGGBB)"""
                            `MLangTags`.ZH -> """主题色代码（#RRGGBB）"""
                            else -> null
                        }
                    } ?: """主题色代码（#RRGGBB）"""

            /** 主题色代码（#RRGGBB） */
            @Composable
            fun `ColorThemeCodeLabel`(vararg args: Any?) =
                FYTxtConfig.observe { `ColorThemeCodeLabel`.fmt(args) }

            /** 当前主题色：%s */
            val `ColorThemeCustomSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Current theme color: %s"""
                            `MLangTags`.ZH -> """当前主题色：%s"""
                            else -> null
                        }
                    } ?: """当前主题色：%s"""

            /** 当前主题色：%s */
            @Composable
            fun `ColorThemeCustomSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `ColorThemeCustomSummary`.fmt(args) }

            /** 顶部栏背景模糊 */
            val `TopBarBlurTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Blur Top Bar"""
                            `MLangTags`.ZH -> """顶部栏背景模糊"""
                            else -> null
                        }
                    } ?: """顶部栏背景模糊"""

            /** 顶部栏背景模糊 */
            @Composable
            fun `TopBarBlurTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `TopBarBlurTitle`.fmt(args) }

            /** 滚动内容时，为顶部栏应用动态模糊背景 */
            val `TopBarBlurSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Apply a dynamic blur background to the top bar while scrolling"""
                            `MLangTags`.ZH -> """滚动内容时，为顶部栏应用动态模糊背景"""
                            else -> null
                        }
                    } ?: """滚动内容时，为顶部栏应用动态模糊背景"""

            /** 滚动内容时，为顶部栏应用动态模糊背景 */
            @Composable
            fun `TopBarBlurSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `TopBarBlurSummary`.fmt(args) }

            /** 底栏液体玻璃 */
            val `BottomBarLiquidGlassTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Liquid Glass Bottom Bar"""
                            `MLangTags`.ZH -> """底栏液体玻璃"""
                            else -> null
                        }
                    } ?: """底栏液体玻璃"""

            /** 底栏液体玻璃 */
            @Composable
            fun `BottomBarLiquidGlassTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `BottomBarLiquidGlassTitle`.fmt(args) }

            /** 为底部导航栏启用液体玻璃采样与折射效果 */
            val `BottomBarLiquidGlassSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Enable sampled liquid glass refraction for the bottom navigation bar"""
                            `MLangTags`.ZH -> """为底部导航栏启用液体玻璃采样与折射效果"""
                            else -> null
                        }
                    } ?: """为底部导航栏启用液体玻璃采样与折射效果"""

            /** 为底部导航栏启用液体玻璃采样与折射效果 */
            @Composable
            fun `BottomBarLiquidGlassSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `BottomBarLiquidGlassSummary`.fmt(args) }

            /** 滑动隐藏底栏 */
            val `AutoHideNavbarTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto-hide Navbar"""
                            `MLangTags`.ZH -> """滑动隐藏底栏"""
                            else -> null
                        }
                    } ?: """滑动隐藏底栏"""

            /** 滑动隐藏底栏 */
            @Composable
            fun `AutoHideNavbarTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoHideNavbarTitle`.fmt(args) }

            /** 向下滑动时自动隐藏底栏，向上滑动时显示 */
            val `AutoHideNavbarSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Hide navbar on scroll down and show on scroll up"""
                            `MLangTags`.ZH -> """向下滑动时自动隐藏底栏，向上滑动时显示"""
                            else -> null
                        }
                    } ?: """向下滑动时自动隐藏底栏，向上滑动时显示"""

            /** 向下滑动时自动隐藏底栏，向上滑动时显示 */
            @Composable
            fun `AutoHideNavbarSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoHideNavbarSummary`.fmt(args) }

            /** 页面缩放 */
            val `PageScaleTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Page Scale"""
                            `MLangTags`.ZH -> """页面缩放"""
                            else -> null
                        }
                    } ?: """页面缩放"""

            /** 页面缩放 */
            @Composable
            fun `PageScaleTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `PageScaleTitle`.fmt(args) }

            /** 调整应用界面的整体缩放比例 */
            val `PageScaleSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Adjust the overall UI scaling ratio of the app"""
                            `MLangTags`.ZH -> """调整应用界面的整体缩放比例"""
                            else -> null
                        }
                    } ?: """调整应用界面的整体缩放比例"""

            /** 调整应用界面的整体缩放比例 */
            @Composable
            fun `PageScaleSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `PageScaleSummary`.fmt(args) }

            /** 80% - 120% */
            val `PageScaleRange`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """80% - 120%"""
                            `MLangTags`.ZH -> """80% - 120%"""
                            else -> null
                        }
                    } ?: """80% - 120%"""

            /** 80% - 120% */
            @Composable
            fun `PageScaleRange`(vararg args: Any?) =
                FYTxtConfig.observe { `PageScaleRange`.fmt(args) }

            /** 隐藏应用图标 */
            val `HideIconTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Hide App Icon"""
                            `MLangTags`.ZH -> """隐藏应用图标"""
                            else -> null
                        }
                    } ?: """隐藏应用图标"""

            /** 隐藏应用图标 */
            @Composable
            fun `HideIconTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `HideIconTitle`.fmt(args) }

            /** 隐藏后可通过拨号盘 *#*#0721#*#* 打开 */
            val `HideIconSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """After hiding, you can open via dialer *#*#0721#*#*"""
                            `MLangTags`.ZH -> """隐藏后可通过拨号盘 *#*#0721#*#* 打开"""
                            else -> null
                        }
                    } ?: """隐藏后可通过拨号盘 *#*#0721#*#* 打开"""

            /** 隐藏后可通过拨号盘 *#*#0721#*#* 打开 */
            @Composable
            fun `HideIconSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `HideIconSummary`.fmt(args) }

            /** 隐藏后台卡片 */
            val `HideFromRecentsTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Hide Recents Card"""
                            `MLangTags`.ZH -> """隐藏后台卡片"""
                            else -> null
                        }
                    } ?: """隐藏后台卡片"""

            /** 隐藏后台卡片 */
            @Composable
            fun `HideFromRecentsTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `HideFromRecentsTitle`.fmt(args) }

            /** 启用后不在最近任务列表（后台卡片）中显示应用 */
            val `HideFromRecentsSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Do not show this app in the recent tasks list (recents card)"""
                            `MLangTags`.ZH -> """启用后不在最近任务列表（后台卡片）中显示应用"""
                            else -> null
                        }
                    } ?: """启用后不在最近任务列表（后台卡片）中显示应用"""

            /** 启用后不在最近任务列表（后台卡片）中显示应用 */
            @Composable
            fun `HideFromRecentsSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `HideFromRecentsSummary`.fmt(args) }
        }

        object `ServiceSection` {
            init {
                `MLangGroups`
            }

            /** 显示流量通知 */
            val `TrafficNotificationTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Show Traffic Notification"""
                            `MLangTags`.ZH -> """显示流量通知"""
                            else -> null
                        }
                    } ?: """显示流量通知"""

            /** 显示流量通知 */
            @Composable
            fun `TrafficNotificationTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `TrafficNotificationTitle`.fmt(args) }

            /** 在通知栏中显示流量使用情况 */
            val `TrafficNotificationSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Display traffic usage in notification bar"""
                            `MLangTags`.ZH -> """在通知栏中显示流量使用情况"""
                            else -> null
                        }
                    } ?: """在通知栏中显示流量使用情况"""

            /** 在通知栏中显示流量使用情况 */
            @Composable
            fun `TrafficNotificationSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `TrafficNotificationSummary`.fmt(args) }

            /** 单节点测试 */
            val `SingleNodeTestTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Single Node Test"""
                            `MLangTags`.ZH -> """单节点测试"""
                            else -> null
                        }
                    } ?: """单节点测试"""

            /** 单节点测试 */
            @Composable
            fun `SingleNodeTestTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `SingleNodeTestTitle`.fmt(args) }

            /** 点击节点卡片右侧图标测试单个节点延迟 */
            val `SingleNodeTestSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Tap icon on node card to test individual node delay"""
                            `MLangTags`.ZH -> """点击节点卡片右侧图标测试单个节点延迟"""
                            else -> null
                        }
                    } ?: """点击节点卡片右侧图标测试单个节点延迟"""

            /** 点击节点卡片右侧图标测试单个节点延迟 */
            @Composable
            fun `SingleNodeTestSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `SingleNodeTestSummary`.fmt(args) }

            /** 进入日志页自动录制 */
            val `AutoStartLogRecordingTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto-record on Log page"""
                            `MLangTags`.ZH -> """进入日志页自动录制"""
                            else -> null
                        }
                    } ?: """进入日志页自动录制"""

            /** 进入日志页自动录制 */
            @Composable
            fun `AutoStartLogRecordingTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoStartLogRecordingTitle`.fmt(args) }

            /** 进入日志页时自动开启日志录制 */
            val `AutoStartLogRecordingSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Automatically start log recording when entering the Log page"""
                            `MLangTags`.ZH -> """进入日志页时自动开启日志录制"""
                            else -> null
                        }
                    } ?: """进入日志页时自动开启日志录制"""

            /** 进入日志页时自动开启日志录制 */
            @Composable
            fun `AutoStartLogRecordingSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoStartLogRecordingSummary`.fmt(args) }
        }

        object `Network` {
            init {
                `MLangGroups`
            }

            /** 自定义 User-Agent */
            val `CustomUserAgentTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Custom User-Agent"""
                            `MLangTags`.ZH -> """自定义 User-Agent"""
                            else -> null
                        }
                    } ?: """自定义 User-Agent"""

            /** 自定义 User-Agent */
            @Composable
            fun `CustomUserAgentTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `CustomUserAgentTitle`.fmt(args) }

            /** 未设置，使用默认值 */
            val `CustomUserAgentSummaryDefault`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Not set, using default"""
                            `MLangTags`.ZH -> """未设置，使用默认值"""
                            else -> null
                        }
                    } ?: """未设置，使用默认值"""

            /** 未设置，使用默认值 */
            @Composable
            fun `CustomUserAgentSummaryDefault`(vararg args: Any?) =
                FYTxtConfig.observe { `CustomUserAgentSummaryDefault`.fmt(args) }
        }

        object `Cleanup` {
            init {
                `MLangGroups`
            }

            /** 定时自动清理 */
            val `AutoEnabledTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Timed Auto Cleanup"""
                            `MLangTags`.ZH -> """定时自动清理"""
                            else -> null
                        }
                    } ?: """定时自动清理"""

            /** 定时自动清理 */
            @Composable
            fun `AutoEnabledTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoEnabledTitle`.fmt(args) }

            /** 周期检查存储占用，超过阈值时自动清理 */
            val `AutoEnabledSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Periodically check storage usage and clean when threshold is exceeded"""
                            `MLangTags`.ZH -> """周期检查存储占用，超过阈值时自动清理"""
                            else -> null
                        }
                    } ?: """周期检查存储占用，超过阈值时自动清理"""

            /** 周期检查存储占用，超过阈值时自动清理 */
            @Composable
            fun `AutoEnabledSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoEnabledSummary`.fmt(args) }

            /** 清理策略 */
            val `PolicyTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cleanup Policy"""
                            `MLangTags`.ZH -> """清理策略"""
                            else -> null
                        }
                    } ?: """清理策略"""

            /** 清理策略 */
            @Composable
            fun `PolicyTitle`(vararg args: Any?) = FYTxtConfig.observe { `PolicyTitle`.fmt(args) }

            /** 选择清理强度与缓存保留行为 */
            val `PolicySummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Choose cleanup intensity and cache retention behavior"""
                            `MLangTags`.ZH -> """选择清理强度与缓存保留行为"""
                            else -> null
                        }
                    } ?: """选择清理强度与缓存保留行为"""

            /** 选择清理强度与缓存保留行为 */
            @Composable
            fun `PolicySummary`(vararg args: Any?) =
                FYTxtConfig.observe { `PolicySummary`.fmt(args) }

            /** 激进 */
            val `PolicyAggressive`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Aggressive"""
                            `MLangTags`.ZH -> """激进"""
                            else -> null
                        }
                    } ?: """激进"""

            /** 激进 */
            @Composable
            fun `PolicyAggressive`(vararg args: Any?) =
                FYTxtConfig.observe { `PolicyAggressive`.fmt(args) }

            /** 平衡 */
            val `PolicyBalanced`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Balanced"""
                            `MLangTags`.ZH -> """平衡"""
                            else -> null
                        }
                    } ?: """平衡"""

            /** 平衡 */
            @Composable
            fun `PolicyBalanced`(vararg args: Any?) =
                FYTxtConfig.observe { `PolicyBalanced`.fmt(args) }

            /** 保守 */
            val `PolicyConservative`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Conservative"""
                            `MLangTags`.ZH -> """保守"""
                            else -> null
                        }
                    } ?: """保守"""

            /** 保守 */
            @Composable
            fun `PolicyConservative`(vararg args: Any?) =
                FYTxtConfig.observe { `PolicyConservative`.fmt(args) }

            /** 清理阈值 */
            val `ThresholdTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cleanup Threshold"""
                            `MLangTags`.ZH -> """清理阈值"""
                            else -> null
                        }
                    } ?: """清理阈值"""

            /** 清理阈值 */
            @Composable
            fun `ThresholdTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `ThresholdTitle`.fmt(args) }

            /** 应用存储超过 %s MB 时触发清理 */
            val `ThresholdSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Trigger cleanup when app storage exceeds %s MB"""
                            `MLangTags`.ZH -> """应用存储超过 %s MB 时触发清理"""
                            else -> null
                        }
                    } ?: """应用存储超过 %s MB 时触发清理"""

            /** 应用存储超过 %s MB 时触发清理 */
            @Composable
            fun `ThresholdSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `ThresholdSummary`.fmt(args) }

            /** 可设置范围：64 - 4096 MB */
            val `ThresholdRange`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allowed range: 64 - 4096 MB"""
                            `MLangTags`.ZH -> """可设置范围：64 - 4096 MB"""
                            else -> null
                        }
                    } ?: """可设置范围：64 - 4096 MB"""

            /** 可设置范围：64 - 4096 MB */
            @Composable
            fun `ThresholdRange`(vararg args: Any?) =
                FYTxtConfig.observe { `ThresholdRange`.fmt(args) }

            /** 自动清理间隔 */
            val `IntervalTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Cleanup Interval"""
                            `MLangTags`.ZH -> """自动清理间隔"""
                            else -> null
                        }
                    } ?: """自动清理间隔"""

            /** 自动清理间隔 */
            @Composable
            fun `IntervalTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `IntervalTitle`.fmt(args) }

            /** 每 %s 小时检查一次 */
            val `IntervalSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Check every %s hours"""
                            `MLangTags`.ZH -> """每 %s 小时检查一次"""
                            else -> null
                        }
                    } ?: """每 %s 小时检查一次"""

            /** 每 %s 小时检查一次 */
            @Composable
            fun `IntervalSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `IntervalSummary`.fmt(args) }

            /** 可设置范围：1 - 48 小时 */
            val `IntervalRange`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allowed range: 1 - 48 hours"""
                            `MLangTags`.ZH -> """可设置范围：1 - 48 小时"""
                            else -> null
                        }
                    } ?: """可设置范围：1 - 48 小时"""

            /** 可设置范围：1 - 48 小时 */
            @Composable
            fun `IntervalRange`(vararg args: Any?) =
                FYTxtConfig.observe { `IntervalRange`.fmt(args) }

            /** 小时 */
            val `IntervalUnit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """h"""
                            `MLangTags`.ZH -> """小时"""
                            else -> null
                        }
                    } ?: """小时"""

            /** 小时 */
            @Composable
            fun `IntervalUnit`(vararg args: Any?) = FYTxtConfig.observe { `IntervalUnit`.fmt(args) }

            /** 立即清理 */
            val `CleanupNowTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Clean Up Now"""
                            `MLangTags`.ZH -> """立即清理"""
                            else -> null
                        }
                    } ?: """立即清理"""

            /** 立即清理 */
            @Composable
            fun `CleanupNowTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `CleanupNowTitle`.fmt(args) }

            /** 上次清理：%s */
            val `LastRunSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Last run: %s"""
                            `MLangTags`.ZH -> """上次清理：%s"""
                            else -> null
                        }
                    } ?: """上次清理：%s"""

            /** 上次清理：%s */
            @Composable
            fun `LastRunSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `LastRunSummary`.fmt(args) }

            /** 尚未执行过清理 */
            val `LastRunNever`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Never cleaned yet"""
                            `MLangTags`.ZH -> """尚未执行过清理"""
                            else -> null
                        }
                    } ?: """尚未执行过清理"""

            /** 尚未执行过清理 */
            @Composable
            fun `LastRunNever`(vararg args: Any?) = FYTxtConfig.observe { `LastRunNever`.fmt(args) }

            /** 无归档 */
            val `ArchiveSkipped`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """no-archive"""
                            `MLangTags`.ZH -> """无归档"""
                            else -> null
                        }
                    } ?: """无归档"""

            /** 无归档 */
            @Composable
            fun `ArchiveSkipped`(vararg args: Any?) =
                FYTxtConfig.observe { `ArchiveSkipped`.fmt(args) }

            /** 清理完成，释放 %s MB，归档：%s */
            val `CleanupNowSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cleanup completed, freed %s MB, archive: %s"""
                            `MLangTags`.ZH -> """清理完成，释放 %s MB，归档：%s"""
                            else -> null
                        }
                    } ?: """清理完成，释放 %s MB，归档：%s"""

            /** 清理完成，释放 %s MB，归档：%s */
            @Composable
            fun `CleanupNowSuccess`(vararg args: Any?) =
                FYTxtConfig.observe { `CleanupNowSuccess`.fmt(args) }

            /** 本次未触发清理 */
            val `CleanupNowSkipped`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cleanup skipped"""
                            `MLangTags`.ZH -> """本次未触发清理"""
                            else -> null
                        }
                    } ?: """本次未触发清理"""

            /** 本次未触发清理 */
            @Composable
            fun `CleanupNowSkipped`(vararg args: Any?) =
                FYTxtConfig.observe { `CleanupNowSkipped`.fmt(args) }
        }

        object `WarningDialog` {
            init {
                `MLangGroups`
            }

            /** 警告 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Warning"""
                            `MLangTags`.ZH -> """警告"""
                            else -> null
                        }
                    } ?: """警告"""

            /** 警告 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 请在隐藏之前确认你能够访问本应用的设置界面！ */
            val `HideIconMsg1`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Please make sure you can access this app's settings before hiding!"""
                            `MLangTags`.ZH -> """请在隐藏之前确认你能够访问本应用的设置界面！"""
                            else -> null
                        }
                    } ?: """请在隐藏之前确认你能够访问本应用的设置界面！"""

            /** 请在隐藏之前确认你能够访问本应用的设置界面！ */
            @Composable
            fun `HideIconMsg1`(vararg args: Any?) = FYTxtConfig.observe { `HideIconMsg1`.fmt(args) }

            /** 对于 HyperOS, 请开启 自启动 和 后台弹出界面 权限，以接受拨号界面代码！ */
            val `HideIconMsg2`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """For HyperOS, please enable Auto-start and Background Pop-up permissions to receive dialer codes!"""
                            `MLangTags`.ZH -> """对于 HyperOS, 请开启 自启动 和 后台弹出界面 权限，以接受拨号界面代码！"""
                            else -> null
                        }
                    } ?: """对于 HyperOS, 请开启 自启动 和 后台弹出界面 权限，以接受拨号界面代码！"""

            /** 对于 HyperOS, 请开启 自启动 和 后台弹出界面 权限，以接受拨号界面代码！ */
            @Composable
            fun `HideIconMsg2`(vararg args: Any?) = FYTxtConfig.observe { `HideIconMsg2`.fmt(args) }
        }

        object `EditDialog` {
            init {
                `MLangGroups`
            }

            /** 编辑 User-Agent */
            val `UserAgentTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit User-Agent"""
                            `MLangTags`.ZH -> """编辑 User-Agent"""
                            else -> null
                        }
                    } ?: """编辑 User-Agent"""

            /** 编辑 User-Agent */
            @Composable
            fun `UserAgentTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `UserAgentTitle`.fmt(args) }
        }

        object `Button` {
            init {
                `MLangGroups`
            }

            /** 取消 */
            val `Cancel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cancel"""
                            `MLangTags`.ZH -> """取消"""
                            else -> null
                        }
                    } ?: """取消"""

            /** 取消 */
            @Composable fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

            /** 应用 */
            val `Apply`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Apply"""
                            `MLangTags`.ZH -> """应用"""
                            else -> null
                        }
                    } ?: """应用"""

            /** 应用 */
            @Composable fun `Apply`(vararg args: Any?) = FYTxtConfig.observe { `Apply`.fmt(args) }
        }
    }

    object `Component` {
        init {
            `MLangGroups`
        }

        object `ProfileCard` {
            init {
                `MLangGroups`
            }

            /** 导出 */
            val `Export`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Export"""
                            `MLangTags`.ZH -> """导出"""
                            else -> null
                        }
                    } ?: """导出"""

            /** 导出 */
            @Composable fun `Export`(vararg args: Any?) = FYTxtConfig.observe { `Export`.fmt(args) }

            /** 编辑 */
            val `Edit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit"""
                            `MLangTags`.ZH -> """编辑"""
                            else -> null
                        }
                    } ?: """编辑"""

            /** 编辑 */
            @Composable fun `Edit`(vararg args: Any?) = FYTxtConfig.observe { `Edit`.fmt(args) }

            /** 更新 */
            val `Update`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update"""
                            `MLangTags`.ZH -> """更新"""
                            else -> null
                        }
                    } ?: """更新"""

            /** 更新 */
            @Composable fun `Update`(vararg args: Any?) = FYTxtConfig.observe { `Update`.fmt(args) }

            /** 删除 */
            val `Delete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete"""
                            `MLangTags`.ZH -> """删除"""
                            else -> null
                        }
                    } ?: """删除"""

            /** 删除 */
            @Composable fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }

            /** 远程订阅 */
            val `RemoteSubscription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Remote Subscription"""
                            `MLangTags`.ZH -> """远程订阅"""
                            else -> null
                        }
                    } ?: """远程订阅"""

            /** 远程订阅 */
            @Composable
            fun `RemoteSubscription`(vararg args: Any?) =
                FYTxtConfig.observe { `RemoteSubscription`.fmt(args) }

            /** 本地文件 */
            val `LocalFile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Local File"""
                            `MLangTags`.ZH -> """本地文件"""
                            else -> null
                        }
                    } ?: """本地文件"""

            /** 本地文件 */
            @Composable
            fun `LocalFile`(vararg args: Any?) = FYTxtConfig.observe { `LocalFile`.fmt(args) }

            /** 本地配置文件 */
            val `LocalConfig`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Local Configuration"""
                            `MLangTags`.ZH -> """本地配置文件"""
                            else -> null
                        }
                    } ?: """本地配置文件"""

            /** 本地配置文件 */
            @Composable
            fun `LocalConfig`(vararg args: Any?) = FYTxtConfig.observe { `LocalConfig`.fmt(args) }

            /** 点击更新获取订阅信息 */
            val `ClickToUpdate`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Click update to get subscription info"""
                            `MLangTags`.ZH -> """点击更新获取订阅信息"""
                            else -> null
                        }
                    } ?: """点击更新获取订阅信息"""

            /** 点击更新获取订阅信息 */
            @Composable
            fun `ClickToUpdate`(vararg args: Any?) =
                FYTxtConfig.observe { `ClickToUpdate`.fmt(args) }

            /** 流量：%s / %s (%d%%) */
            val `Traffic`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Traffic: %s / %s (%d%%)"""
                            `MLangTags`.ZH -> """流量：%s / %s (%d%%)"""
                            else -> null
                        }
                    } ?: """流量：%s / %s (%d%%)"""

            /** 流量：%s / %s (%d%%) */
            @Composable
            fun `Traffic`(vararg args: Any?) = FYTxtConfig.observe { `Traffic`.fmt(args) }

            /** 已用：%s */
            val `UsedTraffic`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Used: %s"""
                            `MLangTags`.ZH -> """已用：%s"""
                            else -> null
                        }
                    } ?: """已用：%s"""

            /** 已用：%s */
            @Composable
            fun `UsedTraffic`(vararg args: Any?) = FYTxtConfig.observe { `UsedTraffic`.fmt(args) }

            /** 到期：%s (剩余%d 天) */
            val `ExpireAt`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Expires: %s (%d days left)"""
                            `MLangTags`.ZH -> """到期：%s (剩余%d 天)"""
                            else -> null
                        }
                    } ?: """到期：%s (剩余%d 天)"""

            /** 到期：%s (剩余%d 天) */
            @Composable
            fun `ExpireAt`(vararg args: Any?) = FYTxtConfig.observe { `ExpireAt`.fmt(args) }

            /** 到期：今天 */
            val `ExpireToday`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Expires: Today"""
                            `MLangTags`.ZH -> """到期：今天"""
                            else -> null
                        }
                    } ?: """到期：今天"""

            /** 到期：今天 */
            @Composable
            fun `ExpireToday`(vararg args: Any?) = FYTxtConfig.observe { `ExpireToday`.fmt(args) }

            /** 已过期：%s */
            val `Expired`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Expired: %s"""
                            `MLangTags`.ZH -> """已过期：%s"""
                            else -> null
                        }
                    } ?: """已过期：%s"""

            /** 已过期：%s */
            @Composable
            fun `Expired`(vararg args: Any?) = FYTxtConfig.observe { `Expired`.fmt(args) }

            /** 刚刚 */
            val `JustNow`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Just now"""
                            `MLangTags`.ZH -> """刚刚"""
                            else -> null
                        }
                    } ?: """刚刚"""

            /** 刚刚 */
            @Composable
            fun `JustNow`(vararg args: Any?) = FYTxtConfig.observe { `JustNow`.fmt(args) }

            /** %d 分钟前 */
            val `MinutesAgo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d min ago"""
                            `MLangTags`.ZH -> """%d 分钟前"""
                            else -> null
                        }
                    } ?: """%d 分钟前"""

            /** %d 分钟前 */
            @Composable
            fun `MinutesAgo`(vararg args: Any?) = FYTxtConfig.observe { `MinutesAgo`.fmt(args) }

            /** %d 小时前 */
            val `HoursAgo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d hours ago"""
                            `MLangTags`.ZH -> """%d 小时前"""
                            else -> null
                        }
                    } ?: """%d 小时前"""

            /** %d 小时前 */
            @Composable
            fun `HoursAgo`(vararg args: Any?) = FYTxtConfig.observe { `HoursAgo`.fmt(args) }

            /** %d 天前 */
            val `DaysAgo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d days ago"""
                            `MLangTags`.ZH -> """%d 天前"""
                            else -> null
                        }
                    } ?: """%d 天前"""

            /** %d 天前 */
            @Composable
            fun `DaysAgo`(vararg args: Any?) = FYTxtConfig.observe { `DaysAgo`.fmt(args) }
        }

        object `WebView` {
            init {
                `MLangGroups`
            }

            /** 无效的 URL */
            val `InvalidUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Invalid URL"""
                            `MLangTags`.ZH -> """无效的 URL"""
                            else -> null
                        }
                    } ?: """无效的 URL"""

            /** 无效的 URL */
            @Composable
            fun `InvalidUrl`(vararg args: Any?) = FYTxtConfig.observe { `InvalidUrl`.fmt(args) }
        }

        object `Selector` {
            init {
                `MLangGroups`
            }

            /** 不修改 */
            val `NotModify`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Don't Modify"""
                            `MLangTags`.ZH -> """不修改"""
                            else -> null
                        }
                    } ?: """不修改"""

            /** 不修改 */
            @Composable
            fun `NotModify`(vararg args: Any?) = FYTxtConfig.observe { `NotModify`.fmt(args) }

            /** 使用默认 */
            val `UseDefault`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Use Default"""
                            `MLangTags`.ZH -> """使用默认"""
                            else -> null
                        }
                    } ?: """使用默认"""

            /** 使用默认 */
            @Composable
            fun `UseDefault`(vararg args: Any?) = FYTxtConfig.observe { `UseDefault`.fmt(args) }

            /** 启用 */
            val `Enable`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Enable"""
                            `MLangTags`.ZH -> """启用"""
                            else -> null
                        }
                    } ?: """启用"""

            /** 启用 */
            @Composable fun `Enable`(vararg args: Any?) = FYTxtConfig.observe { `Enable`.fmt(args) }

            /** 禁用 */
            val `Disable`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Disable"""
                            `MLangTags`.ZH -> """禁用"""
                            else -> null
                        }
                    } ?: """禁用"""

            /** 禁用 */
            @Composable
            fun `Disable`(vararg args: Any?) = FYTxtConfig.observe { `Disable`.fmt(args) }

            /** 替换 */
            val `Replace`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Replace"""
                            `MLangTags`.ZH -> """替换"""
                            else -> null
                        }
                    } ?: """替换"""

            /** 替换 */
            @Composable
            fun `Replace`(vararg args: Any?) = FYTxtConfig.observe { `Replace`.fmt(args) }

            /** 前置 */
            val `Prepend`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Prepend"""
                            `MLangTags`.ZH -> """前置"""
                            else -> null
                        }
                    } ?: """前置"""

            /** 前置 */
            @Composable
            fun `Prepend`(vararg args: Any?) = FYTxtConfig.observe { `Prepend`.fmt(args) }

            /** 后置 */
            val `Append`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Append"""
                            `MLangTags`.ZH -> """后置"""
                            else -> null
                        }
                    } ?: """后置"""

            /** 后置 */
            @Composable fun `Append`(vararg args: Any?) = FYTxtConfig.observe { `Append`.fmt(args) }

            /** 合并 */
            val `Merge`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Merge"""
                            `MLangTags`.ZH -> """合并"""
                            else -> null
                        }
                    } ?: """合并"""

            /** 合并 */
            @Composable fun `Merge`(vararg args: Any?) = FYTxtConfig.observe { `Merge`.fmt(args) }
        }

        object `Navigation` {
            init {
                `MLangGroups`
            }

            /** 返回 */
            val `Back`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Back"""
                            `MLangTags`.ZH -> """返回"""
                            else -> null
                        }
                    } ?: """返回"""

            /** 返回 */
            @Composable fun `Back`(vararg args: Any?) = FYTxtConfig.observe { `Back`.fmt(args) }

            /** 刷新 */
            val `Refresh`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Refresh"""
                            `MLangTags`.ZH -> """刷新"""
                            else -> null
                        }
                    } ?: """刷新"""

            /** 刷新 */
            @Composable
            fun `Refresh`(vararg args: Any?) = FYTxtConfig.observe { `Refresh`.fmt(args) }

            /** 搜索 */
            val `Search`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Search"""
                            `MLangTags`.ZH -> """搜索"""
                            else -> null
                        }
                    } ?: """搜索"""

            /** 搜索 */
            @Composable fun `Search`(vararg args: Any?) = FYTxtConfig.observe { `Search`.fmt(args) }

            /** 排序 */
            val `Sort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Sort"""
                            `MLangTags`.ZH -> """排序"""
                            else -> null
                        }
                    } ?: """排序"""

            /** 排序 */
            @Composable fun `Sort`(vararg args: Any?) = FYTxtConfig.observe { `Sort`.fmt(args) }

            /** 设置 */
            val `Settings`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Settings"""
                            `MLangTags`.ZH -> """设置"""
                            else -> null
                        }
                    } ?: """设置"""

            /** 设置 */
            @Composable
            fun `Settings`(vararg args: Any?) = FYTxtConfig.observe { `Settings`.fmt(args) }
        }

        object `Message` {
            init {
                `MLangGroups`
            }

            /** 确定 */
            val `Confirm`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Confirm"""
                            `MLangTags`.ZH -> """确定"""
                            else -> null
                        }
                    } ?: """确定"""

            /** 确定 */
            @Composable
            fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }

            /** 提示 */
            val `Hint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Hint"""
                            `MLangTags`.ZH -> """提示"""
                            else -> null
                        }
                    } ?: """提示"""

            /** 提示 */
            @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }

            /** 错误 */
            val `Error`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Error"""
                            `MLangTags`.ZH -> """错误"""
                            else -> null
                        }
                    } ?: """错误"""

            /** 错误 */
            @Composable fun `Error`(vararg args: Any?) = FYTxtConfig.observe { `Error`.fmt(args) }

            /** 成功 */
            val `Success`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Success"""
                            `MLangTags`.ZH -> """成功"""
                            else -> null
                        }
                    } ?: """成功"""

            /** 成功 */
            @Composable
            fun `Success`(vararg args: Any?) = FYTxtConfig.observe { `Success`.fmt(args) }
        }

        object `Button` {
            init {
                `MLangGroups`
            }

            /** 取消 */
            val `Cancel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cancel"""
                            `MLangTags`.ZH -> """取消"""
                            else -> null
                        }
                    } ?: """取消"""

            /** 取消 */
            @Composable fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

            /** 确定 */
            val `Confirm`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Confirm"""
                            `MLangTags`.ZH -> """确定"""
                            else -> null
                        }
                    } ?: """确定"""

            /** 确定 */
            @Composable
            fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }

            /** 清除 */
            val `Clear`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Clear"""
                            `MLangTags`.ZH -> """清除"""
                            else -> null
                        }
                    } ?: """清除"""

            /** 清除 */
            @Composable fun `Clear`(vararg args: Any?) = FYTxtConfig.observe { `Clear`.fmt(args) }

            /** 编辑 */
            val `Edit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit"""
                            `MLangTags`.ZH -> """编辑"""
                            else -> null
                        }
                    } ?: """编辑"""

            /** 编辑 */
            @Composable fun `Edit`(vararg args: Any?) = FYTxtConfig.observe { `Edit`.fmt(args) }

            /** 开始 */
            val `Start`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Start"""
                            `MLangTags`.ZH -> """开始"""
                            else -> null
                        }
                    } ?: """开始"""

            /** 开始 */
            @Composable fun `Start`(vararg args: Any?) = FYTxtConfig.observe { `Start`.fmt(args) }
        }

        object `Loading` {
            init {
                `MLangGroups`
            }

            /** 启动中... */
            val `Starting`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Starting..."""
                            `MLangTags`.ZH -> """启动中..."""
                            else -> null
                        }
                    } ?: """启动中..."""

            /** 启动中... */
            @Composable
            fun `Starting`(vararg args: Any?) = FYTxtConfig.observe { `Starting`.fmt(args) }
        }

        object `Update` {
            init {
                `MLangGroups`
            }

            object `Title` {
                init {
                    `MLangGroups`
                }

                /** 发现新版本 */
                val `Available`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """New Version Available"""
                                `MLangTags`.ZH -> """发现新版本"""
                                else -> null
                            }
                        } ?: """发现新版本"""

                /** 发现新版本 */
                @Composable
                fun `Available`(vararg args: Any?) = FYTxtConfig.observe { `Available`.fmt(args) }

                /** 强制更新提示 */
                val `ForceCancel`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Mandatory Update"""
                                `MLangTags`.ZH -> """强制更新提示"""
                                else -> null
                            }
                        } ?: """强制更新提示"""

                /** 强制更新提示 */
                @Composable
                fun `ForceCancel`(vararg args: Any?) =
                    FYTxtConfig.observe { `ForceCancel`.fmt(args) }

                /** 安装更新 */
                val `Install`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Install Update"""
                                `MLangTags`.ZH -> """安装更新"""
                                else -> null
                            }
                        } ?: """安装更新"""

                /** 安装更新 */
                @Composable
                fun `Install`(vararg args: Any?) = FYTxtConfig.observe { `Install`.fmt(args) }
            }

            object `Message` {
                init {
                    `MLangGroups`
                }

                /** 检测到可用更新 */
                val `Available`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """A new update is available"""
                                `MLangTags`.ZH -> """检测到可用更新"""
                                else -> null
                            }
                        } ?: """检测到可用更新"""

                /** 检测到可用更新 */
                @Composable
                fun `Available`(vararg args: Any?) = FYTxtConfig.observe { `Available`.fmt(args) }

                /** 更新封面 */
                val `CoverDesc`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Update cover"""
                                `MLangTags`.ZH -> """更新封面"""
                                else -> null
                            }
                        } ?: """更新封面"""

                /** 更新封面 */
                @Composable
                fun `CoverDesc`(vararg args: Any?) = FYTxtConfig.observe { `CoverDesc`.fmt(args) }

                /** 当前版本 */
                val `CurrentVersion`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Current Version"""
                                `MLangTags`.ZH -> """当前版本"""
                                else -> null
                            }
                        } ?: """当前版本"""

                /** 当前版本 */
                @Composable
                fun `CurrentVersion`(vararg args: Any?) =
                    FYTxtConfig.observe { `CurrentVersion`.fmt(args) }

                /** 推送版本 */
                val `RemoteVersion`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Target Version"""
                                `MLangTags`.ZH -> """推送版本"""
                                else -> null
                            }
                        } ?: """推送版本"""

                /** 推送版本 */
                @Composable
                fun `RemoteVersion`(vararg args: Any?) =
                    FYTxtConfig.observe { `RemoteVersion`.fmt(args) }

                /** 正在更新 */
                val `Updating`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Updating"""
                                `MLangTags`.ZH -> """正在更新"""
                                else -> null
                            }
                        } ?: """正在更新"""

                /** 正在更新 */
                @Composable
                fun `Updating`(vararg args: Any?) = FYTxtConfig.observe { `Updating`.fmt(args) }

                /** 正在准备下载... */
                val `Preparing`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Preparing download..."""
                                `MLangTags`.ZH -> """正在准备下载..."""
                                else -> null
                            }
                        } ?: """正在准备下载..."""

                /** 正在准备下载... */
                @Composable
                fun `Preparing`(vararg args: Any?) = FYTxtConfig.observe { `Preparing`.fmt(args) }

                /** 正在下载更新包 %d%% */
                val `DownloadingWithProgress`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Downloading update package %d%%"""
                                `MLangTags`.ZH -> """正在下载更新包 %d%%"""
                                else -> null
                            }
                        } ?: """正在下载更新包 %d%%"""

                /** 正在下载更新包 %d%% */
                @Composable
                fun `DownloadingWithProgress`(vararg args: Any?) =
                    FYTxtConfig.observe { `DownloadingWithProgress`.fmt(args) }

                /** 正在校验安装包... */
                val `Verifying`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Verifying update package..."""
                                `MLangTags`.ZH -> """正在校验安装包..."""
                                else -> null
                            }
                        } ?: """正在校验安装包..."""

                /** 正在校验安装包... */
                @Composable
                fun `Verifying`(vararg args: Any?) = FYTxtConfig.observe { `Verifying`.fmt(args) }

                /** 下载完成，等待安装确认 */
                val `Finished`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Download complete, waiting for install confirmation"""
                                `MLangTags`.ZH -> """下载完成，等待安装确认"""
                                else -> null
                            }
                        } ?: """下载完成，等待安装确认"""

                /** 下载完成，等待安装确认 */
                @Composable
                fun `Finished`(vararg args: Any?) = FYTxtConfig.observe { `Finished`.fmt(args) }

                /** 正在下载更新包... */
                val `Downloading`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Downloading update package..."""
                                `MLangTags`.ZH -> """正在下载更新包..."""
                                else -> null
                            }
                        } ?: """正在下载更新包..."""

                /** 正在下载更新包... */
                @Composable
                fun `Downloading`(vararg args: Any?) =
                    FYTxtConfig.observe { `Downloading`.fmt(args) }

                /** 下载完成，准备安装 */
                val `DownloadReady`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Download complete, ready to install"""
                                `MLangTags`.ZH -> """下载完成，准备安装"""
                                else -> null
                            }
                        } ?: """下载完成，准备安装"""

                /** 下载完成，准备安装 */
                @Composable
                fun `DownloadReady`(vararg args: Any?) =
                    FYTxtConfig.observe { `DownloadReady`.fmt(args) }

                /** 安装包校验失败，请重试 */
                val `VerifyFailed`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Package verification failed, please retry"""
                                `MLangTags`.ZH -> """安装包校验失败，请重试"""
                                else -> null
                            }
                        } ?: """安装包校验失败，请重试"""

                /** 安装包校验失败，请重试 */
                @Composable
                fun `VerifyFailed`(vararg args: Any?) =
                    FYTxtConfig.observe { `VerifyFailed`.fmt(args) }

                /** 下载失败 (%d): %s */
                val `DownloadErrorWithCode`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Download failed (%d): %s"""
                                `MLangTags`.ZH -> """下载失败 (%d): %s"""
                                else -> null
                            }
                        } ?: """下载失败 (%d): %s"""

                /** 下载失败 (%d): %s */
                @Composable
                fun `DownloadErrorWithCode`(vararg args: Any?) =
                    FYTxtConfig.observe { `DownloadErrorWithCode`.fmt(args) }

                /** 下载失败，请稍后重试 */
                val `Error`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Download failed, please try again later"""
                                `MLangTags`.ZH -> """下载失败，请稍后重试"""
                                else -> null
                            }
                        } ?: """下载失败，请稍后重试"""

                /** 下载失败，请稍后重试 */
                @Composable
                fun `Error`(vararg args: Any?) = FYTxtConfig.observe { `Error`.fmt(args) }

                /** 知道了 */
                val `Close`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Got it"""
                                `MLangTags`.ZH -> """知道了"""
                                else -> null
                            }
                        } ?: """知道了"""

                /** 知道了 */
                @Composable
                fun `Close`(vararg args: Any?) = FYTxtConfig.observe { `Close`.fmt(args) }

                /** 等待下载开始... */
                val `Waiting`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Waiting for download..."""
                                `MLangTags`.ZH -> """等待下载开始..."""
                                else -> null
                            }
                        } ?: """等待下载开始..."""

                /** 等待下载开始... */
                @Composable
                fun `Waiting`(vararg args: Any?) = FYTxtConfig.observe { `Waiting`.fmt(args) }
            }
        }

        object `ConfigInput` {
            init {
                `MLangGroups`
            }

            /** 端口号 (留空表示不修改) */
            val `PortLabel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Port (leave empty to not modify)"""
                            `MLangTags`.ZH -> """端口号 (留空表示不修改)"""
                            else -> null
                        }
                    } ?: """端口号 (留空表示不修改)"""

            /** 端口号 (留空表示不修改) */
            @Composable
            fun `PortLabel`(vararg args: Any?) = FYTxtConfig.observe { `PortLabel`.fmt(args) }

            /** %d 项 */
            val `CountItems`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d items"""
                            `MLangTags`.ZH -> """%d 项"""
                            else -> null
                        }
                    } ?: """%d 项"""

            /** %d 项 */
            @Composable
            fun `CountItems`(vararg args: Any?) = FYTxtConfig.observe { `CountItems`.fmt(args) }

            /** 替换整个字典 */
            val `ReplaceHelper`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """After saving, this field is replaced by the complete dictionary edited here. Existing keys not listed here will be removed."""
                            `MLangTags`.ZH -> """保存后会用这里编辑的完整字典替换当前字段；这里未填写的旧键不会保留。"""
                            else -> null
                        }
                    } ?: """保存后会用这里编辑的完整字典替换当前字段；这里未填写的旧键不会保留。"""

            /** 保存后会用这里编辑的完整字典替换当前字段；这里未填写的旧键不会保留。 */
            @Composable
            fun `ReplaceHelper`(vararg args: Any?) =
                FYTxtConfig.observe { `ReplaceHelper`.fmt(args) }

            /** 覆盖匹配键对应的值 */
            val `MergeHelper`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """After saving, only the keys edited here are merged into the current dictionary"""
                            `MLangTags`.ZH -> """保存后只会把这里编辑的键合并到当前字典。"""
                            else -> null
                        }
                    } ?: """保存后只会把这里编辑的键合并到当前字典。"""

            /** 保存后只会把这里编辑的键合并到当前字典。 */
            @Composable
            fun `MergeHelper`(vararg args: Any?) = FYTxtConfig.observe { `MergeHelper`.fmt(args) }

            /** 合并模式仅修改指定键，未指定键保持不变。 */
            val `MergeNotice`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """In merge mode, keys with the same name are overwritten, and keys not provided here keep their current values."""
                            `MLangTags`.ZH -> """合并模式下，同名键会被覆盖；这里未填写的键保持当前值。"""
                            else -> null
                        }
                    } ?: """合并模式下，同名键会被覆盖；这里未填写的键保持当前值。"""

            /** 合并模式下，同名键会被覆盖；这里未填写的键保持当前值。 */
            @Composable
            fun `MergeNotice`(vararg args: Any?) = FYTxtConfig.observe { `MergeNotice`.fmt(args) }
        }

        object `Accessibility` {
            init {
                `MLangGroups`
            }

            /** %s 旗帜 */
            val `CountryFlag`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%s flag"""
                            `MLangTags`.ZH -> """%s 旗帜"""
                            else -> null
                        }
                    } ?: """%s 旗帜"""

            /** %s 旗帜 */
            @Composable
            fun `CountryFlag`(vararg args: Any?) = FYTxtConfig.observe { `CountryFlag`.fmt(args) }
        }

        object `BottomBar` {
            init {
                `MLangGroups`
            }

            /** 首页 */
            val `Home`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Home"""
                            `MLangTags`.ZH -> """首页"""
                            else -> null
                        }
                    } ?: """首页"""

            /** 首页 */
            @Composable fun `Home`(vararg args: Any?) = FYTxtConfig.observe { `Home`.fmt(args) }

            /** 代理 */
            val `Proxy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy"""
                            `MLangTags`.ZH -> """代理"""
                            else -> null
                        }
                    } ?: """代理"""

            /** 代理 */
            @Composable fun `Proxy`(vararg args: Any?) = FYTxtConfig.observe { `Proxy`.fmt(args) }

            /** 配置 */
            val `Config`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Config"""
                            `MLangTags`.ZH -> """配置"""
                            else -> null
                        }
                    } ?: """配置"""

            /** 配置 */
            @Composable fun `Config`(vararg args: Any?) = FYTxtConfig.observe { `Config`.fmt(args) }

            /** 设置 */
            val `Setting`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Settings"""
                            `MLangTags`.ZH -> """设置"""
                            else -> null
                        }
                    } ?: """设置"""

            /** 设置 */
            @Composable
            fun `Setting`(vararg args: Any?) = FYTxtConfig.observe { `Setting`.fmt(args) }
        }

        object `Editor` {
            init {
                `MLangGroups`
            }

            /** 共 %d 项 */
            val `CountItems`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Total %d items"""
                            `MLangTags`.ZH -> """共 %d 项"""
                            else -> null
                        }
                    } ?: """共 %d 项"""

            /** 共 %d 项 */
            @Composable
            fun `CountItems`(vararg args: Any?) = FYTxtConfig.observe { `CountItems`.fmt(args) }

            object `Action` {
                init {
                    `MLangGroups`
                }

                /** 重置 */
                val `Reset`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Reset"""
                                `MLangTags`.ZH -> """重置"""
                                else -> null
                            }
                        } ?: """重置"""

                /** 重置 */
                @Composable
                fun `Reset`(vararg args: Any?) = FYTxtConfig.observe { `Reset`.fmt(args) }

                /** 添加 */
                val `Add`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Add"""
                                `MLangTags`.ZH -> """添加"""
                                else -> null
                            }
                        } ?: """添加"""

                /** 添加 */
                @Composable fun `Add`(vararg args: Any?) = FYTxtConfig.observe { `Add`.fmt(args) }

                /** 删除 */
                val `Delete`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Delete"""
                                `MLangTags`.ZH -> """删除"""
                                else -> null
                            }
                        } ?: """删除"""

                /** 删除 */
                @Composable
                fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }

                /** 撤销 */
                val `Undo`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Undo"""
                                `MLangTags`.ZH -> """撤销"""
                                else -> null
                            }
                        } ?: """撤销"""

                /** 撤销 */
                @Composable fun `Undo`(vararg args: Any?) = FYTxtConfig.observe { `Undo`.fmt(args) }

                /** 重做 */
                val `Redo`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Redo"""
                                `MLangTags`.ZH -> """重做"""
                                else -> null
                            }
                        } ?: """重做"""

                /** 重做 */
                @Composable fun `Redo`(vararg args: Any?) = FYTxtConfig.observe { `Redo`.fmt(args) }

                /** 格式化 */
                val `Format`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Format"""
                                `MLangTags`.ZH -> """格式化"""
                                else -> null
                            }
                        } ?: """格式化"""

                /** 格式化 */
                @Composable
                fun `Format`(vararg args: Any?) = FYTxtConfig.observe { `Format`.fmt(args) }

                /** 保存 */
                val `Save`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Save"""
                                `MLangTags`.ZH -> """保存"""
                                else -> null
                            }
                        } ?: """保存"""

                /** 保存 */
                @Composable fun `Save`(vararg args: Any?) = FYTxtConfig.observe { `Save`.fmt(args) }

                /** 保存并退出 */
                val `SaveAndExit`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Save & Exit"""
                                `MLangTags`.ZH -> """保存并退出"""
                                else -> null
                            }
                        } ?: """保存并退出"""

                /** 保存并退出 */
                @Composable
                fun `SaveAndExit`(vararg args: Any?) =
                    FYTxtConfig.observe { `SaveAndExit`.fmt(args) }

                /** 直接保存 */
                val `SaveLocally`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Save Directly"""
                                `MLangTags`.ZH -> """直接保存"""
                                else -> null
                            }
                        } ?: """直接保存"""

                /** 直接保存 */
                @Composable
                fun `SaveLocally`(vararg args: Any?) =
                    FYTxtConfig.observe { `SaveLocally`.fmt(args) }

                /** 直接保存并停止 */
                val `SaveAndStop`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Save Directly & Stop"""
                                `MLangTags`.ZH -> """直接保存并停止"""
                                else -> null
                            }
                        } ?: """直接保存并停止"""

                /** 直接保存并停止 */
                @Composable
                fun `SaveAndStop`(vararg args: Any?) =
                    FYTxtConfig.observe { `SaveAndStop`.fmt(args) }

                /** 继续编辑 */
                val `ContinueEditing`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Continue Editing"""
                                `MLangTags`.ZH -> """继续编辑"""
                                else -> null
                            }
                        } ?: """继续编辑"""

                /** 继续编辑 */
                @Composable
                fun `ContinueEditing`(vararg args: Any?) =
                    FYTxtConfig.observe { `ContinueEditing`.fmt(args) }

                /** 放弃修改 */
                val `Discard`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Discard"""
                                `MLangTags`.ZH -> """放弃修改"""
                                else -> null
                            }
                        } ?: """放弃修改"""

                /** 放弃修改 */
                @Composable
                fun `Discard`(vararg args: Any?) = FYTxtConfig.observe { `Discard`.fmt(args) }

                /** 校验 */
                val `Check`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Check"""
                                `MLangTags`.ZH -> """校验"""
                                else -> null
                            }
                        } ?: """校验"""

                /** 校验 */
                @Composable
                fun `Check`(vararg args: Any?) = FYTxtConfig.observe { `Check`.fmt(args) }
            }

            object `Dialog` {
                init {
                    `MLangGroups`
                }

                /** 添加条目 */
                val `AddTitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Add Entry"""
                                `MLangTags`.ZH -> """添加条目"""
                                else -> null
                            }
                        } ?: """添加条目"""

                /** 添加条目 */
                @Composable
                fun `AddTitle`(vararg args: Any?) = FYTxtConfig.observe { `AddTitle`.fmt(args) }

                /** 编辑条目 */
                val `EditTitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Edit Entry"""
                                `MLangTags`.ZH -> """编辑条目"""
                                else -> null
                            }
                        } ?: """编辑条目"""

                /** 编辑条目 */
                @Composable
                fun `EditTitle`(vararg args: Any?) = FYTxtConfig.observe { `EditTitle`.fmt(args) }

                /** 重置确认 */
                val `ResetTitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Confirm Reset"""
                                `MLangTags`.ZH -> """重置确认"""
                                else -> null
                            }
                        } ?: """重置确认"""

                /** 重置确认 */
                @Composable
                fun `ResetTitle`(vararg args: Any?) = FYTxtConfig.observe { `ResetTitle`.fmt(args) }

                /** 清空所有条目并恢复为不修改状态？ */
                val `ResetMessage`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Clear all entries in this editor and restore this field to the unmodified state?"""
                                `MLangTags`.ZH -> """清空这个编辑器中的全部条目，并将该字段恢复为不修改状态？"""
                                else -> null
                            }
                        } ?: """清空这个编辑器中的全部条目，并将该字段恢复为不修改状态？"""

                /** 清空这个编辑器中的全部条目，并将该字段恢复为不修改状态？ */
                @Composable
                fun `ResetMessage`(vararg args: Any?) =
                    FYTxtConfig.observe { `ResetMessage`.fmt(args) }

                /** 未保存的修改 */
                val `DiscardTitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Unsaved Changes"""
                                `MLangTags`.ZH -> """未保存的修改"""
                                else -> null
                            }
                        } ?: """未保存的修改"""

                /** 未保存的修改 */
                @Composable
                fun `DiscardTitle`(vararg args: Any?) =
                    FYTxtConfig.observe { `DiscardTitle`.fmt(args) }

                /** 当前有未保存的修改，你想怎么处理？ */
                val `DiscardMessage`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Leave without saving? Current changes in the editor will be lost."""
                                `MLangTags`.ZH -> """要不保存直接离开吗？当前编辑器中的修改会丢失。"""
                                else -> null
                            }
                        } ?: """要不保存直接离开吗？当前编辑器中的修改会丢失。"""

                /** 要不保存直接离开吗？当前编辑器中的修改会丢失。 */
                @Composable
                fun `DiscardMessage`(vararg args: Any?) =
                    FYTxtConfig.observe { `DiscardMessage`.fmt(args) }

                /** 使用 JSON 格式编辑 */
                val `JsonSubtitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Edit the raw JSON content directly"""
                                `MLangTags`.ZH -> """直接编辑原始 JSON 内容"""
                                else -> null
                            }
                        } ?: """直接编辑原始 JSON 内容"""

                /** 直接编辑原始 JSON 内容 */
                @Composable
                fun `JsonSubtitle`(vararg args: Any?) =
                    FYTxtConfig.observe { `JsonSubtitle`.fmt(args) }

                /** 配置预览 */
                val `ConfigPreviewTitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Config Preview"""
                                `MLangTags`.ZH -> """配置预览"""
                                else -> null
                            }
                        } ?: """配置预览"""

                /** 配置预览 */
                @Composable
                fun `ConfigPreviewTitle`(vararg args: Any?) =
                    FYTxtConfig.observe { `ConfigPreviewTitle`.fmt(args) }

                /** 正在保存到本地... */
                val `LocalSaving`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Writing the edited result to the local config file..."""
                                `MLangTags`.ZH -> """正在把编辑结果写入本地配置文件..."""
                                else -> null
                            }
                        } ?: """正在把编辑结果写入本地配置文件..."""

                /** 正在把编辑结果写入本地配置文件... */
                @Composable
                fun `LocalSaving`(vararg args: Any?) =
                    FYTxtConfig.observe { `LocalSaving`.fmt(args) }

                /** 正在校验配置... */
                val `ValidatingConfig`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Validating the edited configuration..."""
                                `MLangTags`.ZH -> """正在校验编辑后的配置内容..."""
                                else -> null
                            }
                        } ?: """正在校验编辑后的配置内容..."""

                /** 正在校验编辑后的配置内容... */
                @Composable
                fun `ValidatingConfig`(vararg args: Any?) =
                    FYTxtConfig.observe { `ValidatingConfig`.fmt(args) }

                /** 配置校验通过 */
                val `ValidationPassed`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Configuration check passed"""
                                `MLangTags`.ZH -> """配置校验通过"""
                                else -> null
                            }
                        } ?: """配置校验通过"""

                /** 配置校验通过 */
                @Composable
                fun `ValidationPassed`(vararg args: Any?) =
                    FYTxtConfig.observe { `ValidationPassed`.fmt(args) }

                /** 正在获取远程资源... */
                val `FetchingRemoteResources`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Fetching remote resources referenced by the configuration and finishing final validation..."""
                                `MLangTags`.ZH -> """正在拉取配置引用的远程资源，并完成最终校验..."""
                                else -> null
                            }
                        } ?: """正在拉取配置引用的远程资源，并完成最终校验..."""

                /** 正在拉取配置引用的远程资源，并完成最终校验... */
                @Composable
                fun `FetchingRemoteResources`(vararg args: Any?) =
                    FYTxtConfig.observe { `FetchingRemoteResources`.fmt(args) }

                /** 已中断远程资源获取并直接保存。当前选中配置正在运行，mihomo 已停止，请按需重新启动。 */
                val `DirectSaveStoppedRuntimeSummary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Remote resource fetching was interrupted and the current file was saved locally. Because the selected profile was running, mihomo has been stopped. Restart it if you still want the profile to take effect."""
                                `MLangTags`.ZH ->
                                    """已中断远程资源获取，并把当前文件直接保存到本地。由于当前选中的配置当时正在运行，mihomo 已停止；如需继续生效，请重新启动。"""
                                else -> null
                            }
                        } ?: """已中断远程资源获取，并把当前文件直接保存到本地。由于当前选中的配置当时正在运行，mihomo 已停止；如需继续生效，请重新启动。"""

                /** 已中断远程资源获取，并把当前文件直接保存到本地。由于当前选中的配置当时正在运行，mihomo 已停止；如需继续生效，请重新启动。 */
                @Composable
                fun `DirectSaveStoppedRuntimeSummary`(vararg args: Any?) =
                    FYTxtConfig.observe { `DirectSaveStoppedRuntimeSummary`.fmt(args) }
            }

            object `Empty` {
                init {
                    `MLangGroups`
                }

                /** 暂无条目 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """No Entries"""
                                `MLangTags`.ZH -> """暂无条目"""
                                else -> null
                            }
                        } ?: """暂无条目"""

                /** 暂无条目 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 点击右上角按钮添加 */
                val `Hint`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Use the add action in the top-right corner to create the first entry"""
                                `MLangTags`.ZH -> """使用右上角的新增按钮创建第一条条目"""
                                else -> null
                            }
                        } ?: """使用右上角的新增按钮创建第一条条目"""

                /** 使用右上角的新增按钮创建第一条条目 */
                @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }
            }

            object `Error` {
                init {
                    `MLangGroups`
                }

                /** 键不能为空 */
                val `KeyEmpty`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Key cannot be empty"""
                                `MLangTags`.ZH -> """键不能为空"""
                                else -> null
                            }
                        } ?: """键不能为空"""

                /** 键不能为空 */
                @Composable
                fun `KeyEmpty`(vararg args: Any?) = FYTxtConfig.observe { `KeyEmpty`.fmt(args) }

                /** 键已存在 */
                val `KeyExists`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Key already exists"""
                                `MLangTags`.ZH -> """键已存在"""
                                else -> null
                            }
                        } ?: """键已存在"""

                /** 键已存在 */
                @Composable
                fun `KeyExists`(vararg args: Any?) = FYTxtConfig.observe { `KeyExists`.fmt(args) }

                /** 保存内容失败 */
                val `SaveFailed`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Failed to save content"""
                                `MLangTags`.ZH -> """保存内容失败"""
                                else -> null
                            }
                        } ?: """保存内容失败"""

                /** 保存内容失败 */
                @Composable
                fun `SaveFailed`(vararg args: Any?) = FYTxtConfig.observe { `SaveFailed`.fmt(args) }

                /** JSON 语法错误 */
                val `JsonSyntaxError`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """JSON syntax error"""
                                `MLangTags`.ZH -> """JSON 语法错误"""
                                else -> null
                            }
                        } ?: """JSON 语法错误"""

                /** JSON 语法错误 */
                @Composable
                fun `JsonSyntaxError`(vararg args: Any?) =
                    FYTxtConfig.observe { `JsonSyntaxError`.fmt(args) }

                /** 未终止的字符串或对象 */
                val `Unterminated`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Unterminated string or object"""
                                `MLangTags`.ZH -> """未终止的字符串或对象"""
                                else -> null
                            }
                        } ?: """未终止的字符串或对象"""

                /** 未终止的字符串或对象 */
                @Composable
                fun `Unterminated`(vararg args: Any?) =
                    FYTxtConfig.observe { `Unterminated`.fmt(args) }

                /** 期望 %s */
                val `Expected`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Expected %s"""
                                `MLangTags`.ZH -> """期望 %s"""
                                else -> null
                            }
                        } ?: """期望 %s"""

                /** 期望 %s */
                @Composable
                fun `Expected`(vararg args: Any?) = FYTxtConfig.observe { `Expected`.fmt(args) }

                /** 未知 */
                val `Unknown`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Unknown"""
                                `MLangTags`.ZH -> """未知"""
                                else -> null
                            }
                        } ?: """未知"""

                /** 未知 */
                @Composable
                fun `Unknown`(vararg args: Any?) = FYTxtConfig.observe { `Unknown`.fmt(args) }

                /** 缺少值 */
                val `MissingValue`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Missing value"""
                                `MLangTags`.ZH -> """缺少值"""
                                else -> null
                            }
                        } ?: """缺少值"""

                /** 缺少值 */
                @Composable
                fun `MissingValue`(vararg args: Any?) =
                    FYTxtConfig.observe { `MissingValue`.fmt(args) }

                /** 重复的键 */
                val `DuplicateKey`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Duplicate key"""
                                `MLangTags`.ZH -> """重复的键"""
                                else -> null
                            }
                        } ?: """重复的键"""

                /** 重复的键 */
                @Composable
                fun `DuplicateKey`(vararg args: Any?) =
                    FYTxtConfig.observe { `DuplicateKey`.fmt(args) }
            }

            object `Rule` {
                init {
                    `MLangGroups`
                }

                /** 规则类型 */
                val `Type`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Rule Type"""
                                `MLangTags`.ZH -> """规则类型"""
                                else -> null
                            }
                        } ?: """规则类型"""

                /** 规则类型 */
                @Composable fun `Type`(vararg args: Any?) = FYTxtConfig.observe { `Type`.fmt(args) }

                /** 目标 */
                val `Target`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Target"""
                                `MLangTags`.ZH -> """目标"""
                                else -> null
                            }
                        } ?: """目标"""

                /** 目标 */
                @Composable
                fun `Target`(vararg args: Any?) = FYTxtConfig.observe { `Target`.fmt(args) }

                /** 规则内容 */
                val `Content`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Rule Content"""
                                `MLangTags`.ZH -> """规则内容"""
                                else -> null
                            }
                        } ?: """规则内容"""

                /** 规则内容 */
                @Composable
                fun `Content`(vararg args: Any?) = FYTxtConfig.observe { `Content`.fmt(args) }

                /** 源 IP */
                val `Src`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Src IP"""
                                `MLangTags`.ZH -> """源 IP"""
                                else -> null
                            }
                        } ?: """源 IP"""

                /** 源 IP */
                @Composable fun `Src`(vararg args: Any?) = FYTxtConfig.observe { `Src`.fmt(args) }

                /** 不解析 */
                val `NoResolve`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """No Resolve"""
                                `MLangTags`.ZH -> """不解析"""
                                else -> null
                            }
                        } ?: """不解析"""

                /** 不解析 */
                @Composable
                fun `NoResolve`(vararg args: Any?) = FYTxtConfig.observe { `NoResolve`.fmt(args) }

                /** REJECT */
                val `TargetReject`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """REJECT"""
                                `MLangTags`.ZH -> """REJECT"""
                                else -> null
                            }
                        } ?: """REJECT"""

                /** REJECT */
                @Composable
                fun `TargetReject`(vararg args: Any?) =
                    FYTxtConfig.observe { `TargetReject`.fmt(args) }

                /** DIRECT */
                val `TargetDirect`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """DIRECT"""
                                `MLangTags`.ZH -> """DIRECT"""
                                else -> null
                            }
                        } ?: """DIRECT"""

                /** DIRECT */
                @Composable
                fun `TargetDirect`(vararg args: Any?) =
                    FYTxtConfig.observe { `TargetDirect`.fmt(args) }

                /** MATCH */
                val `TargetMatch`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """MATCH"""
                                `MLangTags`.ZH -> """MATCH"""
                                else -> null
                            }
                        } ?: """MATCH"""

                /** MATCH */
                @Composable
                fun `TargetMatch`(vararg args: Any?) =
                    FYTxtConfig.observe { `TargetMatch`.fmt(args) }

                /** 请选择目标 */
                val `ErrorTargetRequired`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Please select target"""
                                `MLangTags`.ZH -> """请选择目标"""
                                else -> null
                            }
                        } ?: """请选择目标"""

                /** 请选择目标 */
                @Composable
                fun `ErrorTargetRequired`(vararg args: Any?) =
                    FYTxtConfig.observe { `ErrorTargetRequired`.fmt(args) }

                /** 规则内容不能为空 */
                val `ErrorContentRequired`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Rule content cannot be empty"""
                                `MLangTags`.ZH -> """规则内容不能为空"""
                                else -> null
                            }
                        } ?: """规则内容不能为空"""

                /** 规则内容不能为空 */
                @Composable
                fun `ErrorContentRequired`(vararg args: Any?) =
                    FYTxtConfig.observe { `ErrorContentRequired`.fmt(args) }
            }
        }
    }

    object `Connection` {
        init {
            `MLangGroups`
        }

        /** 连接 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Connections"""
                        `MLangTags`.ZH -> """连接"""
                        else -> null
                    }
                } ?: """连接"""

        /** 连接 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 查看当前活动连接 */
        val `Summary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """View active connections"""
                        `MLangTags`.ZH -> """查看当前活动连接"""
                        else -> null
                    }
                } ?: """查看当前活动连接"""

        /** 查看当前活动连接 */
        @Composable fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }

        object `Tab` {
            init {
                `MLangGroups`
            }

            /** 活动中 */
            val `Active`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Active"""
                            `MLangTags`.ZH -> """活动中"""
                            else -> null
                        }
                    } ?: """活动中"""

            /** 活动中 */
            @Composable fun `Active`(vararg args: Any?) = FYTxtConfig.observe { `Active`.fmt(args) }

            /** 已关闭 */
            val `Closed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Closed"""
                            `MLangTags`.ZH -> """已关闭"""
                            else -> null
                        }
                    } ?: """已关闭"""

            /** 已关闭 */
            @Composable fun `Closed`(vararg args: Any?) = FYTxtConfig.observe { `Closed`.fmt(args) }
        }

        /** 搜索 */
        val `Search`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Search"""
                        `MLangTags`.ZH -> """搜索"""
                        else -> null
                    }
                } ?: """搜索"""

        /** 搜索 */
        @Composable fun `Search`(vararg args: Any?) = FYTxtConfig.observe { `Search`.fmt(args) }

        /** 搜索主机、进程... */
        val `SearchHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Search host, process..."""
                        `MLangTags`.ZH -> """搜索主机、进程..."""
                        else -> null
                    }
                } ?: """搜索主机、进程..."""

        /** 搜索主机、进程... */
        @Composable
        fun `SearchHint`(vararg args: Any?) = FYTxtConfig.observe { `SearchHint`.fmt(args) }

        /** 排序: */
        val `SortBy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Sort by:"""
                        `MLangTags`.ZH -> """排序:"""
                        else -> null
                    }
                } ?: """排序:"""

        /** 排序: */
        @Composable fun `SortBy`(vararg args: Any?) = FYTxtConfig.observe { `SortBy`.fmt(args) }

        object `Sort` {
            init {
                `MLangGroups`
            }

            /** 时间 */
            val `Time`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Time"""
                            `MLangTags`.ZH -> """时间"""
                            else -> null
                        }
                    } ?: """时间"""

            /** 时间 */
            @Composable fun `Time`(vararg args: Any?) = FYTxtConfig.observe { `Time`.fmt(args) }

            /** 上传 */
            val `Upload`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Upload"""
                            `MLangTags`.ZH -> """上传"""
                            else -> null
                        }
                    } ?: """上传"""

            /** 上传 */
            @Composable fun `Upload`(vararg args: Any?) = FYTxtConfig.observe { `Upload`.fmt(args) }

            /** 下载 */
            val `Download`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Download"""
                            `MLangTags`.ZH -> """下载"""
                            else -> null
                        }
                    } ?: """下载"""

            /** 下载 */
            @Composable
            fun `Download`(vararg args: Any?) = FYTxtConfig.observe { `Download`.fmt(args) }

            /** 主机 */
            val `Host`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Host"""
                            `MLangTags`.ZH -> """主机"""
                            else -> null
                        }
                    } ?: """主机"""

            /** 主机 */
            @Composable fun `Host`(vararg args: Any?) = FYTxtConfig.observe { `Host`.fmt(args) }
        }

        /** 加载中... */
        val `Loading`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Loading..."""
                        `MLangTags`.ZH -> """加载中..."""
                        else -> null
                    }
                } ?: """加载中..."""

        /** 加载中... */
        @Composable fun `Loading`(vararg args: Any?) = FYTxtConfig.observe { `Loading`.fmt(args) }

        /** 暂无活动连接 */
        val `Empty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """No active connections"""
                        `MLangTags`.ZH -> """暂无活动连接"""
                        else -> null
                    }
                } ?: """暂无活动连接"""

        /** 暂无活动连接 */
        @Composable fun `Empty`(vararg args: Any?) = FYTxtConfig.observe { `Empty`.fmt(args) }

        /** 没有匹配的连接 */
        val `NoResults`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """No matching connections"""
                        `MLangTags`.ZH -> """没有匹配的连接"""
                        else -> null
                    }
                } ?: """没有匹配的连接"""

        /** 没有匹配的连接 */
        @Composable
        fun `NoResults`(vararg args: Any?) = FYTxtConfig.observe { `NoResults`.fmt(args) }

        object `RelativeTime` {
            init {
                `MLangGroups`
            }

            /** 刚刚 */
            val `JustNow`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Just now"""
                            `MLangTags`.ZH -> """刚刚"""
                            else -> null
                        }
                    } ?: """刚刚"""

            /** 刚刚 */
            @Composable
            fun `JustNow`(vararg args: Any?) = FYTxtConfig.observe { `JustNow`.fmt(args) }

            /** %d分钟前 */
            val `MinutesAgo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d min ago"""
                            `MLangTags`.ZH -> """%d分钟前"""
                            else -> null
                        }
                    } ?: """%d分钟前"""

            /** %d分钟前 */
            @Composable
            fun `MinutesAgo`(vararg args: Any?) = FYTxtConfig.observe { `MinutesAgo`.fmt(args) }

            /** %d小时前 */
            val `HoursAgo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d hr ago"""
                            `MLangTags`.ZH -> """%d小时前"""
                            else -> null
                        }
                    } ?: """%d小时前"""

            /** %d小时前 */
            @Composable
            fun `HoursAgo`(vararg args: Any?) = FYTxtConfig.observe { `HoursAgo`.fmt(args) }

            /** %d天前 */
            val `DaysAgo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d d ago"""
                            `MLangTags`.ZH -> """%d天前"""
                            else -> null
                        }
                    } ?: """%d天前"""

            /** %d天前 */
            @Composable
            fun `DaysAgo`(vararg args: Any?) = FYTxtConfig.observe { `DaysAgo`.fmt(args) }
        }

        object `Detail` {
            init {
                `MLangGroups`
            }

            /** 连接信息 */
            val `Info`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Connection Info"""
                            `MLangTags`.ZH -> """连接信息"""
                            else -> null
                        }
                    } ?: """连接信息"""

            /** 连接信息 */
            @Composable fun `Info`(vararg args: Any?) = FYTxtConfig.observe { `Info`.fmt(args) }

            /** 协议 */
            val `Protocol`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Protocol"""
                            `MLangTags`.ZH -> """协议"""
                            else -> null
                        }
                    } ?: """协议"""

            /** 协议 */
            @Composable
            fun `Protocol`(vararg args: Any?) = FYTxtConfig.observe { `Protocol`.fmt(args) }

            /** 源应用 */
            val `SourceApp`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Source App"""
                            `MLangTags`.ZH -> """源应用"""
                            else -> null
                        }
                    } ?: """源应用"""

            /** 源应用 */
            @Composable
            fun `SourceApp`(vararg args: Any?) = FYTxtConfig.observe { `SourceApp`.fmt(args) }

            /** 包名 */
            val `PackageName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Package"""
                            `MLangTags`.ZH -> """包名"""
                            else -> null
                        }
                    } ?: """包名"""

            /** 包名 */
            @Composable
            fun `PackageName`(vararg args: Any?) = FYTxtConfig.observe { `PackageName`.fmt(args) }

            /** 进程 */
            val `Process`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Process"""
                            `MLangTags`.ZH -> """进程"""
                            else -> null
                        }
                    } ?: """进程"""

            /** 进程 */
            @Composable
            fun `Process`(vararg args: Any?) = FYTxtConfig.observe { `Process`.fmt(args) }

            /** 源地址 */
            val `SourceAddress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Source"""
                            `MLangTags`.ZH -> """源地址"""
                            else -> null
                        }
                    } ?: """源地址"""

            /** 源地址 */
            @Composable
            fun `SourceAddress`(vararg args: Any?) =
                FYTxtConfig.observe { `SourceAddress`.fmt(args) }

            /** 目标地址 */
            val `DestinationAddress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Destination"""
                            `MLangTags`.ZH -> """目标地址"""
                            else -> null
                        }
                    } ?: """目标地址"""

            /** 目标地址 */
            @Composable
            fun `DestinationAddress`(vararg args: Any?) =
                FYTxtConfig.observe { `DestinationAddress`.fmt(args) }

            /** 连接时长 */
            val `Duration`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Duration"""
                            `MLangTags`.ZH -> """连接时长"""
                            else -> null
                        }
                    } ?: """连接时长"""

            /** 连接时长 */
            @Composable
            fun `Duration`(vararg args: Any?) = FYTxtConfig.observe { `Duration`.fmt(args) }

            /** 上传 */
            val `Upload`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Upload"""
                            `MLangTags`.ZH -> """上传"""
                            else -> null
                        }
                    } ?: """上传"""

            /** 上传 */
            @Composable fun `Upload`(vararg args: Any?) = FYTxtConfig.observe { `Upload`.fmt(args) }

            /** 下载 */
            val `Download`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Download"""
                            `MLangTags`.ZH -> """下载"""
                            else -> null
                        }
                    } ?: """下载"""

            /** 下载 */
            @Composable
            fun `Download`(vararg args: Any?) = FYTxtConfig.observe { `Download`.fmt(args) }

            /** 规则 */
            val `Rule`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule"""
                            `MLangTags`.ZH -> """规则"""
                            else -> null
                        }
                    } ?: """规则"""

            /** 规则 */
            @Composable fun `Rule`(vararg args: Any?) = FYTxtConfig.observe { `Rule`.fmt(args) }

            /** 类型 */
            val `Type`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Type"""
                            `MLangTags`.ZH -> """类型"""
                            else -> null
                        }
                    } ?: """类型"""

            /** 类型 */
            @Composable fun `Type`(vararg args: Any?) = FYTxtConfig.observe { `Type`.fmt(args) }

            /** 内容 */
            val `Content`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Content"""
                            `MLangTags`.ZH -> """内容"""
                            else -> null
                        }
                    } ?: """内容"""

            /** 内容 */
            @Composable
            fun `Content`(vararg args: Any?) = FYTxtConfig.observe { `Content`.fmt(args) }
        }

        object `Capture` {
            init {
                `MLangGroups`
            }

            /** 开始捕获 */
            val `Start`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Start Capture"""
                            `MLangTags`.ZH -> """开始捕获"""
                            else -> null
                        }
                    } ?: """开始捕获"""

            /** 开始捕获 */
            @Composable fun `Start`(vararg args: Any?) = FYTxtConfig.observe { `Start`.fmt(args) }

            /** 停止捕获 */
            val `Stop`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Stop Capture"""
                            `MLangTags`.ZH -> """停止捕获"""
                            else -> null
                        }
                    } ?: """停止捕获"""

            /** 停止捕获 */
            @Composable fun `Stop`(vararg args: Any?) = FYTxtConfig.observe { `Stop`.fmt(args) }

            /** 导出成功 */
            val `ExportSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """PCAP exported successfully"""
                            `MLangTags`.ZH -> """PCAP 导出成功"""
                            else -> null
                        }
                    } ?: """PCAP 导出成功"""

            /** 导出成功 */
            @Composable
            fun `ExportSuccess`(vararg args: Any?) =
                FYTxtConfig.observe { `ExportSuccess`.fmt(args) }

            /** 导出失败 */
            val `ExportFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """PCAP export failed"""
                            `MLangTags`.ZH -> """PCAP 导出失败"""
                            else -> null
                        }
                    } ?: """PCAP 导出失败"""

            /** 导出失败 */
            @Composable
            fun `ExportFailed`(vararg args: Any?) = FYTxtConfig.observe { `ExportFailed`.fmt(args) }
        }
    }

    object `Home` {
        init {
            `MLangGroups`
        }

        /** MonadBox */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """MonadBox"""
                        `MLangTags`.ZH -> """MonadBox"""
                        else -> null
                    }
                } ?: """MonadBox"""

        /** MonadBox */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Message` {
            init {
                `MLangGroups`
            }

            /** 配置已切换 */
            val `ConfigSwitched`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Config switched"""
                            `MLangTags`.ZH -> """配置已切换"""
                            else -> null
                        }
                    } ?: """配置已切换"""

            /** 配置已切换 */
            @Composable
            fun `ConfigSwitched`(vararg args: Any?) =
                FYTxtConfig.observe { `ConfigSwitched`.fmt(args) }

            /** 配置切换失败：%s */
            val `ConfigSwitchFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Config switch failed: %s"""
                            `MLangTags`.ZH -> """配置切换失败：%s"""
                            else -> null
                        }
                    } ?: """配置切换失败：%s"""

            /** 配置切换失败：%s */
            @Composable
            fun `ConfigSwitchFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `ConfigSwitchFailed`.fmt(args) }

            /** 正在准备... */
            val `Preparing`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Preparing..."""
                            `MLangTags`.ZH -> """正在准备..."""
                            else -> null
                        }
                    } ?: """正在准备..."""

            /** 正在准备... */
            @Composable
            fun `Preparing`(vararg args: Any?) = FYTxtConfig.observe { `Preparing`.fmt(args) }

            /** 启动失败：%s */
            val `StartFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Start failed: %s"""
                            `MLangTags`.ZH -> """启动失败：%s"""
                            else -> null
                        }
                    } ?: """启动失败：%s"""

            /** 启动失败：%s */
            @Composable
            fun `StartFailed`(vararg args: Any?) = FYTxtConfig.observe { `StartFailed`.fmt(args) }

            /** 本地配置启动失败 */
            val `StartFailedDialogTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Local Profile Startup Failed"""
                            `MLangTags`.ZH -> """本地配置启动失败"""
                            else -> null
                        }
                    } ?: """本地配置启动失败"""

            /** 本地配置启动失败 */
            @Composable
            fun `StartFailedDialogTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedDialogTitle`.fmt(args) }

            /** 配置语法校验失败：%s */
            val `StartFailedSyntaxReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Configuration syntax validation failed: %s"""
                            `MLangTags`.ZH -> """配置语法校验失败：%s"""
                            else -> null
                        }
                    } ?: """配置语法校验失败：%s"""

            /** 配置语法校验失败：%s */
            @Composable
            fun `StartFailedSyntaxReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedSyntaxReason`.fmt(args) }

            /** 远程资源获取失败：%s */
            val `StartFailedRemoteReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Remote resource fetch failed: %s"""
                            `MLangTags`.ZH -> """远程资源获取失败：%s"""
                            else -> null
                        }
                    } ?: """远程资源获取失败：%s"""

            /** 远程资源获取失败：%s */
            @Composable
            fun `StartFailedRemoteReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedRemoteReason`.fmt(args) }

            /** 网络连接失败：%s */
            val `StartFailedNetworkReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Network connection failed: %s"""
                            `MLangTags`.ZH -> """网络连接失败：%s"""
                            else -> null
                        }
                    } ?: """网络连接失败：%s"""

            /** 网络连接失败：%s */
            @Composable
            fun `StartFailedNetworkReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedNetworkReason`.fmt(args) }

            /** 权限或访问被拒绝：%s */
            val `StartFailedPermissionReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Permission or access denied: %s"""
                            `MLangTags`.ZH -> """权限或访问被拒绝：%s"""
                            else -> null
                        }
                    } ?: """权限或访问被拒绝：%s"""

            /** 权限或访问被拒绝：%s */
            @Composable
            fun `StartFailedPermissionReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedPermissionReason`.fmt(args) }

            /** 配置状态异常：%s */
            val `StartFailedProfileReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile state error: %s"""
                            `MLangTags`.ZH -> """配置状态异常：%s"""
                            else -> null
                        }
                    } ?: """配置状态异常：%s"""

            /** 配置状态异常：%s */
            @Composable
            fun `StartFailedProfileReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedProfileReason`.fmt(args) }

            /** 运行时服务异常：%s */
            val `StartFailedRuntimeServiceReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Runtime service unavailable: %s"""
                            `MLangTags`.ZH -> """运行时服务异常：%s"""
                            else -> null
                        }
                    } ?: """运行时服务异常：%s"""

            /** 运行时服务异常：%s */
            @Composable
            fun `StartFailedRuntimeServiceReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedRuntimeServiceReason`.fmt(args) }

            /** 运行时控制失败：%s */
            val `StartFailedRuntimeControlReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Runtime control failed: %s"""
                            `MLangTags`.ZH -> """运行时控制失败：%s"""
                            else -> null
                        }
                    } ?: """运行时控制失败：%s"""

            /** 运行时控制失败：%s */
            @Composable
            fun `StartFailedRuntimeControlReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedRuntimeControlReason`.fmt(args) }

            /** 环境或资源限制导致启动失败：%s */
            val `StartFailedEnvironmentReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Environment or resource limitation: %s"""
                            `MLangTags`.ZH -> """环境或资源限制导致启动失败：%s"""
                            else -> null
                        }
                    } ?: """环境或资源限制导致启动失败：%s"""

            /** 环境或资源限制导致启动失败：%s */
            @Composable
            fun `StartFailedEnvironmentReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedEnvironmentReason`.fmt(args) }

            /** 启动失败原因：%s */
            val `StartFailedUnknownReason`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Startup failed: %s"""
                            `MLangTags`.ZH -> """启动失败原因：%s"""
                            else -> null
                        }
                    } ?: """启动失败原因：%s"""

            /** 启动失败原因：%s */
            @Composable
            fun `StartFailedUnknownReason`(vararg args: Any?) =
                FYTxtConfig.observe { `StartFailedUnknownReason`.fmt(args) }

            /** 停止失败：%s */
            val `StopFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Stop failed: %s"""
                            `MLangTags`.ZH -> """停止失败：%s"""
                            else -> null
                        }
                    } ?: """停止失败：%s"""

            /** 停止失败：%s */
            @Composable
            fun `StopFailed`(vararg args: Any?) = FYTxtConfig.observe { `StopFailed`.fmt(args) }
        }

        object `Control` {
            init {
                `MLangGroups`
            }

            /** 请先添加配置文件 */
            val `HintAddProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Please add a profile first"""
                            `MLangTags`.ZH -> """请先添加配置文件"""
                            else -> null
                        }
                    } ?: """请先添加配置文件"""

            /** 请先添加配置文件 */
            @Composable
            fun `HintAddProfile`(vararg args: Any?) =
                FYTxtConfig.observe { `HintAddProfile`.fmt(args) }

            /** 请先在「配置」页面启用一个配置 */
            val `HintEnableProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Please enable a profile in Config page first"""
                            `MLangTags`.ZH -> """请先在「配置」页面启用一个配置"""
                            else -> null
                        }
                    } ?: """请先在「配置」页面启用一个配置"""

            /** 请先在「配置」页面启用一个配置 */
            @Composable
            fun `HintEnableProfile`(vararg args: Any?) =
                FYTxtConfig.observe { `HintEnableProfile`.fmt(args) }
        }

        object `Profile` {
            init {
                `MLangGroups`
            }

            /** 未选择配置 */
            val `NoProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No Profile"""
                            `MLangTags`.ZH -> """未选择配置"""
                            else -> null
                        }
                    } ?: """未选择配置"""

            /** 未选择配置 */
            @Composable
            fun `NoProfile`(vararg args: Any?) = FYTxtConfig.observe { `NoProfile`.fmt(args) }

            /** 直连 */
            val `Direct`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Direct"""
                            `MLangTags`.ZH -> """直连"""
                            else -> null
                        }
                    } ?: """直连"""

            /** 直连 */
            @Composable fun `Direct`(vararg args: Any?) = FYTxtConfig.observe { `Direct`.fmt(args) }

            /** 代理 */
            val `Proxy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy"""
                            `MLangTags`.ZH -> """代理"""
                            else -> null
                        }
                    } ?: """代理"""

            /** 代理 */
            @Composable fun `Proxy`(vararg args: Any?) = FYTxtConfig.observe { `Proxy`.fmt(args) }

            /** 拦截 */
            val `Reject`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Reject"""
                            `MLangTags`.ZH -> """拦截"""
                            else -> null
                        }
                    } ?: """拦截"""

            /** 拦截 */
            @Composable fun `Reject`(vararg args: Any?) = FYTxtConfig.observe { `Reject`.fmt(args) }

            /** 全局代理 */
            val `Global`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Global"""
                            `MLangTags`.ZH -> """全局代理"""
                            else -> null
                        }
                    } ?: """全局代理"""

            /** 全局代理 */
            @Composable fun `Global`(vararg args: Any?) = FYTxtConfig.observe { `Global`.fmt(args) }

            /** 规则分流 */
            val `Rule`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule"""
                            `MLangTags`.ZH -> """规则分流"""
                            else -> null
                        }
                    } ?: """规则分流"""

            /** 规则分流 */
            @Composable fun `Rule`(vararg args: Any?) = FYTxtConfig.observe { `Rule`.fmt(args) }
        }

        object `NodeInfo` {
            init {
                `MLangGroups`
            }

            /** 节点 */
            val `Node`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Node"""
                            `MLangTags`.ZH -> """节点"""
                            else -> null
                        }
                    } ?: """节点"""

            /** 节点 */
            @Composable fun `Node`(vararg args: Any?) = FYTxtConfig.observe { `Node`.fmt(args) }

            /** 延迟 */
            val `Delay`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delay"""
                            `MLangTags`.ZH -> """延迟"""
                            else -> null
                        }
                    } ?: """延迟"""

            /** 延迟 */
            @Composable fun `Delay`(vararg args: Any?) = FYTxtConfig.observe { `Delay`.fmt(args) }

            /** -- */
            val `Unknown`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """--"""
                            `MLangTags`.ZH -> """--"""
                            else -> null
                        }
                    } ?: """--"""

            /** -- */
            @Composable
            fun `Unknown`(vararg args: Any?) = FYTxtConfig.observe { `Unknown`.fmt(args) }
        }

        object `IpInfo` {
            init {
                `MLangGroups`
            }

            /** 出口 IP */
            val `ExitIp`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Exit IP"""
                            `MLangTags`.ZH -> """出口 IP"""
                            else -> null
                        }
                    } ?: """出口 IP"""

            /** 出口 IP */
            @Composable fun `ExitIp`(vararg args: Any?) = FYTxtConfig.observe { `ExitIp`.fmt(args) }
        }

        object `Status` {
            init {
                `MLangGroups`
            }

            /** 启动中 */
            val `Starting`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Starting"""
                            `MLangTags`.ZH -> """启动中"""
                            else -> null
                        }
                    } ?: """启动中"""

            /** 启动中 */
            @Composable
            fun `Starting`(vararg args: Any?) = FYTxtConfig.observe { `Starting`.fmt(args) }

            /** 运行中 */
            val `Running`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Running"""
                            `MLangTags`.ZH -> """运行中"""
                            else -> null
                        }
                    } ?: """运行中"""

            /** 运行中 */
            @Composable
            fun `Running`(vararg args: Any?) = FYTxtConfig.observe { `Running`.fmt(args) }

            /** 停止中 */
            val `Stopping`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Stopping"""
                            `MLangTags`.ZH -> """停止中"""
                            else -> null
                        }
                    } ?: """停止中"""

            /** 停止中 */
            @Composable
            fun `Stopping`(vararg args: Any?) = FYTxtConfig.observe { `Stopping`.fmt(args) }

            /** 轻触启动 */
            val `TapToStart`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Tap to start"""
                            `MLangTags`.ZH -> """轻触启动"""
                            else -> null
                        }
                    } ?: """轻触启动"""

            /** 轻触启动 */
            @Composable
            fun `TapToStart`(vararg args: Any?) = FYTxtConfig.observe { `TapToStart`.fmt(args) }
        }
    }

    object `Log` {
        init {
            `MLangGroups`
        }

        /** 日志 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Logs"""
                        `MLangTags`.ZH -> """日志"""
                        else -> null
                    }
                } ?: """日志"""

        /** 日志 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Action` {
            init {
                `MLangGroups`
            }

            /** 停止记录 */
            val `StopRecording`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Stop Recording"""
                            `MLangTags`.ZH -> """停止记录"""
                            else -> null
                        }
                    } ?: """停止记录"""

            /** 停止记录 */
            @Composable
            fun `StopRecording`(vararg args: Any?) =
                FYTxtConfig.observe { `StopRecording`.fmt(args) }

            /** 开始记录 */
            val `StartRecording`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Start Recording"""
                            `MLangTags`.ZH -> """开始记录"""
                            else -> null
                        }
                    } ?: """开始记录"""

            /** 开始记录 */
            @Composable
            fun `StartRecording`(vararg args: Any?) =
                FYTxtConfig.observe { `StartRecording`.fmt(args) }

            /** 保存 */
            val `Save`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Save"""
                            `MLangTags`.ZH -> """保存"""
                            else -> null
                        }
                    } ?: """保存"""

            /** 保存 */
            @Composable fun `Save`(vararg args: Any?) = FYTxtConfig.observe { `Save`.fmt(args) }

            /** 清理日志 */
            val `Cleanup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cleanup Logs"""
                            `MLangTags`.ZH -> """清理日志"""
                            else -> null
                        }
                    } ?: """清理日志"""

            /** 清理日志 */
            @Composable
            fun `Cleanup`(vararg args: Any?) = FYTxtConfig.observe { `Cleanup`.fmt(args) }

            /** 日志清理完成 */
            val `CleanupDone`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Log cleanup completed"""
                            `MLangTags`.ZH -> """日志清理完成"""
                            else -> null
                        }
                    } ?: """日志清理完成"""

            /** 日志清理完成 */
            @Composable
            fun `CleanupDone`(vararg args: Any?) = FYTxtConfig.observe { `CleanupDone`.fmt(args) }

            /** 导出诊断包 */
            val `ExportDebugBundle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Export Debug Bundle"""
                            `MLangTags`.ZH -> """导出诊断包"""
                            else -> null
                        }
                    } ?: """导出诊断包"""

            /** 导出诊断包 */
            @Composable
            fun `ExportDebugBundle`(vararg args: Any?) =
                FYTxtConfig.observe { `ExportDebugBundle`.fmt(args) }

            /** 包含运行状态与脱敏日志，可能仍含服务器地址等上下文；仅分享给可信接收方。 */
            val `ExportDebugBundleWarning`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Includes runtime state and sanitized logs. May still contain server/context details; share only with trusted recipients."""
                            `MLangTags`.ZH -> """包含运行状态与脱敏日志，可能仍含服务器地址等上下文；仅分享给可信接收方。"""
                            else -> null
                        }
                    } ?: """包含运行状态与脱敏日志，可能仍含服务器地址等上下文；仅分享给可信接收方。"""

            /** 包含运行状态与脱敏日志，可能仍含服务器地址等上下文；仅分享给可信接收方。 */
            @Composable
            fun `ExportDebugBundleWarning`(vararg args: Any?) =
                FYTxtConfig.observe { `ExportDebugBundleWarning`.fmt(args) }

            /** 导出 */
            val `Export`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Export"""
                            `MLangTags`.ZH -> """导出"""
                            else -> null
                        }
                    } ?: """导出"""

            /** 导出 */
            @Composable fun `Export`(vararg args: Any?) = FYTxtConfig.observe { `Export`.fmt(args) }

            /** 取消 */
            val `Cancel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cancel"""
                            `MLangTags`.ZH -> """取消"""
                            else -> null
                        }
                    } ?: """取消"""

            /** 取消 */
            @Composable fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

            /** 导出成功 */
            val `ExportDone`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Export completed"""
                            `MLangTags`.ZH -> """导出成功"""
                            else -> null
                        }
                    } ?: """导出成功"""

            /** 导出成功 */
            @Composable
            fun `ExportDone`(vararg args: Any?) = FYTxtConfig.observe { `ExportDone`.fmt(args) }
        }

        object `Empty` {
            init {
                `MLangGroups`
            }

            /** 暂无日志记录 */
            val `NoLogs`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No log records"""
                            `MLangTags`.ZH -> """暂无日志记录"""
                            else -> null
                        }
                    } ?: """暂无日志记录"""

            /** 暂无日志记录 */
            @Composable fun `NoLogs`(vararg args: Any?) = FYTxtConfig.observe { `NoLogs`.fmt(args) }

            /** 点击右下角按钮开始记录日志 */
            val `StartRecordingHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Click the bottom-right button to start recording logs"""
                            `MLangTags`.ZH -> """点击右下角按钮开始记录日志"""
                            else -> null
                        }
                    } ?: """点击右下角按钮开始记录日志"""

            /** 点击右下角按钮开始记录日志 */
            @Composable
            fun `StartRecordingHint`(vararg args: Any?) =
                FYTxtConfig.observe { `StartRecordingHint`.fmt(args) }

            /** 从主页启动内核后将自动开始记录 */
            val `AutoRecordHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Recording starts automatically when the kernel is started from Home"""
                            `MLangTags`.ZH -> """从主页启动内核后将自动开始记录"""
                            else -> null
                        }
                    } ?: """从主页启动内核后将自动开始记录"""

            /** 从主页启动内核后将自动开始记录 */
            @Composable
            fun `AutoRecordHint`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoRecordHint`.fmt(args) }
        }

        object `Detail` {
            init {
                `MLangGroups`
            }

            /** 等待日志... */
            val `WaitingLog`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Waiting for logs..."""
                            `MLangTags`.ZH -> """等待日志..."""
                            else -> null
                        }
                    } ?: """等待日志..."""

            /** 等待日志... */
            @Composable
            fun `WaitingLog`(vararg args: Any?) = FYTxtConfig.observe { `WaitingLog`.fmt(args) }

            /** 日志将在产生时显示 */
            val `WillShowWhenGenerated`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Logs will appear when generated"""
                            `MLangTags`.ZH -> """日志将在产生时显示"""
                            else -> null
                        }
                    } ?: """日志将在产生时显示"""

            /** 日志将在产生时显示 */
            @Composable
            fun `WillShowWhenGenerated`(vararg args: Any?) =
                FYTxtConfig.observe { `WillShowWhenGenerated`.fmt(args) }
        }

        object `History` {
            init {
                `MLangGroups`
            }

            /** 运行日志归档 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Runtime Log Archives"""
                            `MLangTags`.ZH -> """运行日志归档"""
                            else -> null
                        }
                    } ?: """运行日志归档"""

            /** 运行日志归档 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 实时运行日志（当前会话） */
            val `LiveSection`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Live Runtime Logs (Current Session)"""
                            `MLangTags`.ZH -> """实时运行日志（当前会话）"""
                            else -> null
                        }
                    } ?: """实时运行日志（当前会话）"""

            /** 实时运行日志（当前会话） */
            @Composable
            fun `LiveSection`(vararg args: Any?) = FYTxtConfig.observe { `LiveSection`.fmt(args) }

            /** 记录中 */
            val `Recording`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Recording"""
                            `MLangTags`.ZH -> """记录中"""
                            else -> null
                        }
                    } ?: """记录中"""

            /** 记录中 */
            @Composable
            fun `Recording`(vararg args: Any?) = FYTxtConfig.observe { `Recording`.fmt(args) }

            /** %s · %s */
            val `ItemSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%s · %s"""
                            `MLangTags`.ZH -> """%s · %s"""
                            else -> null
                        }
                    } ?: """%s · %s"""

            /** %s · %s */
            @Composable
            fun `ItemSummary`(vararg args: Any?) = FYTxtConfig.observe { `ItemSummary`.fmt(args) }
        }

        object `Startup` {
            init {
                `MLangGroups`
            }

            /** 启动诊断日志 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Startup Diagnostics"""
                            `MLangTags`.ZH -> """启动诊断日志"""
                            else -> null
                        }
                    } ?: """启动诊断日志"""

            /** 启动诊断日志 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 启动阶段诊断 */
            val `LiveSection`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Startup Phase Diagnostics"""
                            `MLangTags`.ZH -> """启动阶段诊断"""
                            else -> null
                        }
                    } ?: """启动阶段诊断"""

            /** 启动阶段诊断 */
            @Composable
            fun `LiveSection`(vararg args: Any?) = FYTxtConfig.observe { `LiveSection`.fmt(args) }

            /** %s · %s */
            val `ItemSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%s · %s"""
                            `MLangTags`.ZH -> """%s · %s"""
                            else -> null
                        }
                    } ?: """%s · %s"""

            /** %s · %s */
            @Composable
            fun `ItemSummary`(vararg args: Any?) = FYTxtConfig.observe { `ItemSummary`.fmt(args) }
        }
    }

    object `MetaFeature` {
        init {
            `MLangGroups`
        }

        /** Meta 功能 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Meta Features"""
                        `MLangTags`.ZH -> """Meta 功能"""
                        else -> null
                    }
                } ?: """Meta 功能"""

        /** Meta 功能 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `RecentRequests` {
            init {
                `MLangGroups`
            }

            /** 最近请求 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Recent Requests"""
                            `MLangTags`.ZH -> """最近请求"""
                            else -> null
                        }
                    } ?: """最近请求"""

            /** 最近请求 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 查看最近请求与流量统计 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """View recent requests and traffic statistics"""
                            `MLangTags`.ZH -> """查看最近请求与流量统计"""
                            else -> null
                        }
                    } ?: """查看最近请求与流量统计"""

            /** 查看最近请求与流量统计 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `RuntimeConfig` {
            init {
                `MLangGroups`
            }

            /** 运行时配置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Runtime Config"""
                            `MLangTags`.ZH -> """运行时配置"""
                            else -> null
                        }
                    } ?: """运行时配置"""

            /** 运行时配置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 查看当前 mihomo 的运行时配置快照 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """View the current mihomo runtime configuration snapshot"""
                            `MLangTags`.ZH -> """查看当前 mihomo 的运行时配置快照"""
                            else -> null
                        }
                    } ?: """查看当前 mihomo 的运行时配置快照"""

            /** 查看当前 mihomo 的运行时配置快照 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }

            /** 读取运行时配置失败 */
            val `LoadFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to load runtime config"""
                            `MLangTags`.ZH -> """读取运行时配置失败"""
                            else -> null
                        }
                    } ?: """读取运行时配置失败"""

            /** 读取运行时配置失败 */
            @Composable
            fun `LoadFailed`(vararg args: Any?) = FYTxtConfig.observe { `LoadFailed`.fmt(args) }

            /** 当前没有活动配置 */
            val `NoActiveProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No active profile"""
                            `MLangTags`.ZH -> """当前没有活动配置"""
                            else -> null
                        }
                    } ?: """当前没有活动配置"""

            /** 当前没有活动配置 */
            @Composable
            fun `NoActiveProfile`(vararg args: Any?) =
                FYTxtConfig.observe { `NoActiveProfile`.fmt(args) }

            /** 未找到当前活动配置文件 */
            val `ConfigNotFound`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Active profile config file not found"""
                            `MLangTags`.ZH -> """未找到当前活动配置文件"""
                            else -> null
                        }
                    } ?: """未找到当前活动配置文件"""

            /** 未找到当前活动配置文件 */
            @Composable
            fun `ConfigNotFound`(vararg args: Any?) =
                FYTxtConfig.observe { `ConfigNotFound`.fmt(args) }

            /** 运行时配置 */
            val `PreviewTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Runtime Config"""
                            `MLangTags`.ZH -> """运行时配置"""
                            else -> null
                        }
                    } ?: """运行时配置"""

            /** 运行时配置 */
            @Composable
            fun `PreviewTitle`(vararg args: Any?) = FYTxtConfig.observe { `PreviewTitle`.fmt(args) }

            /** 运行时配置 · %s */
            val `PreviewTitleWithProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Runtime Config · %s"""
                            `MLangTags`.ZH -> """运行时配置 · %s"""
                            else -> null
                        }
                    } ?: """运行时配置 · %s"""

            /** 运行时配置 · %s */
            @Composable
            fun `PreviewTitleWithProfile`(vararg args: Any?) =
                FYTxtConfig.observe { `PreviewTitleWithProfile`.fmt(args) }

            /** 无法获取实时配置：%s */
            val `RuntimeConfigFetchFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unable to fetch live config: %s"""
                            `MLangTags`.ZH -> """无法获取实时配置：%s"""
                            else -> null
                        }
                    } ?: """无法获取实时配置：%s"""

            /** 无法获取实时配置：%s */
            @Composable
            fun `RuntimeConfigFetchFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `RuntimeConfigFetchFailed`.fmt(args) }

            /** 密钥不匹配。请尝试重启内核以同步设置。 */
            val `RuntimeConfigUnauthorized`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Mismatching secret. Please try restarting the kernel to sync settings."""
                            `MLangTags`.ZH -> """密钥不匹配。请尝试重启内核以同步设置。"""
                            else -> null
                        }
                    } ?: """密钥不匹配。请尝试重启内核以同步设置。"""

            /** 密钥不匹配。请尝试重启内核以同步设置。 */
            @Composable
            fun `RuntimeConfigUnauthorized`(vararg args: Any?) =
                FYTxtConfig.observe { `RuntimeConfigUnauthorized`.fmt(args) }

            /** 无法获取实时配置：内核未运行 (Connection Refused) */
            val `RuntimeConfigNotRunning`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unable to fetch live config: Kernel not running"""
                            `MLangTags`.ZH -> """无法获取实时配置：内核未运行 (Connection Refused)"""
                            else -> null
                        }
                    } ?: """无法获取实时配置：内核未运行 (Connection Refused)"""

            /** 无法获取实时配置：内核未运行 (Connection Refused) */
            @Composable
            fun `RuntimeConfigNotRunning`(vararg args: Any?) =
                FYTxtConfig.observe { `RuntimeConfigNotRunning`.fmt(args) }
        }

        object `GeoX` {
            init {
                `MLangGroups`
            }

            /** 更新 GeoX */
            val `OnlineUpdateTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update GeoX"""
                            `MLangTags`.ZH -> """更新 GeoX"""
                            else -> null
                        }
                    } ?: """更新 GeoX"""

            /** 更新 GeoX */
            @Composable
            fun `OnlineUpdateTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `OnlineUpdateTitle`.fmt(args) }

            /** 下载数据库文件 */
            val `OnlineUpdateSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Download Database"""
                            `MLangTags`.ZH -> """下载数据库文件"""
                            else -> null
                        }
                    } ?: """下载数据库文件"""

            /** 下载数据库文件 */
            @Composable
            fun `OnlineUpdateSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `OnlineUpdateSummary`.fmt(args) }
        }

        object `Download` {
            init {
                `MLangGroups`
            }

            /** 更新 GeoX */
            val `DialogTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update GeoX Online"""
                            `MLangTags`.ZH -> """更新 GeoX"""
                            else -> null
                        }
                    } ?: """更新 GeoX"""

            /** 更新 GeoX */
            @Composable
            fun `DialogTitle`(vararg args: Any?) = FYTxtConfig.observe { `DialogTitle`.fmt(args) }

            /** 请选择要更新的文件 */
            val `SelectFiles`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Please select files to update"""
                            `MLangTags`.ZH -> """请选择要更新的文件"""
                            else -> null
                        }
                    } ?: """请选择要更新的文件"""

            /** 请选择要更新的文件 */
            @Composable
            fun `SelectFiles`(vararg args: Any?) = FYTxtConfig.observe { `SelectFiles`.fmt(args) }

            /** 下载完成：%d/%d */
            val `DownloadComplete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Download complete: %d/%d"""
                            `MLangTags`.ZH -> """下载完成：%d/%d"""
                            else -> null
                        }
                    } ?: """下载完成：%d/%d"""

            /** 下载完成：%d/%d */
            @Composable
            fun `DownloadComplete`(vararg args: Any?) =
                FYTxtConfig.observe { `DownloadComplete`.fmt(args) }
        }

        object `Dashboard` {
            init {
                `MLangGroups`
            }

            /** 网页控制面板 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Web Dashboard"""
                            `MLangTags`.ZH -> """网页控制面板"""
                            else -> null
                        }
                    } ?: """网页控制面板"""

            /** 网页控制面板 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 控制面板端口 */
            val `Port`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Dashboard Port"""
                            `MLangTags`.ZH -> """控制面板端口"""
                            else -> null
                        }
                    } ?: """控制面板端口"""

            /** 控制面板端口 */
            @Composable fun `Port`(vararg args: Any?) = FYTxtConfig.observe { `Port`.fmt(args) }

            /** 配置网页控制面板的端口 */
            val `PortSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Configure the port for the web dashboard"""
                            `MLangTags`.ZH -> """配置网页控制面板的端口"""
                            else -> null
                        }
                    } ?: """配置网页控制面板的端口"""

            /** 配置网页控制面板的端口 */
            @Composable
            fun `PortSummary`(vararg args: Any?) = FYTxtConfig.observe { `PortSummary`.fmt(args) }

            /** API 密钥 */
            val `Secret`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """API Secret"""
                            `MLangTags`.ZH -> """API 密钥"""
                            else -> null
                        }
                    } ?: """API 密钥"""

            /** API 密钥 */
            @Composable fun `Secret`(vararg args: Any?) = FYTxtConfig.observe { `Secret`.fmt(args) }

            /** 用于验证控制面板用户的密钥 */
            val `SecretSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """The secret used to authenticate the user for the web dashboard"""
                            `MLangTags`.ZH -> """用于验证控制面板用户的密钥"""
                            else -> null
                        }
                    } ?: """用于验证控制面板用户的密钥"""

            /** 用于验证控制面板用户的密钥 */
            @Composable
            fun `SecretSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `SecretSummary`.fmt(args) }

            /** 显示密钥 */
            val `ShowSecret`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Show Secret"""
                            `MLangTags`.ZH -> """显示密钥"""
                            else -> null
                        }
                    } ?: """显示密钥"""

            /** 显示密钥 */
            @Composable
            fun `ShowSecret`(vararg args: Any?) = FYTxtConfig.observe { `ShowSecret`.fmt(args) }

            /** 下载 MetaCubeXD */
            val `Download`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Download MetaCubeXD"""
                            `MLangTags`.ZH -> """下载 MetaCubeXD"""
                            else -> null
                        }
                    } ?: """下载 MetaCubeXD"""

            /** 下载 MetaCubeXD */
            @Composable
            fun `Download`(vararg args: Any?) = FYTxtConfig.observe { `Download`.fmt(args) }

            /** 下载 Web UI 资源并解压到数据目录 */
            val `DownloadSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Download and extract the Web UI assets into the data directory"""
                            `MLangTags`.ZH -> """下载 Web UI 资源并解压到数据目录"""
                            else -> null
                        }
                    } ?: """下载 Web UI 资源并解压到数据目录"""

            /** 下载 Web UI 资源并解压到数据目录 */
            @Composable
            fun `DownloadSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `DownloadSummary`.fmt(args) }

            /** 控制面板 UI 下载完成 */
            val `DownloadOk`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Dashboard UI downloaded successfully"""
                            `MLangTags`.ZH -> """控制面板 UI 下载完成"""
                            else -> null
                        }
                    } ?: """控制面板 UI 下载完成"""

            /** 控制面板 UI 下载完成 */
            @Composable
            fun `DownloadOk`(vararg args: Any?) = FYTxtConfig.observe { `DownloadOk`.fmt(args) }

            /** 下载失败：%s */
            val `DownloadFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Download failed: %s"""
                            `MLangTags`.ZH -> """下载失败：%s"""
                            else -> null
                        }
                    } ?: """下载失败：%s"""

            /** 下载失败：%s */
            @Composable
            fun `DownloadFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `DownloadFailed`.fmt(args) }

            /** 启动控制面板 */
            val `Launch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Launch Dashboard"""
                            `MLangTags`.ZH -> """启动控制面板"""
                            else -> null
                        }
                    } ?: """启动控制面板"""

            /** 启动控制面板 */
            @Composable fun `Launch`(vararg args: Any?) = FYTxtConfig.observe { `Launch`.fmt(args) }

            /** 全屏打开 Web UI */
            val `LaunchSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Open the Web UI in full-screen"""
                            `MLangTags`.ZH -> """全屏打开 Web UI"""
                            else -> null
                        }
                    } ?: """全屏打开 Web UI"""

            /** 全屏打开 Web UI */
            @Composable
            fun `LaunchSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `LaunchSummary`.fmt(args) }

            /** 请先下载面板 */
            val `LaunchDisabled`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Please download dashboard first"""
                            `MLangTags`.ZH -> """请先下载面板"""
                            else -> null
                        }
                    } ?: """请先下载面板"""

            /** 请先下载面板 */
            @Composable
            fun `LaunchDisabled`(vararg args: Any?) =
                FYTxtConfig.observe { `LaunchDisabled`.fmt(args) }
        }
    }

    object `NetworkSettings` {
        init {
            `MLangGroups`
        }

        /** 网络设置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Network Settings"""
                        `MLangTags`.ZH -> """网络设置"""
                        else -> null
                    }
                } ?: """网络设置"""

        /** 网络设置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Section` {
            init {
                `MLangGroups`
            }

            /** 代理模式 */
            val `VpnService`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Mode"""
                            `MLangTags`.ZH -> """代理模式"""
                            else -> null
                        }
                    } ?: """代理模式"""

            /** 代理模式 */
            @Composable
            fun `VpnService`(vararg args: Any?) = FYTxtConfig.observe { `VpnService`.fmt(args) }

            /** 服务配置 */
            val `VpnOptions`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Service Options"""
                            `MLangTags`.ZH -> """服务配置"""
                            else -> null
                        }
                    } ?: """服务配置"""

            /** 服务配置 */
            @Composable
            fun `VpnOptions`(vararg args: Any?) = FYTxtConfig.observe { `VpnOptions`.fmt(args) }

            /** 访问控制 */
            val `ProxyOptions`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Access Control"""
                            `MLangTags`.ZH -> """访问控制"""
                            else -> null
                        }
                    } ?: """访问控制"""

            /** 访问控制 */
            @Composable
            fun `ProxyOptions`(vararg args: Any?) = FYTxtConfig.observe { `ProxyOptions`.fmt(args) }

            /** 高级参数 */
            val `RootTunAdvanced`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Advanced Parameters"""
                            `MLangTags`.ZH -> """高级参数"""
                            else -> null
                        }
                    } ?: """高级参数"""

            /** 高级参数 */
            @Composable
            fun `RootTunAdvanced`(vararg args: Any?) =
                FYTxtConfig.observe { `RootTunAdvanced`.fmt(args) }
        }

        object `VpnService` {
            init {
                `MLangGroups`
            }

            /** 路由系统流量 */
            val `RouteTrafficTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Route System Traffic"""
                            `MLangTags`.ZH -> """路由系统流量"""
                            else -> null
                        }
                    } ?: """路由系统流量"""

            /** 路由系统流量 */
            @Composable
            fun `RouteTrafficTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `RouteTrafficTitle`.fmt(args) }

            /** 选择当前用于接管系统流量的代理模式 */
            val `RouteTrafficSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Select which proxy mode should take over system traffic"""
                            `MLangTags`.ZH -> """选择当前用于接管系统流量的代理模式"""
                            else -> null
                        }
                    } ?: """选择当前用于接管系统流量的代理模式"""

            /** 选择当前用于接管系统流量的代理模式 */
            @Composable
            fun `RouteTrafficSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `RouteTrafficSummary`.fmt(args) }

            /** 当前生效：%s；目标配置：%s */
            val `RouteTrafficEffective`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Effective now: %s; configured target: %s"""
                            `MLangTags`.ZH -> """当前生效：%s；目标配置：%s"""
                            else -> null
                        }
                    } ?: """当前生效：%s；目标配置：%s"""

            /** 当前生效：%s；目标配置：%s */
            @Composable
            fun `RouteTrafficEffective`(vararg args: Any?) =
                FYTxtConfig.observe { `RouteTrafficEffective`.fmt(args) }

            /** 正在串行切换：%s → %s */
            val `RouteTrafficApplying`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Applying serial switch: %s -> %s"""
                            `MLangTags`.ZH -> """正在串行切换：%s → %s"""
                            else -> null
                        }
                    } ?: """正在串行切换：%s → %s"""

            /** 正在串行切换：%s → %s */
            @Composable
            fun `RouteTrafficApplying`(vararg args: Any?) =
                FYTxtConfig.observe { `RouteTrafficApplying`.fmt(args) }

            /** VPN 模式 */
            val `VpnMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """VPN Mode"""
                            `MLangTags`.ZH -> """VPN 模式"""
                            else -> null
                        }
                    } ?: """VPN 模式"""

            /** VPN 模式 */
            @Composable
            fun `VpnMode`(vararg args: Any?) = FYTxtConfig.observe { `VpnMode`.fmt(args) }

            /** Root TUN */
            val `RootTunMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Root TUN"""
                            `MLangTags`.ZH -> """Root TUN"""
                            else -> null
                        }
                    } ?: """Root TUN"""

            /** Root TUN */
            @Composable
            fun `RootTunMode`(vararg args: Any?) = FYTxtConfig.observe { `RootTunMode`.fmt(args) }

            /** HTTP 系统代理 */
            val `SystemProxy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """HTTP System Proxy"""
                            `MLangTags`.ZH -> """HTTP 系统代理"""
                            else -> null
                        }
                    } ?: """HTTP 系统代理"""

            /** HTTP 系统代理 */
            @Composable
            fun `SystemProxy`(vararg args: Any?) = FYTxtConfig.observe { `SystemProxy`.fmt(args) }
        }

        object `HttpMode` {
            init {
                `MLangGroups`
            }

            /** 本地 HTTP 代理 */
            val `InfoTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Local HTTP Proxy"""
                            `MLangTags`.ZH -> """本地 HTTP 代理"""
                            else -> null
                        }
                    } ?: """本地 HTTP 代理"""

            /** 本地 HTTP 代理 */
            @Composable
            fun `InfoTitle`(vararg args: Any?) = FYTxtConfig.observe { `InfoTitle`.fmt(args) }

            /** 启动后在本机随机端口监听 HTTP 代理请求，需手动配置系统或应用代理地址。不创建 VPN 隧道，不拦截全局流量，仅为主动指向该端口的连接提供代理。 */
            val `InfoSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Listens on a random local port for HTTP proxy requests after startup. Requires manual proxy configuration in system or app settings. Does not create a VPN tunnel or intercept global traffic — only proxies connections explicitly directed to the listening port."""
                            `MLangTags`.ZH ->
                                """启动后在本机随机端口监听 HTTP 代理请求，需手动配置系统或应用代理地址。不创建 VPN 隧道，不拦截全局流量，仅为主动指向该端口的连接提供代理。"""
                            else -> null
                        }
                    }
                        ?: """启动后在本机随机端口监听 HTTP 代理请求，需手动配置系统或应用代理地址。不创建 VPN 隧道，不拦截全局流量，仅为主动指向该端口的连接提供代理。"""

            /** 启动后在本机随机端口监听 HTTP 代理请求，需手动配置系统或应用代理地址。不创建 VPN 隧道，不拦截全局流量，仅为主动指向该端口的连接提供代理。 */
            @Composable
            fun `InfoSummary`(vararg args: Any?) = FYTxtConfig.observe { `InfoSummary`.fmt(args) }
        }

        object `VpnOptions` {
            init {
                `MLangGroups`
            }

            /** 绕过私有网络 */
            val `BypassPrivateTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Bypass Private Networks"""
                            `MLangTags`.ZH -> """绕过私有网络"""
                            else -> null
                        }
                    } ?: """绕过私有网络"""

            /** 绕过私有网络 */
            @Composable
            fun `BypassPrivateTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `BypassPrivateTitle`.fmt(args) }

            /** 绕过私有网络和本地地址 */
            val `BypassPrivateSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Bypass private networks and local addresses"""
                            `MLangTags`.ZH -> """绕过私有网络和本地地址"""
                            else -> null
                        }
                    } ?: """绕过私有网络和本地地址"""

            /** 绕过私有网络和本地地址 */
            @Composable
            fun `BypassPrivateSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `BypassPrivateSummary`.fmt(args) }

            /** DNS 劫持 */
            val `DnsHijackTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS Hijack"""
                            `MLangTags`.ZH -> """DNS 劫持"""
                            else -> null
                        }
                    } ?: """DNS 劫持"""

            /** DNS 劫持 */
            @Composable
            fun `DnsHijackTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsHijackTitle`.fmt(args) }

            /** 将所有 DNS 请求重定向到 MonadBox */
            val `DnsHijackSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Redirect all DNS requests to MonadBox"""
                            `MLangTags`.ZH -> """将所有 DNS 请求重定向到 MonadBox"""
                            else -> null
                        }
                    } ?: """将所有 DNS 请求重定向到 MonadBox"""

            /** 将所有 DNS 请求重定向到 MonadBox */
            @Composable
            fun `DnsHijackSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsHijackSummary`.fmt(args) }

            /** 允许应用绕过 */
            val `AllowBypassTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allow App Bypass"""
                            `MLangTags`.ZH -> """允许应用绕过"""
                            else -> null
                        }
                    } ?: """允许应用绕过"""

            /** 允许应用绕过 */
            @Composable
            fun `AllowBypassTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AllowBypassTitle`.fmt(args) }

            /** 允许应用绕过 VPN */
            val `AllowBypassSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allow apps to bypass VPN"""
                            `MLangTags`.ZH -> """允许应用绕过 VPN"""
                            else -> null
                        }
                    } ?: """允许应用绕过 VPN"""

            /** 允许应用绕过 VPN */
            @Composable
            fun `AllowBypassSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AllowBypassSummary`.fmt(args) }

            /** 运行 IPv6 */
            val `EnableIpv6Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Enable IPv6"""
                            `MLangTags`.ZH -> """运行 IPv6"""
                            else -> null
                        }
                    } ?: """运行 IPv6"""

            /** 运行 IPv6 */
            @Composable
            fun `EnableIpv6Title`(vararg args: Any?) =
                FYTxtConfig.observe { `EnableIpv6Title`.fmt(args) }

            /** 允许通过 VPN 路由 IPv6 流量 */
            val `EnableIpv6Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allow IPv6 traffic through VPN"""
                            `MLangTags`.ZH -> """允许通过 VPN 路由 IPv6 流量"""
                            else -> null
                        }
                    } ?: """允许通过 VPN 路由 IPv6 流量"""

            /** 允许通过 VPN 路由 IPv6 流量 */
            @Composable
            fun `EnableIpv6Summary`(vararg args: Any?) =
                FYTxtConfig.observe { `EnableIpv6Summary`.fmt(args) }

            /** VPN 系统代理 */
            val `SystemProxyTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """VPN Built-in System Proxy"""
                            `MLangTags`.ZH -> """VPN 系统代理"""
                            else -> null
                        }
                    } ?: """VPN 系统代理"""

            /** VPN 系统代理 */
            @Composable
            fun `SystemProxyTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `SystemProxyTitle`.fmt(args) }

            /** 仅在 VPN 模式下，为未走 TUN 的应用设置 HTTP 代理 */
            val `SystemProxySummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Only in VPN mode, set an HTTP proxy for apps outside the TUN path"""
                            `MLangTags`.ZH -> """仅在 VPN 模式下，为未走 TUN 的应用设置 HTTP 代理"""
                            else -> null
                        }
                    } ?: """仅在 VPN 模式下，为未走 TUN 的应用设置 HTTP 代理"""

            /** 仅在 VPN 模式下，为未走 TUN 的应用设置 HTTP 代理 */
            @Composable
            fun `SystemProxySummary`(vararg args: Any?) =
                FYTxtConfig.observe { `SystemProxySummary`.fmt(args) }
        }

        object `ProxyOptions` {
            init {
                `MLangGroups`
            }

            /** TUN 协议栈 */
            val `TunStackTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """TUN Stack"""
                            `MLangTags`.ZH -> """TUN 协议栈"""
                            else -> null
                        }
                    } ?: """TUN 协议栈"""

            /** TUN 协议栈 */
            @Composable
            fun `TunStackTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `TunStackTitle`.fmt(args) }

            /** 系统 */
            val `TunStackSystem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """System"""
                            `MLangTags`.ZH -> """系统"""
                            else -> null
                        }
                    } ?: """系统"""

            /** 系统 */
            @Composable
            fun `TunStackSystem`(vararg args: Any?) =
                FYTxtConfig.observe { `TunStackSystem`.fmt(args) }

            /** GVisor */
            val `TunStackGvisor`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GVisor"""
                            `MLangTags`.ZH -> """GVisor"""
                            else -> null
                        }
                    } ?: """GVisor"""

            /** GVisor */
            @Composable
            fun `TunStackGvisor`(vararg args: Any?) =
                FYTxtConfig.observe { `TunStackGvisor`.fmt(args) }

            /** 混合 */
            val `TunStackMixed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Mixed"""
                            `MLangTags`.ZH -> """混合"""
                            else -> null
                        }
                    } ?: """混合"""

            /** 混合 */
            @Composable
            fun `TunStackMixed`(vararg args: Any?) =
                FYTxtConfig.observe { `TunStackMixed`.fmt(args) }

            /** 访问控制模式 */
            val `AccessControlModeTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Access Control Mode"""
                            `MLangTags`.ZH -> """访问控制模式"""
                            else -> null
                        }
                    } ?: """访问控制模式"""

            /** 访问控制模式 */
            @Composable
            fun `AccessControlModeTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AccessControlModeTitle`.fmt(args) }

            /** 允许远程资源使用 HTTP */
            val `AllowNonLocalhostHttpRemoteTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allow HTTP for Remote Resources"""
                            `MLangTags`.ZH -> """允许远程资源使用 HTTP"""
                            else -> null
                        }
                    } ?: """允许远程资源使用 HTTP"""

            /** 允许远程资源使用 HTTP */
            @Composable
            fun `AllowNonLocalhostHttpRemoteTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AllowNonLocalhostHttpRemoteTitle`.fmt(args) }

            /** 默认仅允许 localhost 使用 HTTP；开启后可允许非 localhost 的 HTTP 远程链接（存在中间人风险） */
            val `AllowNonLocalhostHttpRemoteSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """By default, HTTP is only allowed for localhost; enabling this allows non-localhost HTTP remote URLs (MITM risk)."""
                            `MLangTags`.ZH ->
                                """默认仅允许 localhost 使用 HTTP；开启后可允许非 localhost 的 HTTP 远程链接（存在中间人风险）"""
                            else -> null
                        }
                    } ?: """默认仅允许 localhost 使用 HTTP；开启后可允许非 localhost 的 HTTP 远程链接（存在中间人风险）"""

            /** 默认仅允许 localhost 使用 HTTP；开启后可允许非 localhost 的 HTTP 远程链接（存在中间人风险） */
            @Composable
            fun `AllowNonLocalhostHttpRemoteSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AllowNonLocalhostHttpRemoteSummary`.fmt(args) }

            /** 允许所有 */
            val `AllowAll`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allow All"""
                            `MLangTags`.ZH -> """允许所有"""
                            else -> null
                        }
                    } ?: """允许所有"""

            /** 允许所有 */
            @Composable
            fun `AllowAll`(vararg args: Any?) = FYTxtConfig.observe { `AllowAll`.fmt(args) }

            /** 允许选择 */
            val `AllowSelected`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allow Selected"""
                            `MLangTags`.ZH -> """允许选择"""
                            else -> null
                        }
                    } ?: """允许选择"""

            /** 允许选择 */
            @Composable
            fun `AllowSelected`(vararg args: Any?) =
                FYTxtConfig.observe { `AllowSelected`.fmt(args) }

            /** 拒绝选择 */
            val `RejectSelected`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Reject Selected"""
                            `MLangTags`.ZH -> """拒绝选择"""
                            else -> null
                        }
                    } ?: """拒绝选择"""

            /** 拒绝选择 */
            @Composable
            fun `RejectSelected`(vararg args: Any?) =
                FYTxtConfig.observe { `RejectSelected`.fmt(args) }

            /** 管理访问控制列表 */
            val `ManageAccessControlTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Manage Access Control"""
                            `MLangTags`.ZH -> """管理访问控制列表"""
                            else -> null
                        }
                    } ?: """管理访问控制列表"""

            /** 管理访问控制列表 */
            @Composable
            fun `ManageAccessControlTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `ManageAccessControlTitle`.fmt(args) }

            /** 为应用和域名配置访问控制规则 */
            val `ManageAccessControlSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Configure access control rules for apps and domains"""
                            `MLangTags`.ZH -> """为应用和域名配置访问控制规则"""
                            else -> null
                        }
                    } ?: """为应用和域名配置访问控制规则"""

            /** 为应用和域名配置访问控制规则 */
            @Composable
            fun `ManageAccessControlSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `ManageAccessControlSummary`.fmt(args) }
        }

        object `RootTun` {
            init {
                `MLangGroups`
            }

            /** 接口名称 */
            val `IfNameTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Interface Name"""
                            `MLangTags`.ZH -> """接口名称"""
                            else -> null
                        }
                    } ?: """接口名称"""

            /** 接口名称 */
            @Composable
            fun `IfNameTitle`(vararg args: Any?) = FYTxtConfig.observe { `IfNameTitle`.fmt(args) }

            /** RootTun 创建的虚拟网卡名 */
            val `IfNameSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Virtual interface name created by RootTun"""
                            `MLangTags`.ZH -> """RootTun 创建的虚拟网卡名"""
                            else -> null
                        }
                    } ?: """RootTun 创建的虚拟网卡名"""

            /** RootTun 创建的虚拟网卡名 */
            @Composable
            fun `IfNameSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `IfNameSummary`.fmt(args) }

            /** MTU */
            val `MtuTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """MTU"""
                            `MLangTags`.ZH -> """MTU"""
                            else -> null
                        }
                    } ?: """MTU"""

            /** MTU */
            @Composable
            fun `MtuTitle`(vararg args: Any?) = FYTxtConfig.observe { `MtuTitle`.fmt(args) }

            /** RootTun 链路的最大传输单元 */
            val `MtuSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Maximum transmission unit used by RootTun"""
                            `MLangTags`.ZH -> """RootTun 链路的最大传输单元"""
                            else -> null
                        }
                    } ?: """RootTun 链路的最大传输单元"""

            /** RootTun 链路的最大传输单元 */
            @Composable
            fun `MtuSummary`(vararg args: Any?) = FYTxtConfig.observe { `MtuSummary`.fmt(args) }

            /** Android 用户 */
            val `AndroidUsersTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Android Users"""
                            `MLangTags`.ZH -> """Android 用户"""
                            else -> null
                        }
                    } ?: """Android 用户"""

            /** Android 用户 */
            @Composable
            fun `AndroidUsersTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AndroidUsersTitle`.fmt(args) }

            /** 允许使用 RootTun 的 Android 用户 ID */
            val `AndroidUsersSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Android user IDs allowed to use RootTun"""
                            `MLangTags`.ZH -> """允许使用 RootTun 的 Android 用户 ID"""
                            else -> null
                        }
                    } ?: """允许使用 RootTun 的 Android 用户 ID"""

            /** 允许使用 RootTun 的 Android 用户 ID */
            @Composable
            fun `AndroidUsersSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AndroidUsersSummary`.fmt(args) }

            /** 0, 10 */
            val `AndroidUsersPlaceholder`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """0, 10"""
                            `MLangTags`.ZH -> """0, 10"""
                            else -> null
                        }
                    } ?: """0, 10"""

            /** 0, 10 */
            @Composable
            fun `AndroidUsersPlaceholder`(vararg args: Any?) =
                FYTxtConfig.observe { `AndroidUsersPlaceholder`.fmt(args) }

            /** 排除路由 */
            val `RouteExcludesTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Route Excludes"""
                            `MLangTags`.ZH -> """排除路由"""
                            else -> null
                        }
                    } ?: """排除路由"""

            /** 排除路由 */
            @Composable
            fun `RouteExcludesTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `RouteExcludesTitle`.fmt(args) }

            /** 不纳入 RootTun 路由的地址 */
            val `RouteExcludesSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Addresses excluded from RootTun routes"""
                            `MLangTags`.ZH -> """不纳入 RootTun 路由的地址"""
                            else -> null
                        }
                    } ?: """不纳入 RootTun 路由的地址"""

            /** 不纳入 RootTun 路由的地址 */
            @Composable
            fun `RouteExcludesSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `RouteExcludesSummary`.fmt(args) }

            /** 未设置排除地址 */
            val `RouteExcludesPlaceholder`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No excluded addresses"""
                            `MLangTags`.ZH -> """未设置排除地址"""
                            else -> null
                        }
                    } ?: """未设置排除地址"""

            /** 未设置排除地址 */
            @Composable
            fun `RouteExcludesPlaceholder`(vararg args: Any?) =
                FYTxtConfig.observe { `RouteExcludesPlaceholder`.fmt(args) }

            /** 自动路由 */
            val `AutoRouteTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Route"""
                            `MLangTags`.ZH -> """自动路由"""
                            else -> null
                        }
                    } ?: """自动路由"""

            /** 自动路由 */
            @Composable
            fun `AutoRouteTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoRouteTitle`.fmt(args) }

            /** 自动添加转发所需路由 */
            val `AutoRouteSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Automatically add required routes"""
                            `MLangTags`.ZH -> """自动添加转发所需路由"""
                            else -> null
                        }
                    } ?: """自动添加转发所需路由"""

            /** 自动添加转发所需路由 */
            @Composable
            fun `AutoRouteSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoRouteSummary`.fmt(args) }

            /** 严格路由 */
            val `StrictRouteTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Strict Route"""
                            `MLangTags`.ZH -> """严格路由"""
                            else -> null
                        }
                    } ?: """严格路由"""

            /** 严格路由 */
            @Composable
            fun `StrictRouteTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `StrictRouteTitle`.fmt(args) }

            /** 仅允许命中的流量走 RootTun */
            val `StrictRouteSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Only matched traffic is routed into RootTun"""
                            `MLangTags`.ZH -> """仅允许命中的流量走 RootTun"""
                            else -> null
                        }
                    } ?: """仅允许命中的流量走 RootTun"""

            /** 仅允许命中的流量走 RootTun */
            @Composable
            fun `StrictRouteSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `StrictRouteSummary`.fmt(args) }

            /** 自动重定向 */
            val `AutoRedirectTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Redirect"""
                            `MLangTags`.ZH -> """自动重定向"""
                            else -> null
                        }
                    } ?: """自动重定向"""

            /** 自动重定向 */
            @Composable
            fun `AutoRedirectTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoRedirectTitle`.fmt(args) }

            /** 自动启用 RootTun 所需的重定向规则 */
            val `AutoRedirectSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Automatically enable required redirect rules"""
                            `MLangTags`.ZH -> """自动启用 RootTun 所需的重定向规则"""
                            else -> null
                        }
                    } ?: """自动启用 RootTun 所需的重定向规则"""

            /** 自动启用 RootTun 所需的重定向规则 */
            @Composable
            fun `AutoRedirectSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoRedirectSummary`.fmt(args) }

            /** DNS 模式 */
            val `DnsModeTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS Mode"""
                            `MLangTags`.ZH -> """DNS 模式"""
                            else -> null
                        }
                    } ?: """DNS 模式"""

            /** DNS 模式 */
            @Composable
            fun `DnsModeTitle`(vararg args: Any?) = FYTxtConfig.observe { `DnsModeTitle`.fmt(args) }

            /** 选择 RedirHost 或 FakeIP */
            val `DnsModeSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Choose between RedirHost and FakeIP"""
                            `MLangTags`.ZH -> """选择 RedirHost 或 FakeIP"""
                            else -> null
                        }
                    } ?: """选择 RedirHost 或 FakeIP"""

            /** 选择 RedirHost 或 FakeIP */
            @Composable
            fun `DnsModeSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsModeSummary`.fmt(args) }

            /** RedirHost */
            val `DnsModeRedirHost`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """RedirHost"""
                            `MLangTags`.ZH -> """RedirHost"""
                            else -> null
                        }
                    } ?: """RedirHost"""

            /** RedirHost */
            @Composable
            fun `DnsModeRedirHost`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsModeRedirHost`.fmt(args) }

            /** FakeIP */
            val `DnsModeFakeIp`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """FakeIP"""
                            `MLangTags`.ZH -> """FakeIP"""
                            else -> null
                        }
                    } ?: """FakeIP"""

            /** FakeIP */
            @Composable
            fun `DnsModeFakeIp`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsModeFakeIp`.fmt(args) }

            /** FakeIP IPv4 地址段 */
            val `FakeIpRangeTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """FakeIP IPv4 Range"""
                            `MLangTags`.ZH -> """FakeIP IPv4 地址段"""
                            else -> null
                        }
                    } ?: """FakeIP IPv4 地址段"""

            /** FakeIP IPv4 地址段 */
            @Composable
            fun `FakeIpRangeTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `FakeIpRangeTitle`.fmt(args) }

            /** 仅在 FakeIP 模式下生效 */
            val `FakeIpRangeSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Only effective when DNS mode is FakeIP"""
                            `MLangTags`.ZH -> """仅在 FakeIP 模式下生效"""
                            else -> null
                        }
                    } ?: """仅在 FakeIP 模式下生效"""

            /** 仅在 FakeIP 模式下生效 */
            @Composable
            fun `FakeIpRangeSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `FakeIpRangeSummary`.fmt(args) }

            /** FakeIP IPv6 地址段 */
            val `FakeIpRange6Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """FakeIP IPv6 Range"""
                            `MLangTags`.ZH -> """FakeIP IPv6 地址段"""
                            else -> null
                        }
                    } ?: """FakeIP IPv6 地址段"""

            /** FakeIP IPv6 地址段 */
            @Composable
            fun `FakeIpRange6Title`(vararg args: Any?) =
                FYTxtConfig.observe { `FakeIpRange6Title`.fmt(args) }

            /** 仅在 FakeIP 模式下生效 */
            val `FakeIpRange6Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Only effective when DNS mode is FakeIP"""
                            `MLangTags`.ZH -> """仅在 FakeIP 模式下生效"""
                            else -> null
                        }
                    } ?: """仅在 FakeIP 模式下生效"""

            /** 仅在 FakeIP 模式下生效 */
            @Composable
            fun `FakeIpRange6Summary`(vararg args: Any?) =
                FYTxtConfig.observe { `FakeIpRange6Summary`.fmt(args) }
        }

        object `Error` {
            init {
                `MLangGroups`
            }

            /** VPN 权限被拒绝 */
            val `VpnDenied`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """VPN permission denied"""
                            `MLangTags`.ZH -> """VPN 权限被拒绝"""
                            else -> null
                        }
                    } ?: """VPN 权限被拒绝"""

            /** VPN 权限被拒绝 */
            @Composable
            fun `VpnDenied`(vararg args: Any?) = FYTxtConfig.observe { `VpnDenied`.fmt(args) }
        }
    }

    object `Onboarding` {
        init {
            `MLangGroups`
        }

        object `Navigation` {
            init {
                `MLangGroups`
            }

            /** 返回 */
            val `Back`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Back"""
                            `MLangTags`.ZH -> """返回"""
                            else -> null
                        }
                    } ?: """返回"""

            /** 返回 */
            @Composable fun `Back`(vararg args: Any?) = FYTxtConfig.observe { `Back`.fmt(args) }

            /** 下一步 */
            val `Next`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Next"""
                            `MLangTags`.ZH -> """下一步"""
                            else -> null
                        }
                    } ?: """下一步"""

            /** 下一步 */
            @Composable fun `Next`(vararg args: Any?) = FYTxtConfig.observe { `Next`.fmt(args) }

            /** 开始设置 */
            val `Start`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Start Setup"""
                            `MLangTags`.ZH -> """开始设置"""
                            else -> null
                        }
                    } ?: """开始设置"""

            /** 开始设置 */
            @Composable fun `Start`(vararg args: Any?) = FYTxtConfig.observe { `Start`.fmt(args) }

            /** 进入应用 */
            val `Enter`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Enter App"""
                            `MLangTags`.ZH -> """进入应用"""
                            else -> null
                        }
                    } ?: """进入应用"""

            /** 进入应用 */
            @Composable fun `Enter`(vararg args: Any?) = FYTxtConfig.observe { `Enter`.fmt(args) }
        }

        object `Permission` {
            init {
                `MLangGroups`
            }

            /** 确认运行权限 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Confirm Runtime Access"""
                            `MLangTags`.ZH -> """确认运行权限"""
                            else -> null
                        }
                    } ?: """确认运行权限"""

            /** 确认运行权限 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 权限会影响通知和分应用代理等功能 */
            val `Subtitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Permissions affect notifications and per-app proxy features"""
                            `MLangTags`.ZH -> """权限会影响通知和分应用代理等功能"""
                            else -> null
                        }
                    } ?: """权限会影响通知和分应用代理等功能"""

            /** 权限会影响通知和分应用代理等功能 */
            @Composable
            fun `Subtitle`(vararg args: Any?) = FYTxtConfig.observe { `Subtitle`.fmt(args) }

            object `Common` {
                init {
                    `MLangGroups`
                }

                /** 已授权 */
                val `Granted`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Granted"""
                                `MLangTags`.ZH -> """已授权"""
                                else -> null
                            }
                        } ?: """已授权"""

                /** 已授权 */
                @Composable
                fun `Granted`(vararg args: Any?) = FYTxtConfig.observe { `Granted`.fmt(args) }
            }

            object `Notification` {
                init {
                    `MLangGroups`
                }

                /** 通知权限 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Notification Permission"""
                                `MLangTags`.ZH -> """通知权限"""
                                else -> null
                            }
                        } ?: """通知权限"""

                /** 通知权限 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 用于显示连接状态和流量通知 */
                val `SummaryNeed`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Show connection status and traffic notifications"""
                                `MLangTags`.ZH -> """用于显示连接状态和流量通知"""
                                else -> null
                            }
                        } ?: """用于显示连接状态和流量通知"""

                /** 用于显示连接状态和流量通知 */
                @Composable
                fun `SummaryNeed`(vararg args: Any?) =
                    FYTxtConfig.observe { `SummaryNeed`.fmt(args) }

                /** 当前系统无需额外授权 */
                val `SummaryNotRequired`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Not required on your Android version"""
                                `MLangTags`.ZH -> """当前系统无需额外授权"""
                                else -> null
                            }
                        } ?: """当前系统无需额外授权"""

                /** 当前系统无需额外授权 */
                @Composable
                fun `SummaryNotRequired`(vararg args: Any?) =
                    FYTxtConfig.observe { `SummaryNotRequired`.fmt(args) }
            }

            object `AppList` {
                init {
                    `MLangGroups`
                }

                /** 应用列表权限 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """App List Permission"""
                                `MLangTags`.ZH -> """应用列表权限"""
                                else -> null
                            }
                        } ?: """应用列表权限"""

                /** 应用列表权限 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 用于分应用代理等功能 */
                val `SummaryNeed`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Required for per-app proxy features"""
                                `MLangTags`.ZH -> """用于分应用代理等功能"""
                                else -> null
                            }
                        } ?: """用于分应用代理等功能"""

                /** 用于分应用代理等功能 */
                @Composable
                fun `SummaryNeed`(vararg args: Any?) =
                    FYTxtConfig.observe { `SummaryNeed`.fmt(args) }

                /** 当前系统无需额外授权 */
                val `SummaryNotRequired`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Not required on your Android version"""
                                `MLangTags`.ZH -> """当前系统无需额外授权"""
                                else -> null
                            }
                        } ?: """当前系统无需额外授权"""

                /** 当前系统无需额外授权 */
                @Composable
                fun `SummaryNotRequired`(vararg args: Any?) =
                    FYTxtConfig.observe { `SummaryNotRequired`.fmt(args) }
            }
        }

        object `Privacy` {
            init {
                `MLangGroups`
            }

            /** 确认隐私说明 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Confirm Privacy Notice"""
                            `MLangTags`.ZH -> """确认隐私说明"""
                            else -> null
                        }
                    } ?: """确认隐私说明"""

            /** 确认隐私说明 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 阅读并同意隐私说明后方可继续 */
            val `Subtitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Review and accept the privacy notice to continue"""
                            `MLangTags`.ZH -> """阅读并同意隐私说明后方可继续"""
                            else -> null
                        }
                    } ?: """阅读并同意隐私说明后方可继续"""

            /** 阅读并同意隐私说明后方可继续 */
            @Composable
            fun `Subtitle`(vararg args: Any?) = FYTxtConfig.observe { `Subtitle`.fmt(args) }

            /** 在开始使用 MonadBox 前，请先阅读并同意隐私说明。 */
            val `RichTextLead`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """You need to review and accept the privacy notice before using MonadBox."""
                            `MLangTags`.ZH -> """在开始使用 MonadBox 前，请先阅读并同意隐私说明。"""
                            else -> null
                        }
                    } ?: """在开始使用 MonadBox 前，请先阅读并同意隐私说明。"""

            /** 在开始使用 MonadBox 前，请先阅读并同意隐私说明。 */
            @Composable
            fun `RichTextLead`(vararg args: Any?) = FYTxtConfig.observe { `RichTextLead`.fmt(args) }

            /** 继续前请先阅读 */
            val `RichTextPrefix`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Before continuing, please review """
                            `MLangTags`.ZH -> """继续前请先阅读 """
                            else -> null
                        }
                    } ?: """继续前请先阅读 """

            /** 继续前请先阅读 */
            @Composable
            fun `RichTextPrefix`(vararg args: Any?) =
                FYTxtConfig.observe { `RichTextPrefix`.fmt(args) }

            /** 。 */
            val `RichTextSuffix`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """."""
                            `MLangTags`.ZH -> """。"""
                            else -> null
                        }
                    } ?: """。"""

            /** 。 */
            @Composable
            fun `RichTextSuffix`(vararg args: Any?) =
                FYTxtConfig.observe { `RichTextSuffix`.fmt(args) }

            /** 《隐私说明》 */
            val `PolicyLink`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Privacy Notice"""
                            `MLangTags`.ZH -> """《隐私说明》"""
                            else -> null
                        }
                    } ?: """《隐私说明》"""

            /** 《隐私说明》 */
            @Composable
            fun `PolicyLink`(vararg args: Any?) = FYTxtConfig.observe { `PolicyLink`.fmt(args) }

            object `Privacy` {
                init {
                    `MLangGroups`
                }

                /** 隐私说明 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Privacy Notice"""
                                `MLangTags`.ZH -> """隐私说明"""
                                else -> null
                            }
                        } ?: """隐私说明"""

                /** 隐私说明 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }
            }

            object `Accept` {
                init {
                    `MLangGroups`
                }

                /** 我已阅读并同意隐私说明 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """I have read and agree to the privacy notice"""
                                `MLangTags`.ZH -> """我已阅读并同意隐私说明"""
                                else -> null
                            }
                        } ?: """我已阅读并同意隐私说明"""

                /** 我已阅读并同意隐私说明 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }
            }
        }

        object `Personalize` {
            init {
                `MLangGroups`
            }

            /** 调整界面风格 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Tune the Interface"""
                            `MLangTags`.ZH -> """调整界面风格"""
                            else -> null
                        }
                    } ?: """调整界面风格"""

            /** 调整界面风格 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 主题模式和主色可随时在设置中修改 */
            val `Subtitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Theme mode and accent color can be changed anytime in settings"""
                            `MLangTags`.ZH -> """主题模式和主色可随时在设置中修改"""
                            else -> null
                        }
                    } ?: """主题模式和主色可随时在设置中修改"""

            /** 主题模式和主色可随时在设置中修改 */
            @Composable
            fun `Subtitle`(vararg args: Any?) = FYTxtConfig.observe { `Subtitle`.fmt(args) }
        }

        object `Finish` {
            init {
                `MLangGroups`
            }

            /** 准备完成 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Ready to Go"""
                            `MLangTags`.ZH -> """准备完成"""
                            else -> null
                        }
                    } ?: """准备完成"""

            /** 准备完成 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 基础设置已就绪即将进入主页 */
            val `Subtitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Setup complete, entering the main app"""
                            `MLangTags`.ZH -> """基础设置已就绪即将进入主页"""
                            else -> null
                        }
                    } ?: """基础设置已就绪即将进入主页"""

            /** 基础设置已就绪即将进入主页 */
            @Composable
            fun `Subtitle`(vararg args: Any?) = FYTxtConfig.observe { `Subtitle`.fmt(args) }
        }

        object `Project` {
            init {
                `MLangGroups`
            }

            object `Github` {
                init {
                    `MLangGroups`
                }

                /** GitHub 仓库 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """GitHub Repo"""
                                `MLangTags`.ZH -> """GitHub 仓库"""
                                else -> null
                            }
                        } ?: """GitHub 仓库"""

                /** GitHub 仓库 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** https://github.com/NomadBoxLab/NomadBox */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """https://github.com/NomadBoxLab/NomadBox"""
                                `MLangTags`.ZH -> """https://github.com/NomadBoxLab/NomadBox"""
                                else -> null
                            }
                        } ?: """https://github.com/NomadBoxLab/NomadBox"""

                /** https://github.com/NomadBoxLab/NomadBox */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }
        }

        object `Sheet` {
            init {
                `MLangGroups`
            }

            /** 隐私说明 */
            val `PrivacyPolicyTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Privacy Notice"""
                            `MLangTags`.ZH -> """隐私说明"""
                            else -> null
                        }
                    } ?: """隐私说明"""

            /** 隐私说明 */
            @Composable
            fun `PrivacyPolicyTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `PrivacyPolicyTitle`.fmt(args) }

            /** 无法加载协议内容 */
            val `LoadFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to load policy content"""
                            `MLangTags`.ZH -> """无法加载协议内容"""
                            else -> null
                        }
                    } ?: """无法加载协议内容"""

            /** 无法加载协议内容 */
            @Composable
            fun `LoadFailed`(vararg args: Any?) = FYTxtConfig.observe { `LoadFailed`.fmt(args) }
        }
    }

    object `OpenSourceLicenses` {
        init {
            `MLangGroups`
        }

        /** 开源许可证 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Open Source Licenses"""
                        `MLangTags`.ZH -> """开源许可证"""
                        else -> null
                    }
                } ?: """开源许可证"""

        /** 开源许可证 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `LicenseSheet` {
            init {
                `MLangGroups`
            }

            /** 暂无许可证内容 */
            val `NoContent`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No license content"""
                            `MLangTags`.ZH -> """暂无许可证内容"""
                            else -> null
                        }
                    } ?: """暂无许可证内容"""

            /** 暂无许可证内容 */
            @Composable
            fun `NoContent`(vararg args: Any?) = FYTxtConfig.observe { `NoContent`.fmt(args) }
        }
    }

    object `Override` {
        init {
            `MLangGroups`
        }

        /** 覆写配置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Override Configs"""
                        `MLangTags`.ZH -> """覆写配置"""
                        else -> null
                    }
                } ?: """覆写配置"""

        /** 覆写配置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Action` {
            init {
                `MLangGroups`
            }

            /** 创建配置 */
            val `Create`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Create Config"""
                            `MLangTags`.ZH -> """创建配置"""
                            else -> null
                        }
                    } ?: """创建配置"""

            /** 创建配置 */
            @Composable fun `Create`(vararg args: Any?) = FYTxtConfig.observe { `Create`.fmt(args) }

            /** 新建配置 */
            val `New`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """New Config"""
                            `MLangTags`.ZH -> """新建配置"""
                            else -> null
                        }
                    } ?: """新建配置"""

            /** 新建配置 */
            @Composable fun `New`(vararg args: Any?) = FYTxtConfig.observe { `New`.fmt(args) }

            /** 导入配置/规则 */
            val `Import`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import Config/Rules"""
                            `MLangTags`.ZH -> """导入配置/规则"""
                            else -> null
                        }
                    } ?: """导入配置/规则"""

            /** 导入配置/规则 */
            @Composable fun `Import`(vararg args: Any?) = FYTxtConfig.observe { `Import`.fmt(args) }

            /** 导入配置/规则文件 */
            val `ImportFile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import Config/Rules File"""
                            `MLangTags`.ZH -> """导入配置/规则文件"""
                            else -> null
                        }
                    } ?: """导入配置/规则文件"""

            /** 导入配置/规则文件 */
            @Composable
            fun `ImportFile`(vararg args: Any?) = FYTxtConfig.observe { `ImportFile`.fmt(args) }

            /** 从 URL 导入配置/规则 */
            val `ImportFromUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import Config/Rules from URL"""
                            `MLangTags`.ZH -> """从 URL 导入配置/规则"""
                            else -> null
                        }
                    } ?: """从 URL 导入配置/规则"""

            /** 从 URL 导入配置/规则 */
            @Composable
            fun `ImportFromUrl`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportFromUrl`.fmt(args) }

            /** 导入 Surge/Loon 插件规则 */
            val `ImportSurge`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import Surge/Loon Plugin Rules"""
                            `MLangTags`.ZH -> """导入 Surge/Loon 插件规则"""
                            else -> null
                        }
                    } ?: """导入 Surge/Loon 插件规则"""

            /** 导入 Surge/Loon 插件规则 */
            @Composable
            fun `ImportSurge`(vararg args: Any?) = FYTxtConfig.observe { `ImportSurge`.fmt(args) }

            /** 从 URL 导入配置 */
            val `ImportJsonUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import Config from URL"""
                            `MLangTags`.ZH -> """从 URL 导入配置"""
                            else -> null
                        }
                    } ?: """从 URL 导入配置"""

            /** 从 URL 导入配置 */
            @Composable
            fun `ImportJsonUrl`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportJsonUrl`.fmt(args) }

            /** 从 URL 导入 Surge 插件规则 */
            val `ImportSurgeUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import Surge Plugin Rules from URL"""
                            `MLangTags`.ZH -> """从 URL 导入 Surge 插件规则"""
                            else -> null
                        }
                    } ?: """从 URL 导入 Surge 插件规则"""

            /** 从 URL 导入 Surge 插件规则 */
            @Composable
            fun `ImportSurgeUrl`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportSurgeUrl`.fmt(args) }
        }

        object `Empty` {
            init {
                `MLangGroups`
            }

            /** 暂无覆写配置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No override configs"""
                            `MLangTags`.ZH -> """暂无覆写配置"""
                            else -> null
                        }
                    } ?: """暂无覆写配置"""

            /** 暂无覆写配置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 点击下方按钮创建新配置， 或导入规则（JSON / Surge / Loon） */
            val `Hint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Click the button below to create a new config, or import JSON / Surge / Loon rules"""
                            `MLangTags`.ZH ->
                                """点击下方按钮创建新配置，
或导入规则（JSON / Surge / Loon）"""
                            else -> null
                        }
                    }
                        ?: """点击下方按钮创建新配置，
或导入规则（JSON / Surge / Loon）"""

            /** 点击下方按钮创建新配置， 或导入规则（JSON / Surge / Loon） */
            @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }
        }

        object `Status` {
            init {
                `MLangGroups`
            }

            /** 使用中 */
            val `InUse`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """In Use"""
                            `MLangTags`.ZH -> """使用中"""
                            else -> null
                        }
                    } ?: """使用中"""

            /** 使用中 */
            @Composable fun `InUse`(vararg args: Any?) = FYTxtConfig.observe { `InUse`.fmt(args) }

            /** 未使用 */
            val `NotInUse`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Not in Use"""
                            `MLangTags`.ZH -> """未使用"""
                            else -> null
                        }
                    } ?: """未使用"""

            /** 未使用 */
            @Composable
            fun `NotInUse`(vararg args: Any?) = FYTxtConfig.observe { `NotInUse`.fmt(args) }
        }

        object `Card` {
            init {
                `MLangGroups`
            }

            /** 复制配置 */
            val `Copy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Copy Config"""
                            `MLangTags`.ZH -> """复制配置"""
                            else -> null
                        }
                    } ?: """复制配置"""

            /** 复制配置 */
            @Composable fun `Copy`(vararg args: Any?) = FYTxtConfig.observe { `Copy`.fmt(args) }

            /** 导出配置 */
            val `Export`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Export Config"""
                            `MLangTags`.ZH -> """导出配置"""
                            else -> null
                        }
                    } ?: """导出配置"""

            /** 导出配置 */
            @Composable fun `Export`(vararg args: Any?) = FYTxtConfig.observe { `Export`.fmt(args) }

            /** 编辑配置 */
            val `Edit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Config"""
                            `MLangTags`.ZH -> """编辑配置"""
                            else -> null
                        }
                    } ?: """编辑配置"""

            /** 编辑配置 */
            @Composable fun `Edit`(vararg args: Any?) = FYTxtConfig.observe { `Edit`.fmt(args) }

            /** 删除配置 */
            val `Delete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete Config"""
                            `MLangTags`.ZH -> """删除配置"""
                            else -> null
                        }
                    } ?: """删除配置"""

            /** 删除配置 */
            @Composable fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }

            /** 编辑 */
            val `EditButton`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit"""
                            `MLangTags`.ZH -> """编辑"""
                            else -> null
                        }
                    } ?: """编辑"""

            /** 编辑 */
            @Composable
            fun `EditButton`(vararg args: Any?) = FYTxtConfig.observe { `EditButton`.fmt(args) }

            /** 删除 */
            val `DeleteButton`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete"""
                            `MLangTags`.ZH -> """删除"""
                            else -> null
                        }
                    } ?: """删除"""

            /** 删除 */
            @Composable
            fun `DeleteButton`(vararg args: Any?) = FYTxtConfig.observe { `DeleteButton`.fmt(args) }

            /** 未填写描述 */
            val `NoDescription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No description"""
                            `MLangTags`.ZH -> """未填写描述"""
                            else -> null
                        }
                    } ?: """未填写描述"""

            /** 未填写描述 */
            @Composable
            fun `NoDescription`(vararg args: Any?) =
                FYTxtConfig.observe { `NoDescription`.fmt(args) }
        }

        object `Import` {
            init {
                `MLangGroups`
            }

            /** 无法读取导入文件 */
            val `ReadError`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cannot read import file"""
                            `MLangTags`.ZH -> """无法读取导入文件"""
                            else -> null
                        }
                    } ?: """无法读取导入文件"""

            /** 无法读取导入文件 */
            @Composable
            fun `ReadError`(vararg args: Any?) = FYTxtConfig.observe { `ReadError`.fmt(args) }

            /** 已从 %s 导入 %d 个配置 */
            val `Success`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Imported %d configs from %s"""
                            `MLangTags`.ZH -> """已从 %s 导入 %d 个配置"""
                            else -> null
                        }
                    } ?: """已从 %s 导入 %d 个配置"""

            /** 已从 %s 导入 %d 个配置 */
            @Composable
            fun `Success`(vararg args: Any?) = FYTxtConfig.observe { `Success`.fmt(args) }

            /** 已导入 %d 个配置 */
            val `SuccessDefault`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Imported %d configs"""
                            `MLangTags`.ZH -> """已导入 %d 个配置"""
                            else -> null
                        }
                    } ?: """已导入 %d 个配置"""

            /** 已导入 %d 个配置 */
            @Composable
            fun `SuccessDefault`(vararg args: Any?) =
                FYTxtConfig.observe { `SuccessDefault`.fmt(args) }

            /** 导入失败: %s */
            val `Failed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import failed: %s"""
                            `MLangTags`.ZH -> """导入失败: %s"""
                            else -> null
                        }
                    } ?: """导入失败: %s"""

            /** 导入失败: %s */
            @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

            /** 读取文件失败: %s */
            val `FileError`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to read file: %s"""
                            `MLangTags`.ZH -> """读取文件失败: %s"""
                            else -> null
                        }
                    } ?: """读取文件失败: %s"""

            /** 读取文件失败: %s */
            @Composable
            fun `FileError`(vararg args: Any?) = FYTxtConfig.observe { `FileError`.fmt(args) }

            /** 已从 %s 导入 %d 条插件规则 */
            val `SurgeSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Imported %d plugin rules from %s"""
                            `MLangTags`.ZH -> """已从 %s 导入 %d 条插件规则"""
                            else -> null
                        }
                    } ?: """已从 %s 导入 %d 条插件规则"""

            /** 已从 %s 导入 %d 条插件规则 */
            @Composable
            fun `SurgeSuccess`(vararg args: Any?) = FYTxtConfig.observe { `SurgeSuccess`.fmt(args) }

            /** 已导入 %d 条插件规则 */
            val `SurgeSuccessDefault`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Imported %d plugin rules"""
                            `MLangTags`.ZH -> """已导入 %d 条插件规则"""
                            else -> null
                        }
                    } ?: """已导入 %d 条插件规则"""

            /** 已导入 %d 条插件规则 */
            @Composable
            fun `SurgeSuccessDefault`(vararg args: Any?) =
                FYTxtConfig.observe { `SurgeSuccessDefault`.fmt(args) }

            /** 未在 [Rule] 节中找到可用规则 */
            val `SurgeNoRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No usable rules found in [Rule] section"""
                            `MLangTags`.ZH -> """未在 [Rule] 节中找到可用规则"""
                            else -> null
                        }
                    } ?: """未在 [Rule] 节中找到可用规则"""

            /** 未在 [Rule] 节中找到可用规则 */
            @Composable
            fun `SurgeNoRules`(vararg args: Any?) = FYTxtConfig.observe { `SurgeNoRules`.fmt(args) }

            /** 从 Surge 插件导入的规则 */
            val `SurgeImportDescription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rules imported from Surge plugin"""
                            `MLangTags`.ZH -> """从 Surge 插件导入的规则"""
                            else -> null
                        }
                    } ?: """从 Surge 插件导入的规则"""

            /** 从 Surge 插件导入的规则 */
            @Composable
            fun `SurgeImportDescription`(vararg args: Any?) =
                FYTxtConfig.observe { `SurgeImportDescription`.fmt(args) }

            /** 未在 [Rule] 节中找到可用规则 */
            val `PluginNoRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No usable rules found in [Rule] section"""
                            `MLangTags`.ZH -> """未在 [Rule] 节中找到可用规则"""
                            else -> null
                        }
                    } ?: """未在 [Rule] 节中找到可用规则"""

            /** 未在 [Rule] 节中找到可用规则 */
            @Composable
            fun `PluginNoRules`(vararg args: Any?) =
                FYTxtConfig.observe { `PluginNoRules`.fmt(args) }

            /** 从 Surge/Loon 插件导入的规则 */
            val `PluginImportDescription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rules imported from Surge/Loon plugin"""
                            `MLangTags`.ZH -> """从 Surge/Loon 插件导入的规则"""
                            else -> null
                        }
                    } ?: """从 Surge/Loon 插件导入的规则"""

            /** 从 Surge/Loon 插件导入的规则 */
            @Composable
            fun `PluginImportDescription`(vararg args: Any?) =
                FYTxtConfig.observe { `PluginImportDescription`.fmt(args) }

            /** 未识别到可导入内容，请检查是否为 JSON 或含 [Rule] 节的插件 */
            val `AutoDetectFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """No importable content found. Expect JSON or a plugin with [Rule] section"""
                            `MLangTags`.ZH -> """未识别到可导入内容，请检查是否为 JSON 或含 [Rule] 节的插件"""
                            else -> null
                        }
                    } ?: """未识别到可导入内容，请检查是否为 JSON 或含 [Rule] 节的插件"""

            /** 未识别到可导入内容，请检查是否为 JSON 或含 [Rule] 节的插件 */
            @Composable
            fun `AutoDetectFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoDetectFailed`.fmt(args) }

            /** 远程 URL */
            val `UrlLabel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Remote URL"""
                            `MLangTags`.ZH -> """远程 URL"""
                            else -> null
                        }
                    } ?: """远程 URL"""

            /** 远程 URL */
            @Composable
            fun `UrlLabel`(vararg args: Any?) = FYTxtConfig.observe { `UrlLabel`.fmt(args) }

            /** 正在下载，请稍候… */
            val `UrlDownloading`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Downloading, please wait…"""
                            `MLangTags`.ZH -> """正在下载，请稍候…"""
                            else -> null
                        }
                    } ?: """正在下载，请稍候…"""

            /** 正在下载，请稍候… */
            @Composable
            fun `UrlDownloading`(vararg args: Any?) =
                FYTxtConfig.observe { `UrlDownloading`.fmt(args) }

            /** 仅支持 http:// 或 https:// 链接 */
            val `UrlInvalidScheme`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Only http:// or https:// URLs are supported"""
                            `MLangTags`.ZH -> """仅支持 http:// 或 https:// 链接"""
                            else -> null
                        }
                    } ?: """仅支持 http:// 或 https:// 链接"""

            /** 仅支持 http:// 或 https:// 链接 */
            @Composable
            fun `UrlInvalidScheme`(vararg args: Any?) =
                FYTxtConfig.observe { `UrlInvalidScheme`.fmt(args) }

            /** 仅允许 https:// 链接（localhost 调试地址除外） */
            val `UrlHttpsRequired`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Only https:// URLs are allowed (except localhost for debugging)"""
                            `MLangTags`.ZH -> """仅允许 https:// 链接（localhost 调试地址除外）"""
                            else -> null
                        }
                    } ?: """仅允许 https:// 链接（localhost 调试地址除外）"""

            /** 仅允许 https:// 链接（localhost 调试地址除外） */
            @Composable
            fun `UrlHttpsRequired`(vararg args: Any?) =
                FYTxtConfig.observe { `UrlHttpsRequired`.fmt(args) }

            /** 请求失败：HTTP %d */
            val `UrlHttpError`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Request failed: HTTP %d"""
                            `MLangTags`.ZH -> """请求失败：HTTP %d"""
                            else -> null
                        }
                    } ?: """请求失败：HTTP %d"""

            /** 请求失败：HTTP %d */
            @Composable
            fun `UrlHttpError`(vararg args: Any?) = FYTxtConfig.observe { `UrlHttpError`.fmt(args) }

            /** 远程内容超过 %dMB 大小限制 */
            val `UrlContentTooLarge`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Remote content exceeds %dMB size limit"""
                            `MLangTags`.ZH -> """远程内容超过 %dMB 大小限制"""
                            else -> null
                        }
                    } ?: """远程内容超过 %dMB 大小限制"""

            /** 远程内容超过 %dMB 大小限制 */
            @Composable
            fun `UrlContentTooLarge`(vararg args: Any?) =
                FYTxtConfig.observe { `UrlContentTooLarge`.fmt(args) }

            /** 重定向地址无效，无法继续下载 */
            val `UrlRedirectInvalid`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Invalid redirect target, download aborted"""
                            `MLangTags`.ZH -> """重定向地址无效，无法继续下载"""
                            else -> null
                        }
                    } ?: """重定向地址无效，无法继续下载"""

            /** 重定向地址无效，无法继续下载 */
            @Composable
            fun `UrlRedirectInvalid`(vararg args: Any?) =
                FYTxtConfig.observe { `UrlRedirectInvalid`.fmt(args) }

            /** 重定向次数过多，已停止下载 */
            val `UrlTooManyRedirects`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Too many redirects, download aborted"""
                            `MLangTags`.ZH -> """重定向次数过多，已停止下载"""
                            else -> null
                        }
                    } ?: """重定向次数过多，已停止下载"""

            /** 重定向次数过多，已停止下载 */
            @Composable
            fun `UrlTooManyRedirects`(vararg args: Any?) =
                FYTxtConfig.observe { `UrlTooManyRedirects`.fmt(args) }

            object `UrlSheet` {
                init {
                    `MLangGroups`
                }

                /** 从 URL 自动导入配置/规则 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Auto Import Config/Rules from URL"""
                                `MLangTags`.ZH -> """从 URL 自动导入配置/规则"""
                                else -> null
                            }
                        } ?: """从 URL 自动导入配置/规则"""

                /** 从 URL 自动导入配置/规则 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 从 URL 导入配置 */
                val `JsonTitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Import Config from URL"""
                                `MLangTags`.ZH -> """从 URL 导入配置"""
                                else -> null
                            }
                        } ?: """从 URL 导入配置"""

                /** 从 URL 导入配置 */
                @Composable
                fun `JsonTitle`(vararg args: Any?) = FYTxtConfig.observe { `JsonTitle`.fmt(args) }

                /** 从 URL 导入 Surge 插件规则 */
                val `SurgeTitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Import Surge Plugin Rules from URL"""
                                `MLangTags`.ZH -> """从 URL 导入 Surge 插件规则"""
                                else -> null
                            }
                        } ?: """从 URL 导入 Surge 插件规则"""

                /** 从 URL 导入 Surge 插件规则 */
                @Composable
                fun `SurgeTitle`(vararg args: Any?) = FYTxtConfig.observe { `SurgeTitle`.fmt(args) }
            }
        }

        object `Export` {
            init {
                `MLangGroups`
            }

            /** 导出失败：%s */
            val `Failed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Export failed: %s"""
                            `MLangTags`.ZH -> """导出失败：%s"""
                            else -> null
                        }
                    } ?: """导出失败：%s"""

            /** 导出失败：%s */
            @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

            /** 已导出配置：%s */
            val `Success`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Exported config: %s"""
                            `MLangTags`.ZH -> """已导出配置：%s"""
                            else -> null
                        }
                    } ?: """已导出配置：%s"""

            /** 已导出配置：%s */
            @Composable
            fun `Success`(vararg args: Any?) = FYTxtConfig.observe { `Success`.fmt(args) }
        }

        object `Dialog` {
            init {
                `MLangGroups`
            }

            object `Create` {
                init {
                    `MLangGroups`
                }

                /** 添加配置 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Add Config"""
                                `MLangTags`.ZH -> """添加配置"""
                                else -> null
                            }
                        } ?: """添加配置"""

                /** 添加配置 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 配置名称 */
                val `Name`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Config Name"""
                                `MLangTags`.ZH -> """配置名称"""
                                else -> null
                            }
                        } ?: """配置名称"""

                /** 配置名称 */
                @Composable fun `Name`(vararg args: Any?) = FYTxtConfig.observe { `Name`.fmt(args) }

                /** 配置描述 */
                val `Description`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Config Description"""
                                `MLangTags`.ZH -> """配置描述"""
                                else -> null
                            }
                        } ?: """配置描述"""

                /** 配置描述 */
                @Composable
                fun `Description`(vararg args: Any?) =
                    FYTxtConfig.observe { `Description`.fmt(args) }

                /** 自动识别 JSON 配置，或 Surge/Loon 插件中的 [Rule] 规则 */
                val `ImportHint`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Auto-detect a JSON config, or extract [Rule] entries from a Surge/Loon plugin"""
                                `MLangTags`.ZH -> """自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则"""
                                else -> null
                            }
                        } ?: """自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则"""

                /** 自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则 */
                @Composable
                fun `ImportHint`(vararg args: Any?) = FYTxtConfig.observe { `ImportHint`.fmt(args) }

                /** 输入远程 URL，自动识别 JSON 或 Surge/Loon 插件规则 */
                val `ImportFromUrlHint`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Enter a remote URL to auto-detect a JSON config, or extract [Rule] entries from a Surge/Loon plugin"""
                                `MLangTags`.ZH ->
                                    """输入远程 URL；可自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则"""
                                else -> null
                            }
                        } ?: """输入远程 URL；可自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则"""

                /** 输入远程 URL；可自动识别 JSON 配置，或从 Surge/Loon 插件中提取 [Rule] 规则 */
                @Composable
                fun `ImportFromUrlHint`(vararg args: Any?) =
                    FYTxtConfig.observe { `ImportFromUrlHint`.fmt(args) }

                /** 选择 .sgmodule 文件提取 [Rule] 规则 */
                val `ImportSurgeHint`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Select a .sgmodule file to extract [Rule] rules"""
                                `MLangTags`.ZH -> """选择 .sgmodule 文件提取 [Rule] 规则"""
                                else -> null
                            }
                        } ?: """选择 .sgmodule 文件提取 [Rule] 规则"""

                /** 选择 .sgmodule 文件提取 [Rule] 规则 */
                @Composable
                fun `ImportSurgeHint`(vararg args: Any?) =
                    FYTxtConfig.observe { `ImportSurgeHint`.fmt(args) }

                /** 输入 JSON 配置的远程 URL */
                val `ImportJsonUrlHint`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Enter the remote URL of a JSON config"""
                                `MLangTags`.ZH -> """输入 JSON 配置的远程 URL"""
                                else -> null
                            }
                        } ?: """输入 JSON 配置的远程 URL"""

                /** 输入 JSON 配置的远程 URL */
                @Composable
                fun `ImportJsonUrlHint`(vararg args: Any?) =
                    FYTxtConfig.observe { `ImportJsonUrlHint`.fmt(args) }

                /** 输入 .sgmodule 的远程 URL */
                val `ImportSurgeUrlHint`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Enter the remote URL of a .sgmodule file"""
                                `MLangTags`.ZH -> """输入 .sgmodule 的远程 URL"""
                                else -> null
                            }
                        } ?: """输入 .sgmodule 的远程 URL"""

                /** 输入 .sgmodule 的远程 URL */
                @Composable
                fun `ImportSurgeUrlHint`(vararg args: Any?) =
                    FYTxtConfig.observe { `ImportSurgeUrlHint`.fmt(args) }
            }

            object `Delete` {
                init {
                    `MLangGroups`
                }

                /** 删除配置 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Delete Config"""
                                `MLangTags`.ZH -> """删除配置"""
                                else -> null
                            }
                        } ?: """删除配置"""

                /** 删除配置 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 配置 %s 正在被订阅使用，删除后将解除绑定关系。确定要删除吗？此操作不可恢复。 */
                val `InUseMessage`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Config %s is being used by subscriptions. Deleting will unbind the relationship. Are you sure? This action cannot be undone."""
                                `MLangTags`.ZH -> """配置 %s 正在被订阅使用，删除后将解除绑定关系。确定要删除吗？此操作不可恢复。"""
                                else -> null
                            }
                        } ?: """配置 %s 正在被订阅使用，删除后将解除绑定关系。确定要删除吗？此操作不可恢复。"""

                /** 配置 %s 正在被订阅使用，删除后将解除绑定关系。确定要删除吗？此操作不可恢复。 */
                @Composable
                fun `InUseMessage`(vararg args: Any?) =
                    FYTxtConfig.observe { `InUseMessage`.fmt(args) }

                /** 确定要删除配置 %s 吗？此操作不可恢复。 */
                val `Message`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Are you sure you want to delete config %s? This action cannot be undone."""
                                `MLangTags`.ZH -> """确定要删除配置 %s 吗？此操作不可恢复。"""
                                else -> null
                            }
                        } ?: """确定要删除配置 %s 吗？此操作不可恢复。"""

                /** 确定要删除配置 %s 吗？此操作不可恢复。 */
                @Composable
                fun `Message`(vararg args: Any?) = FYTxtConfig.observe { `Message`.fmt(args) }
            }

            object `EditOptions` {
                init {
                    `MLangGroups`
                }

                /** 编辑配置 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Edit Config"""
                                `MLangTags`.ZH -> """编辑配置"""
                                else -> null
                            }
                        } ?: """编辑配置"""

                /** 编辑配置 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 代码编辑器 */
                val `CodeEditor`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Code Editor"""
                                `MLangTags`.ZH -> """代码编辑器"""
                                else -> null
                            }
                        } ?: """代码编辑器"""

                /** 代码编辑器 */
                @Composable
                fun `CodeEditor`(vararg args: Any?) = FYTxtConfig.observe { `CodeEditor`.fmt(args) }

                /** 可视化编辑 */
                val `VisualEditor`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Visual Editor"""
                                `MLangTags`.ZH -> """可视化编辑"""
                                else -> null
                            }
                        } ?: """可视化编辑"""

                /** 可视化编辑 */
                @Composable
                fun `VisualEditor`(vararg args: Any?) =
                    FYTxtConfig.observe { `VisualEditor`.fmt(args) }
            }

            object `Button` {
                init {
                    `MLangGroups`
                }

                /** 取消 */
                val `Cancel`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Cancel"""
                                `MLangTags`.ZH -> """取消"""
                                else -> null
                            }
                        } ?: """取消"""

                /** 取消 */
                @Composable
                fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

                /** 删除 */
                val `Delete`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Delete"""
                                `MLangTags`.ZH -> """删除"""
                                else -> null
                            }
                        } ?: """删除"""

                /** 删除 */
                @Composable
                fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }
            }
        }

        object `Edit` {
            init {
                `MLangGroups`
            }

            /** 新建配置 */
            val `TitleNew`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """New Config"""
                            `MLangTags`.ZH -> """新建配置"""
                            else -> null
                        }
                    } ?: """新建配置"""

            /** 新建配置 */
            @Composable
            fun `TitleNew`(vararg args: Any?) = FYTxtConfig.observe { `TitleNew`.fmt(args) }

            /** 使用 JSON 格式进行高级编辑 */
            val `JsonEditHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Edit the raw JSON directly for fields not covered by the structured form"""
                            `MLangTags`.ZH -> """直接编辑原始 JSON，适合补充结构化表单未覆盖的字段"""
                            else -> null
                        }
                    } ?: """直接编辑原始 JSON，适合补充结构化表单未覆盖的字段"""

            /** 直接编辑原始 JSON，适合补充结构化表单未覆盖的字段 */
            @Composable
            fun `JsonEditHint`(vararg args: Any?) = FYTxtConfig.observe { `JsonEditHint`.fmt(args) }

            /** 使用结构化对象列表编辑 */
            val `StructuredObjectListHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Edit the object list item by item with the structured form"""
                            `MLangTags`.ZH -> """使用结构化表单逐项编辑对象列表"""
                            else -> null
                        }
                    } ?: """使用结构化表单逐项编辑对象列表"""

            /** 使用结构化表单逐项编辑对象列表 */
            @Composable
            fun `StructuredObjectListHint`(vararg args: Any?) =
                FYTxtConfig.observe { `StructuredObjectListHint`.fmt(args) }

            /** 使用结构化提供者字典编辑 */
            val `StructuredProviderDictHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Edit the provider dictionary with the structured form; each key maps to one provider"""
                            `MLangTags`.ZH -> """使用结构化表单编辑 provider 字典；每个键对应一个 provider"""
                            else -> null
                        }
                    } ?: """使用结构化表单编辑 provider 字典；每个键对应一个 provider"""

            /** 使用结构化表单编辑 provider 字典；每个键对应一个 provider */
            @Composable
            fun `StructuredProviderDictHint`(vararg args: Any?) =
                FYTxtConfig.observe { `StructuredProviderDictHint`.fmt(args) }

            /** 可在此部分定义规则组 */
            val `SubRuleGroupHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Manage sub-rule groups here; each group can contain multiple rules"""
                            `MLangTags`.ZH -> """在这里维护子规则组；每个分组可包含多条规则"""
                            else -> null
                        }
                    } ?: """在这里维护子规则组；每个分组可包含多条规则"""

            /** 在这里维护子规则组；每个分组可包含多条规则 */
            @Composable
            fun `SubRuleGroupHint`(vararg args: Any?) =
                FYTxtConfig.observe { `SubRuleGroupHint`.fmt(args) }

            /** JSON 对象字段需为键值对格式 */
            val `JsonKeyValueHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Fields must be valid JSON key-value pairs, for example { \"name\": \"proxy\" }"""
                            `MLangTags`.ZH -> """字段必须写成合法的 JSON 键值对，例如 { \"name\": \"proxy\" }"""
                            else -> null
                        }
                    } ?: """字段必须写成合法的 JSON 键值对，例如 { \"name\": \"proxy\" }"""

            /** 字段必须写成合法的 JSON 键值对，例如 { "name": "proxy" } */
            @Composable
            fun `JsonKeyValueHint`(vararg args: Any?) =
                FYTxtConfig.observe { `JsonKeyValueHint`.fmt(args) }

            /** 每行一个提供者名称 */
            val `OneProviderPerLineHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Enter one provider name per line; each name must match an existing provider"""
                            `MLangTags`.ZH -> """每行填写一个提供者名称；名称需与已定义的 provider 一致"""
                            else -> null
                        }
                    } ?: """每行填写一个提供者名称；名称需与已定义的 provider 一致"""

            /** 每行填写一个提供者名称；名称需与已定义的 provider 一致 */
            @Composable
            fun `OneProviderPerLineHint`(vararg args: Any?) =
                FYTxtConfig.observe { `OneProviderPerLineHint`.fmt(args) }

            /** 编辑配置 */
            val `TitleEdit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Config"""
                            `MLangTags`.ZH -> """编辑配置"""
                            else -> null
                        }
                    } ?: """编辑配置"""

            /** 编辑配置 */
            @Composable
            fun `TitleEdit`(vararg args: Any?) = FYTxtConfig.observe { `TitleEdit`.fmt(args) }

            object `EmptyName` {
                init {
                    `MLangGroups`
                }

                /** 名称为空 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Empty Name"""
                                `MLangTags`.ZH -> """名称为空"""
                                else -> null
                            }
                        } ?: """名称为空"""

                /** 名称为空 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 当前名称为空，无法实时保存。确定放弃这次未保存的修改吗？ */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN ->
                                    """Current name is empty, cannot save in real-time. Are you sure to discard these unsaved changes?"""
                                `MLangTags`.ZH -> """当前名称为空，无法实时保存。确定放弃这次未保存的修改吗？"""
                                else -> null
                            }
                        } ?: """当前名称为空，无法实时保存。确定放弃这次未保存的修改吗？"""

                /** 当前名称为空，无法实时保存。确定放弃这次未保存的修改吗？ */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `Button` {
                init {
                    `MLangGroups`
                }

                /** 取消 */
                val `Cancel`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Cancel"""
                                `MLangTags`.ZH -> """取消"""
                                else -> null
                            }
                        } ?: """取消"""

                /** 取消 */
                @Composable
                fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

                /** 放弃 */
                val `Discard`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Discard"""
                                `MLangTags`.ZH -> """放弃"""
                                else -> null
                            }
                        } ?: """放弃"""

                /** 放弃 */
                @Composable
                fun `Discard`(vararg args: Any?) = FYTxtConfig.observe { `Discard`.fmt(args) }
            }

            /** 已更新预设分流模板 */
            val `PresetApplied`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Preset routing template applied and routing sections rebuilt"""
                            `MLangTags`.ZH -> """已应用预设分流模板，并重建相关分流配置"""
                            else -> null
                        }
                    } ?: """已应用预设分流模板，并重建相关分流配置"""

            /** 已应用预设分流模板，并重建相关分流配置 */
            @Composable
            fun `PresetApplied`(vararg args: Any?) =
                FYTxtConfig.observe { `PresetApplied`.fmt(args) }
        }

        object `Section` {
            init {
                `MLangGroups`
            }

            object `General` {
                init {
                    `MLangGroups`
                }

                /** 全局配置 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """General"""
                                `MLangTags`.ZH -> """全局配置"""
                                else -> null
                            }
                        } ?: """全局配置"""

                /** 全局配置 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 运行模式、控制器、持久化与 GEO */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Run mode, controller, persistence & GEO"""
                                `MLangTags`.ZH -> """运行模式、控制器、持久化与 GEO"""
                                else -> null
                            }
                        } ?: """运行模式、控制器、持久化与 GEO"""

                /** 运行模式、控制器、持久化与 GEO */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `Dns` {
                init {
                    `MLangGroups`
                }

                /** DNS */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """DNS"""
                                `MLangTags`.ZH -> """DNS"""
                                else -> null
                            }
                        } ?: """DNS"""

                /** DNS */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 基础开关、Fake-IP、上游与策略 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Basic switches, Fake-IP, upstream & policy"""
                                `MLangTags`.ZH -> """基础开关、Fake-IP、上游与策略"""
                                else -> null
                            }
                        } ?: """基础开关、Fake-IP、上游与策略"""

                /** 基础开关、Fake-IP、上游与策略 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `Sniffer` {
                init {
                    `MLangGroups`
                }

                /** 域名嗅探 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Domain Sniffer"""
                                `MLangTags`.ZH -> """域名嗅探"""
                                else -> null
                            }
                        } ?: """域名嗅探"""

                /** 域名嗅探 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 策略开关、协议端口、跳过规则 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Policy switches, protocol ports, skip rules"""
                                `MLangTags`.ZH -> """策略开关、协议端口、跳过规则"""
                                else -> null
                            }
                        } ?: """策略开关、协议端口、跳过规则"""

                /** 策略开关、协议端口、跳过规则 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `Inbound` {
                init {
                    `MLangGroups`
                }

                /** 入站 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Inbound"""
                                `MLangTags`.ZH -> """入站"""
                                else -> null
                            }
                        } ?: """入站"""

                /** 入站 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 端口、鉴权、局域网访问 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Ports, authentication, LAN access"""
                                `MLangTags`.ZH -> """端口、鉴权、局域网访问"""
                                else -> null
                            }
                        } ?: """端口、鉴权、局域网访问"""

                /** 端口、鉴权、局域网访问 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `Tun` {
                init {
                    `MLangGroups`
                }

                /** Tun */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Tun"""
                                `MLangTags`.ZH -> """Tun"""
                                else -> null
                            }
                        } ?: """Tun"""

                /** Tun */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 入站 Tun、路由与应用范围 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Tun inbound, routing & app scope"""
                                `MLangTags`.ZH -> """入站 Tun、路由与应用范围"""
                                else -> null
                            }
                        } ?: """入站 Tun、路由与应用范围"""

                /** 入站 Tun、路由与应用范围 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `Rules` {
                init {
                    `MLangGroups`
                }

                /** 路由规则 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Routing Rules"""
                                `MLangTags`.ZH -> """路由规则"""
                                else -> null
                            }
                        } ?: """路由规则"""

                /** 路由规则 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 规则链与匹配顺序 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Rule chain & matching order"""
                                `MLangTags`.ZH -> """规则链与匹配顺序"""
                                else -> null
                            }
                        } ?: """规则链与匹配顺序"""

                /** 规则链与匹配顺序 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `Proxies` {
                init {
                    `MLangGroups`
                }

                /** 出站代理 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Outbound Proxies"""
                                `MLangTags`.ZH -> """出站代理"""
                                else -> null
                            }
                        } ?: """出站代理"""

                /** 出站代理 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 代理节点与协议对象 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Proxy nodes & protocol objects"""
                                `MLangTags`.ZH -> """代理节点与协议对象"""
                                else -> null
                            }
                        } ?: """代理节点与协议对象"""

                /** 代理节点与协议对象 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `ProxyProviders` {
                init {
                    `MLangGroups`
                }

                /** 代理集合 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Proxy Providers"""
                                `MLangTags`.ZH -> """代理集合"""
                                else -> null
                            }
                        } ?: """代理集合"""

                /** 代理集合 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 提供者来源、路径与更新配置 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Provider sources, paths & update settings"""
                                `MLangTags`.ZH -> """提供者来源、路径与更新配置"""
                                else -> null
                            }
                        } ?: """提供者来源、路径与更新配置"""

                /** 提供者来源、路径与更新配置 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `ProxyGroups` {
                init {
                    `MLangGroups`
                }

                /** 代理组 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Proxy Groups"""
                                `MLangTags`.ZH -> """代理组"""
                                else -> null
                            }
                        } ?: """代理组"""

                /** 代理组 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 分组策略、成员与顺序 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Group strategy, members & order"""
                                `MLangTags`.ZH -> """分组策略、成员与顺序"""
                                else -> null
                            }
                        } ?: """分组策略、成员与顺序"""

                /** 分组策略、成员与顺序 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `RuleProviders` {
                init {
                    `MLangGroups`
                }

                /** 规则集合 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Rule Providers"""
                                `MLangTags`.ZH -> """规则集合"""
                                else -> null
                            }
                        } ?: """规则集合"""

                /** 规则集合 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 规则来源、路径与更新配置 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Rule sources, paths & update settings"""
                                `MLangTags`.ZH -> """规则来源、路径与更新配置"""
                                else -> null
                            }
                        } ?: """规则来源、路径与更新配置"""

                /** 规则来源、路径与更新配置 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            object `SubRules` {
                init {
                    `MLangGroups`
                }

                /** 子规则 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Sub Rules"""
                                `MLangTags`.ZH -> """子规则"""
                                else -> null
                            }
                        } ?: """子规则"""

                /** 子规则 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 规则分组与引用关系 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Rule groups & references"""
                                `MLangTags`.ZH -> """规则分组与引用关系"""
                                else -> null
                            }
                        } ?: """规则分组与引用关系"""

                /** 规则分组与引用关系 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }
        }

        object `Modifier` {
            init {
                `MLangGroups`
            }

            /** 覆盖 */
            val `Replace`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Replace"""
                            `MLangTags`.ZH -> """覆盖"""
                            else -> null
                        }
                    } ?: """覆盖"""

            /** 覆盖 */
            @Composable
            fun `Replace`(vararg args: Any?) = FYTxtConfig.observe { `Replace`.fmt(args) }

            /** 前置追加 */
            val `Start`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Prepend"""
                            `MLangTags`.ZH -> """前置追加"""
                            else -> null
                        }
                    } ?: """前置追加"""

            /** 前置追加 */
            @Composable fun `Start`(vararg args: Any?) = FYTxtConfig.observe { `Start`.fmt(args) }

            /** 后置追加 */
            val `End`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Append"""
                            `MLangTags`.ZH -> """后置追加"""
                            else -> null
                        }
                    } ?: """后置追加"""

            /** 后置追加 */
            @Composable fun `End`(vararg args: Any?) = FYTxtConfig.observe { `End`.fmt(args) }

            /** 合并 */
            val `Merge`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Merge"""
                            `MLangTags`.ZH -> """合并"""
                            else -> null
                        }
                    } ?: """合并"""

            /** 合并 */
            @Composable fun `Merge`(vararg args: Any?) = FYTxtConfig.observe { `Merge`.fmt(args) }

            /** 强制覆盖 */
            val `Force`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Force Replace"""
                            `MLangTags`.ZH -> """强制覆盖"""
                            else -> null
                        }
                    } ?: """强制覆盖"""

            /** 强制覆盖 */
            @Composable fun `Force`(vararg args: Any?) = FYTxtConfig.observe { `Force`.fmt(args) }

            /** 未修改 */
            val `NotModified`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Not Modified"""
                            `MLangTags`.ZH -> """未修改"""
                            else -> null
                        }
                    } ?: """未修改"""

            /** 未修改 */
            @Composable
            fun `NotModified`(vararg args: Any?) = FYTxtConfig.observe { `NotModified`.fmt(args) }

            /** %d 项 */
            val `ItemsCount`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d items"""
                            `MLangTags`.ZH -> """%d 项"""
                            else -> null
                        }
                    } ?: """%d 项"""

            /** %d 项 */
            @Composable
            fun `ItemsCount`(vararg args: Any?) = FYTxtConfig.observe { `ItemsCount`.fmt(args) }

            /** 暂无改动 */
            val `NoChanges`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No changes"""
                            `MLangTags`.ZH -> """暂无改动"""
                            else -> null
                        }
                    } ?: """暂无改动"""

            /** 暂无改动 */
            @Composable
            fun `NoChanges`(vararg args: Any?) = FYTxtConfig.observe { `NoChanges`.fmt(args) }
        }

        object `Structured` {
            init {
                `MLangGroups`
            }

            object `Proxies` {
                init {
                    `MLangGroups`
                }

                /** 代理节点 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Proxy Nodes"""
                                `MLangTags`.ZH -> """代理节点"""
                                else -> null
                            }
                        } ?: """代理节点"""

                /** 代理节点 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 代理节点 */
                val `ItemLabel`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Proxy Node"""
                                `MLangTags`.ZH -> """代理节点"""
                                else -> null
                            }
                        } ?: """代理节点"""

                /** 代理节点 */
                @Composable
                fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }

                /** 暂无代理节点 */
                val `EmptyHint`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """No proxy nodes"""
                                `MLangTags`.ZH -> """暂无代理节点"""
                                else -> null
                            }
                        } ?: """暂无代理节点"""

                /** 暂无代理节点 */
                @Composable
                fun `EmptyHint`(vararg args: Any?) = FYTxtConfig.observe { `EmptyHint`.fmt(args) }
            }

            object `ProxyGroups` {
                init {
                    `MLangGroups`
                }

                /** 策略组 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Proxy Groups"""
                                `MLangTags`.ZH -> """策略组"""
                                else -> null
                            }
                        } ?: """策略组"""

                /** 策略组 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 策略组 */
                val `ItemLabel`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Proxy Group"""
                                `MLangTags`.ZH -> """策略组"""
                                else -> null
                            }
                        } ?: """策略组"""

                /** 策略组 */
                @Composable
                fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }

                /** 暂无策略组 */
                val `EmptyHint`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """No proxy groups"""
                                `MLangTags`.ZH -> """暂无策略组"""
                                else -> null
                            }
                        } ?: """暂无策略组"""

                /** 暂无策略组 */
                @Composable
                fun `EmptyHint`(vararg args: Any?) = FYTxtConfig.observe { `EmptyHint`.fmt(args) }
            }

            object `RuleProviders` {
                init {
                    `MLangGroups`
                }

                /** 规则提供者 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Rule Providers"""
                                `MLangTags`.ZH -> """规则提供者"""
                                else -> null
                            }
                        } ?: """规则提供者"""

                /** 规则提供者 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** Provider */
                val `ItemLabel`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Provider"""
                                `MLangTags`.ZH -> """Provider"""
                                else -> null
                            }
                        } ?: """Provider"""

                /** Provider */
                @Composable
                fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }
            }

            object `ProxyProviders` {
                init {
                    `MLangGroups`
                }

                /** 代理提供者 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Proxy Providers"""
                                `MLangTags`.ZH -> """代理提供者"""
                                else -> null
                            }
                        } ?: """代理提供者"""

                /** 代理提供者 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** Provider */
                val `ItemLabel`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Provider"""
                                `MLangTags`.ZH -> """Provider"""
                                else -> null
                            }
                        } ?: """Provider"""

                /** Provider */
                @Composable
                fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }
            }

            object `SubRules` {
                init {
                    `MLangGroups`
                }

                /** 子规则 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Sub Rules"""
                                `MLangTags`.ZH -> """子规则"""
                                else -> null
                            }
                        } ?: """子规则"""

                /** 子规则 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 子规则组 */
                val `ItemLabel`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Sub Rule Group"""
                                `MLangTags`.ZH -> """子规则组"""
                                else -> null
                            }
                        } ?: """子规则组"""

                /** 子规则组 */
                @Composable
                fun `ItemLabel`(vararg args: Any?) = FYTxtConfig.observe { `ItemLabel`.fmt(args) }
            }
        }

        object `Editor` {
            init {
                `MLangGroups`
            }

            object `Mode` {
                init {
                    `MLangGroups`
                }

                /** 修饰符模式 */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Modifier Mode"""
                                `MLangTags`.ZH -> """修饰符模式"""
                                else -> null
                            }
                        } ?: """修饰符模式"""

                /** 修饰符模式 */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 修改方式 */
                val `EditTitle`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Edit Mode"""
                                `MLangTags`.ZH -> """修改方式"""
                                else -> null
                            }
                        } ?: """修改方式"""

                /** 修改方式 */
                @Composable
                fun `EditTitle`(vararg args: Any?) = FYTxtConfig.observe { `EditTitle`.fmt(args) }

                /** 直接修改 */
                val `DirectEdit`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Direct Edit"""
                                `MLangTags`.ZH -> """直接修改"""
                                else -> null
                            }
                        } ?: """直接修改"""

                /** 直接修改 */
                @Composable
                fun `DirectEdit`(vararg args: Any?) = FYTxtConfig.observe { `DirectEdit`.fmt(args) }
            }

            object `ClearDialog` {
                init {
                    `MLangGroups`
                }

                /** 清空%s */
                val `Title`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Clear %s"""
                                `MLangTags`.ZH -> """清空%s"""
                                else -> null
                            }
                        } ?: """清空%s"""

                /** 清空%s */
                @Composable
                fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

                /** 清空后将移除当前模式里的所有%s。 */
                val `Summary`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Clearing will remove all %s in current mode."""
                                `MLangTags`.ZH -> """清空后将移除当前模式里的所有%s。"""
                                else -> null
                            }
                        } ?: """清空后将移除当前模式里的所有%s。"""

                /** 清空后将移除当前模式里的所有%s。 */
                @Composable
                fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
            }

            /** 新增 */
            val `New`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add"""
                            `MLangTags`.ZH -> """新增"""
                            else -> null
                        }
                    } ?: """新增"""

            /** 新增 */
            @Composable fun `New`(vararg args: Any?) = FYTxtConfig.observe { `New`.fmt(args) }

            /** 新增%s */
            val `AddNamedItem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add %s"""
                            `MLangTags`.ZH -> """新增%s"""
                            else -> null
                        }
                    } ?: """新增%s"""

            /** 新增%s */
            @Composable
            fun `AddNamedItem`(vararg args: Any?) = FYTxtConfig.observe { `AddNamedItem`.fmt(args) }

            /** 新增规则 */
            val `NewRule`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add New Rule"""
                            `MLangTags`.ZH -> """新增规则"""
                            else -> null
                        }
                    } ?: """新增规则"""

            /** 新增规则 */
            @Composable
            fun `NewRule`(vararg args: Any?) = FYTxtConfig.observe { `NewRule`.fmt(args) }

            /** 编辑规则 */
            val `EditRule`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Rule"""
                            `MLangTags`.ZH -> """编辑规则"""
                            else -> null
                        }
                    } ?: """编辑规则"""

            /** 编辑规则 */
            @Composable
            fun `EditRule`(vararg args: Any?) = FYTxtConfig.observe { `EditRule`.fmt(args) }

            /** 新增子规则组 */
            val `NewSubRuleGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add New Sub Rule Group"""
                            `MLangTags`.ZH -> """新增子规则组"""
                            else -> null
                        }
                    } ?: """新增子规则组"""

            /** 新增子规则组 */
            @Composable
            fun `NewSubRuleGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `NewSubRuleGroup`.fmt(args) }

            /** 未命名子规则组 */
            val `UnnamedSubRuleGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unnamed Sub Rule Group"""
                            `MLangTags`.ZH -> """未命名子规则组"""
                            else -> null
                        }
                    } ?: """未命名子规则组"""

            /** 未命名子规则组 */
            @Composable
            fun `UnnamedSubRuleGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `UnnamedSubRuleGroup`.fmt(args) }

            /** 编辑子规则组 */
            val `EditSubRuleGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Sub Rule Group"""
                            `MLangTags`.ZH -> """编辑子规则组"""
                            else -> null
                        }
                    } ?: """编辑子规则组"""

            /** 编辑子规则组 */
            @Composable
            fun `EditSubRuleGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `EditSubRuleGroup`.fmt(args) }

            /** 清空子规则 */
            val `ClearSubRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Clear Sub Rules"""
                            `MLangTags`.ZH -> """清空子规则"""
                            else -> null
                        }
                    } ?: """清空子规则"""

            /** 清空子规则 */
            @Composable
            fun `ClearSubRules`(vararg args: Any?) =
                FYTxtConfig.observe { `ClearSubRules`.fmt(args) }

            /** 未命名%s */
            val `Unnamed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unnamed %s"""
                            `MLangTags`.ZH -> """未命名%s"""
                            else -> null
                        }
                    } ?: """未命名%s"""

            /** 未命名%s */
            @Composable
            fun `Unnamed`(vararg args: Any?) = FYTxtConfig.observe { `Unnamed`.fmt(args) }

            /** 未命名规则 */
            val `UnnamedRule`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unnamed Rule"""
                            `MLangTags`.ZH -> """未命名规则"""
                            else -> null
                        }
                    } ?: """未命名规则"""

            /** 未命名规则 */
            @Composable
            fun `UnnamedRule`(vararg args: Any?) = FYTxtConfig.observe { `UnnamedRule`.fmt(args) }

            /** 编辑 */
            val `Edit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit"""
                            `MLangTags`.ZH -> """编辑"""
                            else -> null
                        }
                    } ?: """编辑"""

            /** 编辑 */
            @Composable fun `Edit`(vararg args: Any?) = FYTxtConfig.observe { `Edit`.fmt(args) }

            /** 拖拽排序 */
            val `DragToSort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Drag to Sort"""
                            `MLangTags`.ZH -> """拖拽排序"""
                            else -> null
                        }
                    } ?: """拖拽排序"""

            /** 拖拽排序 */
            @Composable
            fun `DragToSort`(vararg args: Any?) = FYTxtConfig.observe { `DragToSort`.fmt(args) }

            /** 取消删除 */
            val `CancelDelete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cancel Delete"""
                            `MLangTags`.ZH -> """取消删除"""
                            else -> null
                        }
                    } ?: """取消删除"""

            /** 取消删除 */
            @Composable
            fun `CancelDelete`(vararg args: Any?) = FYTxtConfig.observe { `CancelDelete`.fmt(args) }

            /** 删除已选条目 */
            val `DeleteSelected`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete Selected"""
                            `MLangTags`.ZH -> """删除已选条目"""
                            else -> null
                        }
                    } ?: """删除已选条目"""

            /** 删除已选条目 */
            @Composable
            fun `DeleteSelected`(vararg args: Any?) =
                FYTxtConfig.observe { `DeleteSelected`.fmt(args) }

            /** 删除已选%s */
            val `DeleteSelectedNamedItem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete selected %s"""
                            `MLangTags`.ZH -> """删除已选%s"""
                            else -> null
                        }
                    } ?: """删除已选%s"""

            /** 删除已选%s */
            @Composable
            fun `DeleteSelectedNamedItem`(vararg args: Any?) =
                FYTxtConfig.observe { `DeleteSelectedNamedItem`.fmt(args) }

            /** 删除已选规则 */
            val `DeleteSelectedRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete Selected Rules"""
                            `MLangTags`.ZH -> """删除已选规则"""
                            else -> null
                        }
                    } ?: """删除已选规则"""

            /** 删除已选规则 */
            @Composable
            fun `DeleteSelectedRules`(vararg args: Any?) =
                FYTxtConfig.observe { `DeleteSelectedRules`.fmt(args) }

            /** 清空当前模式 */
            val `ClearMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Clear Current Mode"""
                            `MLangTags`.ZH -> """清空当前模式"""
                            else -> null
                        }
                    } ?: """清空当前模式"""

            /** 清空当前模式 */
            @Composable
            fun `ClearMode`(vararg args: Any?) = FYTxtConfig.observe { `ClearMode`.fmt(args) }

            /** 进入删除模式进入删除模式 */
            val `EnterDeleteMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Enter Delete Mode"""
                            `MLangTags`.ZH -> """进入删除模式"""
                            else -> null
                        }
                    } ?: """进入删除模式"""

            /** 进入删除模式 */
            @Composable
            fun `EnterDeleteMode`(vararg args: Any?) =
                FYTxtConfig.observe { `EnterDeleteMode`.fmt(args) }

            /** 空字符串 */
            val `EmptyString`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Empty String"""
                            `MLangTags`.ZH -> """空字符串"""
                            else -> null
                        }
                    } ?: """空字符串"""

            /** 空字符串 */
            @Composable
            fun `EmptyString`(vararg args: Any?) = FYTxtConfig.observe { `EmptyString`.fmt(args) }

            /** 数组 %d 项 */
            val `ArrayItems`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Array %d items"""
                            `MLangTags`.ZH -> """数组 %d 项"""
                            else -> null
                        }
                    } ?: """数组 %d 项"""

            /** 数组 %d 项 */
            @Composable
            fun `ArrayItems`(vararg args: Any?) = FYTxtConfig.observe { `ArrayItems`.fmt(args) }

            /** 对象 %d 个字段 */
            val `ObjectFields`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Object %d fields"""
                            `MLangTags`.ZH -> """对象 %d 个字段"""
                            else -> null
                        }
                    } ?: """对象 %d 个字段"""

            /** 对象 %d 个字段 */
            @Composable
            fun `ObjectFields`(vararg args: Any?) = FYTxtConfig.observe { `ObjectFields`.fmt(args) }

            /** 清空 */
            val `Clear`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Clear"""
                            `MLangTags`.ZH -> """清空"""
                            else -> null
                        }
                    } ?: """清空"""

            /** 清空 */
            @Composable fun `Clear`(vararg args: Any?) = FYTxtConfig.observe { `Clear`.fmt(args) }

            /** 规则 */
            val `Rules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rules"""
                            `MLangTags`.ZH -> """规则"""
                            else -> null
                        }
                    } ?: """规则"""

            /** 规则 */
            @Composable fun `Rules`(vararg args: Any?) = FYTxtConfig.observe { `Rules`.fmt(args) }

            /** 添加自定义 */
            val `AddCustom`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Custom"""
                            `MLangTags`.ZH -> """添加自定义"""
                            else -> null
                        }
                    } ?: """添加自定义"""

            /** 添加自定义 */
            @Composable
            fun `AddCustom`(vararg args: Any?) = FYTxtConfig.observe { `AddCustom`.fmt(args) }

            /** 内容不能为空 */
            val `ContentEmpty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Content cannot be empty"""
                            `MLangTags`.ZH -> """内容不能为空"""
                            else -> null
                        }
                    } ?: """内容不能为空"""

            /** 内容不能为空 */
            @Composable
            fun `ContentEmpty`(vararg args: Any?) = FYTxtConfig.observe { `ContentEmpty`.fmt(args) }

            /** 确定 */
            val `Confirm`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Confirm"""
                            `MLangTags`.ZH -> """确定"""
                            else -> null
                        }
                    } ?: """确定"""

            /** 确定 */
            @Composable
            fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }

            /** 每行一个条目 */
            val `OneItemPerLine`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Enter one item per line; blank lines are ignored"""
                            `MLangTags`.ZH -> """每行填写一个条目；空行会被忽略"""
                            else -> null
                        }
                    } ?: """每行填写一个条目；空行会被忽略"""

            /** 每行填写一个条目；空行会被忽略 */
            @Composable
            fun `OneItemPerLine`(vararg args: Any?) =
                FYTxtConfig.observe { `OneItemPerLine`.fmt(args) }

            /** 新增一项 */
            val `AddItem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Item"""
                            `MLangTags`.ZH -> """新增一项"""
                            else -> null
                        }
                    } ?: """新增一项"""

            /** 新增一项 */
            @Composable
            fun `AddItem`(vararg args: Any?) = FYTxtConfig.observe { `AddItem`.fmt(args) }

            /** 删除最后一项 */
            val `DeleteLastItem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete Last Item"""
                            `MLangTags`.ZH -> """删除最后一项"""
                            else -> null
                        }
                    } ?: """删除最后一项"""

            /** 删除最后一项 */
            @Composable
            fun `DeleteLastItem`(vararg args: Any?) =
                FYTxtConfig.observe { `DeleteLastItem`.fmt(args) }

            /** 复制 */
            val `Copy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Copy"""
                            `MLangTags`.ZH -> """复制"""
                            else -> null
                        }
                    } ?: """复制"""

            /** 复制 */
            @Composable fun `Copy`(vararg args: Any?) = FYTxtConfig.observe { `Copy`.fmt(args) }

            /** 删除 */
            val `Delete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete"""
                            `MLangTags`.ZH -> """删除"""
                            else -> null
                        }
                    } ?: """删除"""

            /** 删除 */
            @Composable fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }

            /** 上移 */
            val `MoveUp`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Move Up"""
                            `MLangTags`.ZH -> """上移"""
                            else -> null
                        }
                    } ?: """上移"""

            /** 上移 */
            @Composable fun `MoveUp`(vararg args: Any?) = FYTxtConfig.observe { `MoveUp`.fmt(args) }

            /** 下移 */
            val `MoveDown`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Move Down"""
                            `MLangTags`.ZH -> """下移"""
                            else -> null
                        }
                    } ?: """下移"""

            /** 下移 */
            @Composable
            fun `MoveDown`(vararg args: Any?) = FYTxtConfig.observe { `MoveDown`.fmt(args) }

            /** 新增对象 */
            val `AddObject`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Object"""
                            `MLangTags`.ZH -> """新增对象"""
                            else -> null
                        }
                    } ?: """新增对象"""

            /** 新增对象 */
            @Composable
            fun `AddObject`(vararg args: Any?) = FYTxtConfig.observe { `AddObject`.fmt(args) }

            /** 子规则名称 */
            val `SubRuleName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Sub Rule Name"""
                            `MLangTags`.ZH -> """子规则名称"""
                            else -> null
                        }
                    } ?: """子规则名称"""

            /** 子规则名称 */
            @Composable
            fun `SubRuleName`(vararg args: Any?) = FYTxtConfig.observe { `SubRuleName`.fmt(args) }

            /** 暂无规则 */
            val `NoRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No rules"""
                            `MLangTags`.ZH -> """暂无规则"""
                            else -> null
                        }
                    } ?: """暂无规则"""

            /** 暂无规则 */
            @Composable
            fun `NoRules`(vararg args: Any?) = FYTxtConfig.observe { `NoRules`.fmt(args) }

            /** 已配置 %d 条规则 */
            val `RulesConfiguredInline`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d rules configured"""
                            `MLangTags`.ZH -> """已配置 %d 条规则"""
                            else -> null
                        }
                    } ?: """已配置 %d 条规则"""

            /** 已配置 %d 条规则 */
            @Composable
            fun `RulesConfiguredInline`(vararg args: Any?) =
                FYTxtConfig.observe { `RulesConfiguredInline`.fmt(args) }

            /** 新增子规则组 */
            val `AddSubRuleGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Sub Rule Group"""
                            `MLangTags`.ZH -> """新增子规则组"""
                            else -> null
                        }
                    } ?: """新增子规则组"""

            /** 新增子规则组 */
            @Composable
            fun `AddSubRuleGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `AddSubRuleGroup`.fmt(args) }

            /** 编辑子规则 */
            val `EditSubRule`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Sub Rule"""
                            `MLangTags`.ZH -> """编辑子规则"""
                            else -> null
                        }
                    } ?: """编辑子规则"""

            /** 编辑子规则 */
            @Composable
            fun `EditSubRule`(vararg args: Any?) = FYTxtConfig.observe { `EditSubRule`.fmt(args) }

            /** 键名 */
            val `KeyName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Key Name"""
                            `MLangTags`.ZH -> """键名"""
                            else -> null
                        }
                    } ?: """键名"""

            /** 键名 */
            @Composable
            fun `KeyName`(vararg args: Any?) = FYTxtConfig.observe { `KeyName`.fmt(args) }

            /** 列表 */
            val `List`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """List"""
                            `MLangTags`.ZH -> """列表"""
                            else -> null
                        }
                    } ?: """列表"""

            /** 列表 */
            @Composable fun `List`(vararg args: Any?) = FYTxtConfig.observe { `List`.fmt(args) }

            /** 编辑条目 */
            val `EditItem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Item"""
                            `MLangTags`.ZH -> """编辑条目"""
                            else -> null
                        }
                    } ?: """编辑条目"""

            /** 编辑条目 */
            @Composable
            fun `EditItem`(vararg args: Any?) = FYTxtConfig.observe { `EditItem`.fmt(args) }

            /** 清空当前模式 */
            val `ClearCurrentMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Clear Current Mode"""
                            `MLangTags`.ZH -> """清空当前模式"""
                            else -> null
                        }
                    } ?: """清空当前模式"""

            /** 清空当前模式 */
            @Composable
            fun `ClearCurrentMode`(vararg args: Any?) =
                FYTxtConfig.observe { `ClearCurrentMode`.fmt(args) }

            /** 新增代理节点 */
            val `NewProxyNode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Proxy Node"""
                            `MLangTags`.ZH -> """新增代理节点"""
                            else -> null
                        }
                    } ?: """新增代理节点"""

            /** 新增代理节点 */
            @Composable
            fun `NewProxyNode`(vararg args: Any?) = FYTxtConfig.observe { `NewProxyNode`.fmt(args) }

            /** 新增策略组 */
            val `NewProxyGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Proxy Group"""
                            `MLangTags`.ZH -> """新增策略组"""
                            else -> null
                        }
                    } ?: """新增策略组"""

            /** 新增策略组 */
            @Composable
            fun `NewProxyGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `NewProxyGroup`.fmt(args) }

            /** 编辑代理节点 */
            val `EditProxyNode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Proxy Node"""
                            `MLangTags`.ZH -> """编辑代理节点"""
                            else -> null
                        }
                    } ?: """编辑代理节点"""

            /** 编辑代理节点 */
            @Composable
            fun `EditProxyNode`(vararg args: Any?) =
                FYTxtConfig.observe { `EditProxyNode`.fmt(args) }

            /** 编辑策略组 */
            val `EditProxyGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Proxy Group"""
                            `MLangTags`.ZH -> """编辑策略组"""
                            else -> null
                        }
                    } ?: """编辑策略组"""

            /** 编辑策略组 */
            @Composable
            fun `EditProxyGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `EditProxyGroup`.fmt(args) }

            /** 未命名代理节点 */
            val `UnnamedProxyNode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unnamed Proxy Node"""
                            `MLangTags`.ZH -> """未命名代理节点"""
                            else -> null
                        }
                    } ?: """未命名代理节点"""

            /** 未命名代理节点 */
            @Composable
            fun `UnnamedProxyNode`(vararg args: Any?) =
                FYTxtConfig.observe { `UnnamedProxyNode`.fmt(args) }

            /** 未命名策略组 */
            val `UnnamedProxyGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unnamed Proxy Group"""
                            `MLangTags`.ZH -> """未命名策略组"""
                            else -> null
                        }
                    } ?: """未命名策略组"""

            /** 未命名策略组 */
            @Composable
            fun `UnnamedProxyGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `UnnamedProxyGroup`.fmt(args) }

            /** 代理节点 */
            val `ProxyNode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Node"""
                            `MLangTags`.ZH -> """代理节点"""
                            else -> null
                        }
                    } ?: """代理节点"""

            /** 代理节点 */
            @Composable
            fun `ProxyNode`(vararg args: Any?) = FYTxtConfig.observe { `ProxyNode`.fmt(args) }

            /** 策略组 */
            val `ProxyGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Group"""
                            `MLangTags`.ZH -> """策略组"""
                            else -> null
                        }
                    } ?: """策略组"""

            /** 策略组 */
            @Composable
            fun `ProxyGroup`(vararg args: Any?) = FYTxtConfig.observe { `ProxyGroup`.fmt(args) }

            /** 规则编辑 */
            val `RuleEdit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule Edit"""
                            `MLangTags`.ZH -> """规则编辑"""
                            else -> null
                        }
                    } ?: """规则编辑"""

            /** 规则编辑 */
            @Composable
            fun `RuleEdit`(vararg args: Any?) = FYTxtConfig.observe { `RuleEdit`.fmt(args) }

            /** 子规则目标 */
            val `SubRuleTarget`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Sub Rule Target"""
                            `MLangTags`.ZH -> """子规则目标"""
                            else -> null
                        }
                    } ?: """子规则目标"""

            /** 子规则目标 */
            @Composable
            fun `SubRuleTarget`(vararg args: Any?) =
                FYTxtConfig.observe { `SubRuleTarget`.fmt(args) }

            /** 策略组目标 */
            val `ProxyGroupTarget`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Group Target"""
                            `MLangTags`.ZH -> """策略组目标"""
                            else -> null
                        }
                    } ?: """策略组目标"""

            /** 策略组目标 */
            @Composable
            fun `ProxyGroupTarget`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyGroupTarget`.fmt(args) }

            /** 规则类型不能为空 */
            val `RuleTypeEmpty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule type cannot be empty"""
                            `MLangTags`.ZH -> """规则类型不能为空"""
                            else -> null
                        }
                    } ?: """规则类型不能为空"""

            /** 规则类型不能为空 */
            @Composable
            fun `RuleTypeEmpty`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleTypeEmpty`.fmt(args) }

            /** 匹配内容不能为空 */
            val `PayloadEmpty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Payload cannot be empty"""
                            `MLangTags`.ZH -> """匹配内容不能为空"""
                            else -> null
                        }
                    } ?: """匹配内容不能为空"""

            /** 匹配内容不能为空 */
            @Composable
            fun `PayloadEmpty`(vararg args: Any?) = FYTxtConfig.observe { `PayloadEmpty`.fmt(args) }

            /** 目标不能为空 */
            val `TargetEmpty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Target cannot be empty"""
                            `MLangTags`.ZH -> """目标不能为空"""
                            else -> null
                        }
                    } ?: """目标不能为空"""

            /** 目标不能为空 */
            @Composable
            fun `TargetEmpty`(vararg args: Any?) = FYTxtConfig.observe { `TargetEmpty`.fmt(args) }

            /** 匹配结果 */
            val `MatchResult`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Match Result"""
                            `MLangTags`.ZH -> """匹配结果"""
                            else -> null
                        }
                    } ?: """匹配结果"""

            /** 匹配结果 */
            @Composable
            fun `MatchResult`(vararg args: Any?) = FYTxtConfig.observe { `MatchResult`.fmt(args) }

            /** 选择匹配结果 */
            val `SelectMatchResult`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Select Match Result"""
                            `MLangTags`.ZH -> """选择匹配结果"""
                            else -> null
                        }
                    } ?: """选择匹配结果"""

            /** 选择匹配结果 */
            @Composable
            fun `SelectMatchResult`(vararg args: Any?) =
                FYTxtConfig.observe { `SelectMatchResult`.fmt(args) }

            /** 自定义匹配结果 */
            val `CustomMatchResult`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Custom Match Result"""
                            `MLangTags`.ZH -> """自定义匹配结果"""
                            else -> null
                        }
                    } ?: """自定义匹配结果"""

            /** 自定义匹配结果 */
            @Composable
            fun `CustomMatchResult`(vararg args: Any?) =
                FYTxtConfig.observe { `CustomMatchResult`.fmt(args) }

            /** 选择子规则目标 */
            val `SelectSubRuleTarget`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Select Sub Rule Target"""
                            `MLangTags`.ZH -> """选择子规则目标"""
                            else -> null
                        }
                    } ?: """选择子规则目标"""

            /** 选择子规则目标 */
            @Composable
            fun `SelectSubRuleTarget`(vararg args: Any?) =
                FYTxtConfig.observe { `SelectSubRuleTarget`.fmt(args) }

            /** 选择策略组目标 */
            val `SelectProxyGroupTarget`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Select Proxy Group Target"""
                            `MLangTags`.ZH -> """选择策略组目标"""
                            else -> null
                        }
                    } ?: """选择策略组目标"""

            /** 选择策略组目标 */
            @Composable
            fun `SelectProxyGroupTarget`(vararg args: Any?) =
                FYTxtConfig.observe { `SelectProxyGroupTarget`.fmt(args) }

            /** 自定义子规则目标 */
            val `CustomSubRuleTarget`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Custom Sub Rule Target"""
                            `MLangTags`.ZH -> """自定义子规则目标"""
                            else -> null
                        }
                    } ?: """自定义子规则目标"""

            /** 自定义子规则目标 */
            @Composable
            fun `CustomSubRuleTarget`(vararg args: Any?) =
                FYTxtConfig.observe { `CustomSubRuleTarget`.fmt(args) }

            /** 自定义策略组目标 */
            val `CustomProxyGroupTarget`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Custom Proxy Group Target"""
                            `MLangTags`.ZH -> """自定义策略组目标"""
                            else -> null
                        }
                    } ?: """自定义策略组目标"""

            /** 自定义策略组目标 */
            @Composable
            fun `CustomProxyGroupTarget`(vararg args: Any?) =
                FYTxtConfig.observe { `CustomProxyGroupTarget`.fmt(args) }

            /** 选择规则提供者 */
            val `SelectRuleProvider`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Select Rule Provider"""
                            `MLangTags`.ZH -> """选择规则提供者"""
                            else -> null
                        }
                    } ?: """选择规则提供者"""

            /** 选择规则提供者 */
            @Composable
            fun `SelectRuleProvider`(vararg args: Any?) =
                FYTxtConfig.observe { `SelectRuleProvider`.fmt(args) }

            /** 规则主体 */
            val `RuleBody`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule Body"""
                            `MLangTags`.ZH -> """规则主体"""
                            else -> null
                        }
                    } ?: """规则主体"""

            /** 规则主体 */
            @Composable
            fun `RuleBody`(vararg args: Any?) = FYTxtConfig.observe { `RuleBody`.fmt(args) }

            /** 类型 */
            val `RuleType`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Type"""
                            `MLangTags`.ZH -> """类型"""
                            else -> null
                        }
                    } ?: """类型"""

            /** 类型 */
            @Composable
            fun `RuleType`(vararg args: Any?) = FYTxtConfig.observe { `RuleType`.fmt(args) }

            /** 匹配内容 */
            val `Payload`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Payload"""
                            `MLangTags`.ZH -> """匹配内容"""
                            else -> null
                        }
                    } ?: """匹配内容"""

            /** 匹配内容 */
            @Composable
            fun `Payload`(vararg args: Any?) = FYTxtConfig.observe { `Payload`.fmt(args) }

            /** 附加参数 */
            val `AdditionalParams`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Additional Params"""
                            `MLangTags`.ZH -> """附加参数"""
                            else -> null
                        }
                    } ?: """附加参数"""

            /** 附加参数 */
            @Composable
            fun `AdditionalParams`(vararg args: Any?) =
                FYTxtConfig.observe { `AdditionalParams`.fmt(args) }

            /** 基础连接 */
            val `BasicConnection`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Basic Connection"""
                            `MLangTags`.ZH -> """基础连接"""
                            else -> null
                        }
                    } ?: """基础连接"""

            /** 基础连接 */
            @Composable
            fun `BasicConnection`(vararg args: Any?) =
                FYTxtConfig.observe { `BasicConnection`.fmt(args) }

            /** 网络与路由 */
            val `NetworkAndRoute`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Network & Route"""
                            `MLangTags`.ZH -> """网络与路由"""
                            else -> null
                        }
                    } ?: """网络与路由"""

            /** 网络与路由 */
            @Composable
            fun `NetworkAndRoute`(vararg args: Any?) =
                FYTxtConfig.observe { `NetworkAndRoute`.fmt(args) }

            /** 留空表示不覆写端口 */
            val `PortEmptyHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Leave empty to not override port"""
                            `MLangTags`.ZH -> """留空表示不覆写端口"""
                            else -> null
                        }
                    } ?: """留空表示不覆写端口"""

            /** 留空表示不覆写端口 */
            @Composable
            fun `PortEmptyHint`(vararg args: Any?) =
                FYTxtConfig.observe { `PortEmptyHint`.fmt(args) }

            /** 类型不能为空 */
            val `TypeEmpty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Type cannot be empty"""
                            `MLangTags`.ZH -> """类型不能为空"""
                            else -> null
                        }
                    } ?: """类型不能为空"""

            /** 类型不能为空 */
            @Composable
            fun `TypeEmpty`(vararg args: Any?) = FYTxtConfig.observe { `TypeEmpty`.fmt(args) }

            /** 成员来源 */
            val `MemberSource`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Member Source"""
                            `MLangTags`.ZH -> """成员来源"""
                            else -> null
                        }
                    } ?: """成员来源"""

            /** 成员来源 */
            @Composable
            fun `MemberSource`(vararg args: Any?) = FYTxtConfig.observe { `MemberSource`.fmt(args) }

            /** 健康检查与过滤 */
            val `HealthCheckAndFilter`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Health Check & Filter"""
                            `MLangTags`.ZH -> """健康检查与过滤"""
                            else -> null
                        }
                    } ?: """健康检查与过滤"""

            /** 健康检查与过滤 */
            @Composable
            fun `HealthCheckAndFilter`(vararg args: Any?) =
                FYTxtConfig.observe { `HealthCheckAndFilter`.fmt(args) }

            /** 选择策略组成员 */
            val `SelectProxyGroupMember`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Select Proxy Group Member"""
                            `MLangTags`.ZH -> """选择策略组成员"""
                            else -> null
                        }
                    } ?: """选择策略组成员"""

            /** 选择策略组成员 */
            @Composable
            fun `SelectProxyGroupMember`(vararg args: Any?) =
                FYTxtConfig.observe { `SelectProxyGroupMember`.fmt(args) }

            /** 自定义成员 */
            val `CustomMember`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Custom Member"""
                            `MLangTags`.ZH -> """自定义成员"""
                            else -> null
                        }
                    } ?: """自定义成员"""

            /** 自定义成员 */
            @Composable
            fun `CustomMember`(vararg args: Any?) = FYTxtConfig.observe { `CustomMember`.fmt(args) }

            /** 保存代理节点 */
            val `SaveProxyNode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Save Proxy Node"""
                            `MLangTags`.ZH -> """保存代理节点"""
                            else -> null
                        }
                    } ?: """保存代理节点"""

            /** 保存代理节点 */
            @Composable
            fun `SaveProxyNode`(vararg args: Any?) =
                FYTxtConfig.observe { `SaveProxyNode`.fmt(args) }

            /** 保存策略组 */
            val `SaveProxyGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Save Proxy Group"""
                            `MLangTags`.ZH -> """保存策略组"""
                            else -> null
                        }
                    } ?: """保存策略组"""

            /** 保存策略组 */
            @Composable
            fun `SaveProxyGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `SaveProxyGroup`.fmt(args) }

            /** 保存规则 */
            val `SaveRule`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Save Rule"""
                            `MLangTags`.ZH -> """保存规则"""
                            else -> null
                        }
                    } ?: """保存规则"""

            /** 保存规则 */
            @Composable
            fun `SaveRule`(vararg args: Any?) = FYTxtConfig.observe { `SaveRule`.fmt(args) }

            /** 输入框是自定义内容；留空时使用下面选中的规则提供者 */
            val `RuleProviderInputHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """This field accepts a custom payload. Leave it empty to use the selected rule provider below"""
                            `MLangTags`.ZH -> """这里填写的是自定义 payload；留空时改为使用下方选中的规则提供者"""
                            else -> null
                        }
                    } ?: """这里填写的是自定义 payload；留空时改为使用下方选中的规则提供者"""

            /** 这里填写的是自定义 payload；留空时改为使用下方选中的规则提供者 */
            @Composable
            fun `RuleProviderInputHint`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleProviderInputHint`.fmt(args) }

            /** 逻辑规则可直接填写完整 payload */
            val `LogicalRuleHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Logical rules should fill in the complete payload directly"""
                            `MLangTags`.ZH -> """逻辑规则需要直接填写完整 payload"""
                            else -> null
                        }
                    } ?: """逻辑规则需要直接填写完整 payload"""

            /** 逻辑规则需要直接填写完整 payload */
            @Composable
            fun `LogicalRuleHint`(vararg args: Any?) =
                FYTxtConfig.observe { `LogicalRuleHint`.fmt(args) }

            /** 其他附加参数，多个值用逗号分隔 */
            val `OtherExtraParams`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Other extra params, comma-separated"""
                            `MLangTags`.ZH -> """其他附加参数，多个值用逗号分隔"""
                            else -> null
                        }
                    } ?: """其他附加参数，多个值用逗号分隔"""

            /** 其他附加参数，多个值用逗号分隔 */
            @Composable
            fun `OtherExtraParams`(vararg args: Any?) =
                FYTxtConfig.observe { `OtherExtraParams`.fmt(args) }

            /**
             * 例如 src,no-resolve 之外的额外参数 逻辑规则请直接填写完整 payload，例如 ((DOMAIN,google.com),(NETWORK,udp))。
             */
            val `ExtraParamsHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Extra parameters other than src and no-resolve.
For logical rules, enter the complete payload here, e.g. ((DOMAIN,google.com),(NETWORK,udp))."""
                            `MLangTags`.ZH ->
                                """这里填写除 src、no-resolve 之外的附加参数。
逻辑规则请直接在这里填写完整 payload，例如 ((DOMAIN,google.com),(NETWORK,udp))。"""
                            else -> null
                        }
                    }
                        ?: """这里填写除 src、no-resolve 之外的附加参数。
逻辑规则请直接在这里填写完整 payload，例如 ((DOMAIN,google.com),(NETWORK,udp))。"""

            /**
             * 这里填写除 src、no-resolve 之外的附加参数。 逻辑规则请直接在这里填写完整 payload，例如
             * ((DOMAIN,google.com),(NETWORK,udp))。
             */
            @Composable
            fun `ExtraParamsHint`(vararg args: Any?) =
                FYTxtConfig.observe { `ExtraParamsHint`.fmt(args) }
        }

        object `Draft` {
            init {
                `MLangGroups`
            }

            /** 对象 */
            val `Object`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Object"""
                            `MLangTags`.ZH -> """对象"""
                            else -> null
                        }
                    } ?: """对象"""

            /** 对象 */
            @Composable fun `Object`(vararg args: Any?) = FYTxtConfig.observe { `Object`.fmt(args) }

            /** 子规则组 */
            val `SubRuleGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Sub Rule Group"""
                            `MLangTags`.ZH -> """子规则组"""
                            else -> null
                        }
                    } ?: """子规则组"""

            /** 子规则组 */
            @Composable
            fun `SubRuleGroup`(vararg args: Any?) = FYTxtConfig.observe { `SubRuleGroup`.fmt(args) }

            /** 名称 */
            val `Name`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Name"""
                            `MLangTags`.ZH -> """名称"""
                            else -> null
                        }
                    } ?: """名称"""

            /** 名称 */
            @Composable fun `Name`(vararg args: Any?) = FYTxtConfig.observe { `Name`.fmt(args) }

            /** 名称不能为空 */
            val `NameRequired`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Name cannot be empty"""
                            `MLangTags`.ZH -> """名称不能为空"""
                            else -> null
                        }
                    } ?: """名称不能为空"""

            /** 名称不能为空 */
            @Composable
            fun `NameRequired`(vararg args: Any?) = FYTxtConfig.observe { `NameRequired`.fmt(args) }

            /** 保存 */
            val `Save`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Save"""
                            `MLangTags`.ZH -> """保存"""
                            else -> null
                        }
                    } ?: """保存"""

            /** 保存 */
            @Composable fun `Save`(vararg args: Any?) = FYTxtConfig.observe { `Save`.fmt(args) }

            /** 基础信息 */
            val `BasicInfo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Basic Info"""
                            `MLangTags`.ZH -> """基础信息"""
                            else -> null
                        }
                    } ?: """基础信息"""

            /** 基础信息 */
            @Composable
            fun `BasicInfo`(vararg args: Any?) = FYTxtConfig.observe { `BasicInfo`.fmt(args) }

            /** 基础身份 */
            val `BasicIdentity`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Basic Identity"""
                            `MLangTags`.ZH -> """基础身份"""
                            else -> null
                        }
                    } ?: """基础身份"""

            /** 基础身份 */
            @Composable
            fun `BasicIdentity`(vararg args: Any?) =
                FYTxtConfig.observe { `BasicIdentity`.fmt(args) }

            /** 核心来源 */
            val `CoreSource`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Core Source"""
                            `MLangTags`.ZH -> """核心来源"""
                            else -> null
                        }
                    } ?: """核心来源"""

            /** 核心来源 */
            @Composable
            fun `CoreSource`(vararg args: Any?) = FYTxtConfig.observe { `CoreSource`.fmt(args) }

            /** 网络与认证 */
            val `NetworkAuth`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Network & Auth"""
                            `MLangTags`.ZH -> """网络与认证"""
                            else -> null
                        }
                    } ?: """网络与认证"""

            /** 网络与认证 */
            @Composable
            fun `NetworkAuth`(vararg args: Any?) = FYTxtConfig.observe { `NetworkAuth`.fmt(args) }

            /** 类型 */
            val `Type`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Type"""
                            `MLangTags`.ZH -> """类型"""
                            else -> null
                        }
                    } ?: """类型"""

            /** 类型 */
            @Composable fun `Type`(vararg args: Any?) = FYTxtConfig.observe { `Type`.fmt(args) }

            /** 载体 */
            val `Vehicle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Vehicle"""
                            `MLangTags`.ZH -> """载体"""
                            else -> null
                        }
                    } ?: """载体"""

            /** 载体 */
            @Composable
            fun `Vehicle`(vararg args: Any?) = FYTxtConfig.observe { `Vehicle`.fmt(args) }

            /** 行为 */
            val `Behavior`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Behavior"""
                            `MLangTags`.ZH -> """行为"""
                            else -> null
                        }
                    } ?: """行为"""

            /** 行为 */
            @Composable
            fun `Behavior`(vararg args: Any?) = FYTxtConfig.observe { `Behavior`.fmt(args) }

            /** 格式 */
            val `Format`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Format"""
                            `MLangTags`.ZH -> """格式"""
                            else -> null
                        }
                    } ?: """格式"""

            /** 格式 */
            @Composable fun `Format`(vararg args: Any?) = FYTxtConfig.observe { `Format`.fmt(args) }

            /** 健康检查 */
            val `HealthCheck`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Health Check"""
                            `MLangTags`.ZH -> """健康检查"""
                            else -> null
                        }
                    } ?: """健康检查"""

            /** 健康检查 */
            @Composable
            fun `HealthCheck`(vararg args: Any?) = FYTxtConfig.observe { `HealthCheck`.fmt(args) }

            /** 覆写 */
            val `Override`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Override"""
                            `MLangTags`.ZH -> """覆写"""
                            else -> null
                        }
                    } ?: """覆写"""

            /** 覆写 */
            @Composable
            fun `Override`(vararg args: Any?) = FYTxtConfig.observe { `Override`.fmt(args) }

            /** 规则列表 */
            val `RuleList`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule List"""
                            `MLangTags`.ZH -> """规则列表"""
                            else -> null
                        }
                    } ?: """规则列表"""

            /** 规则列表 */
            @Composable
            fun `RuleList`(vararg args: Any?) = FYTxtConfig.observe { `RuleList`.fmt(args) }

            /** 未配置规则 */
            val `NoRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No rules configured"""
                            `MLangTags`.ZH -> """未配置规则"""
                            else -> null
                        }
                    } ?: """未配置规则"""

            /** 未配置规则 */
            @Composable
            fun `NoRules`(vararg args: Any?) = FYTxtConfig.observe { `NoRules`.fmt(args) }

            /** 已配置 %d 条规则 */
            val `RulesConfigured`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d rules configured"""
                            `MLangTags`.ZH -> """已配置 %d 条规则"""
                            else -> null
                        }
                    } ?: """已配置 %d 条规则"""

            /** 已配置 %d 条规则 */
            @Composable
            fun `RulesConfigured`(vararg args: Any?) =
                FYTxtConfig.observe { `RulesConfigured`.fmt(args) }

            /** 编辑子规则 */
            val `EditSubRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Sub Rules"""
                            `MLangTags`.ZH -> """编辑子规则"""
                            else -> null
                        }
                    } ?: """编辑子规则"""

            /** 编辑子规则 */
            @Composable
            fun `EditSubRules`(vararg args: Any?) = FYTxtConfig.observe { `EditSubRules`.fmt(args) }

            /** 额外字段 */
            val `ExtraFields`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Extra Fields"""
                            `MLangTags`.ZH -> """额外字段"""
                            else -> null
                        }
                    } ?: """额外字段"""

            /** 额外字段 */
            @Composable
            fun `ExtraFields`(vararg args: Any?) = FYTxtConfig.observe { `ExtraFields`.fmt(args) }

            /** 新增额外字段 */
            val `AddExtraField`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Extra Field"""
                            `MLangTags`.ZH -> """新增额外字段"""
                            else -> null
                        }
                    } ?: """新增额外字段"""

            /** 新增额外字段 */
            @Composable
            fun `AddExtraField`(vararg args: Any?) =
                FYTxtConfig.observe { `AddExtraField`.fmt(args) }

            /** 编辑额外字段 */
            val `EditExtraField`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Extra Field"""
                            `MLangTags`.ZH -> """编辑额外字段"""
                            else -> null
                        }
                    } ?: """编辑额外字段"""

            /** 编辑额外字段 */
            @Composable
            fun `EditExtraField`(vararg args: Any?) =
                FYTxtConfig.observe { `EditExtraField`.fmt(args) }

            /** 新增 health-check 额外字段 */
            val `AddHealthCheckField`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add health-check extra field"""
                            `MLangTags`.ZH -> """新增 health-check 额外字段"""
                            else -> null
                        }
                    } ?: """新增 health-check 额外字段"""

            /** 新增 health-check 额外字段 */
            @Composable
            fun `AddHealthCheckField`(vararg args: Any?) =
                FYTxtConfig.observe { `AddHealthCheckField`.fmt(args) }

            /** 编辑 health-check 额外字段 */
            val `EditHealthCheckField`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit health-check extra field"""
                            `MLangTags`.ZH -> """编辑 health-check 额外字段"""
                            else -> null
                        }
                    } ?: """编辑 health-check 额外字段"""

            /** 编辑 health-check 额外字段 */
            @Composable
            fun `EditHealthCheckField`(vararg args: Any?) =
                FYTxtConfig.observe { `EditHealthCheckField`.fmt(args) }

            /** 新增 override 额外字段 */
            val `AddOverrideField`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add override extra field"""
                            `MLangTags`.ZH -> """新增 override 额外字段"""
                            else -> null
                        }
                    } ?: """新增 override 额外字段"""

            /** 新增 override 额外字段 */
            @Composable
            fun `AddOverrideField`(vararg args: Any?) =
                FYTxtConfig.observe { `AddOverrideField`.fmt(args) }

            /** 编辑 override 额外字段 */
            val `EditOverrideField`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit override extra field"""
                            `MLangTags`.ZH -> """编辑 override 额外字段"""
                            else -> null
                        }
                    } ?: """编辑 override 额外字段"""

            /** 编辑 override 额外字段 */
            @Composable
            fun `EditOverrideField`(vararg args: Any?) =
                FYTxtConfig.observe { `EditOverrideField`.fmt(args) }

            /** Health Check 开关 */
            val `HealthCheckSwitch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Health Check Switch"""
                            `MLangTags`.ZH -> """Health Check 开关"""
                            else -> null
                        }
                    } ?: """Health Check 开关"""

            /** Health Check 开关 */
            @Composable
            fun `HealthCheckSwitch`(vararg args: Any?) =
                FYTxtConfig.observe { `HealthCheckSwitch`.fmt(args) }

            /** Health Check 额外字段 */
            val `HealthCheckFields`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Health Check Extra Fields"""
                            `MLangTags`.ZH -> """Health Check 额外字段"""
                            else -> null
                        }
                    } ?: """Health Check 额外字段"""

            /** Health Check 额外字段 */
            @Composable
            fun `HealthCheckFields`(vararg args: Any?) =
                FYTxtConfig.observe { `HealthCheckFields`.fmt(args) }

            /** Override 开关 */
            val `OverrideSwitch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Override Switch"""
                            `MLangTags`.ZH -> """Override 开关"""
                            else -> null
                        }
                    } ?: """Override 开关"""

            /** Override 开关 */
            @Composable
            fun `OverrideSwitch`(vararg args: Any?) =
                FYTxtConfig.observe { `OverrideSwitch`.fmt(args) }

            /** Override 额外字段 */
            val `OverrideFields`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Override Extra Fields"""
                            `MLangTags`.ZH -> """Override 额外字段"""
                            else -> null
                        }
                    } ?: """Override 额外字段"""

            /** Override 额外字段 */
            @Composable
            fun `OverrideFields`(vararg args: Any?) =
                FYTxtConfig.observe { `OverrideFields`.fmt(args) }

            /** 布尔选项 */
            val `BooleanOptions`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Boolean Options"""
                            `MLangTags`.ZH -> """布尔选项"""
                            else -> null
                        }
                    } ?: """布尔选项"""

            /** 布尔选项 */
            @Composable
            fun `BooleanOptions`(vararg args: Any?) =
                FYTxtConfig.observe { `BooleanOptions`.fmt(args) }

            /** 每行一个 header，格式：Key: value1 | value2 */
            val `HeaderHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Enter one header per line in the form Key: value. If a header has multiple values, separate them with |"""
                            `MLangTags`.ZH ->
                                """每行填写一个 header，写法为 Key: value。一个 header 有多个值时，用 | 分隔"""
                            else -> null
                        }
                    } ?: """每行填写一个 header，写法为 Key: value。一个 header 有多个值时，用 | 分隔"""

            /** 每行填写一个 header，写法为 Key: value。一个 header 有多个值时，用 | 分隔 */
            @Composable
            fun `HeaderHint`(vararg args: Any?) = FYTxtConfig.observe { `HeaderHint`.fmt(args) }

            /** 配置名称 */
            val `ConfigName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Config Name"""
                            `MLangTags`.ZH -> """配置名称"""
                            else -> null
                        }
                    } ?: """配置名称"""

            /** 配置名称 */
            @Composable
            fun `ConfigName`(vararg args: Any?) = FYTxtConfig.observe { `ConfigName`.fmt(args) }

            /** 配置说明 */
            val `ConfigDescription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Config Description"""
                            `MLangTags`.ZH -> """配置说明"""
                            else -> null
                        }
                    } ?: """配置说明"""

            /** 配置说明 */
            @Composable
            fun `ConfigDescription`(vararg args: Any?) =
                FYTxtConfig.observe { `ConfigDescription`.fmt(args) }

            /** 预设分流模板 */
            val `PresetTemplate`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Preset Routing Template"""
                            `MLangTags`.ZH -> """预设分流模板"""
                            else -> null
                        }
                    } ?: """预设分流模板"""

            /** 预设分流模板 */
            @Composable
            fun `PresetTemplate`(vararg args: Any?) =
                FYTxtConfig.observe { `PresetTemplate`.fmt(args) }

            /** 官方 MRS 常用分流 */
            val `OfficialMrs`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Official MRS Common Routing"""
                            `MLangTags`.ZH -> """官方 MRS 常用分流"""
                            else -> null
                        }
                    } ?: """官方 MRS 常用分流"""

            /** 官方 MRS 常用分流 */
            @Composable
            fun `OfficialMrs`(vararg args: Any?) = FYTxtConfig.observe { `OfficialMrs`.fmt(args) }

            /** 顶部模板编辑器，支持地区自动组和每个分流项单独开关；应用时会重建当前覆写里的规则三块。 */
            val `OfficialMrsSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Use Mihomo's official meta/geo mrs rulesets. Supports regional auto-groups and per-item switches. Applying it rebuilds the rule providers, proxy groups, and rules in the current override."""
                            `MLangTags`.ZH ->
                                """使用 Mihomo 官方 meta/geo mrs 规则集，支持地区自动分组和逐项开关。应用后会重建当前覆写中的规则提供者、策略组和规则。"""
                            else -> null
                        }
                    } ?: """使用 Mihomo 官方 meta/geo mrs 规则集，支持地区自动分组和逐项开关。应用后会重建当前覆写中的规则提供者、策略组和规则。"""

            /** 使用 Mihomo 官方 meta/geo mrs 规则集，支持地区自动分组和逐项开关。应用后会重建当前覆写中的规则提供者、策略组和规则。 */
            @Composable
            fun `OfficialMrsSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `OfficialMrsSummary`.fmt(args) }

            /** 配置分区 */
            val `ConfigSections`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Config Sections"""
                            `MLangTags`.ZH -> """配置分区"""
                            else -> null
                        }
                    } ?: """配置分区"""

            /** 配置分区 */
            @Composable
            fun `ConfigSections`(vararg args: Any?) =
                FYTxtConfig.observe { `ConfigSections`.fmt(args) }

            /** 应用 */
            val `Apply`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Apply"""
                            `MLangTags`.ZH -> """应用"""
                            else -> null
                        }
                    } ?: """应用"""

            /** 应用 */
            @Composable fun `Apply`(vararg args: Any?) = FYTxtConfig.observe { `Apply`.fmt(args) }

            /** 地区自动组 */
            val `RegionalAutoGroup`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Regional Auto Group"""
                            `MLangTags`.ZH -> """地区自动组"""
                            else -> null
                        }
                    } ?: """地区自动组"""

            /** 地区自动组 */
            @Composable
            fun `RegionalAutoGroup`(vararg args: Any?) =
                FYTxtConfig.observe { `RegionalAutoGroup`.fmt(args) }

            /** 基础分流 */
            val `BasicRouting`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Basic Routing"""
                            `MLangTags`.ZH -> """基础分流"""
                            else -> null
                        }
                    } ?: """基础分流"""

            /** 基础分流 */
            @Composable
            fun `BasicRouting`(vararg args: Any?) = FYTxtConfig.observe { `BasicRouting`.fmt(args) }

            /** 服务分流 */
            val `ServiceRouting`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Service Routing"""
                            `MLangTags`.ZH -> """服务分流"""
                            else -> null
                        }
                    } ?: """服务分流"""

            /** 服务分流 */
            @Composable
            fun `ServiceRouting`(vararg args: Any?) =
                FYTxtConfig.observe { `ServiceRouting`.fmt(args) }

            /** 香港自动组 */
            val `RegionHongKong`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Hong Kong Auto Group"""
                            `MLangTags`.ZH -> """香港自动组"""
                            else -> null
                        }
                    } ?: """香港自动组"""

            /** 香港自动组 */
            @Composable
            fun `RegionHongKong`(vararg args: Any?) =
                FYTxtConfig.observe { `RegionHongKong`.fmt(args) }

            /** 台湾自动组 */
            val `RegionTaiwan`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Taiwan Auto Group"""
                            `MLangTags`.ZH -> """台湾自动组"""
                            else -> null
                        }
                    } ?: """台湾自动组"""

            /** 台湾自动组 */
            @Composable
            fun `RegionTaiwan`(vararg args: Any?) = FYTxtConfig.observe { `RegionTaiwan`.fmt(args) }

            /** 日本自动组 */
            val `RegionJapan`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Japan Auto Group"""
                            `MLangTags`.ZH -> """日本自动组"""
                            else -> null
                        }
                    } ?: """日本自动组"""

            /** 日本自动组 */
            @Composable
            fun `RegionJapan`(vararg args: Any?) = FYTxtConfig.observe { `RegionJapan`.fmt(args) }

            /** 新加坡自动组 */
            val `RegionSingapore`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Singapore Auto Group"""
                            `MLangTags`.ZH -> """新加坡自动组"""
                            else -> null
                        }
                    } ?: """新加坡自动组"""

            /** 新加坡自动组 */
            @Composable
            fun `RegionSingapore`(vararg args: Any?) =
                FYTxtConfig.observe { `RegionSingapore`.fmt(args) }

            /** 美国自动组 */
            val `RegionUnitedStates`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """United States Auto Group"""
                            `MLangTags`.ZH -> """美国自动组"""
                            else -> null
                        }
                    } ?: """美国自动组"""

            /** 美国自动组 */
            @Composable
            fun `RegionUnitedStates`(vararg args: Any?) =
                FYTxtConfig.observe { `RegionUnitedStates`.fmt(args) }

            /** 广告拦截 */
            val `ItemAds`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Ad Blocking"""
                            `MLangTags`.ZH -> """广告拦截"""
                            else -> null
                        }
                    } ?: """广告拦截"""

            /** 广告拦截 */
            @Composable
            fun `ItemAds`(vararg args: Any?) = FYTxtConfig.observe { `ItemAds`.fmt(args) }

            /** 私有地址直连 */
            val `ItemPrivate`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Private Network Direct"""
                            `MLangTags`.ZH -> """私有地址直连"""
                            else -> null
                        }
                    } ?: """私有地址直连"""

            /** 私有地址直连 */
            @Composable
            fun `ItemPrivate`(vararg args: Any?) = FYTxtConfig.observe { `ItemPrivate`.fmt(args) }

            /** Google */
            val `ItemGoogle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Google"""
                            `MLangTags`.ZH -> """Google"""
                            else -> null
                        }
                    } ?: """Google"""

            /** Google */
            @Composable
            fun `ItemGoogle`(vararg args: Any?) = FYTxtConfig.observe { `ItemGoogle`.fmt(args) }

            /** Telegram */
            val `ItemTelegram`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Telegram"""
                            `MLangTags`.ZH -> """Telegram"""
                            else -> null
                        }
                    } ?: """Telegram"""

            /** Telegram */
            @Composable
            fun `ItemTelegram`(vararg args: Any?) = FYTxtConfig.observe { `ItemTelegram`.fmt(args) }

            /** GitHub */
            val `ItemGitHub`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GitHub"""
                            `MLangTags`.ZH -> """GitHub"""
                            else -> null
                        }
                    } ?: """GitHub"""

            /** GitHub */
            @Composable
            fun `ItemGitHub`(vararg args: Any?) = FYTxtConfig.observe { `ItemGitHub`.fmt(args) }

            /** Microsoft */
            val `ItemMicrosoft`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Microsoft"""
                            `MLangTags`.ZH -> """Microsoft"""
                            else -> null
                        }
                    } ?: """Microsoft"""

            /** Microsoft */
            @Composable
            fun `ItemMicrosoft`(vararg args: Any?) =
                FYTxtConfig.observe { `ItemMicrosoft`.fmt(args) }

            /** Apple */
            val `ItemApple`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Apple"""
                            `MLangTags`.ZH -> """Apple"""
                            else -> null
                        }
                    } ?: """Apple"""

            /** Apple */
            @Composable
            fun `ItemApple`(vararg args: Any?) = FYTxtConfig.observe { `ItemApple`.fmt(args) }

            /** YouTube */
            val `ItemYouTube`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """YouTube"""
                            `MLangTags`.ZH -> """YouTube"""
                            else -> null
                        }
                    } ?: """YouTube"""

            /** YouTube */
            @Composable
            fun `ItemYouTube`(vararg args: Any?) = FYTxtConfig.observe { `ItemYouTube`.fmt(args) }

            /** Netflix */
            val `ItemNetflix`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Netflix"""
                            `MLangTags`.ZH -> """Netflix"""
                            else -> null
                        }
                    } ?: """Netflix"""

            /** Netflix */
            @Composable
            fun `ItemNetflix`(vararg args: Any?) = FYTxtConfig.observe { `ItemNetflix`.fmt(args) }

            /** Spotify */
            val `ItemSpotify`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Spotify"""
                            `MLangTags`.ZH -> """Spotify"""
                            else -> null
                        }
                    } ?: """Spotify"""

            /** Spotify */
            @Composable
            fun `ItemSpotify`(vararg args: Any?) = FYTxtConfig.observe { `ItemSpotify`.fmt(args) }

            /** OpenAI */
            val `ItemOpenAI`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """OpenAI"""
                            `MLangTags`.ZH -> """OpenAI"""
                            else -> null
                        }
                    } ?: """OpenAI"""

            /** OpenAI */
            @Composable
            fun `ItemOpenAI`(vararg args: Any?) = FYTxtConfig.observe { `ItemOpenAI`.fmt(args) }

            /** Steam */
            val `ItemSteam`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Steam"""
                            `MLangTags`.ZH -> """Steam"""
                            else -> null
                        }
                    } ?: """Steam"""

            /** Steam */
            @Composable
            fun `ItemSteam`(vararg args: Any?) = FYTxtConfig.observe { `ItemSteam`.fmt(args) }

            /** 中国大陆直连 */
            val `ItemCn`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Mainland China Direct"""
                            `MLangTags`.ZH -> """中国大陆直连"""
                            else -> null
                        }
                    } ?: """中国大陆直连"""

            /** 中国大陆直连 */
            @Composable fun `ItemCn`(vararg args: Any?) = FYTxtConfig.observe { `ItemCn`.fmt(args) }

            /** 代理规则集 */
            val `ItemProxy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Ruleset"""
                            `MLangTags`.ZH -> """代理规则集"""
                            else -> null
                        }
                    } ?: """代理规则集"""

            /** 代理规则集 */
            @Composable
            fun `ItemProxy`(vararg args: Any?) = FYTxtConfig.observe { `ItemProxy`.fmt(args) }

            /** 境外地理规则 */
            val `ItemGeolocationNotCn`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Overseas Geolocation Rules"""
                            `MLangTags`.ZH -> """境外地理规则"""
                            else -> null
                        }
                    } ?: """境外地理规则"""

            /** 境外地理规则 */
            @Composable
            fun `ItemGeolocationNotCn`(vararg args: Any?) =
                FYTxtConfig.observe { `ItemGeolocationNotCn`.fmt(args) }

            /** 兜底 MATCH */
            val `ItemMatch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Final MATCH"""
                            `MLangTags`.ZH -> """兜底 MATCH"""
                            else -> null
                        }
                    } ?: """兜底 MATCH"""

            /** 兜底 MATCH */
            @Composable
            fun `ItemMatch`(vararg args: Any?) = FYTxtConfig.observe { `ItemMatch`.fmt(args) }

            /** 外部资源 URL */
            val `RemoteSourceUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Remote Resource URL"""
                            `MLangTags`.ZH -> """外部资源 URL"""
                            else -> null
                        }
                    } ?: """外部资源 URL"""

            /** 外部资源 URL */
            @Composable
            fun `RemoteSourceUrl`(vararg args: Any?) =
                FYTxtConfig.observe { `RemoteSourceUrl`.fmt(args) }

            /** 更新间隔（秒） */
            val `RemoteUpdateInterval`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update Interval (seconds)"""
                            `MLangTags`.ZH -> """更新间隔（秒）"""
                            else -> null
                        }
                    } ?: """更新间隔（秒）"""

            /** 更新间隔（秒） */
            @Composable
            fun `RemoteUpdateInterval`(vararg args: Any?) =
                FYTxtConfig.observe { `RemoteUpdateInterval`.fmt(args) }

            /** 最小 60，默认 86400 */
            val `RemoteUpdateIntervalPlaceholder`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Minimum 60, default 86400"""
                            `MLangTags`.ZH -> """最小 60，默认 86400"""
                            else -> null
                        }
                    } ?: """最小 60，默认 86400"""

            /** 最小 60，默认 86400 */
            @Composable
            fun `RemoteUpdateIntervalPlaceholder`(vararg args: Any?) =
                FYTxtConfig.observe { `RemoteUpdateIntervalPlaceholder`.fmt(args) }

            /** 点击添加额外字段 */
            val `ClickToAddExtraField`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Click to add extra field"""
                            `MLangTags`.ZH -> """点击添加额外字段"""
                            else -> null
                        }
                    } ?: """点击添加额外字段"""

            /** 点击添加额外字段 */
            @Composable
            fun `ClickToAddExtraField`(vararg args: Any?) =
                FYTxtConfig.observe { `ClickToAddExtraField`.fmt(args) }

            /** 删除额外字段 */
            val `DeleteExtraField`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete extra field"""
                            `MLangTags`.ZH -> """删除额外字段"""
                            else -> null
                        }
                    } ?: """删除额外字段"""

            /** 删除额外字段 */
            @Composable
            fun `DeleteExtraField`(vararg args: Any?) =
                FYTxtConfig.observe { `DeleteExtraField`.fmt(args) }

            /** 值类型 */
            val `ValueType`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Value Type"""
                            `MLangTags`.ZH -> """值类型"""
                            else -> null
                        }
                    } ?: """值类型"""

            /** 值类型 */
            @Composable
            fun `ValueType`(vararg args: Any?) = FYTxtConfig.observe { `ValueType`.fmt(args) }

            /** 字符串值 */
            val `StringValue`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """String value"""
                            `MLangTags`.ZH -> """字符串值"""
                            else -> null
                        }
                    } ?: """字符串值"""

            /** 字符串值 */
            @Composable
            fun `StringValue`(vararg args: Any?) = FYTxtConfig.observe { `StringValue`.fmt(args) }

            /** 整数值 */
            val `IntValue`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Integer value"""
                            `MLangTags`.ZH -> """整数值"""
                            else -> null
                        }
                    } ?: """整数值"""

            /** 整数值 */
            @Composable
            fun `IntValue`(vararg args: Any?) = FYTxtConfig.observe { `IntValue`.fmt(args) }

            /** 浮点数值 */
            val `DoubleValue`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Double value"""
                            `MLangTags`.ZH -> """浮点数值"""
                            else -> null
                        }
                    } ?: """浮点数值"""

            /** 浮点数值 */
            @Composable
            fun `DoubleValue`(vararg args: Any?) = FYTxtConfig.observe { `DoubleValue`.fmt(args) }

            /** 单个 JSON 片段 */
            val `JsonFragment`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Single JSON fragment"""
                            `MLangTags`.ZH -> """单个 JSON 片段"""
                            else -> null
                        }
                    } ?: """单个 JSON 片段"""

            /** 单个 JSON 片段 */
            @Composable
            fun `JsonFragment`(vararg args: Any?) = FYTxtConfig.observe { `JsonFragment`.fmt(args) }

            /** 键名不能为空 */
            val `KeyNameEmpty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Key name cannot be empty"""
                            `MLangTags`.ZH -> """键名不能为空"""
                            else -> null
                        }
                    } ?: """键名不能为空"""

            /** 键名不能为空 */
            @Composable
            fun `KeyNameEmpty`(vararg args: Any?) = FYTxtConfig.observe { `KeyNameEmpty`.fmt(args) }

            /** 当前值与所选类型不匹配 */
            val `ValueTypeMismatch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Current value does not match selected type"""
                            `MLangTags`.ZH -> """当前值与所选类型不匹配"""
                            else -> null
                        }
                    } ?: """当前值与所选类型不匹配"""

            /** 当前值与所选类型不匹配 */
            @Composable
            fun `ValueTypeMismatch`(vararg args: Any?) =
                FYTxtConfig.observe { `ValueTypeMismatch`.fmt(args) }
        }

        object `Form` {
            init {
                `MLangGroups`
            }

            /** 规则链 */
            val `RuleChain`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule Chain"""
                            `MLangTags`.ZH -> """规则链"""
                            else -> null
                        }
                    } ?: """规则链"""

            /** 规则链 */
            @Composable
            fun `RuleChain`(vararg args: Any?) = FYTxtConfig.observe { `RuleChain`.fmt(args) }

            /** 未设置规则链 */
            val `RuleChainNotSet`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule chain not set"""
                            `MLangTags`.ZH -> """未设置规则链"""
                            else -> null
                        }
                    } ?: """未设置规则链"""

            /** 未设置规则链 */
            @Composable
            fun `RuleChainNotSet`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleChainNotSet`.fmt(args) }

            /** 子规则 */
            val `SubRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Sub Rules"""
                            `MLangTags`.ZH -> """子规则"""
                            else -> null
                        }
                    } ?: """子规则"""

            /** 子规则 */
            @Composable
            fun `SubRules`(vararg args: Any?) = FYTxtConfig.observe { `SubRules`.fmt(args) }

            /** 结构化规则组 */
            val `SubRulesHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Use the structured form to manage sub-rule groups"""
                            `MLangTags`.ZH -> """使用结构化表单管理子规则组"""
                            else -> null
                        }
                    } ?: """使用结构化表单管理子规则组"""

            /** 使用结构化表单管理子规则组 */
            @Composable
            fun `SubRulesHint`(vararg args: Any?) = FYTxtConfig.observe { `SubRulesHint`.fmt(args) }

            /** 复杂子规则结构统一收在高级 JSON 中 */
            val `SubRulesAdvanced`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Complex sub rule structures are collected in Advanced JSON"""
                            `MLangTags`.ZH -> """复杂子规则结构统一收在高级 JSON 中"""
                            else -> null
                        }
                    } ?: """复杂子规则结构统一收在高级 JSON 中"""

            /** 复杂子规则结构统一收在高级 JSON 中 */
            @Composable
            fun `SubRulesAdvanced`(vararg args: Any?) =
                FYTxtConfig.observe { `SubRulesAdvanced`.fmt(args) }

            /** 规则提供者 */
            val `RuleProviders`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule Providers"""
                            `MLangTags`.ZH -> """规则提供者"""
                            else -> null
                        }
                    } ?: """规则提供者"""

            /** 规则提供者 */
            @Composable
            fun `RuleProviders`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleProviders`.fmt(args) }

            /** 结构化 Provider */
            val `RuleProvidersHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Use the structured form to manage rule providers"""
                            `MLangTags`.ZH -> """使用结构化表单管理规则提供者"""
                            else -> null
                        }
                    } ?: """使用结构化表单管理规则提供者"""

            /** 使用结构化表单管理规则提供者 */
            @Composable
            fun `RuleProvidersHint`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleProvidersHint`.fmt(args) }

            /** 需要复杂 Provider 字段时再进入高级 JSON */
            val `RuleProvidersAdvanced`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Enter Advanced JSON when complex Provider fields are needed"""
                            `MLangTags`.ZH -> """需要复杂 Provider 字段时再进入高级 JSON"""
                            else -> null
                        }
                    } ?: """需要复杂 Provider 字段时再进入高级 JSON"""

            /** 需要复杂 Provider 字段时再进入高级 JSON */
            @Composable
            fun `RuleProvidersAdvanced`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleProvidersAdvanced`.fmt(args) }

            /** 代理节点 */
            val `ProxyNodes`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Nodes"""
                            `MLangTags`.ZH -> """代理节点"""
                            else -> null
                        }
                    } ?: """代理节点"""

            /** 代理节点 */
            @Composable
            fun `ProxyNodes`(vararg args: Any?) = FYTxtConfig.observe { `ProxyNodes`.fmt(args) }

            /** 结构化代理条目 */
            val `ProxyNodesHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Use the structured form to manage proxy nodes"""
                            `MLangTags`.ZH -> """使用结构化表单管理代理节点"""
                            else -> null
                        }
                    } ?: """使用结构化表单管理代理节点"""

            /** 使用结构化表单管理代理节点 */
            @Composable
            fun `ProxyNodesHint`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyNodesHint`.fmt(args) }

            /** 代理提供者 */
            val `ProxyProviders`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Providers"""
                            `MLangTags`.ZH -> """代理提供者"""
                            else -> null
                        }
                    } ?: """代理提供者"""

            /** 代理提供者 */
            @Composable
            fun `ProxyProviders`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyProviders`.fmt(args) }

            /** 结构化 Provider */
            val `ProxyProvidersHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Use the structured form to manage proxy providers"""
                            `MLangTags`.ZH -> """使用结构化表单管理代理提供者"""
                            else -> null
                        }
                    } ?: """使用结构化表单管理代理提供者"""

            /** 使用结构化表单管理代理提供者 */
            @Composable
            fun `ProxyProvidersHint`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyProvidersHint`.fmt(args) }

            /** 需要协议细节、校验或额外字段时再进入高级 JSON */
            val `ProxyProvidersAdvanced`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Enter Advanced JSON when protocol details, validation or extra fields are needed"""
                            `MLangTags`.ZH -> """需要协议细节、校验或额外字段时再进入高级 JSON"""
                            else -> null
                        }
                    } ?: """需要协议细节、校验或额外字段时再进入高级 JSON"""

            /** 需要协议细节、校验或额外字段时再进入高级 JSON */
            @Composable
            fun `ProxyProvidersAdvanced`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyProvidersAdvanced`.fmt(args) }

            /** 策略组 */
            val `ProxyGroups`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Groups"""
                            `MLangTags`.ZH -> """策略组"""
                            else -> null
                        }
                    } ?: """策略组"""

            /** 策略组 */
            @Composable
            fun `ProxyGroups`(vararg args: Any?) = FYTxtConfig.observe { `ProxyGroups`.fmt(args) }

            /** 结构化策略组 */
            val `ProxyGroupsHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Use the structured form to manage proxy groups"""
                            `MLangTags`.ZH -> """使用结构化表单管理策略组"""
                            else -> null
                        }
                    } ?: """使用结构化表单管理策略组"""

            /** 使用结构化表单管理策略组 */
            @Composable
            fun `ProxyGroupsHint`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyGroupsHint`.fmt(args) }

            /** %s · 结构化编辑 */
            val `StructuredEdit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%s · Structured Edit"""
                            `MLangTags`.ZH -> """%s · 结构化编辑"""
                            else -> null
                        }
                    } ?: """%s · 结构化编辑"""

            /** %s · 结构化编辑 */
            @Composable
            fun `StructuredEdit`(vararg args: Any?) =
                FYTxtConfig.observe { `StructuredEdit`.fmt(args) }

            /** %s · 高级 JSON */
            val `AdvancedJson`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%s · Advanced JSON"""
                            `MLangTags`.ZH -> """%s · 高级 JSON"""
                            else -> null
                        }
                    } ?: """%s · 高级 JSON"""

            /** %s · 高级 JSON */
            @Composable
            fun `AdvancedJson`(vararg args: Any?) = FYTxtConfig.observe { `AdvancedJson`.fmt(args) }

            /** 打开高级编辑 */
            val `OpenAdvancedEdit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Open Advanced Edit"""
                            `MLangTags`.ZH -> """打开高级编辑"""
                            else -> null
                        }
                    } ?: """打开高级编辑"""

            /** 打开高级编辑 */
            @Composable
            fun `OpenAdvancedEdit`(vararg args: Any?) =
                FYTxtConfig.observe { `OpenAdvancedEdit`.fmt(args) }

            /** 直接编辑原始对象，用于补充结构化表单未覆盖的字段 */
            val `OpenAdvancedEditSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Edit the raw JSON of the current object directly for fields not covered by the structured form"""
                            `MLangTags`.ZH -> """直接编辑当前对象的原始 JSON，适合补充结构化表单未覆盖的字段"""
                            else -> null
                        }
                    } ?: """直接编辑当前对象的原始 JSON，适合补充结构化表单未覆盖的字段"""

            /** 直接编辑当前对象的原始 JSON，适合补充结构化表单未覆盖的字段 */
            @Composable
            fun `OpenAdvancedEditSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `OpenAdvancedEditSummary`.fmt(args) }

            /** 已配置 %d 项 */
            val `ItemsConfigured`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d items configured"""
                            `MLangTags`.ZH -> """已配置%d项"""
                            else -> null
                        }
                    } ?: """已配置%d项"""

            /** 已配置 %d 项 */
            @Composable
            fun `ItemsConfigured`(vararg args: Any?) =
                FYTxtConfig.observe { `ItemsConfigured`.fmt(args) }

            /** 代理端口 */
            val `ProxyPorts`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Ports"""
                            `MLangTags`.ZH -> """代理端口"""
                            else -> null
                        }
                    } ?: """代理端口"""

            /** 代理端口 */
            @Composable
            fun `ProxyPorts`(vararg args: Any?) = FYTxtConfig.observe { `ProxyPorts`.fmt(args) }

            /** 运行与日志 */
            val `RunAndLog`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Run & Log"""
                            `MLangTags`.ZH -> """运行与日志"""
                            else -> null
                        }
                    } ?: """运行与日志"""

            /** 运行与日志 */
            @Composable
            fun `RunAndLog`(vararg args: Any?) = FYTxtConfig.observe { `RunAndLog`.fmt(args) }

            /** 进程匹配模式 */
            val `ProcessMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Process Match Mode"""
                            `MLangTags`.ZH -> """进程匹配模式"""
                            else -> null
                        }
                    } ?: """进程匹配模式"""

            /** 进程匹配模式 */
            @Composable
            fun `ProcessMode`(vararg args: Any?) = FYTxtConfig.observe { `ProcessMode`.fmt(args) }

            /** 不修改 */
            val `NotModify`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Don't Modify"""
                            `MLangTags`.ZH -> """不修改"""
                            else -> null
                        }
                    } ?: """不修改"""

            /** 不修改 */
            @Composable
            fun `NotModify`(vararg args: Any?) = FYTxtConfig.observe { `NotModify`.fmt(args) }

            /** 统一延迟 */
            val `UnifiedDelay`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unified Delay"""
                            `MLangTags`.ZH -> """统一延迟"""
                            else -> null
                        }
                    } ?: """统一延迟"""

            /** 统一延迟 */
            @Composable
            fun `UnifiedDelay`(vararg args: Any?) = FYTxtConfig.observe { `UnifiedDelay`.fmt(args) }

            /** TCP 并发 */
            val `TcpConcurrent`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """TCP Concurrent"""
                            `MLangTags`.ZH -> """TCP 并发"""
                            else -> null
                        }
                    } ?: """TCP 并发"""

            /** TCP 并发 */
            @Composable
            fun `TcpConcurrent`(vararg args: Any?) =
                FYTxtConfig.observe { `TcpConcurrent`.fmt(args) }

            /** Geodata 模式 */
            val `GeodataMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Geodata Mode"""
                            `MLangTags`.ZH -> """Geodata 模式"""
                            else -> null
                        }
                    } ?: """Geodata 模式"""

            /** Geodata 模式 */
            @Composable
            fun `GeodataMode`(vararg args: Any?) = FYTxtConfig.observe { `GeodataMode`.fmt(args) }

            /** 运行与日志补充 */
            val `RunAndLogExtra`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Run & Log Extra"""
                            `MLangTags`.ZH -> """运行与日志补充"""
                            else -> null
                        }
                    } ?: """运行与日志补充"""

            /** 运行与日志补充 */
            @Composable
            fun `RunAndLogExtra`(vararg args: Any?) =
                FYTxtConfig.observe { `RunAndLogExtra`.fmt(args) }

            /** 秒 */
            val `Seconds`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """seconds"""
                            `MLangTags`.ZH -> """秒"""
                            else -> null
                        }
                    } ?: """秒"""

            /** 秒 */
            @Composable
            fun `Seconds`(vararg args: Any?) = FYTxtConfig.observe { `Seconds`.fmt(args) }

            /** 连接与网络 */
            val `ConnectionNetwork`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Connection & Network"""
                            `MLangTags`.ZH -> """连接与网络"""
                            else -> null
                        }
                    } ?: """连接与网络"""

            /** 连接与网络 */
            @Composable
            fun `ConnectionNetwork`(vararg args: Any?) =
                FYTxtConfig.observe { `ConnectionNetwork`.fmt(args) }

            /** 出站接口 */
            val `OutboundInterface`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Outbound Interface"""
                            `MLangTags`.ZH -> """出站接口"""
                            else -> null
                        }
                    } ?: """出站接口"""

            /** 出站接口 */
            @Composable
            fun `OutboundInterface`(vararg args: Any?) =
                FYTxtConfig.observe { `OutboundInterface`.fmt(args) }

            /** 路由标记 */
            val `RoutingMark`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Routing Mark"""
                            `MLangTags`.ZH -> """路由标记"""
                            else -> null
                        }
                    } ?: """路由标记"""

            /** 路由标记 */
            @Composable
            fun `RoutingMark`(vararg args: Any?) = FYTxtConfig.observe { `RoutingMark`.fmt(args) }

            /** Geosite 匹配器 */
            val `GeositeMatcher`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Geosite Matcher"""
                            `MLangTags`.ZH -> """Geosite 匹配器"""
                            else -> null
                        }
                    } ?: """Geosite 匹配器"""

            /** Geosite 匹配器 */
            @Composable
            fun `GeositeMatcher`(vararg args: Any?) =
                FYTxtConfig.observe { `GeositeMatcher`.fmt(args) }

            /** 全局客户端指纹 */
            val `GlobalClientFingerprint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Global Client Fingerprint"""
                            `MLangTags`.ZH -> """全局客户端指纹"""
                            else -> null
                        }
                    } ?: """全局客户端指纹"""

            /** 全局客户端指纹 */
            @Composable
            fun `GlobalClientFingerprint`(vararg args: Any?) =
                FYTxtConfig.observe { `GlobalClientFingerprint`.fmt(args) }

            /** 局域网访问 */
            val `LanAccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """LAN Access"""
                            `MLangTags`.ZH -> """局域网访问"""
                            else -> null
                        }
                    } ?: """局域网访问"""

            /** 局域网访问 */
            @Composable
            fun `LanAccess`(vararg args: Any?) = FYTxtConfig.observe { `LanAccess`.fmt(args) }

            /** 允许 IP 段 */
            val `AllowedIPs`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allowed IPs"""
                            `MLangTags`.ZH -> """允许 IP 段"""
                            else -> null
                        }
                    } ?: """允许 IP 段"""

            /** 允许 IP 段 */
            @Composable
            fun `AllowedIPs`(vararg args: Any?) = FYTxtConfig.observe { `AllowedIPs`.fmt(args) }

            /** 禁止 IP 段 */
            val `DisallowedIPs`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Disallowed IPs"""
                            `MLangTags`.ZH -> """禁止 IP 段"""
                            else -> null
                        }
                    } ?: """禁止 IP 段"""

            /** 禁止 IP 段 */
            @Composable
            fun `DisallowedIPs`(vararg args: Any?) =
                FYTxtConfig.observe { `DisallowedIPs`.fmt(args) }

            /** 局域网地址 */
            val `LanAddress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """LAN Address"""
                            `MLangTags`.ZH -> """局域网地址"""
                            else -> null
                        }
                    } ?: """局域网地址"""

            /** 局域网地址 */
            @Composable
            fun `LanAddress`(vararg args: Any?) = FYTxtConfig.observe { `LanAddress`.fmt(args) }

            /** 绑定地址 */
            val `BindAddress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Bind Address"""
                            `MLangTags`.ZH -> """绑定地址"""
                            else -> null
                        }
                    } ?: """绑定地址"""

            /** 绑定地址 */
            @Composable
            fun `BindAddress`(vararg args: Any?) = FYTxtConfig.observe { `BindAddress`.fmt(args) }

            /** 用户验证 */
            val `UserAuth`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """User Auth"""
                            `MLangTags`.ZH -> """用户验证"""
                            else -> null
                        }
                    } ?: """用户验证"""

            /** 用户验证 */
            @Composable
            fun `UserAuth`(vararg args: Any?) = FYTxtConfig.observe { `UserAuth`.fmt(args) }

            /** 跳过鉴权网段 */
            val `SkipAuthIPs`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Skip Auth IPs"""
                            `MLangTags`.ZH -> """跳过鉴权网段"""
                            else -> null
                        }
                    } ?: """跳过鉴权网段"""

            /** 跳过鉴权网段 */
            @Composable
            fun `SkipAuthIPs`(vararg args: Any?) = FYTxtConfig.observe { `SkipAuthIPs`.fmt(args) }

            /** 外部控制 */
            val `ExternalControl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """External Control"""
                            `MLangTags`.ZH -> """外部控制"""
                            else -> null
                        }
                    } ?: """外部控制"""

            /** 外部控制 */
            @Composable
            fun `ExternalControl`(vararg args: Any?) =
                FYTxtConfig.observe { `ExternalControl`.fmt(args) }

            /** 外部控制器 */
            val `ExternalController`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """External Controller"""
                            `MLangTags`.ZH -> """外部控制器"""
                            else -> null
                        }
                    } ?: """外部控制器"""

            /** 外部控制器 */
            @Composable
            fun `ExternalController`(vararg args: Any?) =
                FYTxtConfig.observe { `ExternalController`.fmt(args) }

            /** HTTPS 控制器 */
            val `ExternalControllerHttps`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """HTTPS Controller"""
                            `MLangTags`.ZH -> """HTTPS 控制器"""
                            else -> null
                        }
                    } ?: """HTTPS 控制器"""

            /** HTTPS 控制器 */
            @Composable
            fun `ExternalControllerHttps`(vararg args: Any?) =
                FYTxtConfig.observe { `ExternalControllerHttps`.fmt(args) }

            /** 外部 DoH 服务 */
            val `ExternalDoH`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """External DoH Service"""
                            `MLangTags`.ZH -> """外部 DoH 服务"""
                            else -> null
                        }
                    } ?: """外部 DoH 服务"""

            /** 外部 DoH 服务 */
            @Composable
            fun `ExternalDoH`(vararg args: Any?) = FYTxtConfig.observe { `ExternalDoH`.fmt(args) }

            /** API 访问密钥 */
            val `ApiSecret`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """API Secret"""
                            `MLangTags`.ZH -> """API 访问密钥"""
                            else -> null
                        }
                    } ?: """API 访问密钥"""

            /** API 访问密钥 */
            @Composable
            fun `ApiSecret`(vararg args: Any?) = FYTxtConfig.observe { `ApiSecret`.fmt(args) }

            /** 控制器 CORS */
            val `ControllerCors`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Controller CORS"""
                            `MLangTags`.ZH -> """控制器 CORS"""
                            else -> null
                        }
                    } ?: """控制器 CORS"""

            /** 控制器 CORS */
            @Composable
            fun `ControllerCors`(vararg args: Any?) =
                FYTxtConfig.observe { `ControllerCors`.fmt(args) }

            /** CORS 允许来源 */
            val `CorsAllowOrigins`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """CORS Allow Origins"""
                            `MLangTags`.ZH -> """CORS 允许来源"""
                            else -> null
                        }
                    } ?: """CORS 允许来源"""

            /** CORS 允许来源 */
            @Composable
            fun `CorsAllowOrigins`(vararg args: Any?) =
                FYTxtConfig.observe { `CorsAllowOrigins`.fmt(args) }

            /** 允许私有网络 */
            val `AllowPrivateNetwork`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allow Private Network"""
                            `MLangTags`.ZH -> """允许私有网络"""
                            else -> null
                        }
                    } ?: """允许私有网络"""

            /** 允许私有网络 */
            @Composable
            fun `AllowPrivateNetwork`(vararg args: Any?) =
                FYTxtConfig.observe { `AllowPrivateNetwork`.fmt(args) }

            /** 配置持久化 */
            val `ConfigPersistence`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Config Persistence"""
                            `MLangTags`.ZH -> """配置持久化"""
                            else -> null
                        }
                    } ?: """配置持久化"""

            /** 配置持久化 */
            @Composable
            fun `ConfigPersistence`(vararg args: Any?) =
                FYTxtConfig.observe { `ConfigPersistence`.fmt(args) }

            /** 保存策略组选择 */
            val `SaveGroupSelection`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Save Group Selection"""
                            `MLangTags`.ZH -> """保存策略组选择"""
                            else -> null
                        }
                    } ?: """保存策略组选择"""

            /** 保存策略组选择 */
            @Composable
            fun `SaveGroupSelection`(vararg args: Any?) =
                FYTxtConfig.observe { `SaveGroupSelection`.fmt(args) }

            /** 保存 Fake-IP 映射 */
            val `SaveFakeIpMapping`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Save Fake-IP Mapping"""
                            `MLangTags`.ZH -> """保存 Fake-IP 映射"""
                            else -> null
                        }
                    } ?: """保存 Fake-IP 映射"""

            /** 保存 Fake-IP 映射 */
            @Composable
            fun `SaveFakeIpMapping`(vararg args: Any?) =
                FYTxtConfig.observe { `SaveFakeIpMapping`.fmt(args) }

            /** GEO 资源开关 */
            val `GeoResources`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GEO Resources"""
                            `MLangTags`.ZH -> """GEO 资源开关"""
                            else -> null
                        }
                    } ?: """GEO 资源开关"""

            /** GEO 资源开关 */
            @Composable
            fun `GeoResources`(vararg args: Any?) = FYTxtConfig.observe { `GeoResources`.fmt(args) }

            /** 自动更新 GEO */
            val `AutoUpdateGeo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Update GEO"""
                            `MLangTags`.ZH -> """自动更新 GEO"""
                            else -> null
                        }
                    } ?: """自动更新 GEO"""

            /** 自动更新 GEO */
            @Composable
            fun `AutoUpdateGeo`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoUpdateGeo`.fmt(args) }

            /** GEO 更新间隔 */
            val `GeoUpdateInterval`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GEO Update Interval"""
                            `MLangTags`.ZH -> """GEO 更新间隔"""
                            else -> null
                        }
                    } ?: """GEO 更新间隔"""

            /** GEO 更新间隔 */
            @Composable
            fun `GeoUpdateInterval`(vararg args: Any?) =
                FYTxtConfig.observe { `GeoUpdateInterval`.fmt(args) }

            /** 小时 */
            val `Hours`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """hours"""
                            `MLangTags`.ZH -> """小时"""
                            else -> null
                        }
                    } ?: """小时"""

            /** 小时 */
            @Composable fun `Hours`(vararg args: Any?) = FYTxtConfig.observe { `Hours`.fmt(args) }

            /** GeoIP 地址 */
            val `GeoipUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GeoIP URL"""
                            `MLangTags`.ZH -> """GeoIP 地址"""
                            else -> null
                        }
                    } ?: """GeoIP 地址"""

            /** GeoIP 地址 */
            @Composable
            fun `GeoipUrl`(vararg args: Any?) = FYTxtConfig.observe { `GeoipUrl`.fmt(args) }

            /** GeoSite 地址 */
            val `GeositeUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GeoSite URL"""
                            `MLangTags`.ZH -> """GeoSite 地址"""
                            else -> null
                        }
                    } ?: """GeoSite 地址"""

            /** GeoSite 地址 */
            @Composable
            fun `GeositeUrl`(vararg args: Any?) = FYTxtConfig.observe { `GeositeUrl`.fmt(args) }

            /** MMDB 地址 */
            val `MmdbUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """MMDB URL"""
                            `MLangTags`.ZH -> """MMDB 地址"""
                            else -> null
                        }
                    } ?: """MMDB 地址"""

            /** MMDB 地址 */
            @Composable
            fun `MmdbUrl`(vararg args: Any?) = FYTxtConfig.observe { `MmdbUrl`.fmt(args) }

            /** 基础开关 */
            val `TunBasicSwitch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Basic Switch"""
                            `MLangTags`.ZH -> """基础开关"""
                            else -> null
                        }
                    } ?: """基础开关"""

            /** 基础开关 */
            @Composable
            fun `TunBasicSwitch`(vararg args: Any?) =
                FYTxtConfig.observe { `TunBasicSwitch`.fmt(args) }

            /** 协议栈 */
            val `Stack`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Stack"""
                            `MLangTags`.ZH -> """协议栈"""
                            else -> null
                        }
                    } ?: """协议栈"""

            /** 协议栈 */
            @Composable fun `Stack`(vararg args: Any?) = FYTxtConfig.observe { `Stack`.fmt(args) }

            /** 自动路由 */
            val `AutoRoute`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Route"""
                            `MLangTags`.ZH -> """自动路由"""
                            else -> null
                        }
                    } ?: """自动路由"""

            /** 自动路由 */
            @Composable
            fun `AutoRoute`(vararg args: Any?) = FYTxtConfig.observe { `AutoRoute`.fmt(args) }

            /** 自动重定向 */
            val `AutoRedirect`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Redirect"""
                            `MLangTags`.ZH -> """自动重定向"""
                            else -> null
                        }
                    } ?: """自动重定向"""

            /** 自动重定向 */
            @Composable
            fun `AutoRedirect`(vararg args: Any?) = FYTxtConfig.observe { `AutoRedirect`.fmt(args) }

            /** 自动识别网卡 */
            val `AutoDetectInterface`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Detect Interface"""
                            `MLangTags`.ZH -> """自动识别网卡"""
                            else -> null
                        }
                    } ?: """自动识别网卡"""

            /** 自动识别网卡 */
            @Composable
            fun `AutoDetectInterface`(vararg args: Any?) =
                FYTxtConfig.observe { `AutoDetectInterface`.fmt(args) }

            /** 严格路由 */
            val `StrictRoute`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Strict Route"""
                            `MLangTags`.ZH -> """严格路由"""
                            else -> null
                        }
                    } ?: """严格路由"""

            /** 严格路由 */
            @Composable
            fun `StrictRoute`(vararg args: Any?) = FYTxtConfig.observe { `StrictRoute`.fmt(args) }

            /** 独立于端点 NAT */
            val `EndpointIndependentNat`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Endpoint Independent NAT"""
                            `MLangTags`.ZH -> """独立于端点 NAT"""
                            else -> null
                        }
                    } ?: """独立于端点 NAT"""

            /** 独立于端点 NAT */
            @Composable
            fun `EndpointIndependentNat`(vararg args: Any?) =
                FYTxtConfig.observe { `EndpointIndependentNat`.fmt(args) }

            /** 网络性能开关 */
            val `NetworkPerfSwitch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Network Perf Switch"""
                            `MLangTags`.ZH -> """网络性能开关"""
                            else -> null
                        }
                    } ?: """网络性能开关"""

            /** 网络性能开关 */
            @Composable
            fun `NetworkPerfSwitch`(vararg args: Any?) =
                FYTxtConfig.observe { `NetworkPerfSwitch`.fmt(args) }

            /** 启用 GSO */
            val `EnableGso`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Enable GSO"""
                            `MLangTags`.ZH -> """启用 GSO"""
                            else -> null
                        }
                    } ?: """启用 GSO"""

            /** 启用 GSO */
            @Composable
            fun `EnableGso`(vararg args: Any?) = FYTxtConfig.observe { `EnableGso`.fmt(args) }

            /** 禁用 ICMP 转发 */
            val `DisableIcmpForward`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Disable ICMP Forward"""
                            `MLangTags`.ZH -> """禁用 ICMP 转发"""
                            else -> null
                        }
                    } ?: """禁用 ICMP 转发"""

            /** 禁用 ICMP 转发 */
            @Composable
            fun `DisableIcmpForward`(vararg args: Any?) =
                FYTxtConfig.observe { `DisableIcmpForward`.fmt(args) }

            /** 网络性能参数 */
            val `NetworkPerfParams`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Network Perf Params"""
                            `MLangTags`.ZH -> """网络性能参数"""
                            else -> null
                        }
                    } ?: """网络性能参数"""

            /** 网络性能参数 */
            @Composable
            fun `NetworkPerfParams`(vararg args: Any?) =
                FYTxtConfig.observe { `NetworkPerfParams`.fmt(args) }

            /** MTU */
            val `Mtu`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """MTU"""
                            `MLangTags`.ZH -> """MTU"""
                            else -> null
                        }
                    } ?: """MTU"""

            /** MTU */
            @Composable fun `Mtu`(vararg args: Any?) = FYTxtConfig.observe { `Mtu`.fmt(args) }

            /** GSO 最大长度 */
            val `GsoMaxSize`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GSO Max Size"""
                            `MLangTags`.ZH -> """GSO 最大长度"""
                            else -> null
                        }
                    } ?: """GSO 最大长度"""

            /** GSO 最大长度 */
            @Composable
            fun `GsoMaxSize`(vararg args: Any?) = FYTxtConfig.observe { `GsoMaxSize`.fmt(args) }

            /** 基础开关 */
            val `DnsBasicSwitch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Basic Switch"""
                            `MLangTags`.ZH -> """基础开关"""
                            else -> null
                        }
                    } ?: """基础开关"""

            /** 基础开关 */
            @Composable
            fun `DnsBasicSwitch`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsBasicSwitch`.fmt(args) }

            /** DNS 基础参数 */
            val `DnsBasicParams`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS Basic Params"""
                            `MLangTags`.ZH -> """DNS 基础参数"""
                            else -> null
                        }
                    } ?: """DNS 基础参数"""

            /** DNS 基础参数 */
            @Composable
            fun `DnsBasicParams`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsBasicParams`.fmt(args) }

            /** FakeIP 地址段 */
            val `DnsFakeIpRange`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """FakeIP Range"""
                            `MLangTags`.ZH -> """FakeIP 地址段"""
                            else -> null
                        }
                    } ?: """FakeIP 地址段"""

            /** FakeIP 地址段 */
            @Composable
            fun `DnsFakeIpRange`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsFakeIpRange`.fmt(args) }

            /** Fake-IP 模式 */
            val `FakeIpMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Fake-IP Mode"""
                            `MLangTags`.ZH -> """Fake-IP 模式"""
                            else -> null
                        }
                    } ?: """Fake-IP 模式"""

            /** Fake-IP 模式 */
            @Composable
            fun `FakeIpMode`(vararg args: Any?) = FYTxtConfig.observe { `FakeIpMode`.fmt(args) }

            /** Fake-IP 参数 */
            val `FakeIpParams`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Fake-IP Params"""
                            `MLangTags`.ZH -> """Fake-IP 参数"""
                            else -> null
                        }
                    } ?: """Fake-IP 参数"""

            /** Fake-IP 参数 */
            @Composable
            fun `FakeIpParams`(vararg args: Any?) = FYTxtConfig.observe { `FakeIpParams`.fmt(args) }

            /** 上游服务器 */
            val `DnsUpstream`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Upstream Servers"""
                            `MLangTags`.ZH -> """上游服务器"""
                            else -> null
                        }
                    } ?: """上游服务器"""

            /** 上游服务器 */
            @Composable
            fun `DnsUpstream`(vararg args: Any?) = FYTxtConfig.observe { `DnsUpstream`.fmt(args) }

            /** 策略模式 */
            val `DnsPolicyMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Policy Mode"""
                            `MLangTags`.ZH -> """策略模式"""
                            else -> null
                        }
                    } ?: """策略模式"""

            /** 策略模式 */
            @Composable
            fun `DnsPolicyMode`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsPolicyMode`.fmt(args) }

            /** 开关 */
            val `SnifferSwitch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Switch"""
                            `MLangTags`.ZH -> """开关"""
                            else -> null
                        }
                    } ?: """开关"""

            /** 开关 */
            @Composable
            fun `SnifferSwitch`(vararg args: Any?) =
                FYTxtConfig.observe { `SnifferSwitch`.fmt(args) }

            /** 端口 */
            val `SnifferPorts`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Ports"""
                            `MLangTags`.ZH -> """端口"""
                            else -> null
                        }
                    } ?: """端口"""

            /** 端口 */
            @Composable
            fun `SnifferPorts`(vararg args: Any?) = FYTxtConfig.observe { `SnifferPorts`.fmt(args) }

            /** 覆写目标 */
            val `SnifferOverride`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Override Destination"""
                            `MLangTags`.ZH -> """覆写目标"""
                            else -> null
                        }
                    } ?: """覆写目标"""

            /** 覆写目标 */
            @Composable
            fun `SnifferOverride`(vararg args: Any?) =
                FYTxtConfig.observe { `SnifferOverride`.fmt(args) }

            /** 跳过域名 */
            val `SnifferSkipDomain`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Skip Domain"""
                            `MLangTags`.ZH -> """跳过域名"""
                            else -> null
                        }
                    } ?: """跳过域名"""

            /** 跳过域名 */
            @Composable
            fun `SnifferSkipDomain`(vararg args: Any?) =
                FYTxtConfig.observe { `SnifferSkipDomain`.fmt(args) }

            /** 强制域名 */
            val `SnifferForceDomain`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Force Domain"""
                            `MLangTags`.ZH -> """强制域名"""
                            else -> null
                        }
                    } ?: """强制域名"""

            /** 强制域名 */
            @Composable
            fun `SnifferForceDomain`(vararg args: Any?) =
                FYTxtConfig.observe { `SnifferForceDomain`.fmt(args) }

            /** 解析纯 IP */
            val `SnifferParsePureIp`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Parse Pure IP"""
                            `MLangTags`.ZH -> """解析纯 IP"""
                            else -> null
                        }
                    } ?: """解析纯 IP"""

            /** 解析纯 IP */
            @Composable
            fun `SnifferParsePureIp`(vararg args: Any?) =
                FYTxtConfig.observe { `SnifferParsePureIp`.fmt(args) }

            /** 路由与应用 */
            val `TunRouteAndApps`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Route & Apps"""
                            `MLangTags`.ZH -> """路由与应用"""
                            else -> null
                        }
                    } ?: """路由与应用"""

            /** 路由与应用 */
            @Composable
            fun `TunRouteAndApps`(vararg args: Any?) =
                FYTxtConfig.observe { `TunRouteAndApps`.fmt(args) }

            /** DNS 劫持 */
            val `DnsHijack`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS Hijack"""
                            `MLangTags`.ZH -> """DNS 劫持"""
                            else -> null
                        }
                    } ?: """DNS 劫持"""

            /** DNS 劫持 */
            @Composable
            fun `DnsHijack`(vararg args: Any?) = FYTxtConfig.observe { `DnsHijack`.fmt(args) }

            /** 路由网段 */
            val `RouteAddress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Route Address"""
                            `MLangTags`.ZH -> """路由网段"""
                            else -> null
                        }
                    } ?: """路由网段"""

            /** 路由网段 */
            @Composable
            fun `RouteAddress`(vararg args: Any?) = FYTxtConfig.observe { `RouteAddress`.fmt(args) }

            /** 排除路由网段 */
            val `RouteExcludeAddress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Route Exclude Address"""
                            `MLangTags`.ZH -> """排除路由网段"""
                            else -> null
                        }
                    } ?: """排除路由网段"""

            /** 排除路由网段 */
            @Composable
            fun `RouteExcludeAddress`(vararg args: Any?) =
                FYTxtConfig.observe { `RouteExcludeAddress`.fmt(args) }

            /** 包含应用 */
            val `IncludePackage`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Include Package"""
                            `MLangTags`.ZH -> """包含应用"""
                            else -> null
                        }
                    } ?: """包含应用"""

            /** 包含应用 */
            @Composable
            fun `IncludePackage`(vararg args: Any?) =
                FYTxtConfig.observe { `IncludePackage`.fmt(args) }

            /** 排除应用 */
            val `ExcludePackage`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Exclude Package"""
                            `MLangTags`.ZH -> """排除应用"""
                            else -> null
                        }
                    } ?: """排除应用"""

            /** 排除应用 */
            @Composable
            fun `ExcludePackage`(vararg args: Any?) =
                FYTxtConfig.observe { `ExcludePackage`.fmt(args) }

            /** 缓存上限 */
            val `CacheLimit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cache Limit"""
                            `MLangTags`.ZH -> """缓存上限"""
                            else -> null
                        }
                    } ?: """缓存上限"""

            /** 缓存上限 */
            @Composable
            fun `CacheLimit`(vararg args: Any?) = FYTxtConfig.observe { `CacheLimit`.fmt(args) }

            /** 上游服务器 */
            val `DnsUpstreamServers`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Upstream Servers"""
                            `MLangTags`.ZH -> """上游服务器"""
                            else -> null
                        }
                    } ?: """上游服务器"""

            /** 上游服务器 */
            @Composable
            fun `DnsUpstreamServers`(vararg args: Any?) =
                FYTxtConfig.observe { `DnsUpstreamServers`.fmt(args) }

            /** 策略映射 */
            val `NameserverPolicySection`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Nameserver Policy"""
                            `MLangTags`.ZH -> """策略映射"""
                            else -> null
                        }
                    } ?: """策略映射"""

            /** 策略映射 */
            @Composable
            fun `NameserverPolicySection`(vararg args: Any?) =
                FYTxtConfig.observe { `NameserverPolicySection`.fmt(args) }

            /** 过滤列表 */
            val `FilterList`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Filter List"""
                            `MLangTags`.ZH -> """过滤列表"""
                            else -> null
                        }
                    } ?: """过滤列表"""

            /** 过滤列表 */
            @Composable
            fun `FilterList`(vararg args: Any?) = FYTxtConfig.observe { `FilterList`.fmt(args) }

            /** Fallback 开关 */
            val `FallbackSwitch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Fallback Switch"""
                            `MLangTags`.ZH -> """Fallback 开关"""
                            else -> null
                        }
                    } ?: """Fallback 开关"""

            /** Fallback 开关 */
            @Composable
            fun `FallbackSwitch`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackSwitch`.fmt(args) }

            /** Fallback 参数 */
            val `FallbackParams`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Fallback Params"""
                            `MLangTags`.ZH -> """Fallback 参数"""
                            else -> null
                        }
                    } ?: """Fallback 参数"""

            /** Fallback 参数 */
            @Composable
            fun `FallbackParams`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackParams`.fmt(args) }

            /** Fallback 过滤 */
            val `FallbackFilter`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Fallback Filter"""
                            `MLangTags`.ZH -> """Fallback 过滤"""
                            else -> null
                        }
                    } ?: """Fallback 过滤"""

            /** Fallback 过滤 */
            @Composable
            fun `FallbackFilter`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackFilter`.fmt(args) }

            /** 基础策略 */
            val `BasicPolicy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Basic Policy"""
                            `MLangTags`.ZH -> """基础策略"""
                            else -> null
                        }
                    } ?: """基础策略"""

            /** 基础策略 */
            @Composable
            fun `BasicPolicy`(vararg args: Any?) = FYTxtConfig.observe { `BasicPolicy`.fmt(args) }

            /** 跳过与强制 */
            val `SkipAndForce`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Skip & Force"""
                            `MLangTags`.ZH -> """跳过与强制"""
                            else -> null
                        }
                    } ?: """跳过与强制"""

            /** 跳过与强制 */
            @Composable
            fun `SkipAndForce`(vararg args: Any?) = FYTxtConfig.observe { `SkipAndForce`.fmt(args) }

            /** 跳过来源地址 */
            val `SkipSrcAddress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Skip Src Address"""
                            `MLangTags`.ZH -> """跳过来源地址"""
                            else -> null
                        }
                    } ?: """跳过来源地址"""

            /** 跳过来源地址 */
            @Composable
            fun `SkipSrcAddress`(vararg args: Any?) =
                FYTxtConfig.observe { `SkipSrcAddress`.fmt(args) }

            /** 跳过目标地址 */
            val `SkipDstAddress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Skip Dst Address"""
                            `MLangTags`.ZH -> """跳过目标地址"""
                            else -> null
                        }
                    } ?: """跳过目标地址"""

            /** 跳过目标地址 */
            @Composable
            fun `SkipDstAddress`(vararg args: Any?) =
                FYTxtConfig.observe { `SkipDstAddress`.fmt(args) }
        }

        object `Rule` {
            init {
                `MLangGroups`
            }

            /** 规则 #%d 为空 */
            val `EmptyWarning`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule #%d is empty"""
                            `MLangTags`.ZH -> """规则 #%d 为空"""
                            else -> null
                        }
                    } ?: """规则 #%d 为空"""

            /** 规则 #%d 为空 */
            @Composable
            fun `EmptyWarning`(vararg args: Any?) = FYTxtConfig.observe { `EmptyWarning`.fmt(args) }

            /** 规则 #%d 格式可能不正确: %s */
            val `InvalidFormatWarning`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule #%d format may be incorrect: %s"""
                            `MLangTags`.ZH -> """规则 #%d 格式可能不正确: %s"""
                            else -> null
                        }
                    } ?: """规则 #%d 格式可能不正确: %s"""

            /** 规则 #%d 格式可能不正确: %s */
            @Composable
            fun `InvalidFormatWarning`(vararg args: Any?) =
                FYTxtConfig.observe { `InvalidFormatWarning`.fmt(args) }

            /** 规则 #%d 缺少策略组目标: %s */
            val `MissingTargetWarning`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule #%d missing policy group target: %s"""
                            `MLangTags`.ZH -> """规则 #%d 缺少策略组目标: %s"""
                            else -> null
                        }
                    } ?: """规则 #%d 缺少策略组目标: %s"""

            /** 规则 #%d 缺少策略组目标: %s */
            @Composable
            fun `MissingTargetWarning`(vararg args: Any?) =
                FYTxtConfig.observe { `MissingTargetWarning`.fmt(args) }
        }

        object `Save` {
            init {
                `MLangGroups`
            }

            /** 系统预设不可修改 */
            val `PresetNotModifiable`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """System preset cannot be modified"""
                            `MLangTags`.ZH -> """系统预设不可修改"""
                            else -> null
                        }
                    } ?: """系统预设不可修改"""

            /** 系统预设不可修改 */
            @Composable
            fun `PresetNotModifiable`(vararg args: Any?) =
                FYTxtConfig.observe { `PresetNotModifiable`.fmt(args) }

            /** 覆写已保存，但重新应用到当前配置失败 */
            val `ApplyFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Override saved, but failed to reapply to current config"""
                            `MLangTags`.ZH -> """覆写已保存，但重新应用到当前配置失败"""
                            else -> null
                        }
                    } ?: """覆写已保存，但重新应用到当前配置失败"""

            /** 覆写已保存，但重新应用到当前配置失败 */
            @Composable
            fun `ApplyFailed`(vararg args: Any?) = FYTxtConfig.observe { `ApplyFailed`.fmt(args) }

            /** 保存覆写配置失败 */
            val `Failed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to save override config"""
                            `MLangTags`.ZH -> """保存覆写配置失败"""
                            else -> null
                        }
                    } ?: """保存覆写配置失败"""

            /** 保存覆写配置失败 */
            @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

            /** 保存运行时覆写失败 */
            val `RuntimeSaveFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to save runtime override"""
                            `MLangTags`.ZH -> """保存运行时覆写失败"""
                            else -> null
                        }
                    } ?: """保存运行时覆写失败"""

            /** 保存运行时覆写失败 */
            @Composable
            fun `RuntimeSaveFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `RuntimeSaveFailed`.fmt(args) }

            /** 运行时覆写 */
            val `RuntimeOverrideName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Runtime Override"""
                            `MLangTags`.ZH -> """运行时覆写"""
                            else -> null
                        }
                    } ?: """运行时覆写"""

            /** 运行时覆写 */
            @Composable
            fun `RuntimeOverrideName`(vararg args: Any?) =
                FYTxtConfig.observe { `RuntimeOverrideName`.fmt(args) }

            /** 导入内容不能为空 */
            val `ImportEmpty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import content cannot be empty"""
                            `MLangTags`.ZH -> """导入内容不能为空"""
                            else -> null
                        }
                    } ?: """导入内容不能为空"""

            /** 导入内容不能为空 */
            @Composable
            fun `ImportEmpty`(vararg args: Any?) = FYTxtConfig.observe { `ImportEmpty`.fmt(args) }

            /** 导入的覆写配置 */
            val `ImportDefaultName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Imported Override Config"""
                            `MLangTags`.ZH -> """导入的覆写配置"""
                            else -> null
                        }
                    } ?: """导入的覆写配置"""

            /** 导入的覆写配置 */
            @Composable
            fun `ImportDefaultName`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportDefaultName`.fmt(args) }
        }

        object `Dns` {
            init {
                `MLangGroups`
            }

            /** DNS 策略 */
            val `Policy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS Policy"""
                            `MLangTags`.ZH -> """DNS 策略"""
                            else -> null
                        }
                    } ?: """DNS 策略"""

            /** DNS 策略 */
            @Composable fun `Policy`(vararg args: Any?) = FYTxtConfig.observe { `Policy`.fmt(args) }

            /** 不修改 */
            val `PolicyNotModify`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Don't Modify"""
                            `MLangTags`.ZH -> """不修改"""
                            else -> null
                        }
                    } ?: """不修改"""

            /** 不修改 */
            @Composable
            fun `PolicyNotModify`(vararg args: Any?) =
                FYTxtConfig.observe { `PolicyNotModify`.fmt(args) }

            /** 强制启用 */
            val `PolicyForceEnable`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Force Enable"""
                            `MLangTags`.ZH -> """强制启用"""
                            else -> null
                        }
                    } ?: """强制启用"""

            /** 强制启用 */
            @Composable
            fun `PolicyForceEnable`(vararg args: Any?) =
                FYTxtConfig.observe { `PolicyForceEnable`.fmt(args) }

            /** 使用内置 */
            val `PolicyUseBuiltin`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Use Built-in"""
                            `MLangTags`.ZH -> """使用内置"""
                            else -> null
                        }
                    } ?: """使用内置"""

            /** 使用内置 */
            @Composable
            fun `PolicyUseBuiltin`(vararg args: Any?) =
                FYTxtConfig.observe { `PolicyUseBuiltin`.fmt(args) }

            /** 优先 HTTP/3 */
            val `PreferH3`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Prefer HTTP/3"""
                            `MLangTags`.ZH -> """优先 HTTP/3"""
                            else -> null
                        }
                    } ?: """优先 HTTP/3"""

            /** 优先 HTTP/3 */
            @Composable
            fun `PreferH3`(vararg args: Any?) = FYTxtConfig.observe { `PreferH3`.fmt(args) }

            /** 监听地址 */
            val `Listen`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Listen Address"""
                            `MLangTags`.ZH -> """监听地址"""
                            else -> null
                        }
                    } ?: """监听地址"""

            /** 监听地址 */
            @Composable fun `Listen`(vararg args: Any?) = FYTxtConfig.observe { `Listen`.fmt(args) }

            /** 例如：0.0.0.0:53 */
            val `ListenHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """e.g.: 0.0.0.0:53"""
                            `MLangTags`.ZH -> """例如：0.0.0.0:53"""
                            else -> null
                        }
                    } ?: """例如：0.0.0.0:53"""

            /** 例如：0.0.0.0:53 */
            @Composable
            fun `ListenHint`(vararg args: Any?) = FYTxtConfig.observe { `ListenHint`.fmt(args) }

            /** DNS IPv6 */
            val `Ipv6`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS IPv6"""
                            `MLangTags`.ZH -> """DNS IPv6"""
                            else -> null
                        }
                    } ?: """DNS IPv6"""

            /** DNS IPv6 */
            @Composable fun `Ipv6`(vararg args: Any?) = FYTxtConfig.observe { `Ipv6`.fmt(args) }

            /** 使用 hosts */
            val `UseHosts`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Use Hosts"""
                            `MLangTags`.ZH -> """使用 hosts"""
                            else -> null
                        }
                    } ?: """使用 hosts"""

            /** 使用 hosts */
            @Composable
            fun `UseHosts`(vararg args: Any?) = FYTxtConfig.observe { `UseHosts`.fmt(args) }

            /** 追加系统 DNS */
            val `AppendSystem`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Append System DNS"""
                            `MLangTags`.ZH -> """追加系统 DNS"""
                            else -> null
                        }
                    } ?: """追加系统 DNS"""

            /** 追加系统 DNS */
            @Composable
            fun `AppendSystem`(vararg args: Any?) = FYTxtConfig.observe { `AppendSystem`.fmt(args) }

            /** 增强模式 */
            val `EnhancedMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Enhanced Mode"""
                            `MLangTags`.ZH -> """增强模式"""
                            else -> null
                        }
                    } ?: """增强模式"""

            /** 增强模式 */
            @Composable
            fun `EnhancedMode`(vararg args: Any?) = FYTxtConfig.observe { `EnhancedMode`.fmt(args) }

            /** 不修改 */
            val `EnhancedNotModify`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Don't Modify"""
                            `MLangTags`.ZH -> """不修改"""
                            else -> null
                        }
                    } ?: """不修改"""

            /** 不修改 */
            @Composable
            fun `EnhancedNotModify`(vararg args: Any?) =
                FYTxtConfig.observe { `EnhancedNotModify`.fmt(args) }

            /** 禁用 */
            val `EnhancedDisable`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Disable"""
                            `MLangTags`.ZH -> """禁用"""
                            else -> null
                        }
                    } ?: """禁用"""

            /** 禁用 */
            @Composable
            fun `EnhancedDisable`(vararg args: Any?) =
                FYTxtConfig.observe { `EnhancedDisable`.fmt(args) }

            /** FakeIP */
            val `EnhancedFakeip`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """FakeIP"""
                            `MLangTags`.ZH -> """FakeIP"""
                            else -> null
                        }
                    } ?: """FakeIP"""

            /** FakeIP */
            @Composable
            fun `EnhancedFakeip`(vararg args: Any?) =
                FYTxtConfig.observe { `EnhancedFakeip`.fmt(args) }

            /** Mapping */
            val `EnhancedMapping`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Mapping"""
                            `MLangTags`.ZH -> """Mapping"""
                            else -> null
                        }
                    } ?: """Mapping"""

            /** Mapping */
            @Composable
            fun `EnhancedMapping`(vararg args: Any?) =
                FYTxtConfig.observe { `EnhancedMapping`.fmt(args) }

            /** Direct 遵循策略 */
            val `DirectFollowPolicy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Direct Follow Policy"""
                            `MLangTags`.ZH -> """Direct 遵循策略"""
                            else -> null
                        }
                    } ?: """Direct 遵循策略"""

            /** Direct 遵循策略 */
            @Composable
            fun `DirectFollowPolicy`(vararg args: Any?) =
                FYTxtConfig.observe { `DirectFollowPolicy`.fmt(args) }

            /** IPv6 超时 */
            val `Ipv6Timeout`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """IPv6 Timeout"""
                            `MLangTags`.ZH -> """IPv6 超时"""
                            else -> null
                        }
                    } ?: """IPv6 超时"""

            /** IPv6 超时 */
            @Composable
            fun `Ipv6Timeout`(vararg args: Any?) = FYTxtConfig.observe { `Ipv6Timeout`.fmt(args) }

            /** DNS 服务器 */
            val `Servers`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS Servers"""
                            `MLangTags`.ZH -> """DNS 服务器"""
                            else -> null
                        }
                    } ?: """DNS 服务器"""

            /** DNS 服务器 */
            @Composable
            fun `Servers`(vararg args: Any?) = FYTxtConfig.observe { `Servers`.fmt(args) }

            /** 例如：8.8.8.8, tls://dns.google */
            val `ServersHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """e.g.: 8.8.8.8, tls://dns.google"""
                            `MLangTags`.ZH -> """例如：8.8.8.8, tls://dns.google"""
                            else -> null
                        }
                    } ?: """例如：8.8.8.8, tls://dns.google"""

            /** 例如：8.8.8.8, tls://dns.google */
            @Composable
            fun `ServersHint`(vararg args: Any?) = FYTxtConfig.observe { `ServersHint`.fmt(args) }

            /** 备用 DNS */
            val `Fallback`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Fallback DNS"""
                            `MLangTags`.ZH -> """备用 DNS"""
                            else -> null
                        }
                    } ?: """备用 DNS"""

            /** 备用 DNS */
            @Composable
            fun `Fallback`(vararg args: Any?) = FYTxtConfig.observe { `Fallback`.fmt(args) }

            /** 例如：1.1.1.1 */
            val `FallbackHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """e.g.: 1.1.1.1"""
                            `MLangTags`.ZH -> """例如：1.1.1.1"""
                            else -> null
                        }
                    } ?: """例如：1.1.1.1"""

            /** 例如：1.1.1.1 */
            @Composable
            fun `FallbackHint`(vararg args: Any?) = FYTxtConfig.observe { `FallbackHint`.fmt(args) }

            /** 默认 DNS */
            val `Default`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Default DNS"""
                            `MLangTags`.ZH -> """默认 DNS"""
                            else -> null
                        }
                    } ?: """默认 DNS"""

            /** 默认 DNS */
            @Composable
            fun `Default`(vararg args: Any?) = FYTxtConfig.observe { `Default`.fmt(args) }

            /** 用于解析 DNS 服务器域名 */
            val `DefaultHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """For resolving DNS server domains"""
                            `MLangTags`.ZH -> """用于解析 DNS 服务器域名"""
                            else -> null
                        }
                    } ?: """用于解析 DNS 服务器域名"""

            /** 用于解析 DNS 服务器域名 */
            @Composable
            fun `DefaultHint`(vararg args: Any?) = FYTxtConfig.observe { `DefaultHint`.fmt(args) }

            /** FakeIP 过滤 */
            val `FakeipFilter`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """FakeIP Filter"""
                            `MLangTags`.ZH -> """FakeIP 过滤"""
                            else -> null
                        }
                    } ?: """FakeIP 过滤"""

            /** FakeIP 过滤 */
            @Composable
            fun `FakeipFilter`(vararg args: Any?) = FYTxtConfig.observe { `FakeipFilter`.fmt(args) }

            /** 例如：+.lan, localhost */
            val `FakeipFilterHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """e.g.: +.lan, localhost"""
                            `MLangTags`.ZH -> """例如：+.lan, localhost"""
                            else -> null
                        }
                    } ?: """例如：+.lan, localhost"""

            /** 例如：+.lan, localhost */
            @Composable
            fun `FakeipFilterHint`(vararg args: Any?) =
                FYTxtConfig.observe { `FakeipFilterHint`.fmt(args) }

            /** FakeIP 过滤模式 */
            val `FakeipFilterMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """FakeIP Filter Mode"""
                            `MLangTags`.ZH -> """FakeIP 过滤模式"""
                            else -> null
                        }
                    } ?: """FakeIP 过滤模式"""

            /** FakeIP 过滤模式 */
            @Composable
            fun `FakeipFilterMode`(vararg args: Any?) =
                FYTxtConfig.observe { `FakeipFilterMode`.fmt(args) }

            /** 黑名单 */
            val `FakeipBlacklist`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Blacklist"""
                            `MLangTags`.ZH -> """黑名单"""
                            else -> null
                        }
                    } ?: """黑名单"""

            /** 黑名单 */
            @Composable
            fun `FakeipBlacklist`(vararg args: Any?) =
                FYTxtConfig.observe { `FakeipBlacklist`.fmt(args) }

            /** 白名单 */
            val `FakeipWhitelist`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Whitelist"""
                            `MLangTags`.ZH -> """白名单"""
                            else -> null
                        }
                    } ?: """白名单"""

            /** 白名单 */
            @Composable
            fun `FakeipWhitelist`(vararg args: Any?) =
                FYTxtConfig.observe { `FakeipWhitelist`.fmt(args) }

            /** GeoIP 回退 */
            val `FallbackGeoip`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GeoIP Fallback"""
                            `MLangTags`.ZH -> """GeoIP 回退"""
                            else -> null
                        }
                    } ?: """GeoIP 回退"""

            /** GeoIP 回退 */
            @Composable
            fun `FallbackGeoip`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackGeoip`.fmt(args) }

            /** GeoIP 代码 */
            val `FallbackGeoipCode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """GeoIP Code"""
                            `MLangTags`.ZH -> """GeoIP 代码"""
                            else -> null
                        }
                    } ?: """GeoIP 代码"""

            /** GeoIP 代码 */
            @Composable
            fun `FallbackGeoipCode`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackGeoipCode`.fmt(args) }

            /** 例如：CN */
            val `FallbackGeoipCodeHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """e.g.: CN"""
                            `MLangTags`.ZH -> """例如：CN"""
                            else -> null
                        }
                    } ?: """例如：CN"""

            /** 例如：CN */
            @Composable
            fun `FallbackGeoipCodeHint`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackGeoipCodeHint`.fmt(args) }

            /** 域名回退 */
            val `FallbackDomain`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Domain Fallback"""
                            `MLangTags`.ZH -> """域名回退"""
                            else -> null
                        }
                    } ?: """域名回退"""

            /** 域名回退 */
            @Composable
            fun `FallbackDomain`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackDomain`.fmt(args) }

            /** 例如：+.google.com */
            val `FallbackDomainHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """e.g.: +.google.com"""
                            `MLangTags`.ZH -> """例如：+.google.com"""
                            else -> null
                        }
                    } ?: """例如：+.google.com"""

            /** 例如：+.google.com */
            @Composable
            fun `FallbackDomainHint`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackDomainHint`.fmt(args) }

            /** IP CIDR 回退 */
            val `FallbackIpcidr`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """IP CIDR Fallback"""
                            `MLangTags`.ZH -> """IP CIDR 回退"""
                            else -> null
                        }
                    } ?: """IP CIDR 回退"""

            /** IP CIDR 回退 */
            @Composable
            fun `FallbackIpcidr`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackIpcidr`.fmt(args) }

            /** 例如：240.0.0.0/4 */
            val `FallbackIpcidrHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """e.g.: 240.0.0.0/4"""
                            `MLangTags`.ZH -> """例如：240.0.0.0/4"""
                            else -> null
                        }
                    } ?: """例如：240.0.0.0/4"""

            /** 例如：240.0.0.0/4 */
            @Composable
            fun `FallbackIpcidrHint`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackIpcidrHint`.fmt(args) }

            /** Geosite 回退 */
            val `FallbackGeosite`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Geosite Fallback"""
                            `MLangTags`.ZH -> """Geosite 回退"""
                            else -> null
                        }
                    } ?: """Geosite 回退"""

            /** Geosite 回退 */
            @Composable
            fun `FallbackGeosite`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackGeosite`.fmt(args) }

            /** 例如：gfw */
            val `FallbackGeositeHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """e.g.: gfw"""
                            `MLangTags`.ZH -> """例如：gfw"""
                            else -> null
                        }
                    } ?: """例如：gfw"""

            /** 例如：gfw */
            @Composable
            fun `FallbackGeositeHint`(vararg args: Any?) =
                FYTxtConfig.observe { `FallbackGeositeHint`.fmt(args) }

            /** DNS 策略 */
            val `NameserverPolicy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Nameserver Policy"""
                            `MLangTags`.ZH -> """DNS 策略"""
                            else -> null
                        }
                    } ?: """DNS 策略"""

            /** DNS 策略 */
            @Composable
            fun `NameserverPolicy`(vararg args: Any?) =
                FYTxtConfig.observe { `NameserverPolicy`.fmt(args) }

            /** 域名匹配规则 */
            val `NameserverPolicyKey`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Domain match rule"""
                            `MLangTags`.ZH -> """域名匹配规则"""
                            else -> null
                        }
                    } ?: """域名匹配规则"""

            /** 域名匹配规则 */
            @Composable
            fun `NameserverPolicyKey`(vararg args: Any?) =
                FYTxtConfig.observe { `NameserverPolicyKey`.fmt(args) }

            /** DNS 服务器 */
            val `NameserverPolicyValue`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS server"""
                            `MLangTags`.ZH -> """DNS 服务器"""
                            else -> null
                        }
                    } ?: """DNS 服务器"""

            /** DNS 服务器 */
            @Composable
            fun `NameserverPolicyValue`(vararg args: Any?) =
                FYTxtConfig.observe { `NameserverPolicyValue`.fmt(args) }

            /** Fake-IP IPv6 网段 */
            val `FakeipRange6`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Fake-IP IPv6 Range"""
                            `MLangTags`.ZH -> """Fake-IP IPv6 网段"""
                            else -> null
                        }
                    } ?: """Fake-IP IPv6 网段"""

            /** Fake-IP IPv6 网段 */
            @Composable
            fun `FakeipRange6`(vararg args: Any?) = FYTxtConfig.observe { `FakeipRange6`.fmt(args) }

            /** Fake-IP TTL */
            val `FakeipTtl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Fake-IP TTL"""
                            `MLangTags`.ZH -> """Fake-IP TTL"""
                            else -> null
                        }
                    } ?: """Fake-IP TTL"""

            /** Fake-IP TTL */
            @Composable
            fun `FakeipTtl`(vararg args: Any?) = FYTxtConfig.observe { `FakeipTtl`.fmt(args) }

            /** 代理服务器 DNS */
            val `ProxyServerNameserver`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Server Nameserver"""
                            `MLangTags`.ZH -> """代理服务器 DNS"""
                            else -> null
                        }
                    } ?: """代理服务器 DNS"""

            /** 代理服务器 DNS */
            @Composable
            fun `ProxyServerNameserver`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyServerNameserver`.fmt(args) }

            /** 直连 DNS */
            val `DirectNameserver`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Direct Nameserver"""
                            `MLangTags`.ZH -> """直连 DNS"""
                            else -> null
                        }
                    } ?: """直连 DNS"""

            /** 直连 DNS */
            @Composable
            fun `DirectNameserver`(vararg args: Any?) =
                FYTxtConfig.observe { `DirectNameserver`.fmt(args) }

            /** 代理服务器 DNS 策略 */
            val `ProxyServerNameserverPolicy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Server Nameserver Policy"""
                            `MLangTags`.ZH -> """代理服务器 DNS 策略"""
                            else -> null
                        }
                    } ?: """代理服务器 DNS 策略"""

            /** 代理服务器 DNS 策略 */
            @Composable
            fun `ProxyServerNameserverPolicy`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyServerNameserverPolicy`.fmt(args) }

            /** 域名 / RuleSet */
            val `ProxyServerNameserverPolicyKey`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Domain / RuleSet"""
                            `MLangTags`.ZH -> """域名 / RuleSet"""
                            else -> null
                        }
                    } ?: """域名 / RuleSet"""

            /** 域名 / RuleSet */
            @Composable
            fun `ProxyServerNameserverPolicyKey`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyServerNameserverPolicyKey`.fmt(args) }

            /** DNS 服务器 */
            val `ProxyServerNameserverPolicyValue`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS server"""
                            `MLangTags`.ZH -> """DNS 服务器"""
                            else -> null
                        }
                    } ?: """DNS 服务器"""

            /** DNS 服务器 */
            @Composable
            fun `ProxyServerNameserverPolicyValue`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyServerNameserverPolicyValue`.fmt(args) }

            /** Hosts */
            val `Hosts`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Hosts"""
                            `MLangTags`.ZH -> """Hosts"""
                            else -> null
                        }
                    } ?: """Hosts"""

            /** Hosts */
            @Composable fun `Hosts`(vararg args: Any?) = FYTxtConfig.observe { `Hosts`.fmt(args) }

            /** 域名 */
            val `HostsKey`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Domain"""
                            `MLangTags`.ZH -> """域名"""
                            else -> null
                        }
                    } ?: """域名"""

            /** 域名 */
            @Composable
            fun `HostsKey`(vararg args: Any?) = FYTxtConfig.observe { `HostsKey`.fmt(args) }

            /** IP */
            val `HostsValue`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """IP"""
                            `MLangTags`.ZH -> """IP"""
                            else -> null
                        }
                    } ?: """IP"""

            /** IP */
            @Composable
            fun `HostsValue`(vararg args: Any?) = FYTxtConfig.observe { `HostsValue`.fmt(args) }
        }

        object `General` {
            init {
                `MLangGroups`
            }

            /** HTTP 端口 */
            val `HttpPort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """HTTP Port"""
                            `MLangTags`.ZH -> """HTTP 端口"""
                            else -> null
                        }
                    } ?: """HTTP 端口"""

            /** HTTP 端口 */
            @Composable
            fun `HttpPort`(vararg args: Any?) = FYTxtConfig.observe { `HttpPort`.fmt(args) }

            /** TLS 端口 */
            val `TlsPort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """TLS Port"""
                            `MLangTags`.ZH -> """TLS 端口"""
                            else -> null
                        }
                    } ?: """TLS 端口"""

            /** TLS 端口 */
            @Composable
            fun `TlsPort`(vararg args: Any?) = FYTxtConfig.observe { `TlsPort`.fmt(args) }

            /** QUIC 端口 */
            val `QuicPort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """QUIC Port"""
                            `MLangTags`.ZH -> """QUIC 端口"""
                            else -> null
                        }
                    } ?: """QUIC 端口"""

            /** QUIC 端口 */
            @Composable
            fun `QuicPort`(vararg args: Any?) = FYTxtConfig.observe { `QuicPort`.fmt(args) }

            /** SOCKS 端口 */
            val `SocksPort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """SOCKS Port"""
                            `MLangTags`.ZH -> """SOCKS 端口"""
                            else -> null
                        }
                    } ?: """SOCKS 端口"""

            /** SOCKS 端口 */
            @Composable
            fun `SocksPort`(vararg args: Any?) = FYTxtConfig.observe { `SocksPort`.fmt(args) }

            /** Mixed 端口 */
            val `MixedPort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Mixed Port"""
                            `MLangTags`.ZH -> """Mixed 端口"""
                            else -> null
                        }
                    } ?: """Mixed 端口"""

            /** Mixed 端口 */
            @Composable
            fun `MixedPort`(vararg args: Any?) = FYTxtConfig.observe { `MixedPort`.fmt(args) }

            /** Redirect 端口 */
            val `RedirectPort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Redirect Port"""
                            `MLangTags`.ZH -> """Redirect 端口"""
                            else -> null
                        }
                    } ?: """Redirect 端口"""

            /** Redirect 端口 */
            @Composable
            fun `RedirectPort`(vararg args: Any?) = FYTxtConfig.observe { `RedirectPort`.fmt(args) }

            /** TProxy 端口 */
            val `TproxyPort`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """TProxy Port"""
                            `MLangTags`.ZH -> """TProxy 端口"""
                            else -> null
                        }
                    } ?: """TProxy 端口"""

            /** TProxy 端口 */
            @Composable
            fun `TproxyPort`(vararg args: Any?) = FYTxtConfig.observe { `TproxyPort`.fmt(args) }

            /** 允许局域网 */
            val `AllowLan`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Allow LAN"""
                            `MLangTags`.ZH -> """允许局域网"""
                            else -> null
                        }
                    } ?: """允许局域网"""

            /** 允许局域网 */
            @Composable
            fun `AllowLan`(vararg args: Any?) = FYTxtConfig.observe { `AllowLan`.fmt(args) }

            /** IPv6 */
            val `Ipv6`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """IPv6"""
                            `MLangTags`.ZH -> """IPv6"""
                            else -> null
                        }
                    } ?: """IPv6"""

            /** IPv6 */
            @Composable fun `Ipv6`(vararg args: Any?) = FYTxtConfig.observe { `Ipv6`.fmt(args) }

            /** 代理模式 */
            val `ProxyMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Mode"""
                            `MLangTags`.ZH -> """代理模式"""
                            else -> null
                        }
                    } ?: """代理模式"""

            /** 代理模式 */
            @Composable
            fun `ProxyMode`(vararg args: Any?) = FYTxtConfig.observe { `ProxyMode`.fmt(args) }

            /** 日志等级 */
            val `LogLevel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Log Level"""
                            `MLangTags`.ZH -> """日志等级"""
                            else -> null
                        }
                    } ?: """日志等级"""

            /** 日志等级 */
            @Composable
            fun `LogLevel`(vararg args: Any?) = FYTxtConfig.observe { `LogLevel`.fmt(args) }
        }

        object `Label` {
            init {
                `MLangGroups`
            }

            /** 缓存算法 */
            val `CacheAlgorithm`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cache Algorithm"""
                            `MLangTags`.ZH -> """缓存算法"""
                            else -> null
                        }
                    } ?: """缓存算法"""

            /** 缓存算法 */
            @Composable
            fun `CacheAlgorithm`(vararg args: Any?) =
                FYTxtConfig.observe { `CacheAlgorithm`.fmt(args) }

            /** 启用 */
            val `Enable`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Enable"""
                            `MLangTags`.ZH -> """启用"""
                            else -> null
                        }
                    } ?: """启用"""

            /** 启用 */
            @Composable fun `Enable`(vararg args: Any?) = FYTxtConfig.observe { `Enable`.fmt(args) }

            /** FakeIP 地址段 */
            val `FakeIpRange`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """FakeIP Range"""
                            `MLangTags`.ZH -> """FakeIP 地址段"""
                            else -> null
                        }
                    } ?: """FakeIP 地址段"""

            /** FakeIP 地址段 */
            @Composable
            fun `FakeIpRange`(vararg args: Any?) = FYTxtConfig.observe { `FakeIpRange`.fmt(args) }

            /** 强制 DNS 映射 */
            val `ForceDnsMapping`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Force DNS Mapping"""
                            `MLangTags`.ZH -> """强制 DNS 映射"""
                            else -> null
                        }
                    } ?: """强制 DNS 映射"""

            /** 强制 DNS 映射 */
            @Composable
            fun `ForceDnsMapping`(vararg args: Any?) =
                FYTxtConfig.observe { `ForceDnsMapping`.fmt(args) }

            /** 强制嗅探域名 */
            val `ForceDomain`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Force Domain"""
                            `MLangTags`.ZH -> """强制嗅探域名"""
                            else -> null
                        }
                    } ?: """强制嗅探域名"""

            /** 强制嗅探域名 */
            @Composable
            fun `ForceDomain`(vararg args: Any?) = FYTxtConfig.observe { `ForceDomain`.fmt(args) }

            /** HTTP 覆写 */
            val `HttpOverride`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """HTTP Override"""
                            `MLangTags`.ZH -> """HTTP 覆写"""
                            else -> null
                        }
                    } ?: """HTTP 覆写"""

            /** HTTP 覆写 */
            @Composable
            fun `HttpOverride`(vararg args: Any?) = FYTxtConfig.observe { `HttpOverride`.fmt(args) }

            /** Keep Alive 空闲阈值 */
            val `KeepAliveIdle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Keep Alive Idle"""
                            `MLangTags`.ZH -> """Keep Alive 空闲阈值"""
                            else -> null
                        }
                    } ?: """Keep Alive 空闲阈值"""

            /** Keep Alive 空闲阈值 */
            @Composable
            fun `KeepAliveIdle`(vararg args: Any?) =
                FYTxtConfig.observe { `KeepAliveIdle`.fmt(args) }

            /** Keep Alive 间隔 */
            val `KeepAliveInterval`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Keep Alive Interval"""
                            `MLangTags`.ZH -> """Keep Alive 间隔"""
                            else -> null
                        }
                    } ?: """Keep Alive 间隔"""

            /** Keep Alive 间隔 */
            @Composable
            fun `KeepAliveInterval`(vararg args: Any?) =
                FYTxtConfig.observe { `KeepAliveInterval`.fmt(args) }

            /** 覆写目标地址 */
            val `OverrideDestination`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Override Destination"""
                            `MLangTags`.ZH -> """覆写目标地址"""
                            else -> null
                        }
                    } ?: """覆写目标地址"""

            /** 覆写目标地址 */
            @Composable
            fun `OverrideDestination`(vararg args: Any?) =
                FYTxtConfig.observe { `OverrideDestination`.fmt(args) }

            /** 解析纯 IP */
            val `ParsePureIp`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Parse Pure IP"""
                            `MLangTags`.ZH -> """解析纯 IP"""
                            else -> null
                        }
                    } ?: """解析纯 IP"""

            /** 解析纯 IP */
            @Composable
            fun `ParsePureIp`(vararg args: Any?) = FYTxtConfig.observe { `ParsePureIp`.fmt(args) }

            /** QUIC 覆写 */
            val `QuicOverride`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """QUIC Override"""
                            `MLangTags`.ZH -> """QUIC 覆写"""
                            else -> null
                        }
                    } ?: """QUIC 覆写"""

            /** QUIC 覆写 */
            @Composable
            fun `QuicOverride`(vararg args: Any?) = FYTxtConfig.observe { `QuicOverride`.fmt(args) }

            /** 遵循路由规则 */
            val `RespectRules`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Respect Rules"""
                            `MLangTags`.ZH -> """遵循路由规则"""
                            else -> null
                        }
                    } ?: """遵循路由规则"""

            /** 遵循路由规则 */
            @Composable
            fun `RespectRules`(vararg args: Any?) = FYTxtConfig.observe { `RespectRules`.fmt(args) }

            /** 覆盖规则 */
            val `RulesReplace`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Override Rules"""
                            `MLangTags`.ZH -> """覆盖规则"""
                            else -> null
                        }
                    } ?: """覆盖规则"""

            /** 覆盖规则 */
            @Composable
            fun `RulesReplace`(vararg args: Any?) = FYTxtConfig.observe { `RulesReplace`.fmt(args) }

            /** 跳过嗅探域名 */
            val `SkipDomain`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Skip Domain"""
                            `MLangTags`.ZH -> """跳过嗅探域名"""
                            else -> null
                        }
                    } ?: """跳过嗅探域名"""

            /** 跳过嗅探域名 */
            @Composable
            fun `SkipDomain`(vararg args: Any?) = FYTxtConfig.observe { `SkipDomain`.fmt(args) }

            /** TLS 覆写 */
            val `TlsOverride`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """TLS Override"""
                            `MLangTags`.ZH -> """TLS 覆写"""
                            else -> null
                        }
                    } ?: """TLS 覆写"""

            /** TLS 覆写 */
            @Composable
            fun `TlsOverride`(vararg args: Any?) = FYTxtConfig.observe { `TlsOverride`.fmt(args) }

            /** 使用系统 Hosts */
            val `UseSystemHosts`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Use System Hosts"""
                            `MLangTags`.ZH -> """使用系统 Hosts"""
                            else -> null
                        }
                    } ?: """使用系统 Hosts"""

            /** 使用系统 Hosts */
            @Composable
            fun `UseSystemHosts`(vararg args: Any?) =
                FYTxtConfig.observe { `UseSystemHosts`.fmt(args) }
        }
    }

    object `ProfilesPage` {
        init {
            `MLangGroups`
        }

        /** 配置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Profiles"""
                        `MLangTags`.ZH -> """配置"""
                        else -> null
                    }
                } ?: """配置"""

        /** 配置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Action` {
            init {
                `MLangGroups`
            }

            /** 一键更新所有 */
            val `UpdateAll`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update All"""
                            `MLangTags`.ZH -> """一键更新所有"""
                            else -> null
                        }
                    } ?: """一键更新所有"""

            /** 一键更新所有 */
            @Composable
            fun `UpdateAll`(vararg args: Any?) = FYTxtConfig.observe { `UpdateAll`.fmt(args) }

            /** 添加配置 */
            val `AddProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Profile"""
                            `MLangTags`.ZH -> """添加配置"""
                            else -> null
                        }
                    } ?: """添加配置"""

            /** 添加配置 */
            @Composable
            fun `AddProfile`(vararg args: Any?) = FYTxtConfig.observe { `AddProfile`.fmt(args) }
        }

        object `Empty` {
            init {
                `MLangGroups`
            }

            /** 暂无配置文件 */
            val `NoProfiles`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No profiles"""
                            `MLangTags`.ZH -> """暂无配置文件"""
                            else -> null
                        }
                    } ?: """暂无配置文件"""

            /** 暂无配置文件 */
            @Composable
            fun `NoProfiles`(vararg args: Any?) = FYTxtConfig.observe { `NoProfiles`.fmt(args) }

            /** 点击右上角添加配置 */
            val `Hint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Click top-right to add profile"""
                            `MLangTags`.ZH -> """点击右上角添加配置"""
                            else -> null
                        }
                    } ?: """点击右上角添加配置"""

            /** 点击右上角添加配置 */
            @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }
        }

        object `Sheet` {
            init {
                `MLangGroups`
            }

            /** 添加配置文件 */
            val `AddTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Profile"""
                            `MLangTags`.ZH -> """添加配置文件"""
                            else -> null
                        }
                    } ?: """添加配置文件"""

            /** 添加配置文件 */
            @Composable
            fun `AddTitle`(vararg args: Any?) = FYTxtConfig.observe { `AddTitle`.fmt(args) }

            /** 编辑配置文件 */
            val `EditTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Profile"""
                            `MLangTags`.ZH -> """编辑配置文件"""
                            else -> null
                        }
                    } ?: """编辑配置文件"""

            /** 编辑配置文件 */
            @Composable
            fun `EditTitle`(vararg args: Any?) = FYTxtConfig.observe { `EditTitle`.fmt(args) }
        }

        object `Type` {
            init {
                `MLangGroups`
            }

            /** 配置类型 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile Type"""
                            `MLangTags`.ZH -> """配置类型"""
                            else -> null
                        }
                    } ?: """配置类型"""

            /** 配置类型 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 订阅链接 */
            val `Subscription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Subscription URL"""
                            `MLangTags`.ZH -> """订阅链接"""
                            else -> null
                        }
                    } ?: """订阅链接"""

            /** 订阅链接 */
            @Composable
            fun `Subscription`(vararg args: Any?) = FYTxtConfig.observe { `Subscription`.fmt(args) }

            /** 本地文件 */
            val `LocalFile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Local File"""
                            `MLangTags`.ZH -> """本地文件"""
                            else -> null
                        }
                    } ?: """本地文件"""

            /** 本地文件 */
            @Composable
            fun `LocalFile`(vararg args: Any?) = FYTxtConfig.observe { `LocalFile`.fmt(args) }

            /** 扫码添加 */
            val `QrScan`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Scan QR Code"""
                            `MLangTags`.ZH -> """扫码添加"""
                            else -> null
                        }
                    } ?: """扫码添加"""

            /** 扫码添加 */
            @Composable fun `QrScan`(vararg args: Any?) = FYTxtConfig.observe { `QrScan`.fmt(args) }

            /** 空白配置 */
            val `BlankConfig`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Blank Config"""
                            `MLangTags`.ZH -> """空白配置"""
                            else -> null
                        }
                    } ?: """空白配置"""

            /** 空白配置 */
            @Composable
            fun `BlankConfig`(vararg args: Any?) = FYTxtConfig.observe { `BlankConfig`.fmt(args) }
        }

        object `Input` {
            init {
                `MLangGroups`
            }

            /** 配置名称 */
            val `ProfileName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile Name"""
                            `MLangTags`.ZH -> """配置名称"""
                            else -> null
                        }
                    } ?: """配置名称"""

            /** 配置名称 */
            @Composable
            fun `ProfileName`(vararg args: Any?) = FYTxtConfig.observe { `ProfileName`.fmt(args) }

            /** 订阅链接 (HTTP/HTTPS) */
            val `SubscriptionUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Subscription URL (HTTP/HTTPS)"""
                            `MLangTags`.ZH -> """订阅链接 (HTTP/HTTPS)"""
                            else -> null
                        }
                    } ?: """订阅链接 (HTTP/HTTPS)"""

            /** 订阅链接 (HTTP/HTTPS) */
            @Composable
            fun `SubscriptionUrl`(vararg args: Any?) =
                FYTxtConfig.observe { `SubscriptionUrl`.fmt(args) }

            /** 点击选择文件 */
            val `SelectFile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Click to select file"""
                            `MLangTags`.ZH -> """点击选择文件"""
                            else -> null
                        }
                    } ?: """点击选择文件"""

            /** 点击选择文件 */
            @Composable
            fun `SelectFile`(vararg args: Any?) = FYTxtConfig.observe { `SelectFile`.fmt(args) }

            /** 新配置 */
            val `NewProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """New Profile"""
                            `MLangTags`.ZH -> """新配置"""
                            else -> null
                        }
                    } ?: """新配置"""

            /** 新配置 */
            @Composable
            fun `NewProfile`(vararg args: Any?) = FYTxtConfig.observe { `NewProfile`.fmt(args) }

            /** 创建一个本地配置模板，并立即在编辑器中打开。 */
            val `BlankConfigHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Create a local profile from the built-in basic template and open it in the editor immediately."""
                            `MLangTags`.ZH -> """基于内置基础模板创建本地配置，并立即在编辑器中打开。"""
                            else -> null
                        }
                    } ?: """基于内置基础模板创建本地配置，并立即在编辑器中打开。"""

            /** 基于内置基础模板创建本地配置，并立即在编辑器中打开。 */
            @Composable
            fun `BlankConfigHint`(vararg args: Any?) =
                FYTxtConfig.observe { `BlankConfigHint`.fmt(args) }
        }

        object `QrScanner` {
            init {
                `MLangGroups`
            }

            /** 需要相机权限 */
            val `NeedPermission`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Camera permission required"""
                            `MLangTags`.ZH -> """需要相机权限"""
                            else -> null
                        }
                    } ?: """需要相机权限"""

            /** 需要相机权限 */
            @Composable
            fun `NeedPermission`(vararg args: Any?) =
                FYTxtConfig.observe { `NeedPermission`.fmt(args) }

            /** 需要相机权限才能扫码 */
            val `NeedCamera`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Camera permission required for scanning"""
                            `MLangTags`.ZH -> """需要相机权限才能扫码"""
                            else -> null
                        }
                    } ?: """需要相机权限才能扫码"""

            /** 需要相机权限才能扫码 */
            @Composable
            fun `NeedCamera`(vararg args: Any?) = FYTxtConfig.observe { `NeedCamera`.fmt(args) }

            /** 从相册选择二维码图片 */
            val `SelectFromAlbum`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Select QR code from album"""
                            `MLangTags`.ZH -> """从相册选择二维码图片"""
                            else -> null
                        }
                    } ?: """从相册选择二维码图片"""

            /** 从相册选择二维码图片 */
            @Composable
            fun `SelectFromAlbum`(vararg args: Any?) =
                FYTxtConfig.observe { `SelectFromAlbum`.fmt(args) }

            /** 扫描成功 */
            val `ScanSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Scan successful"""
                            `MLangTags`.ZH -> """扫描成功"""
                            else -> null
                        }
                    } ?: """扫描成功"""

            /** 扫描成功 */
            @Composable
            fun `ScanSuccess`(vararg args: Any?) = FYTxtConfig.observe { `ScanSuccess`.fmt(args) }

            /** 识别成功 */
            val `RecognizeSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Recognition successful"""
                            `MLangTags`.ZH -> """识别成功"""
                            else -> null
                        }
                    } ?: """识别成功"""

            /** 识别成功 */
            @Composable
            fun `RecognizeSuccess`(vararg args: Any?) =
                FYTxtConfig.observe { `RecognizeSuccess`.fmt(args) }

            /** 未能识别到二维码 */
            val `RecognizeFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to recognize QR code"""
                            `MLangTags`.ZH -> """未能识别到二维码"""
                            else -> null
                        }
                    } ?: """未能识别到二维码"""

            /** 未能识别到二维码 */
            @Composable
            fun `RecognizeFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `RecognizeFailed`.fmt(args) }

            /** 识别失败：%s */
            val `RecognizeError`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Recognition failed: %s"""
                            `MLangTags`.ZH -> """识别失败：%s"""
                            else -> null
                        }
                    } ?: """识别失败：%s"""

            /** 识别失败：%s */
            @Composable
            fun `RecognizeError`(vararg args: Any?) =
                FYTxtConfig.observe { `RecognizeError`.fmt(args) }
        }

        object `Message` {
            init {
                `MLangGroups`
            }

            /** 未知文件 */
            val `UnknownFile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unknown file"""
                            `MLangTags`.ZH -> """未知文件"""
                            else -> null
                        }
                    } ?: """未知文件"""

            /** 未知文件 */
            @Composable
            fun `UnknownFile`(vararg args: Any?) = FYTxtConfig.observe { `UnknownFile`.fmt(args) }

            /** 读取配置失败 */
            val `ReadProfileFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to read profile"""
                            `MLangTags`.ZH -> """读取配置失败"""
                            else -> null
                        }
                    } ?: """读取配置失败"""

            /** 读取配置失败 */
            @Composable
            fun `ReadProfileFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `ReadProfileFailed`.fmt(args) }

            /** 配置文件不存在 */
            val `ProfileFileNotExist`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile file does not exist"""
                            `MLangTags`.ZH -> """配置文件不存在"""
                            else -> null
                        }
                    } ?: """配置文件不存在"""

            /** 配置文件不存在 */
            @Composable
            fun `ProfileFileNotExist`(vararg args: Any?) =
                FYTxtConfig.observe { `ProfileFileNotExist`.fmt(args) }

            /** 分享失败 */
            val `ShareFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Share failed"""
                            `MLangTags`.ZH -> """分享失败"""
                            else -> null
                        }
                    } ?: """分享失败"""

            /** 分享失败 */
            @Composable
            fun `ShareFailed`(vararg args: Any?) = FYTxtConfig.observe { `ShareFailed`.fmt(args) }
        }

        object `Validation` {
            init {
                `MLangGroups`
            }

            /** 请输入链接 */
            val `EnterUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Please enter URL"""
                            `MLangTags`.ZH -> """请输入链接"""
                            else -> null
                        }
                    } ?: """请输入链接"""

            /** 请输入链接 */
            @Composable
            fun `EnterUrl`(vararg args: Any?) = FYTxtConfig.observe { `EnterUrl`.fmt(args) }

            /** 请选择文件 */
            val `SelectFile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Please select a file"""
                            `MLangTags`.ZH -> """请选择文件"""
                            else -> null
                        }
                    } ?: """请选择文件"""

            /** 请选择文件 */
            @Composable
            fun `SelectFile`(vararg args: Any?) = FYTxtConfig.observe { `SelectFile`.fmt(args) }

            /** 仅支持 .yaml 或 .yml 格式的配置文件 */
            val `YamlOnly`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Only .yaml or .yml format supported"""
                            `MLangTags`.ZH -> """仅支持 .yaml 或 .yml 格式的配置文件"""
                            else -> null
                        }
                    } ?: """仅支持 .yaml 或 .yml 格式的配置文件"""

            /** 仅支持 .yaml 或 .yml 格式的配置文件 */
            @Composable
            fun `YamlOnly`(vararg args: Any?) = FYTxtConfig.observe { `YamlOnly`.fmt(args) }
        }

        object `Progress` {
            init {
                `MLangGroups`
            }

            /** 下载中... */
            val `Downloading`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Downloading..."""
                            `MLangTags`.ZH -> """下载中..."""
                            else -> null
                        }
                    } ?: """下载中..."""

            /** 下载中... */
            @Composable
            fun `Downloading`(vararg args: Any?) = FYTxtConfig.observe { `Downloading`.fmt(args) }
        }

        object `Button` {
            init {
                `MLangGroups`
            }

            /** 取消 */
            val `Cancel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cancel"""
                            `MLangTags`.ZH -> """取消"""
                            else -> null
                        }
                    } ?: """取消"""

            /** 取消 */
            @Composable fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

            /** 确定 */
            val `Confirm`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Confirm"""
                            `MLangTags`.ZH -> """确定"""
                            else -> null
                        }
                    } ?: """确定"""

            /** 确定 */
            @Composable
            fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }
        }

        object `DeleteDialog` {
            init {
                `MLangGroups`
            }

            /** 删除配置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete Profile"""
                            `MLangTags`.ZH -> """删除配置"""
                            else -> null
                        }
                    } ?: """删除配置"""

            /** 删除配置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 确定要删除「%s」吗？ */
            val `Message`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Are you sure you want to delete '%s'?"""
                            `MLangTags`.ZH -> """确定要删除「%s」吗？"""
                            else -> null
                        }
                    } ?: """确定要删除「%s」吗？"""

            /** 确定要删除「%s」吗？ */
            @Composable
            fun `Message`(vararg args: Any?) = FYTxtConfig.observe { `Message`.fmt(args) }

            /** 删除 */
            val `Confirm`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete"""
                            `MLangTags`.ZH -> """删除"""
                            else -> null
                        }
                    } ?: """删除"""

            /** 删除 */
            @Composable
            fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }
        }

        object `EditDialog` {
            init {
                `MLangGroups`
            }

            /** 编辑配置名称 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Profile Name"""
                            `MLangTags`.ZH -> """编辑配置名称"""
                            else -> null
                        }
                    } ?: """编辑配置名称"""

            /** 编辑配置名称 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }
        }

        object `LinkSettings` {
            init {
                `MLangGroups`
            }

            /** 链接设置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Link Settings"""
                            `MLangTags`.ZH -> """链接设置"""
                            else -> null
                        }
                    } ?: """链接设置"""

            /** 链接设置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 打开方式 */
            val `OpenMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Open Mode"""
                            `MLangTags`.ZH -> """打开方式"""
                            else -> null
                        }
                    } ?: """打开方式"""

            /** 打开方式 */
            @Composable
            fun `OpenMode`(vararg args: Any?) = FYTxtConfig.observe { `OpenMode`.fmt(args) }

            /** App 内打开 */
            val `OpenModeInApp`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """In App"""
                            `MLangTags`.ZH -> """App 内打开"""
                            else -> null
                        }
                    } ?: """App 内打开"""

            /** App 内打开 */
            @Composable
            fun `OpenModeInApp`(vararg args: Any?) =
                FYTxtConfig.observe { `OpenModeInApp`.fmt(args) }

            /** 外部浏览器 */
            val `OpenModeExternal`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """External Browser"""
                            `MLangTags`.ZH -> """外部浏览器"""
                            else -> null
                        }
                    } ?: """外部浏览器"""

            /** 外部浏览器 */
            @Composable
            fun `OpenModeExternal`(vararg args: Any?) =
                FYTxtConfig.observe { `OpenModeExternal`.fmt(args) }

            /** 默认链接 */
            val `DefaultLink`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Default Link"""
                            `MLangTags`.ZH -> """默认链接"""
                            else -> null
                        }
                    } ?: """默认链接"""

            /** 默认链接 */
            @Composable
            fun `DefaultLink`(vararg args: Any?) = FYTxtConfig.observe { `DefaultLink`.fmt(args) }

            /** 点击左上角快捷按钮时打开的链接 */
            val `DefaultLinkSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Link opened when clicking top-left shortcut button"""
                            `MLangTags`.ZH -> """点击左上角快捷按钮时打开的链接"""
                            else -> null
                        }
                    } ?: """点击左上角快捷按钮时打开的链接"""

            /** 点击左上角快捷按钮时打开的链接 */
            @Composable
            fun `DefaultLinkSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `DefaultLinkSummary`.fmt(args) }

            /** 添加链接 */
            val `AddLink`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Link"""
                            `MLangTags`.ZH -> """添加链接"""
                            else -> null
                        }
                    } ?: """添加链接"""

            /** 添加链接 */
            @Composable
            fun `AddLink`(vararg args: Any?) = FYTxtConfig.observe { `AddLink`.fmt(args) }

            /** 编辑链接 */
            val `EditLink`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Link"""
                            `MLangTags`.ZH -> """编辑链接"""
                            else -> null
                        }
                    } ?: """编辑链接"""

            /** 编辑链接 */
            @Composable
            fun `EditLink`(vararg args: Any?) = FYTxtConfig.observe { `EditLink`.fmt(args) }

            /** 名称 */
            val `Name`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Name"""
                            `MLangTags`.ZH -> """名称"""
                            else -> null
                        }
                    } ?: """名称"""

            /** 名称 */
            @Composable fun `Name`(vararg args: Any?) = FYTxtConfig.observe { `Name`.fmt(args) }

            /** 链接 */
            val `Url`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """URL"""
                            `MLangTags`.ZH -> """链接"""
                            else -> null
                        }
                    } ?: """链接"""

            /** 链接 */
            @Composable fun `Url`(vararg args: Any?) = FYTxtConfig.observe { `Url`.fmt(args) }

            /** 关闭 */
            val `Close`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Close"""
                            `MLangTags`.ZH -> """关闭"""
                            else -> null
                        }
                    } ?: """关闭"""

            /** 关闭 */
            @Composable fun `Close`(vararg args: Any?) = FYTxtConfig.observe { `Close`.fmt(args) }

            object `Validation` {
                init {
                    `MLangGroups`
                }

                /** 请输入名称 */
                val `EnterName`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Please enter name"""
                                `MLangTags`.ZH -> """请输入名称"""
                                else -> null
                            }
                        } ?: """请输入名称"""

                /** 请输入名称 */
                @Composable
                fun `EnterName`(vararg args: Any?) = FYTxtConfig.observe { `EnterName`.fmt(args) }

                /** 请输入链接 */
                val `EnterUrl`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Please enter URL"""
                                `MLangTags`.ZH -> """请输入链接"""
                                else -> null
                            }
                        } ?: """请输入链接"""

                /** 请输入链接 */
                @Composable
                fun `EnterUrl`(vararg args: Any?) = FYTxtConfig.observe { `EnterUrl`.fmt(args) }

                /** 请输入有效的链接 */
                val `InvalidUrl`
                    get() =
                        FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                            it as `MLangTags`
                            when (it) {
                                `MLangTags`.EN -> """Please enter a valid URL"""
                                `MLangTags`.ZH -> """请输入有效的链接"""
                                else -> null
                            }
                        } ?: """请输入有效的链接"""

                /** 请输入有效的链接 */
                @Composable
                fun `InvalidUrl`(vararg args: Any?) = FYTxtConfig.observe { `InvalidUrl`.fmt(args) }
            }
        }

        object `ShareDialog` {
            init {
                `MLangGroups`
            }

            /** 分享配置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Share Profile"""
                            `MLangTags`.ZH -> """分享配置"""
                            else -> null
                        }
                    } ?: """分享配置"""

            /** 分享配置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 分享配置文件 */
            val `ShareFile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Share Config File"""
                            `MLangTags`.ZH -> """分享配置文件"""
                            else -> null
                        }
                    } ?: """分享配置文件"""

            /** 分享配置文件 */
            @Composable
            fun `ShareFile`(vararg args: Any?) = FYTxtConfig.observe { `ShareFile`.fmt(args) }

            /** 分享订阅链接 */
            val `ShareLink`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Share Subscription Link"""
                            `MLangTags`.ZH -> """分享订阅链接"""
                            else -> null
                        }
                    } ?: """分享订阅链接"""

            /** 分享订阅链接 */
            @Composable
            fun `ShareLink`(vararg args: Any?) = FYTxtConfig.observe { `ShareLink`.fmt(args) }

            /** 该配置没有订阅链接 */
            val `NoLink`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """This profile has no subscription link"""
                            `MLangTags`.ZH -> """该配置没有订阅链接"""
                            else -> null
                        }
                    } ?: """该配置没有订阅链接"""

            /** 该配置没有订阅链接 */
            @Composable fun `NoLink`(vararg args: Any?) = FYTxtConfig.observe { `NoLink`.fmt(args) }
        }

        object `Misc` {
            init {
                `MLangGroups`
            }

            /** 完成 */
            val `Complete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Complete"""
                            `MLangTags`.ZH -> """完成"""
                            else -> null
                        }
                    } ?: """完成"""

            /** 完成 */
            @Composable
            fun `Complete`(vararg args: Any?) = FYTxtConfig.observe { `Complete`.fmt(args) }

            /** 错误 */
            val `Error`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Error"""
                            `MLangTags`.ZH -> """错误"""
                            else -> null
                        }
                    } ?: """错误"""

            /** 错误 */
            @Composable fun `Error`(vararg args: Any?) = FYTxtConfig.observe { `Error`.fmt(args) }
        }

        object `SettingsDialog` {
            init {
                `MLangGroups`
            }

            /** 订阅设置 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Subscription Settings"""
                            `MLangTags`.ZH -> """订阅设置"""
                            else -> null
                        }
                    } ?: """订阅设置"""

            /** 订阅设置 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 更改订阅链接 */
            val `ChangeLink`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Change Subscription Link"""
                            `MLangTags`.ZH -> """更改订阅链接"""
                            else -> null
                        }
                    } ?: """更改订阅链接"""

            /** 更改订阅链接 */
            @Composable
            fun `ChangeLink`(vararg args: Any?) = FYTxtConfig.observe { `ChangeLink`.fmt(args) }

            /** 预设覆写 */
            val `SystemPreset`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """System Preset Override"""
                            `MLangTags`.ZH -> """预设覆写"""
                            else -> null
                        }
                    } ?: """预设覆写"""

            /** 预设覆写 */
            @Composable
            fun `SystemPreset`(vararg args: Any?) = FYTxtConfig.observe { `SystemPreset`.fmt(args) }

            /** 如果不知道什么是分流，推荐打开 */
            val `SystemPresetSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Enable the built-in default override preset if you want a usable routing baseline without configuring rules yourself"""
                            `MLangTags`.ZH -> """如果想先得到一套可用的默认分流基础、又不打算自己配置规则，可以直接开启"""
                            else -> null
                        }
                    } ?: """如果想先得到一套可用的默认分流基础、又不打算自己配置规则，可以直接开启"""

            /** 如果想先得到一套可用的默认分流基础、又不打算自己配置规则，可以直接开启 */
            @Composable
            fun `SystemPresetSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `SystemPresetSummary`.fmt(args) }

            /** 未设置说明 */
            val `NoDescription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No description set"""
                            `MLangTags`.ZH -> """未设置说明"""
                            else -> null
                        }
                    } ?: """未设置说明"""

            /** 未设置说明 */
            @Composable
            fun `NoDescription`(vararg args: Any?) =
                FYTxtConfig.observe { `NoDescription`.fmt(args) }

            /** 本地配置编辑器 */
            val `LocalConfigEditorSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Open the raw YAML content of the current local config for direct editing"""
                            `MLangTags`.ZH -> """打开当前本地配置的原始 YAML 内容进行直接编辑"""
                            else -> null
                        }
                    } ?: """打开当前本地配置的原始 YAML 内容进行直接编辑"""

            /** 打开当前本地配置的原始 YAML 内容进行直接编辑 */
            @Composable
            fun `LocalConfigEditorSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `LocalConfigEditorSummary`.fmt(args) }

            /** 配置来源 */
            val `LocalSource`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Config Source"""
                            `MLangTags`.ZH -> """配置来源"""
                            else -> null
                        }
                    } ?: """配置来源"""

            /** 配置来源 */
            @Composable
            fun `LocalSource`(vararg args: Any?) = FYTxtConfig.observe { `LocalSource`.fmt(args) }

            /** 空白配置 */
            val `LocalSourceBlank`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Built-in Basic Template"""
                            `MLangTags`.ZH -> """内置基础模板"""
                            else -> null
                        }
                    } ?: """内置基础模板"""

            /** 内置基础模板 */
            @Composable
            fun `LocalSourceBlank`(vararg args: Any?) =
                FYTxtConfig.observe { `LocalSourceBlank`.fmt(args) }

            /** 导入文件 */
            val `LocalSourceImported`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Imported Local File"""
                            `MLangTags`.ZH -> """已导入本地文件"""
                            else -> null
                        }
                    } ?: """已导入本地文件"""

            /** 已导入本地文件 */
            @Composable
            fun `LocalSourceImported`(vararg args: Any?) =
                FYTxtConfig.observe { `LocalSourceImported`.fmt(args) }

            /** 编辑配置 */
            val `EditProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Profile"""
                            `MLangTags`.ZH -> """编辑配置"""
                            else -> null
                        }
                    } ?: """编辑配置"""

            /** 编辑配置 */
            @Composable
            fun `EditProfile`(vararg args: Any?) = FYTxtConfig.observe { `EditProfile`.fmt(args) }

            /** 打开配置 */
            val `OpenConfig`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Edit Text"""
                            `MLangTags`.ZH -> """编辑文本"""
                            else -> null
                        }
                    } ?: """编辑文本"""

            /** 打开配置 */
            @Composable
            fun `OpenConfig`(vararg args: Any?) = FYTxtConfig.observe { `OpenConfig`.fmt(args) }

            /** 编辑设置 */
            val `EditSettings`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile Options"""
                            `MLangTags`.ZH -> """配置选项"""
                            else -> null
                        }
                    } ?: """配置选项"""

            /** 编辑设置 */
            @Composable
            fun `EditSettings`(vararg args: Any?) = FYTxtConfig.observe { `EditSettings`.fmt(args) }

            /** 保存配置失败 */
            val `SaveFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to save profile"""
                            `MLangTags`.ZH -> """保存配置失败"""
                            else -> null
                        }
                    } ?: """保存配置失败"""

            /** 保存配置失败 */
            @Composable
            fun `SaveFailed`(vararg args: Any?) = FYTxtConfig.observe { `SaveFailed`.fmt(args) }

            /** 规则复写 */
            val `RuleOverrides`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule Overrides"""
                            `MLangTags`.ZH -> """规则复写"""
                            else -> null
                        }
                    } ?: """规则复写"""

            /** 规则复写 */
            @Composable
            fun `RuleOverrides`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleOverrides`.fmt(args) }

            /** 越靠上优先级越高，新增项会插入最前 */
            val `RuleOverridesPriorityHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Overrides are applied from top to bottom.
Higher items take precedence, and new items are inserted at the top."""
                            `MLangTags`.ZH ->
                                """规则复写会按从上到下的顺序应用。
越靠上优先级越高，新项会插入最前。"""
                            else -> null
                        }
                    }
                        ?: """规则复写会按从上到下的顺序应用。
越靠上优先级越高，新项会插入最前。"""

            /** 规则复写会按从上到下的顺序应用。 越靠上优先级越高，新项会插入最前。 */
            @Composable
            fun `RuleOverridesPriorityHint`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleOverridesPriorityHint`.fmt(args) }
        }
    }

    object `ProfilesVM` {
        init {
            `MLangGroups`
        }

        object `Message` {
            init {
                `MLangGroups`
            }

            /** 配置已添加：%s */
            val `ProfileAdded`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile added: %s"""
                            `MLangTags`.ZH -> """配置已添加：%s"""
                            else -> null
                        }
                    } ?: """配置已添加：%s"""

            /** 配置已添加：%s */
            @Composable
            fun `ProfileAdded`(vararg args: Any?) = FYTxtConfig.observe { `ProfileAdded`.fmt(args) }

            /** 添加配置失败：%s */
            val `AddFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add profile failed: %s"""
                            `MLangTags`.ZH -> """添加配置失败：%s"""
                            else -> null
                        }
                    } ?: """添加配置失败：%s"""

            /** 添加配置失败：%s */
            @Composable
            fun `AddFailed`(vararg args: Any?) = FYTxtConfig.observe { `AddFailed`.fmt(args) }

            /** 配置已删除 */
            val `ProfileDeleted`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile deleted"""
                            `MLangTags`.ZH -> """配置已删除"""
                            else -> null
                        }
                    } ?: """配置已删除"""

            /** 配置已删除 */
            @Composable
            fun `ProfileDeleted`(vararg args: Any?) =
                FYTxtConfig.observe { `ProfileDeleted`.fmt(args) }

            /** 删除配置失败：%s */
            val `DeleteFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Delete profile failed: %s"""
                            `MLangTags`.ZH -> """删除配置失败：%s"""
                            else -> null
                        }
                    } ?: """删除配置失败：%s"""

            /** 删除配置失败：%s */
            @Composable
            fun `DeleteFailed`(vararg args: Any?) = FYTxtConfig.observe { `DeleteFailed`.fmt(args) }

            /** 配置已更新：%s */
            val `ProfileUpdated`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile updated: %s"""
                            `MLangTags`.ZH -> """配置已更新：%s"""
                            else -> null
                        }
                    } ?: """配置已更新：%s"""

            /** 配置已更新：%s */
            @Composable
            fun `ProfileUpdated`(vararg args: Any?) =
                FYTxtConfig.observe { `ProfileUpdated`.fmt(args) }

            /** 更新配置失败：%s */
            val `UpdateFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update profile failed: %s"""
                            `MLangTags`.ZH -> """更新配置失败：%s"""
                            else -> null
                        }
                    } ?: """更新配置失败：%s"""

            /** 更新配置失败：%s */
            @Composable
            fun `UpdateFailed`(vararg args: Any?) = FYTxtConfig.observe { `UpdateFailed`.fmt(args) }

            /** 切换状态失败：%s */
            val `ToggleFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Toggle state failed: %s"""
                            `MLangTags`.ZH -> """切换状态失败：%s"""
                            else -> null
                        }
                    } ?: """切换状态失败：%s"""

            /** 切换状态失败：%s */
            @Composable
            fun `ToggleFailed`(vararg args: Any?) = FYTxtConfig.observe { `ToggleFailed`.fmt(args) }

            /** 配置已导入：%s */
            val `ProfileImported`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile imported: %s"""
                            `MLangTags`.ZH -> """配置已导入：%s"""
                            else -> null
                        }
                    } ?: """配置已导入：%s"""

            /** 配置已导入：%s */
            @Composable
            fun `ProfileImported`(vararg args: Any?) =
                FYTxtConfig.observe { `ProfileImported`.fmt(args) }

            /** 导入配置失败：%s */
            val `ImportFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import profile failed: %s"""
                            `MLangTags`.ZH -> """导入配置失败：%s"""
                            else -> null
                        }
                    } ?: """导入配置失败：%s"""

            /** 导入配置失败：%s */
            @Composable
            fun `ImportFailed`(vararg args: Any?) = FYTxtConfig.observe { `ImportFailed`.fmt(args) }
        }

        object `Progress` {
            init {
                `MLangGroups`
            }

            /** 准备下载... */
            val `Preparing`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Preparing download..."""
                            `MLangTags`.ZH -> """准备下载..."""
                            else -> null
                        }
                    } ?: """准备下载..."""

            /** 准备下载... */
            @Composable
            fun `Preparing`(vararg args: Any?) = FYTxtConfig.observe { `Preparing`.fmt(args) }

            /** 准备导入文件... */
            val `ImportPreparing`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Preparing to import file..."""
                            `MLangTags`.ZH -> """准备导入文件..."""
                            else -> null
                        }
                    } ?: """准备导入文件..."""

            /** 准备导入文件... */
            @Composable
            fun `ImportPreparing`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportPreparing`.fmt(args) }

            /** 正在验证配置... */
            val `Verifying`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Verifying configuration..."""
                            `MLangTags`.ZH -> """正在验证配置..."""
                            else -> null
                        }
                    } ?: """正在验证配置..."""

            /** 正在验证配置... */
            @Composable
            fun `Verifying`(vararg args: Any?) = FYTxtConfig.observe { `Verifying`.fmt(args) }

            /** 导入完成 */
            val `ImportComplete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Import complete"""
                            `MLangTags`.ZH -> """导入完成"""
                            else -> null
                        }
                    } ?: """导入完成"""

            /** 导入完成 */
            @Composable
            fun `ImportComplete`(vararg args: Any?) =
                FYTxtConfig.observe { `ImportComplete`.fmt(args) }
        }

        object `Error` {
            init {
                `MLangGroups`
            }

            /** 配置不存在 */
            val `ProfileNotExist`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Profile does not exist"""
                            `MLangTags`.ZH -> """配置不存在"""
                            else -> null
                        }
                    } ?: """配置不存在"""

            /** 配置不存在 */
            @Composable
            fun `ProfileNotExist`(vararg args: Any?) =
                FYTxtConfig.observe { `ProfileNotExist`.fmt(args) }

            /** 未知错误 */
            val `Unknown`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unknown"""
                            `MLangTags`.ZH -> """未知错误"""
                            else -> null
                        }
                    } ?: """未知错误"""

            /** 未知错误 */
            @Composable
            fun `Unknown`(vararg args: Any?) = FYTxtConfig.observe { `Unknown`.fmt(args) }
        }
    }

    object `Providers` {
        init {
            `MLangGroups`
        }

        /** 外部资源 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """External Resources"""
                        `MLangTags`.ZH -> """外部资源"""
                        else -> null
                    }
                } ?: """外部资源"""

        /** 外部资源 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Action` {
            init {
                `MLangGroups`
            }

            /** 更新全部 */
            val `UpdateAll`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update All"""
                            `MLangTags`.ZH -> """更新全部"""
                            else -> null
                        }
                    } ?: """更新全部"""

            /** 更新全部 */
            @Composable
            fun `UpdateAll`(vararg args: Any?) = FYTxtConfig.observe { `UpdateAll`.fmt(args) }

            /** 更新 */
            val `Update`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update"""
                            `MLangTags`.ZH -> """更新"""
                            else -> null
                        }
                    } ?: """更新"""

            /** 更新 */
            @Composable fun `Update`(vararg args: Any?) = FYTxtConfig.observe { `Update`.fmt(args) }

            /** 上传 */
            val `Upload`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Upload"""
                            `MLangTags`.ZH -> """上传"""
                            else -> null
                        }
                    } ?: """上传"""

            /** 上传 */
            @Composable fun `Upload`(vararg args: Any?) = FYTxtConfig.observe { `Upload`.fmt(args) }

            /** 操作 */
            val `Operation`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Operation"""
                            `MLangTags`.ZH -> """操作"""
                            else -> null
                        }
                    } ?: """操作"""

            /** 操作 */
            @Composable
            fun `Operation`(vararg args: Any?) = FYTxtConfig.observe { `Operation`.fmt(args) }
        }

        object `Empty` {
            init {
                `MLangGroups`
            }

            /** 代理未启动 */
            val `NotRunning`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy not running"""
                            `MLangTags`.ZH -> """代理未启动"""
                            else -> null
                        }
                    } ?: """代理未启动"""

            /** 代理未启动 */
            @Composable
            fun `NotRunning`(vararg args: Any?) = FYTxtConfig.observe { `NotRunning`.fmt(args) }

            /** 请先启动代理服务以查看外部资源 */
            val `NotRunningHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Please start proxy service to view external resources"""
                            `MLangTags`.ZH -> """请先启动代理服务以查看外部资源"""
                            else -> null
                        }
                    } ?: """请先启动代理服务以查看外部资源"""

            /** 请先启动代理服务以查看外部资源 */
            @Composable
            fun `NotRunningHint`(vararg args: Any?) =
                FYTxtConfig.observe { `NotRunningHint`.fmt(args) }

            /** 暂无外部资源 */
            val `NoProviders`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No external resources"""
                            `MLangTags`.ZH -> """暂无外部资源"""
                            else -> null
                        }
                    } ?: """暂无外部资源"""

            /** 暂无外部资源 */
            @Composable
            fun `NoProviders`(vararg args: Any?) = FYTxtConfig.observe { `NoProviders`.fmt(args) }

            /** 当前配置未包含外部资源 */
            val `NoProvidersHint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Current profile doesn't contain external resources"""
                            `MLangTags`.ZH -> """当前配置未包含外部资源"""
                            else -> null
                        }
                    } ?: """当前配置未包含外部资源"""

            /** 当前配置未包含外部资源 */
            @Composable
            fun `NoProvidersHint`(vararg args: Any?) =
                FYTxtConfig.observe { `NoProvidersHint`.fmt(args) }
        }

        object `Type` {
            init {
                `MLangGroups`
            }

            /** 代理提供者 (%d) */
            val `ProxyProviders`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Proxy Providers (%d)"""
                            `MLangTags`.ZH -> """代理提供者 (%d)"""
                            else -> null
                        }
                    } ?: """代理提供者 (%d)"""

            /** 代理提供者 (%d) */
            @Composable
            fun `ProxyProviders`(vararg args: Any?) =
                FYTxtConfig.observe { `ProxyProviders`.fmt(args) }

            /** 规则提供者 (%d) */
            val `RuleProviders`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule Providers (%d)"""
                            `MLangTags`.ZH -> """规则提供者 (%d)"""
                            else -> null
                        }
                    } ?: """规则提供者 (%d)"""

            /** 规则提供者 (%d) */
            @Composable
            fun `RuleProviders`(vararg args: Any?) =
                FYTxtConfig.observe { `RuleProviders`.fmt(args) }

            /** 覆写外部资源 (%d) */
            val `OverrideResources`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Override Remote Resources (%d)"""
                            `MLangTags`.ZH -> """覆写外部资源 (%d)"""
                            else -> null
                        }
                    } ?: """覆写外部资源 (%d)"""

            /** 覆写外部资源 (%d) */
            @Composable
            fun `OverrideResources`(vararg args: Any?) =
                FYTxtConfig.observe { `OverrideResources`.fmt(args) }
        }

        object `Summary` {
            init {
                `MLangGroups`
            }

            /** 更新间隔 %d 秒 · 规则 %d 条 */
            val `OverrideIntervalAndCount`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update every %d s · %d rules"""
                            `MLangTags`.ZH -> """更新间隔 %d 秒 · 规则 %d 条"""
                            else -> null
                        }
                    } ?: """更新间隔 %d 秒 · 规则 %d 条"""

            /** 更新间隔 %d 秒 · 规则 %d 条 */
            @Composable
            fun `OverrideIntervalAndCount`(vararg args: Any?) =
                FYTxtConfig.observe { `OverrideIntervalAndCount`.fmt(args) }
        }

        object `Message` {
            init {
                `MLangGroups`
            }

            /** 获取外部资源失败: %s */
            val `FetchFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed to fetch resources: %s"""
                            `MLangTags`.ZH -> """获取外部资源失败: %s"""
                            else -> null
                        }
                    } ?: """获取外部资源失败: %s"""

            /** 获取外部资源失败: %s */
            @Composable
            fun `FetchFailed`(vararg args: Any?) = FYTxtConfig.observe { `FetchFailed`.fmt(args) }

            /** %s 更新成功 */
            val `UpdateSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%s updated successfully"""
                            `MLangTags`.ZH -> """%s 更新成功"""
                            else -> null
                        }
                    } ?: """%s 更新成功"""

            /** %s 更新成功 */
            @Composable
            fun `UpdateSuccess`(vararg args: Any?) =
                FYTxtConfig.observe { `UpdateSuccess`.fmt(args) }

            /** 更新失败: %s */
            val `UpdateFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Update failed: %s"""
                            `MLangTags`.ZH -> """更新失败: %s"""
                            else -> null
                        }
                    } ?: """更新失败: %s"""

            /** 更新失败: %s */
            @Composable
            fun `UpdateFailed`(vararg args: Any?) = FYTxtConfig.observe { `UpdateFailed`.fmt(args) }

            /** 以下资源更新失败: %s */
            val `UpdateFailedResources`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Failed resources: %s"""
                            `MLangTags`.ZH -> """以下资源更新失败: %s"""
                            else -> null
                        }
                    } ?: """以下资源更新失败: %s"""

            /** 以下资源更新失败: %s */
            @Composable
            fun `UpdateFailedResources`(vararg args: Any?) =
                FYTxtConfig.observe { `UpdateFailedResources`.fmt(args) }

            /** 全部更新完成 */
            val `AllUpdated`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """All updated"""
                            `MLangTags`.ZH -> """全部更新完成"""
                            else -> null
                        }
                    } ?: """全部更新完成"""

            /** 全部更新完成 */
            @Composable
            fun `AllUpdated`(vararg args: Any?) = FYTxtConfig.observe { `AllUpdated`.fmt(args) }

            /** %s 上传成功 */
            val `UploadSuccess`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%s uploaded successfully"""
                            `MLangTags`.ZH -> """%s 上传成功"""
                            else -> null
                        }
                    } ?: """%s 上传成功"""

            /** %s 上传成功 */
            @Composable
            fun `UploadSuccess`(vararg args: Any?) =
                FYTxtConfig.observe { `UploadSuccess`.fmt(args) }

            /** 上传失败: %s */
            val `UploadFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Upload failed: %s"""
                            `MLangTags`.ZH -> """上传失败: %s"""
                            else -> null
                        }
                    } ?: """上传失败: %s"""

            /** 上传失败: %s */
            @Composable
            fun `UploadFailed`(vararg args: Any?) = FYTxtConfig.observe { `UploadFailed`.fmt(args) }

            /** 无法读取文件: %s */
            val `ReadFileFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Cannot read file: %s"""
                            `MLangTags`.ZH -> """无法读取文件: %s"""
                            else -> null
                        }
                    } ?: """无法读取文件: %s"""

            /** 无法读取文件: %s */
            @Composable
            fun `ReadFileFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `ReadFileFailed`.fmt(args) }

            /** 文件超过 %dMB 限制 */
            val `UploadSizeExceeded`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """File exceeds %dMB limit"""
                            `MLangTags`.ZH -> """文件超过 %dMB 限制"""
                            else -> null
                        }
                    } ?: """文件超过 %dMB 限制"""

            /** 文件超过 %dMB 限制 */
            @Composable
            fun `UploadSizeExceeded`(vararg args: Any?) =
                FYTxtConfig.observe { `UploadSizeExceeded`.fmt(args) }

            /** 未知错误 */
            val `UnknownError`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unknown error"""
                            `MLangTags`.ZH -> """未知错误"""
                            else -> null
                        }
                    } ?: """未知错误"""

            /** 未知错误 */
            @Composable
            fun `UnknownError`(vararg args: Any?) = FYTxtConfig.observe { `UnknownError`.fmt(args) }
        }
    }

    object `Proxy` {
        init {
            `MLangGroups`
        }

        /** 节点 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Nodes"""
                        `MLangTags`.ZH -> """节点"""
                        else -> null
                    }
                } ?: """节点"""

        /** 节点 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Mode` {
            init {
                `MLangGroups`
            }

            /** 直连 */
            val `Direct`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Direct"""
                            `MLangTags`.ZH -> """直连"""
                            else -> null
                        }
                    } ?: """直连"""

            /** 直连 */
            @Composable fun `Direct`(vararg args: Any?) = FYTxtConfig.observe { `Direct`.fmt(args) }

            /** 全局 */
            val `Global`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Global"""
                            `MLangTags`.ZH -> """全局"""
                            else -> null
                        }
                    } ?: """全局"""

            /** 全局 */
            @Composable fun `Global`(vararg args: Any?) = FYTxtConfig.observe { `Global`.fmt(args) }

            /** 规则 */
            val `Rule`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule"""
                            `MLangTags`.ZH -> """规则"""
                            else -> null
                        }
                    } ?: """规则"""

            /** 规则 */
            @Composable fun `Rule`(vararg args: Any?) = FYTxtConfig.observe { `Rule`.fmt(args) }

            /** 未知 */
            val `Unknown`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unknown"""
                            `MLangTags`.ZH -> """未知"""
                            else -> null
                        }
                    } ?: """未知"""

            /** 未知 */
            @Composable
            fun `Unknown`(vararg args: Any?) = FYTxtConfig.observe { `Unknown`.fmt(args) }

            /** 已切换到：%s 模式 */
            val `Switched`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Switched to: %s mode"""
                            `MLangTags`.ZH -> """已切换到：%s 模式"""
                            else -> null
                        }
                    } ?: """已切换到：%s 模式"""

            /** 已切换到：%s 模式 */
            @Composable
            fun `Switched`(vararg args: Any?) = FYTxtConfig.observe { `Switched`.fmt(args) }

            /** 切换模式失败：%s */
            val `SwitchFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Mode switch failed: %s"""
                            `MLangTags`.ZH -> """切换模式失败：%s"""
                            else -> null
                        }
                    } ?: """切换模式失败：%s"""

            /** 切换模式失败：%s */
            @Composable
            fun `SwitchFailed`(vararg args: Any?) = FYTxtConfig.observe { `SwitchFailed`.fmt(args) }
        }

        object `Action` {
            init {
                `MLangGroups`
            }

            /** 面板 */
            val `Panel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Panel"""
                            `MLangTags`.ZH -> """面板"""
                            else -> null
                        }
                    } ?: """面板"""

            /** 面板 */
            @Composable fun `Panel`(vararg args: Any?) = FYTxtConfig.observe { `Panel`.fmt(args) }

            /** 测试 */
            val `Test`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Test"""
                            `MLangTags`.ZH -> """测试"""
                            else -> null
                        }
                    } ?: """测试"""

            /** 测试 */
            @Composable fun `Test`(vararg args: Any?) = FYTxtConfig.observe { `Test`.fmt(args) }

            /** 资源管理 */
            val `Resources`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Resources"""
                            `MLangTags`.ZH -> """资源管理"""
                            else -> null
                        }
                    } ?: """资源管理"""

            /** 资源管理 */
            @Composable
            fun `Resources`(vararg args: Any?) = FYTxtConfig.observe { `Resources`.fmt(args) }

            /** 控制面板 */
            val `ControlPanel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Control Panel"""
                            `MLangTags`.ZH -> """控制面板"""
                            else -> null
                        }
                    } ?: """控制面板"""

            /** 控制面板 */
            @Composable
            fun `ControlPanel`(vararg args: Any?) = FYTxtConfig.observe { `ControlPanel`.fmt(args) }

            /** 更多 */
            val `More`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """More"""
                            `MLangTags`.ZH -> """更多"""
                            else -> null
                        }
                    } ?: """更多"""

            /** 更多 */
            @Composable fun `More`(vararg args: Any?) = FYTxtConfig.observe { `More`.fmt(args) }

            /** 展开风格 */
            val `GroupStyle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Group Style"""
                            `MLangTags`.ZH -> """展开风格"""
                            else -> null
                        }
                    } ?: """展开风格"""

            /** 展开风格 */
            @Composable
            fun `GroupStyle`(vararg args: Any?) = FYTxtConfig.observe { `GroupStyle`.fmt(args) }

            /** 显示隐藏策略组 */
            val `ShowHiddenGroups`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Show Hidden Proxy Groups"""
                            `MLangTags`.ZH -> """显示隐藏策略组"""
                            else -> null
                        }
                    } ?: """显示隐藏策略组"""

            /** 显示隐藏策略组 */
            @Composable
            fun `ShowHiddenGroups`(vararg args: Any?) =
                FYTxtConfig.observe { `ShowHiddenGroups`.fmt(args) }

            /** 返回 */
            val `Back`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Back"""
                            `MLangTags`.ZH -> """返回"""
                            else -> null
                        }
                    } ?: """返回"""

            /** 返回 */
            @Composable fun `Back`(vararg args: Any?) = FYTxtConfig.observe { `Back`.fmt(args) }

            /** 关闭 */
            val `Close`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Close"""
                            `MLangTags`.ZH -> """关闭"""
                            else -> null
                        }
                    } ?: """关闭"""

            /** 关闭 */
            @Composable fun `Close`(vararg args: Any?) = FYTxtConfig.observe { `Close`.fmt(args) }

            /** 测速 */
            val `TestDelay`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Test Delay"""
                            `MLangTags`.ZH -> """测速"""
                            else -> null
                        }
                    } ?: """测速"""

            /** 测速 */
            @Composable
            fun `TestDelay`(vararg args: Any?) = FYTxtConfig.observe { `TestDelay`.fmt(args) }

            /** 排序方式 */
            val `SortMode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Group Item Order"""
                            `MLangTags`.ZH -> """组内排序"""
                            else -> null
                        }
                    } ?: """组内排序"""

            /** 组内排序 */
            @Composable
            fun `SortMode`(vararg args: Any?) = FYTxtConfig.observe { `SortMode`.fmt(args) }

            /** 调整组内条目顺序 */
            val `SortModeSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Adjust item order inside each group"""
                            `MLangTags`.ZH -> """调整组内条目顺序"""
                            else -> null
                        }
                    } ?: """调整组内条目顺序"""

            /** 调整组内条目顺序 */
            @Composable
            fun `SortModeSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `SortModeSummary`.fmt(args) }

            /** 添加订阅 */
            val `AddProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Profile"""
                            `MLangTags`.ZH -> """添加订阅"""
                            else -> null
                        }
                    } ?: """添加订阅"""

            /** 添加订阅 */
            @Composable
            fun `AddProfile`(vararg args: Any?) = FYTxtConfig.observe { `AddProfile`.fmt(args) }

            /** 添加资源 */
            val `AddProvider`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Provider"""
                            `MLangTags`.ZH -> """添加资源"""
                            else -> null
                        }
                    } ?: """添加资源"""

            /** 添加资源 */
            @Composable
            fun `AddProvider`(vararg args: Any?) = FYTxtConfig.observe { `AddProvider`.fmt(args) }

            /** 添加覆写 */
            val `AddOverride`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Override"""
                            `MLangTags`.ZH -> """添加覆写"""
                            else -> null
                        }
                    } ?: """添加覆写"""

            /** 添加覆写 */
            @Composable
            fun `AddOverride`(vararg args: Any?) = FYTxtConfig.observe { `AddOverride`.fmt(args) }

            /** 添加链式代理 */
            val `AddChain`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Add Chain"""
                            `MLangTags`.ZH -> """添加链式代理"""
                            else -> null
                        }
                    } ?: """添加链式代理"""

            /** 添加链式代理 */
            @Composable
            fun `AddChain`(vararg args: Any?) = FYTxtConfig.observe { `AddChain`.fmt(args) }
        }

        object `Empty` {
            init {
                `MLangGroups`
            }

            /** 暂无节点 */
            val `NoNodes`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No nodes"""
                            `MLangTags`.ZH -> """暂无节点"""
                            else -> null
                        }
                    } ?: """暂无节点"""

            /** 暂无节点 */
            @Composable
            fun `NoNodes`(vararg args: Any?) = FYTxtConfig.observe { `NoNodes`.fmt(args) }

            /** 请在配置页面加载配置文件 */
            val `Hint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Please load a profile in Config page"""
                            `MLangTags`.ZH -> """请在配置页面加载配置文件"""
                            else -> null
                        }
                    } ?: """请在配置页面加载配置文件"""

            /** 请在配置页面加载配置文件 */
            @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }
        }

        object `Testing` {
            init {
                `MLangGroups`
            }

            /** 正在测试节点组：%s */
            val `Group`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Testing node group: %s"""
                            `MLangTags`.ZH -> """正在测试节点组：%s"""
                            else -> null
                        }
                    } ?: """正在测试节点组：%s"""

            /** 正在测试节点组：%s */
            @Composable fun `Group`(vararg args: Any?) = FYTxtConfig.observe { `Group`.fmt(args) }

            /** 正在测试所有节点组... */
            val `All`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Testing all node groups..."""
                            `MLangTags`.ZH -> """正在测试所有节点组..."""
                            else -> null
                        }
                    } ?: """正在测试所有节点组..."""

            /** 正在测试所有节点组... */
            @Composable fun `All`(vararg args: Any?) = FYTxtConfig.observe { `All`.fmt(args) }

            /** 测试请求已发送 */
            val `RequestSent`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Test request sent"""
                            `MLangTags`.ZH -> """测试请求已发送"""
                            else -> null
                        }
                    } ?: """测试请求已发送"""

            /** 测试请求已发送 */
            @Composable
            fun `RequestSent`(vararg args: Any?) = FYTxtConfig.observe { `RequestSent`.fmt(args) }

            /** 测试失败：%s */
            val `Failed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Test failed: %s"""
                            `MLangTags`.ZH -> """测试失败：%s"""
                            else -> null
                        }
                    } ?: """测试失败：%s"""

            /** 测试失败：%s */
            @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

            /** 正在测试节点 */
            val `InProgress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Checking"""
                            `MLangTags`.ZH -> """检测中"""
                            else -> null
                        }
                    } ?: """检测中"""

            /** 检测中 */
            @Composable
            fun `InProgress`(vararg args: Any?) = FYTxtConfig.observe { `InProgress`.fmt(args) }
        }

        object `Selection` {
            init {
                `MLangGroups`
            }

            /** 已切换到：%s */
            val `Switched`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Switched to: %s"""
                            `MLangTags`.ZH -> """已切换到：%s"""
                            else -> null
                        }
                    } ?: """已切换到：%s"""

            /** 已切换到：%s */
            @Composable
            fun `Switched`(vararg args: Any?) = FYTxtConfig.observe { `Switched`.fmt(args) }

            /** 切换失败 */
            val `Failed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Switch failed"""
                            `MLangTags`.ZH -> """切换失败"""
                            else -> null
                        }
                    } ?: """切换失败"""

            /** 切换失败 */
            @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

            /** 切换失败：%s */
            val `Error`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Switch failed: %s"""
                            `MLangTags`.ZH -> """切换失败：%s"""
                            else -> null
                        }
                    } ?: """切换失败：%s"""

            /** 切换失败：%s */
            @Composable fun `Error`(vararg args: Any?) = FYTxtConfig.observe { `Error`.fmt(args) }

            /** 当前 */
            val `Current`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Current"""
                            `MLangTags`.ZH -> """当前"""
                            else -> null
                        }
                    } ?: """当前"""

            /** 当前 */
            @Composable
            fun `Current`(vararg args: Any?) = FYTxtConfig.observe { `Current`.fmt(args) }

            /** 当前节点 */
            val `CurrentNode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Current Node"""
                            `MLangTags`.ZH -> """当前节点"""
                            else -> null
                        }
                    } ?: """当前节点"""

            /** 当前节点 */
            @Composable
            fun `CurrentNode`(vararg args: Any?) = FYTxtConfig.observe { `CurrentNode`.fmt(args) }

            /** %d 个节点 */
            val `NodeCount`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d nodes"""
                            `MLangTags`.ZH -> """%d 个节点"""
                            else -> null
                        }
                    } ?: """%d 个节点"""

            /** %d 个节点 */
            @Composable
            fun `NodeCount`(vararg args: Any?) = FYTxtConfig.observe { `NodeCount`.fmt(args) }

            /** 延迟 */
            val `Latency`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Latency"""
                            `MLangTags`.ZH -> """延迟"""
                            else -> null
                        }
                    } ?: """延迟"""

            /** 延迟 */
            @Composable
            fun `Latency`(vararg args: Any?) = FYTxtConfig.observe { `Latency`.fmt(args) }

            /** 超时 */
            val `Timeout`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Timed out"""
                            `MLangTags`.ZH -> """检测超时"""
                            else -> null
                        }
                    } ?: """检测超时"""

            /** 检测超时 */
            @Composable
            fun `Timeout`(vararg args: Any?) = FYTxtConfig.observe { `Timeout`.fmt(args) }

            /** 待检测 */
            val `UnknownLatency`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Pending"""
                            `MLangTags`.ZH -> """待检测"""
                            else -> null
                        }
                    } ?: """待检测"""

            /** 待检测 */
            @Composable
            fun `UnknownLatency`(vararg args: Any?) =
                FYTxtConfig.observe { `UnknownLatency`.fmt(args) }
        }

        object `SortMode` {
            init {
                `MLangGroups`
            }

            /** 默认顺序 */
            val `Default`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Default"""
                            `MLangTags`.ZH -> """默认顺序"""
                            else -> null
                        }
                    } ?: """默认顺序"""

            /** 默认顺序 */
            @Composable
            fun `Default`(vararg args: Any?) = FYTxtConfig.observe { `Default`.fmt(args) }

            /** 按名称排序 */
            val `ByName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """By Name"""
                            `MLangTags`.ZH -> """按名称排序"""
                            else -> null
                        }
                    } ?: """按名称排序"""

            /** 按名称排序 */
            @Composable fun `ByName`(vararg args: Any?) = FYTxtConfig.observe { `ByName`.fmt(args) }

            /** 按延迟排序 */
            val `ByLatency`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """By Latency"""
                            `MLangTags`.ZH -> """按延迟排序"""
                            else -> null
                        }
                    } ?: """按延迟排序"""

            /** 按延迟排序 */
            @Composable
            fun `ByLatency`(vararg args: Any?) = FYTxtConfig.observe { `ByLatency`.fmt(args) }
        }

        object `DisplayMode` {
            init {
                `MLangGroups`
            }

            /** 单列详细 */
            val `SingleDetailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Single Detailed"""
                            `MLangTags`.ZH -> """单列详细"""
                            else -> null
                        }
                    } ?: """单列详细"""

            /** 单列详细 */
            @Composable
            fun `SingleDetailed`(vararg args: Any?) =
                FYTxtConfig.observe { `SingleDetailed`.fmt(args) }

            /** 单列简洁 */
            val `SingleSimple`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Single Simple"""
                            `MLangTags`.ZH -> """单列简洁"""
                            else -> null
                        }
                    } ?: """单列简洁"""

            /** 单列简洁 */
            @Composable
            fun `SingleSimple`(vararg args: Any?) = FYTxtConfig.observe { `SingleSimple`.fmt(args) }

            /** 双列详细 */
            val `DoubleDetailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Double Detailed"""
                            `MLangTags`.ZH -> """双列详细"""
                            else -> null
                        }
                    } ?: """双列详细"""

            /** 双列详细 */
            @Composable
            fun `DoubleDetailed`(vararg args: Any?) =
                FYTxtConfig.observe { `DoubleDetailed`.fmt(args) }

            /** 双列简洁 */
            val `DoubleSimple`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Double Simple"""
                            `MLangTags`.ZH -> """双列简洁"""
                            else -> null
                        }
                    } ?: """双列简洁"""

            /** 双列简洁 */
            @Composable
            fun `DoubleSimple`(vararg args: Any?) = FYTxtConfig.observe { `DoubleSimple`.fmt(args) }
        }

        object `GroupStyle` {
            init {
                `MLangGroups`
            }

            /** 列表展开 */
            val `Inline`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Inline Expand"""
                            `MLangTags`.ZH -> """列表展开"""
                            else -> null
                        }
                    } ?: """列表展开"""

            /** 列表展开 */
            @Composable fun `Inline`(vararg args: Any?) = FYTxtConfig.observe { `Inline`.fmt(args) }

            /** 浮窗展开 */
            val `Floating`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Floating Popup"""
                            `MLangTags`.ZH -> """浮窗展开"""
                            else -> null
                        }
                    } ?: """浮窗展开"""

            /** 浮窗展开 */
            @Composable
            fun `Floating`(vararg args: Any?) = FYTxtConfig.observe { `Floating`.fmt(args) }
        }
    }

    object `Service` {
        init {
            `MLangGroups`
        }

        object `Notification` {
            init {
                `MLangGroups`
            }

            /** 运行中 */
            val `Running`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Running"""
                            `MLangTags`.ZH -> """运行中"""
                            else -> null
                        }
                    } ?: """运行中"""

            /** 运行中 */
            @Composable
            fun `Running`(vararg args: Any?) = FYTxtConfig.observe { `Running`.fmt(args) }

            /** 总计：%s */
            val `TrafficFormat`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Total: %s"""
                            `MLangTags`.ZH -> """总计：%s"""
                            else -> null
                        }
                    } ?: """总计：%s"""

            /** 总计：%s */
            @Composable
            fun `TrafficFormat`(vararg args: Any?) =
                FYTxtConfig.observe { `TrafficFormat`.fmt(args) }

            /** 未知配置 */
            val `UnknownProfile`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unknown profile"""
                            `MLangTags`.ZH -> """未知配置"""
                            else -> null
                        }
                    } ?: """未知配置"""

            /** 未知配置 */
            @Composable
            fun `UnknownProfile`(vararg args: Any?) =
                FYTxtConfig.observe { `UnknownProfile`.fmt(args) }
        }

        object `Tile` {
            init {
                `MLangGroups`
            }

            /** 点击打开应用 */
            val `ClickToOpen`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Click to open app"""
                            `MLangTags`.ZH -> """点击打开应用"""
                            else -> null
                        }
                    } ?: """点击打开应用"""

            /** 点击打开应用 */
            @Composable
            fun `ClickToOpen`(vararg args: Any?) = FYTxtConfig.observe { `ClickToOpen`.fmt(args) }

            /** 启动代理 */
            val `ClickToStartProxy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Start proxy"""
                            `MLangTags`.ZH -> """启动代理"""
                            else -> null
                        }
                    } ?: """启动代理"""

            /** 启动代理 */
            @Composable
            fun `ClickToStartProxy`(vararg args: Any?) =
                FYTxtConfig.observe { `ClickToStartProxy`.fmt(args) }

            /** 停止代理 */
            val `ClickToStopProxy`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Stop proxy"""
                            `MLangTags`.ZH -> """停止代理"""
                            else -> null
                        }
                    } ?: """停止代理"""

            /** 停止代理 */
            @Composable
            fun `ClickToStopProxy`(vararg args: Any?) =
                FYTxtConfig.observe { `ClickToStopProxy`.fmt(args) }

            /** 正在连接... */
            val `Connecting`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Connecting..."""
                            `MLangTags`.ZH -> """正在连接..."""
                            else -> null
                        }
                    } ?: """正在连接..."""

            /** 正在连接... */
            @Composable
            fun `Connecting`(vararg args: Any?) = FYTxtConfig.observe { `Connecting`.fmt(args) }

            /** 正在断开... */
            val `Disconnecting`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Disconnecting..."""
                            `MLangTags`.ZH -> """正在断开..."""
                            else -> null
                        }
                    } ?: """正在断开..."""

            /** 正在断开... */
            @Composable
            fun `Disconnecting`(vararg args: Any?) =
                FYTxtConfig.observe { `Disconnecting`.fmt(args) }
        }

        object `AutoRestart` {
            init {
                `MLangGroups`
            }

            /** 自动重启服务 */
            val `ChannelName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Auto Restart Service"""
                            `MLangTags`.ZH -> """自动重启服务"""
                            else -> null
                        }
                    } ?: """自动重启服务"""

            /** 自动重启服务 */
            @Composable
            fun `ChannelName`(vararg args: Any?) = FYTxtConfig.observe { `ChannelName`.fmt(args) }

            /** 用于自动重启代理服务 */
            val `ChannelDescription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Used to restart proxy service automatically"""
                            `MLangTags`.ZH -> """用于自动重启代理服务"""
                            else -> null
                        }
                    } ?: """用于自动重启代理服务"""

            /** 用于自动重启代理服务 */
            @Composable
            fun `ChannelDescription`(vararg args: Any?) =
                FYTxtConfig.observe { `ChannelDescription`.fmt(args) }

            /** 正在检查自动启动... */
            val `Checking`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Checking auto-start..."""
                            `MLangTags`.ZH -> """正在检查自动启动..."""
                            else -> null
                        }
                    } ?: """正在检查自动启动..."""

            /** 正在检查自动启动... */
            @Composable
            fun `Checking`(vararg args: Any?) = FYTxtConfig.observe { `Checking`.fmt(args) }
        }

        object `LogRecord` {
            init {
                `MLangGroups`
            }

            /** 日志录制 */
            val `ChannelName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Log Recording"""
                            `MLangTags`.ZH -> """日志录制"""
                            else -> null
                        }
                    } ?: """日志录制"""

            /** 日志录制 */
            @Composable
            fun `ChannelName`(vararg args: Any?) = FYTxtConfig.observe { `ChannelName`.fmt(args) }

            /** 日志录制服务通知 */
            val `ChannelDescription`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Notification for log recording service"""
                            `MLangTags`.ZH -> """日志录制服务通知"""
                            else -> null
                        }
                    } ?: """日志录制服务通知"""

            /** 日志录制服务通知 */
            @Composable
            fun `ChannelDescription`(vararg args: Any?) =
                FYTxtConfig.observe { `ChannelDescription`.fmt(args) }

            /** 正在录制日志 */
            val `NotificationTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Recording logs"""
                            `MLangTags`.ZH -> """正在录制日志"""
                            else -> null
                        }
                    } ?: """正在录制日志"""

            /** 正在录制日志 */
            @Composable
            fun `NotificationTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `NotificationTitle`.fmt(args) }

            /** 正在录制日志... */
            val `NotificationContent`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Recording logs..."""
                            `MLangTags`.ZH -> """正在录制日志..."""
                            else -> null
                        }
                    } ?: """正在录制日志..."""

            /** 正在录制日志... */
            @Composable
            fun `NotificationContent`(vararg args: Any?) =
                FYTxtConfig.observe { `NotificationContent`.fmt(args) }

            /** 停止 */
            val `ActionStop`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Stop"""
                            `MLangTags`.ZH -> """停止"""
                            else -> null
                        }
                    } ?: """停止"""

            /** 停止 */
            @Composable
            fun `ActionStop`(vararg args: Any?) = FYTxtConfig.observe { `ActionStop`.fmt(args) }
        }
    }

    object `Settings` {
        init {
            `MLangGroups`
        }

        /** 设置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Settings"""
                        `MLangTags`.ZH -> """设置"""
                        else -> null
                    }
                } ?: """设置"""

        /** 设置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        object `Section` {
            init {
                `MLangGroups`
            }

            /** 界面设置 */
            val `UiSettings`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """UI Settings"""
                            `MLangTags`.ZH -> """界面设置"""
                            else -> null
                        }
                    } ?: """界面设置"""

            /** 界面设置 */
            @Composable
            fun `UiSettings`(vararg args: Any?) = FYTxtConfig.observe { `UiSettings`.fmt(args) }

            /** 更多 */
            val `More`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """More"""
                            `MLangTags`.ZH -> """更多"""
                            else -> null
                        }
                    } ?: """更多"""

            /** 更多 */
            @Composable fun `More`(vararg args: Any?) = FYTxtConfig.observe { `More`.fmt(args) }

            /** 高级 */
            val `Advanced`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Advanced"""
                            `MLangTags`.ZH -> """高级"""
                            else -> null
                        }
                    } ?: """高级"""

            /** 高级 */
            @Composable
            fun `Advanced`(vararg args: Any?) = FYTxtConfig.observe { `Advanced`.fmt(args) }
        }

        object `UiSettings` {
            init {
                `MLangGroups`
            }

            /** 应用 */
            val `App`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """App"""
                            `MLangTags`.ZH -> """应用"""
                            else -> null
                        }
                    } ?: """应用"""

            /** 应用 */
            @Composable fun `App`(vararg args: Any?) = FYTxtConfig.observe { `App`.fmt(args) }

            /** 外观 · 语言 · 主题 */
            val `AppSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Appearance · Language · Theme"""
                            `MLangTags`.ZH -> """外观 · 语言 · 主题"""
                            else -> null
                        }
                    } ?: """外观 · 语言 · 主题"""

            /** 外观 · 语言 · 主题 */
            @Composable
            fun `AppSummary`(vararg args: Any?) = FYTxtConfig.observe { `AppSummary`.fmt(args) }

            /** 网络 */
            val `Network`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Network"""
                            `MLangTags`.ZH -> """网络"""
                            else -> null
                        }
                    } ?: """网络"""

            /** 网络 */
            @Composable
            fun `Network`(vararg args: Any?) = FYTxtConfig.observe { `Network`.fmt(args) }

            /** DNS · 端口 · 入站 */
            val `NetworkSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """DNS · Port · Inbound"""
                            `MLangTags`.ZH -> """DNS · 端口 · 入站"""
                            else -> null
                        }
                    } ?: """DNS · 端口 · 入站"""

            /** DNS · 端口 · 入站 */
            @Composable
            fun `NetworkSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `NetworkSummary`.fmt(args) }

            /** 覆写 */
            val `Override`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Override"""
                            `MLangTags`.ZH -> """覆写"""
                            else -> null
                        }
                    } ?: """覆写"""

            /** 覆写 */
            @Composable
            fun `Override`(vararg args: Any?) = FYTxtConfig.observe { `Override`.fmt(args) }

            /** 规则覆写 */
            val `OverrideSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Rule Override"""
                            `MLangTags`.ZH -> """规则覆写"""
                            else -> null
                        }
                    } ?: """规则覆写"""

            /** 规则覆写 */
            @Composable
            fun `OverrideSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `OverrideSummary`.fmt(args) }

            /** Meta 功能 */
            val `MetaFeatures`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Meta Features"""
                            `MLangTags`.ZH -> """Meta 功能"""
                            else -> null
                        }
                    } ?: """Meta 功能"""

            /** Meta 功能 */
            @Composable
            fun `MetaFeatures`(vararg args: Any?) = FYTxtConfig.observe { `MetaFeatures`.fmt(args) }

            /** Meta 扩展 */
            val `MetaFeaturesSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Meta Extensions"""
                            `MLangTags`.ZH -> """Meta 扩展"""
                            else -> null
                        }
                    } ?: """Meta 扩展"""

            /** Meta 扩展 */
            @Composable
            fun `MetaFeaturesSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `MetaFeaturesSummary`.fmt(args) }
        }

        object `More` {
            init {
                `MLangGroups`
            }

            /** 实验室 */
            val `Lab`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Lab"""
                            `MLangTags`.ZH -> """实验室"""
                            else -> null
                        }
                    } ?: """实验室"""

            /** 实验室 */
            @Composable fun `Lab`(vararg args: Any?) = FYTxtConfig.observe { `Lab`.fmt(args) }

            /** SubStore · 实验功能 */
            val `LabSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """SubStore · Experiments"""
                            `MLangTags`.ZH -> """SubStore · 实验功能"""
                            else -> null
                        }
                    } ?: """SubStore · 实验功能"""

            /** SubStore · 实验功能 */
            @Composable
            fun `LabSummary`(vararg args: Any?) = FYTxtConfig.observe { `LabSummary`.fmt(args) }

            /** 流量统计 */
            val `TrafficStatistics`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Traffic Statistics"""
                            `MLangTags`.ZH -> """流量统计"""
                            else -> null
                        }
                    } ?: """流量统计"""

            /** 流量统计 */
            @Composable
            fun `TrafficStatistics`(vararg args: Any?) =
                FYTxtConfig.observe { `TrafficStatistics`.fmt(args) }

            /** 流量概览与最近请求 */
            val `TrafficStatisticsSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Traffic overview and recent requests"""
                            `MLangTags`.ZH -> """流量概览与最近请求"""
                            else -> null
                        }
                    } ?: """流量概览与最近请求"""

            /** 流量概览与最近请求 */
            @Composable
            fun `TrafficStatisticsSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `TrafficStatisticsSummary`.fmt(args) }

            /** 日志与诊断 */
            val `Logs`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Logs & Diagnostics"""
                            `MLangTags`.ZH -> """日志与诊断"""
                            else -> null
                        }
                    } ?: """日志与诊断"""

            /** 日志与诊断 */
            @Composable fun `Logs`(vararg args: Any?) = FYTxtConfig.observe { `Logs`.fmt(args) }

            /** 运行日志、启动诊断与故障排查 */
            val `LogsSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Runtime logs, startup diagnostics & troubleshooting"""
                            `MLangTags`.ZH -> """运行日志、启动诊断与故障排查"""
                            else -> null
                        }
                    } ?: """运行日志、启动诊断与故障排查"""

            /** 运行日志、启动诊断与故障排查 */
            @Composable
            fun `LogsSummary`(vararg args: Any?) = FYTxtConfig.observe { `LogsSummary`.fmt(args) }

            /** 关于 */
            val `About`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """About"""
                            `MLangTags`.ZH -> """关于"""
                            else -> null
                        }
                    } ?: """关于"""

            /** 关于 */
            @Composable fun `About`(vararg args: Any?) = FYTxtConfig.observe { `About`.fmt(args) }

            /** 版本与许可 */
            val `AboutSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Version & License"""
                            `MLangTags`.ZH -> """版本与许可"""
                            else -> null
                        }
                    } ?: """版本与许可"""

            /** 版本与许可 */
            @Composable
            fun `AboutSummary`(vararg args: Any?) = FYTxtConfig.observe { `AboutSummary`.fmt(args) }
        }

        object `Error` {
            init {
                `MLangGroups`
            }

            /** 无法打开 WebView：%s */
            val `WebviewFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unable to open WebView: %s"""
                            `MLangTags`.ZH -> """无法打开 WebView：%s"""
                            else -> null
                        }
                    } ?: """无法打开 WebView：%s"""

            /** 无法打开 WebView：%s */
            @Composable
            fun `WebviewFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `WebviewFailed`.fmt(args) }
        }
    }

    object `TrafficStatistics` {
        init {
            `MLangGroups`
        }

        /** 流量统计与最近请求 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Traffic Statistics & Recent Requests"""
                        `MLangTags`.ZH -> """流量统计与最近请求"""
                        else -> null
                    }
                } ?: """流量统计与最近请求"""

        /** 流量统计与最近请求 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 查看流量概览与最近请求 */
        val `EntrySummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """View traffic overview and recent requests"""
                        `MLangTags`.ZH -> """查看流量概览与最近请求"""
                        else -> null
                    }
                } ?: """查看流量概览与最近请求"""

        /** 查看流量概览与最近请求 */
        @Composable
        fun `EntrySummary`(vararg args: Any?) = FYTxtConfig.observe { `EntrySummary`.fmt(args) }

        /** 流量概览 */
        val `OverviewTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as `MLangTags`
                    when (it) {
                        `MLangTags`.EN -> """Traffic Overview"""
                        `MLangTags`.ZH -> """流量概览"""
                        else -> null
                    }
                } ?: """流量概览"""

        /** 流量概览 */
        @Composable
        fun `OverviewTitle`(vararg args: Any?) = FYTxtConfig.observe { `OverviewTitle`.fmt(args) }

        object `Detail` {
            init {
                `MLangGroups`
            }

            /** 请求与站点流量 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Requests & Site Traffic"""
                            `MLangTags`.ZH -> """请求与站点流量"""
                            else -> null
                        }
                    } ?: """请求与站点流量"""

            /** 请求与站点流量 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 在“最近请求”和“站点流量”之间切换查看 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Switch between recent requests and site-level traffic aggregation"""
                            `MLangTags`.ZH -> """在“最近请求”和“站点流量”之间切换查看"""
                            else -> null
                        }
                    } ?: """在“最近请求”和“站点流量”之间切换查看"""

            /** 在“最近请求”和“站点流量”之间切换查看 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }

        object `TargetSites` {
            init {
                `MLangGroups`
            }

            /** 站点流量 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Site Traffic"""
                            `MLangTags`.ZH -> """站点流量"""
                            else -> null
                        }
                    } ?: """站点流量"""

            /** 站点流量 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 按目标域名或目标 IP 聚合上下行流量 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Aggregate upload and download traffic by destination host or IP"""
                            `MLangTags`.ZH -> """按目标域名或目标 IP 聚合上下行流量"""
                            else -> null
                        }
                    } ?: """按目标域名或目标 IP 聚合上下行流量"""

            /** 按目标域名或目标 IP 聚合上下行流量 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }

            /** 暂无目标网站流量记录 */
            val `Empty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No destination traffic yet"""
                            `MLangTags`.ZH -> """暂无目标网站流量记录"""
                            else -> null
                        }
                    } ?: """暂无目标网站流量记录"""

            /** 暂无目标网站流量记录 */
            @Composable fun `Empty`(vararg args: Any?) = FYTxtConfig.observe { `Empty`.fmt(args) }

            /** %d 项 */
            val `Count`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d"""
                            `MLangTags`.ZH -> """%d 项"""
                            else -> null
                        }
                    } ?: """%d 项"""

            /** %d 项 */
            @Composable fun `Count`(vararg args: Any?) = FYTxtConfig.observe { `Count`.fmt(args) }

            /** 上行 %s */
            val `Upload`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Up %s"""
                            `MLangTags`.ZH -> """上行 %s"""
                            else -> null
                        }
                    } ?: """上行 %s"""

            /** 上行 %s */
            @Composable fun `Upload`(vararg args: Any?) = FYTxtConfig.observe { `Upload`.fmt(args) }

            /** 下行 %s */
            val `Download`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Down %s"""
                            `MLangTags`.ZH -> """下行 %s"""
                            else -> null
                        }
                    } ?: """下行 %s"""

            /** 下行 %s */
            @Composable
            fun `Download`(vararg args: Any?) = FYTxtConfig.observe { `Download`.fmt(args) }
        }

        object `RecentRequests` {
            init {
                `MLangGroups`
            }

            /** 最近请求 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Recent Requests"""
                            `MLangTags`.ZH -> """最近请求"""
                            else -> null
                        }
                    } ?: """最近请求"""

            /** 最近请求 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 按时间顺序查看最近的网络访问与命中链路 */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN ->
                                """Browse recent network requests and matched proxy chains in chronological order"""
                            `MLangTags`.ZH -> """按时间顺序查看最近的网络访问与命中链路"""
                            else -> null
                        }
                    } ?: """按时间顺序查看最近的网络访问与命中链路"""

            /** 按时间顺序查看最近的网络访问与命中链路 */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }

            /** 暂无最近请求记录 */
            val `Empty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """No recent requests"""
                            `MLangTags`.ZH -> """暂无最近请求记录"""
                            else -> null
                        }
                    } ?: """暂无最近请求记录"""

            /** 暂无最近请求记录 */
            @Composable fun `Empty`(vararg args: Any?) = FYTxtConfig.observe { `Empty`.fmt(args) }

            /** 未知请求 */
            val `UnknownRequest`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unknown Request"""
                            `MLangTags`.ZH -> """未知请求"""
                            else -> null
                        }
                    } ?: """未知请求"""

            /** 未知请求 */
            @Composable
            fun `UnknownRequest`(vararg args: Any?) =
                FYTxtConfig.observe { `UnknownRequest`.fmt(args) }

            /** %d 条 */
            val `Count`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d"""
                            `MLangTags`.ZH -> """%d 条"""
                            else -> null
                        }
                    } ?: """%d 条"""

            /** %d 条 */
            @Composable fun `Count`(vararg args: Any?) = FYTxtConfig.observe { `Count`.fmt(args) }
        }

        object `Status` {
            init {
                `MLangGroups`
            }

            /** 进行中 */
            val `Active`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Active"""
                            `MLangTags`.ZH -> """进行中"""
                            else -> null
                        }
                    } ?: """进行中"""

            /** 进行中 */
            @Composable fun `Active`(vararg args: Any?) = FYTxtConfig.observe { `Active`.fmt(args) }

            /** 已结束 */
            val `Closed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Closed"""
                            `MLangTags`.ZH -> """已结束"""
                            else -> null
                        }
                    } ?: """已结束"""

            /** 已结束 */
            @Composable fun `Closed`(vararg args: Any?) = FYTxtConfig.observe { `Closed`.fmt(args) }
        }

        object `RelativeTime` {
            init {
                `MLangGroups`
            }

            /** 刚刚 */
            val `JustNow`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Just now"""
                            `MLangTags`.ZH -> """刚刚"""
                            else -> null
                        }
                    } ?: """刚刚"""

            /** 刚刚 */
            @Composable
            fun `JustNow`(vararg args: Any?) = FYTxtConfig.observe { `JustNow`.fmt(args) }

            /** %d分钟前 */
            val `MinutesAgo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d min ago"""
                            `MLangTags`.ZH -> """%d分钟前"""
                            else -> null
                        }
                    } ?: """%d分钟前"""

            /** %d分钟前 */
            @Composable
            fun `MinutesAgo`(vararg args: Any?) = FYTxtConfig.observe { `MinutesAgo`.fmt(args) }

            /** %d小时前 */
            val `HoursAgo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """%d hr ago"""
                            `MLangTags`.ZH -> """%d小时前"""
                            else -> null
                        }
                    } ?: """%d小时前"""

            /** %d小时前 */
            @Composable
            fun `HoursAgo`(vararg args: Any?) = FYTxtConfig.observe { `HoursAgo`.fmt(args) }
        }

        object `TimeRange` {
            init {
                `MLangGroups`
            }

            /** 今日 */
            val `Today`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Today"""
                            `MLangTags`.ZH -> """今日"""
                            else -> null
                        }
                    } ?: """今日"""

            /** 今日 */
            @Composable fun `Today`(vararg args: Any?) = FYTxtConfig.observe { `Today`.fmt(args) }

            /** 本周 */
            val `Week`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """This Week"""
                            `MLangTags`.ZH -> """本周"""
                            else -> null
                        }
                    } ?: """本周"""

            /** 本周 */
            @Composable fun `Week`(vararg args: Any?) = FYTxtConfig.observe { `Week`.fmt(args) }
        }

        object `Summary` {
            init {
                `MLangGroups`
            }

            /** 今日流量 */
            val `TodayTraffic`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Today's Traffic"""
                            `MLangTags`.ZH -> """今日流量"""
                            else -> null
                        }
                    } ?: """今日流量"""

            /** 今日流量 */
            @Composable
            fun `TodayTraffic`(vararg args: Any?) = FYTxtConfig.observe { `TodayTraffic`.fmt(args) }

            /** 本周流量 */
            val `WeekTraffic`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """This Week's Traffic"""
                            `MLangTags`.ZH -> """本周流量"""
                            else -> null
                        }
                    } ?: """本周流量"""

            /** 本周流量 */
            @Composable
            fun `WeekTraffic`(vararg args: Any?) = FYTxtConfig.observe { `WeekTraffic`.fmt(args) }
        }

        object `Compare` {
            init {
                `MLangGroups`
            }

            /** 较昨日 +%s */
            val `MoreThanYesterday`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """vs yesterday +%s"""
                            `MLangTags`.ZH -> """较昨日 +%s"""
                            else -> null
                        }
                    } ?: """较昨日 +%s"""

            /** 较昨日 +%s */
            @Composable
            fun `MoreThanYesterday`(vararg args: Any?) =
                FYTxtConfig.observe { `MoreThanYesterday`.fmt(args) }

            /** 较昨日 %s */
            val `LessThanYesterday`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """vs yesterday %s"""
                            `MLangTags`.ZH -> """较昨日 %s"""
                            else -> null
                        }
                    } ?: """较昨日 %s"""

            /** 较昨日 %s */
            @Composable
            fun `LessThanYesterday`(vararg args: Any?) =
                FYTxtConfig.observe { `LessThanYesterday`.fmt(args) }

            /** 与昨日持平 */
            val `SameAsYesterday`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Same as yesterday"""
                            `MLangTags`.ZH -> """与昨日持平"""
                            else -> null
                        }
                    } ?: """与昨日持平"""

            /** 与昨日持平 */
            @Composable
            fun `SameAsYesterday`(vararg args: Any?) =
                FYTxtConfig.observe { `SameAsYesterday`.fmt(args) }

            /** 近 7 天统计 */
            val `WeekStats`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Last 7 days stats"""
                            `MLangTags`.ZH -> """近 7 天统计"""
                            else -> null
                        }
                    } ?: """近 7 天统计"""

            /** 近 7 天统计 */
            @Composable
            fun `WeekStats`(vararg args: Any?) = FYTxtConfig.observe { `WeekStats`.fmt(args) }
        }

        object `Chart` {
            init {
                `MLangGroups`
            }

            /** 4 小时 */
            val `Hourly`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """4 Hours"""
                            `MLangTags`.ZH -> """4 小时"""
                            else -> null
                        }
                    } ?: """4 小时"""

            /** 4 小时 */
            @Composable fun `Hourly`(vararg args: Any?) = FYTxtConfig.observe { `Hourly`.fmt(args) }

            /** 按天 */
            val `Daily`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Daily"""
                            `MLangTags`.ZH -> """按天"""
                            else -> null
                        }
                    } ?: """按天"""

            /** 按天 */
            @Composable fun `Daily`(vararg args: Any?) = FYTxtConfig.observe { `Daily`.fmt(args) }
        }
    }

    object `Util` {
        init {
            `MLangGroups`
        }

        object `Error` {
            init {
                `MLangGroups`
            }

            /** 未知错误 */
            val `UnknownError`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as `MLangTags`
                        when (it) {
                            `MLangTags`.EN -> """Unknown error"""
                            `MLangTags`.ZH -> """未知错误"""
                            else -> null
                        }
                    } ?: """未知错误"""

            /** 未知错误 */
            @Composable
            fun `UnknownError`(vararg args: Any?) = FYTxtConfig.observe { `UnknownError`.fmt(args) }
        }
    }
}
