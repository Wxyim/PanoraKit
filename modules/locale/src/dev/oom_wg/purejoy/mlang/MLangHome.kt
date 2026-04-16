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

object MLangHome {
    init {
        RootMLangGroups
    }

    /** MonadBox */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """MonadBox"""
                    RootMLangTags.ZH -> """MonadBox"""
                    else -> null
                }
            } ?: """MonadBox"""

    /** MonadBox */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Message` {
        init {
            RootMLangGroups
        }

        /** 配置已切换 */
        val `ConfigSwitched`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Config switched"""
                        RootMLangTags.ZH -> """配置已切换"""
                        else -> null
                    }
                } ?: """配置已切换"""

        /** 配置已切换 */
        @Composable
        fun `ConfigSwitched`(vararg args: Any?) = FYTxtConfig.observe { `ConfigSwitched`.fmt(args) }

        /** 配置切换失败：%s */
        val `ConfigSwitchFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Config switch failed: %s"""
                        RootMLangTags.ZH -> """配置切换失败：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Preparing..."""
                        RootMLangTags.ZH -> """正在准备..."""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Start failed: %s"""
                        RootMLangTags.ZH -> """启动失败：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Local Profile Startup Failed"""
                        RootMLangTags.ZH -> """本地配置启动失败"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Configuration syntax validation failed: %s"""
                        RootMLangTags.ZH -> """配置语法校验失败：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Remote resource fetch failed: %s"""
                        RootMLangTags.ZH -> """远程资源获取失败：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Network connection failed: %s"""
                        RootMLangTags.ZH -> """网络连接失败：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Permission or access denied: %s"""
                        RootMLangTags.ZH -> """权限或访问被拒绝：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile state error: %s"""
                        RootMLangTags.ZH -> """配置状态异常：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Runtime service unavailable: %s"""
                        RootMLangTags.ZH -> """运行时服务异常：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Runtime control failed: %s"""
                        RootMLangTags.ZH -> """运行时控制失败：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Environment or resource limitation: %s"""
                        RootMLangTags.ZH -> """环境或资源限制导致启动失败：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Startup failed: %s"""
                        RootMLangTags.ZH -> """启动失败原因：%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Stop failed: %s"""
                        RootMLangTags.ZH -> """停止失败：%s"""
                        else -> null
                    }
                } ?: """停止失败：%s"""

        /** 停止失败：%s */
        @Composable
        fun `StopFailed`(vararg args: Any?) = FYTxtConfig.observe { `StopFailed`.fmt(args) }
    }

    object `Control` {
        init {
            RootMLangGroups
        }

        /** 请先添加配置文件 */
        val `HintAddProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Please add a profile first"""
                        RootMLangTags.ZH -> """请先添加配置文件"""
                        else -> null
                    }
                } ?: """请先添加配置文件"""

        /** 请先添加配置文件 */
        @Composable
        fun `HintAddProfile`(vararg args: Any?) = FYTxtConfig.observe { `HintAddProfile`.fmt(args) }

        /** 请先在「配置」页面启用一个配置 */
        val `HintEnableProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Please enable a profile in Config page first"""
                        RootMLangTags.ZH -> """请先在「配置」页面启用一个配置"""
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
            RootMLangGroups
        }

        /** 未选择配置 */
        val `NoProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No Profile"""
                        RootMLangTags.ZH -> """未选择配置"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Direct"""
                        RootMLangTags.ZH -> """直连"""
                        else -> null
                    }
                } ?: """直连"""

        /** 直连 */
        @Composable fun `Direct`(vararg args: Any?) = FYTxtConfig.observe { `Direct`.fmt(args) }

        /** 代理 */
        val `Proxy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy"""
                        RootMLangTags.ZH -> """代理"""
                        else -> null
                    }
                } ?: """代理"""

        /** 代理 */
        @Composable fun `Proxy`(vararg args: Any?) = FYTxtConfig.observe { `Proxy`.fmt(args) }

        /** 拦截 */
        val `Reject`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Reject"""
                        RootMLangTags.ZH -> """拦截"""
                        else -> null
                    }
                } ?: """拦截"""

        /** 拦截 */
        @Composable fun `Reject`(vararg args: Any?) = FYTxtConfig.observe { `Reject`.fmt(args) }

        /** 全局代理 */
        val `Global`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Global"""
                        RootMLangTags.ZH -> """全局代理"""
                        else -> null
                    }
                } ?: """全局代理"""

        /** 全局代理 */
        @Composable fun `Global`(vararg args: Any?) = FYTxtConfig.observe { `Global`.fmt(args) }

        /** 规则分流 */
        val `Rule`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule"""
                        RootMLangTags.ZH -> """规则分流"""
                        else -> null
                    }
                } ?: """规则分流"""

        /** 规则分流 */
        @Composable fun `Rule`(vararg args: Any?) = FYTxtConfig.observe { `Rule`.fmt(args) }
    }

    object `NodeInfo` {
        init {
            RootMLangGroups
        }

        /** 节点 */
        val `Node`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Node"""
                        RootMLangTags.ZH -> """节点"""
                        else -> null
                    }
                } ?: """节点"""

        /** 节点 */
        @Composable fun `Node`(vararg args: Any?) = FYTxtConfig.observe { `Node`.fmt(args) }

        /** 延迟 */
        val `Delay`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delay"""
                        RootMLangTags.ZH -> """延迟"""
                        else -> null
                    }
                } ?: """延迟"""

        /** 延迟 */
        @Composable fun `Delay`(vararg args: Any?) = FYTxtConfig.observe { `Delay`.fmt(args) }

        /** -- */
        val `Unknown`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """--"""
                        RootMLangTags.ZH -> """--"""
                        else -> null
                    }
                } ?: """--"""

        /** -- */
        @Composable fun `Unknown`(vararg args: Any?) = FYTxtConfig.observe { `Unknown`.fmt(args) }
    }

    object `IpInfo` {
        init {
            RootMLangGroups
        }

        /** 出口 IP */
        val `ExitIp`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Exit IP"""
                        RootMLangTags.ZH -> """出口 IP"""
                        else -> null
                    }
                } ?: """出口 IP"""

        /** 出口 IP */
        @Composable fun `ExitIp`(vararg args: Any?) = FYTxtConfig.observe { `ExitIp`.fmt(args) }
    }

    object `Status` {
        init {
            RootMLangGroups
        }

        /** 启动中 */
        val `Starting`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Starting"""
                        RootMLangTags.ZH -> """启动中"""
                        else -> null
                    }
                } ?: """启动中"""

        /** 启动中 */
        @Composable fun `Starting`(vararg args: Any?) = FYTxtConfig.observe { `Starting`.fmt(args) }

        /** 运行中 */
        val `Running`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Running"""
                        RootMLangTags.ZH -> """运行中"""
                        else -> null
                    }
                } ?: """运行中"""

        /** 运行中 */
        @Composable fun `Running`(vararg args: Any?) = FYTxtConfig.observe { `Running`.fmt(args) }

        /** 停止中 */
        val `Stopping`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Stopping"""
                        RootMLangTags.ZH -> """停止中"""
                        else -> null
                    }
                } ?: """停止中"""

        /** 停止中 */
        @Composable fun `Stopping`(vararg args: Any?) = FYTxtConfig.observe { `Stopping`.fmt(args) }

        /** 轻触启动 */
        val `TapToStart`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Tap to start"""
                        RootMLangTags.ZH -> """轻触启动"""
                        else -> null
                    }
                } ?: """轻触启动"""

        /** 轻触启动 */
        @Composable
        fun `TapToStart`(vararg args: Any?) = FYTxtConfig.observe { `TapToStart`.fmt(args) }
    }
}
