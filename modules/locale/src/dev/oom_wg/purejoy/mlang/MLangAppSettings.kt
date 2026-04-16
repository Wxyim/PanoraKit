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
import dev.oom_wg.purejoy.fyl.fytxt.compose.observe
import dev.oom_wg.purejoy.fyl.fytxt.strfmt.fmt
import dev.oom_wg.purejoy.mlang.MLang.`MLangGroups` as RootMLangGroups
import dev.oom_wg.purejoy.mlang.MLang.`MLangTags` as RootMLangTags

object MLangAppSettings {
    init {
        RootMLangGroups
    }

    /** 应用设置 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """App Settings"""
                    RootMLangTags.ZH -> """应用设置"""
                    else -> null
                }
            } ?: """应用设置"""

    /** 应用设置 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Section` {
        init {
            RootMLangGroups
        }

        /** 行为 */
        val `Behavior`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Behavior"""
                        RootMLangTags.ZH -> """行为"""
                        else -> null
                    }
                } ?: """行为"""

        /** 行为 */
        @Composable fun `Behavior`(vararg args: Any?) = FYTxtConfig.observe { `Behavior`.fmt(args) }

        /** 界面 */
        val `Interface`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Interface"""
                        RootMLangTags.ZH -> """界面"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Service"""
                        RootMLangTags.ZH -> """服务"""
                        else -> null
                    }
                } ?: """服务"""

        /** 服务 */
        @Composable fun `Service`(vararg args: Any?) = FYTxtConfig.observe { `Service`.fmt(args) }

        /** 网络 */
        val `Network`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Network"""
                        RootMLangTags.ZH -> """网络"""
                        else -> null
                    }
                } ?: """网络"""

        /** 网络 */
        @Composable fun `Network`(vararg args: Any?) = FYTxtConfig.observe { `Network`.fmt(args) }

        /** 清理 */
        val `Cleanup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cleanup"""
                        RootMLangTags.ZH -> """清理"""
                        else -> null
                    }
                } ?: """清理"""

        /** 清理 */
        @Composable fun `Cleanup`(vararg args: Any?) = FYTxtConfig.observe { `Cleanup`.fmt(args) }
    }

    object `Behavior` {
        init {
            RootMLangGroups
        }

        /** 自动启动 */
        val `AutoStartTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Start"""
                        RootMLangTags.ZH -> """自动启动"""
                        else -> null
                    }
                } ?: """自动启动"""

        /** 自动启动 */
        @Composable
        fun `AutoStartTitle`(vararg args: Any?) = FYTxtConfig.observe { `AutoStartTitle`.fmt(args) }

        /** 应用启动和开机时自动启动代理服务 */
        val `AutoStartSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Automatically start proxy service on app launch and boot"""
                        RootMLangTags.ZH -> """应用启动和开机时自动启动代理服务"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update on Start"""
                        RootMLangTags.ZH -> """启动更新配置"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Auto-update the active subscription profile on start"""
                        RootMLangTags.ZH -> """启动时自动更新当前订阅配置"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """One-China Policy"""
                        RootMLangTags.ZH -> """坚持一个中国原则"""
                        else -> null
                    }
                } ?: """坚持一个中国原则"""

        /** 坚持一个中国原则 */
        @Composable
        fun `OneChinaTitle`(vararg args: Any?) = FYTxtConfig.observe { `OneChinaTitle`.fmt(args) }

        /** 自动将台湾地区旗帜及区域码显示为中国 */
        val `OneChinaSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Automatically display TW region flags and codes as China"""
                        RootMLangTags.ZH -> """自动将台湾地区旗帜及区域码显示为中国"""
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
            RootMLangGroups
        }

        /** 界面语言 */
        val `LanguageTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """App Language"""
                        RootMLangTags.ZH -> """界面语言"""
                        else -> null
                    }
                } ?: """界面语言"""

        /** 界面语言 */
        @Composable
        fun `LanguageTitle`(vararg args: Any?) = FYTxtConfig.observe { `LanguageTitle`.fmt(args) }

        /** 选择应用界面的显示语言 */
        val `LanguageSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Choose the display language for the app UI"""
                        RootMLangTags.ZH -> """选择应用界面的显示语言"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Follow System"""
                        RootMLangTags.ZH -> """跟随系统"""
                        else -> null
                    }
                } ?: """跟随系统"""

        /** 跟随系统 */
        @Composable
        fun `LanguageSystem`(vararg args: Any?) = FYTxtConfig.observe { `LanguageSystem`.fmt(args) }

        /** 简体中文 */
        val `LanguageChinese`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """简体中文"""
                        RootMLangTags.ZH -> """简体中文"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """English"""
                        RootMLangTags.ZH -> """English"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Theme Mode"""
                        RootMLangTags.ZH -> """主题模式"""
                        else -> null
                    }
                } ?: """主题模式"""

        /** 主题模式 */
        @Composable
        fun `ThemeModeTitle`(vararg args: Any?) = FYTxtConfig.observe { `ThemeModeTitle`.fmt(args) }

        /** 选择应用的主题样式 */
        val `ThemeModeSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Select the app's theme style"""
                        RootMLangTags.ZH -> """选择应用的主题样式"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Follow System"""
                        RootMLangTags.ZH -> """跟随系统"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Light"""
                        RootMLangTags.ZH -> """浅色"""
                        else -> null
                    }
                } ?: """浅色"""

        /** 浅色 */
        @Composable
        fun `ThemeModeLight`(vararg args: Any?) = FYTxtConfig.observe { `ThemeModeLight`.fmt(args) }

        /** 深色 */
        val `ThemeModeDark`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Dark"""
                        RootMLangTags.ZH -> """深色"""
                        else -> null
                    }
                } ?: """深色"""

        /** 深色 */
        @Composable
        fun `ThemeModeDark`(vararg args: Any?) = FYTxtConfig.observe { `ThemeModeDark`.fmt(args) }

        /** 主题配色 */
        val `ColorThemeTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Theme Palette"""
                        RootMLangTags.ZH -> """主题配色"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Pick a seed color and auto-derive monochrome theme colors"""
                        RootMLangTags.ZH -> """点击选择主题色，并自动推导黑白主题配色"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Pick Theme Color"""
                        RootMLangTags.ZH -> """选择主题色"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Theme Color Code (#RRGGBB)"""
                        RootMLangTags.ZH -> """主题色代码（#RRGGBB）"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Current theme color: %s"""
                        RootMLangTags.ZH -> """当前主题色：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Blur Top Bar"""
                        RootMLangTags.ZH -> """顶部栏背景模糊"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Apply a dynamic blur background to the top bar while scrolling"""
                        RootMLangTags.ZH -> """滚动内容时，为顶部栏应用动态模糊背景"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Liquid Glass Bottom Bar"""
                        RootMLangTags.ZH -> """底栏液体玻璃"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Enable sampled liquid glass refraction for the bottom navigation bar"""
                        RootMLangTags.ZH -> """为底部导航栏启用液体玻璃采样与折射效果"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto-hide Navbar"""
                        RootMLangTags.ZH -> """滑动隐藏底栏"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Hide navbar on scroll down and show on scroll up"""
                        RootMLangTags.ZH -> """向下滑动时自动隐藏底栏，向上滑动时显示"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Page Scale"""
                        RootMLangTags.ZH -> """页面缩放"""
                        else -> null
                    }
                } ?: """页面缩放"""

        /** 页面缩放 */
        @Composable
        fun `PageScaleTitle`(vararg args: Any?) = FYTxtConfig.observe { `PageScaleTitle`.fmt(args) }

        /** 调整应用界面的整体缩放比例 */
        val `PageScaleSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Adjust the overall UI scaling ratio of the app"""
                        RootMLangTags.ZH -> """调整应用界面的整体缩放比例"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """80% - 120%"""
                        RootMLangTags.ZH -> """80% - 120%"""
                        else -> null
                    }
                } ?: """80% - 120%"""

        /** 80% - 120% */
        @Composable
        fun `PageScaleRange`(vararg args: Any?) = FYTxtConfig.observe { `PageScaleRange`.fmt(args) }

        /** 隐藏应用图标 */
        val `HideIconTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Hide App Icon"""
                        RootMLangTags.ZH -> """隐藏应用图标"""
                        else -> null
                    }
                } ?: """隐藏应用图标"""

        /** 隐藏应用图标 */
        @Composable
        fun `HideIconTitle`(vararg args: Any?) = FYTxtConfig.observe { `HideIconTitle`.fmt(args) }

        /** 隐藏后可通过拨号盘 *#*#0721#*#* 打开 */
        val `HideIconSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """After hiding, you can open via dialer *#*#0721#*#*"""
                        RootMLangTags.ZH -> """隐藏后可通过拨号盘 *#*#0721#*#* 打开"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Hide Recents Card"""
                        RootMLangTags.ZH -> """隐藏后台卡片"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Do not show this app in the recent tasks list (recents card)"""
                        RootMLangTags.ZH -> """启用后不在最近任务列表（后台卡片）中显示应用"""
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
            RootMLangGroups
        }

        /** 显示流量通知 */
        val `TrafficNotificationTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Show Traffic Notification"""
                        RootMLangTags.ZH -> """显示流量通知"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Display traffic usage in notification bar"""
                        RootMLangTags.ZH -> """在通知栏中显示流量使用情况"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Single Node Test"""
                        RootMLangTags.ZH -> """单节点测试"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Tap icon on node card to test individual node delay"""
                        RootMLangTags.ZH -> """点击节点卡片右侧图标测试单个节点延迟"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto-record on Log page"""
                        RootMLangTags.ZH -> """进入日志页自动录制"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Automatically start log recording when entering the Log page"""
                        RootMLangTags.ZH -> """进入日志页时自动开启日志录制"""
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
            RootMLangGroups
        }

        /** 自定义 User-Agent */
        val `CustomUserAgentTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Custom User-Agent"""
                        RootMLangTags.ZH -> """自定义 User-Agent"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Not set, using default"""
                        RootMLangTags.ZH -> """未设置，使用默认值"""
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
            RootMLangGroups
        }

        /** 定时自动清理 */
        val `AutoEnabledTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Timed Auto Cleanup"""
                        RootMLangTags.ZH -> """定时自动清理"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Periodically check storage usage and clean when threshold is exceeded"""
                        RootMLangTags.ZH -> """周期检查存储占用，超过阈值时自动清理"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cleanup Policy"""
                        RootMLangTags.ZH -> """清理策略"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Choose cleanup intensity and cache retention behavior"""
                        RootMLangTags.ZH -> """选择清理强度与缓存保留行为"""
                        else -> null
                    }
                } ?: """选择清理强度与缓存保留行为"""

        /** 选择清理强度与缓存保留行为 */
        @Composable
        fun `PolicySummary`(vararg args: Any?) = FYTxtConfig.observe { `PolicySummary`.fmt(args) }

        /** 激进 */
        val `PolicyAggressive`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Aggressive"""
                        RootMLangTags.ZH -> """激进"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Balanced"""
                        RootMLangTags.ZH -> """平衡"""
                        else -> null
                    }
                } ?: """平衡"""

        /** 平衡 */
        @Composable
        fun `PolicyBalanced`(vararg args: Any?) = FYTxtConfig.observe { `PolicyBalanced`.fmt(args) }

        /** 保守 */
        val `PolicyConservative`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Conservative"""
                        RootMLangTags.ZH -> """保守"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cleanup Threshold"""
                        RootMLangTags.ZH -> """清理阈值"""
                        else -> null
                    }
                } ?: """清理阈值"""

        /** 清理阈值 */
        @Composable
        fun `ThresholdTitle`(vararg args: Any?) = FYTxtConfig.observe { `ThresholdTitle`.fmt(args) }

        /** 应用存储超过 %s MB 时触发清理 */
        val `ThresholdSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Trigger cleanup when app storage exceeds %s MB"""
                        RootMLangTags.ZH -> """应用存储超过 %s MB 时触发清理"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allowed range: 64 - 4096 MB"""
                        RootMLangTags.ZH -> """可设置范围：64 - 4096 MB"""
                        else -> null
                    }
                } ?: """可设置范围：64 - 4096 MB"""

        /** 可设置范围：64 - 4096 MB */
        @Composable
        fun `ThresholdRange`(vararg args: Any?) = FYTxtConfig.observe { `ThresholdRange`.fmt(args) }

        /** 自动清理间隔 */
        val `IntervalTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Cleanup Interval"""
                        RootMLangTags.ZH -> """自动清理间隔"""
                        else -> null
                    }
                } ?: """自动清理间隔"""

        /** 自动清理间隔 */
        @Composable
        fun `IntervalTitle`(vararg args: Any?) = FYTxtConfig.observe { `IntervalTitle`.fmt(args) }

        /** 每 %s 小时检查一次 */
        val `IntervalSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Check every %s hours"""
                        RootMLangTags.ZH -> """每 %s 小时检查一次"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allowed range: 1 - 48 hours"""
                        RootMLangTags.ZH -> """可设置范围：1 - 48 小时"""
                        else -> null
                    }
                } ?: """可设置范围：1 - 48 小时"""

        /** 可设置范围：1 - 48 小时 */
        @Composable
        fun `IntervalRange`(vararg args: Any?) = FYTxtConfig.observe { `IntervalRange`.fmt(args) }

        /** 小时 */
        val `IntervalUnit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """h"""
                        RootMLangTags.ZH -> """小时"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Clean Up Now"""
                        RootMLangTags.ZH -> """立即清理"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Last run: %s"""
                        RootMLangTags.ZH -> """上次清理：%s"""
                        else -> null
                    }
                } ?: """上次清理：%s"""

        /** 上次清理：%s */
        @Composable
        fun `LastRunSummary`(vararg args: Any?) = FYTxtConfig.observe { `LastRunSummary`.fmt(args) }

        /** 尚未执行过清理 */
        val `LastRunNever`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Never cleaned yet"""
                        RootMLangTags.ZH -> """尚未执行过清理"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """no-archive"""
                        RootMLangTags.ZH -> """无归档"""
                        else -> null
                    }
                } ?: """无归档"""

        /** 无归档 */
        @Composable
        fun `ArchiveSkipped`(vararg args: Any?) = FYTxtConfig.observe { `ArchiveSkipped`.fmt(args) }

        /** 清理完成，释放 %s MB，归档：%s */
        val `CleanupNowSuccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cleanup completed, freed %s MB, archive: %s"""
                        RootMLangTags.ZH -> """清理完成，释放 %s MB，归档：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cleanup skipped"""
                        RootMLangTags.ZH -> """本次未触发清理"""
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
            RootMLangGroups
        }

        /** 警告 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Warning"""
                        RootMLangTags.ZH -> """警告"""
                        else -> null
                    }
                } ?: """警告"""

        /** 警告 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 请在隐藏之前确认你能够访问本应用的设置界面！ */
        val `HideIconMsg1`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Please make sure you can access this app's settings before hiding!"""
                        RootMLangTags.ZH -> """请在隐藏之前确认你能够访问本应用的设置界面！"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """For HyperOS, please enable Auto-start and Background Pop-up permissions to receive dialer codes!"""
                        RootMLangTags.ZH -> """对于 HyperOS, 请开启 自启动 和 后台弹出界面 权限，以接受拨号界面代码！"""
                        else -> null
                    }
                } ?: """对于 HyperOS, 请开启 自启动 和 后台弹出界面 权限，以接受拨号界面代码！"""

        /** 对于 HyperOS, 请开启 自启动 和 后台弹出界面 权限，以接受拨号界面代码！ */
        @Composable
        fun `HideIconMsg2`(vararg args: Any?) = FYTxtConfig.observe { `HideIconMsg2`.fmt(args) }
    }

    object `EditDialog` {
        init {
            RootMLangGroups
        }

        /** 编辑 User-Agent */
        val `UserAgentTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit User-Agent"""
                        RootMLangTags.ZH -> """编辑 User-Agent"""
                        else -> null
                    }
                } ?: """编辑 User-Agent"""

        /** 编辑 User-Agent */
        @Composable
        fun `UserAgentTitle`(vararg args: Any?) = FYTxtConfig.observe { `UserAgentTitle`.fmt(args) }
    }

    object `Button` {
        init {
            RootMLangGroups
        }

        /** 取消 */
        val `Cancel`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cancel"""
                        RootMLangTags.ZH -> """取消"""
                        else -> null
                    }
                } ?: """取消"""

        /** 取消 */
        @Composable fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

        /** 应用 */
        val `Apply`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Apply"""
                        RootMLangTags.ZH -> """应用"""
                        else -> null
                    }
                } ?: """应用"""

        /** 应用 */
        @Composable fun `Apply`(vararg args: Any?) = FYTxtConfig.observe { `Apply`.fmt(args) }
    }
}
