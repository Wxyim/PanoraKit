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

object MLangProxy {
    init {
        RootMLangGroups
    }

    /** 节点 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Nodes"""
                    RootMLangTags.ZH -> """节点"""
                    else -> null
                }
            } ?: """节点"""

    /** 节点 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Mode` {
        init {
            RootMLangGroups
        }

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

        /** 全局 */
        val `Global`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Global"""
                        RootMLangTags.ZH -> """全局"""
                        else -> null
                    }
                } ?: """全局"""

        /** 全局 */
        @Composable fun `Global`(vararg args: Any?) = FYTxtConfig.observe { `Global`.fmt(args) }

        /** 规则 */
        val `Rule`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule"""
                        RootMLangTags.ZH -> """规则"""
                        else -> null
                    }
                } ?: """规则"""

        /** 规则 */
        @Composable fun `Rule`(vararg args: Any?) = FYTxtConfig.observe { `Rule`.fmt(args) }

        /** 未知 */
        val `Unknown`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unknown"""
                        RootMLangTags.ZH -> """未知"""
                        else -> null
                    }
                } ?: """未知"""

        /** 未知 */
        @Composable fun `Unknown`(vararg args: Any?) = FYTxtConfig.observe { `Unknown`.fmt(args) }

        /** 已切换到：%s 模式 */
        val `Switched`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Switched to: %s mode"""
                        RootMLangTags.ZH -> """已切换到：%s 模式"""
                        else -> null
                    }
                } ?: """已切换到：%s 模式"""

        /** 已切换到：%s 模式 */
        @Composable fun `Switched`(vararg args: Any?) = FYTxtConfig.observe { `Switched`.fmt(args) }

        /** 切换模式失败：%s */
        val `SwitchFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Mode switch failed: %s"""
                        RootMLangTags.ZH -> """切换模式失败：%s"""
                        else -> null
                    }
                } ?: """切换模式失败：%s"""

        /** 切换模式失败：%s */
        @Composable
        fun `SwitchFailed`(vararg args: Any?) = FYTxtConfig.observe { `SwitchFailed`.fmt(args) }
    }

    object `Action` {
        init {
            RootMLangGroups
        }

        /** 面板 */
        val `Panel`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Panel"""
                        RootMLangTags.ZH -> """面板"""
                        else -> null
                    }
                } ?: """面板"""

        /** 面板 */
        @Composable fun `Panel`(vararg args: Any?) = FYTxtConfig.observe { `Panel`.fmt(args) }

        /** 测试 */
        val `Test`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Test"""
                        RootMLangTags.ZH -> """测试"""
                        else -> null
                    }
                } ?: """测试"""

        /** 测试 */
        @Composable fun `Test`(vararg args: Any?) = FYTxtConfig.observe { `Test`.fmt(args) }

        /** 资源管理 */
        val `Resources`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Resources"""
                        RootMLangTags.ZH -> """资源管理"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Control Panel"""
                        RootMLangTags.ZH -> """控制面板"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """More"""
                        RootMLangTags.ZH -> """更多"""
                        else -> null
                    }
                } ?: """更多"""

        /** 更多 */
        @Composable fun `More`(vararg args: Any?) = FYTxtConfig.observe { `More`.fmt(args) }

        /** 展开风格 */
        val `GroupStyle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Group Style"""
                        RootMLangTags.ZH -> """展开风格"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Show Hidden Proxy Groups"""
                        RootMLangTags.ZH -> """显示隐藏策略组"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Back"""
                        RootMLangTags.ZH -> """返回"""
                        else -> null
                    }
                } ?: """返回"""

        /** 返回 */
        @Composable fun `Back`(vararg args: Any?) = FYTxtConfig.observe { `Back`.fmt(args) }

        /** 关闭 */
        val `Close`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Close"""
                        RootMLangTags.ZH -> """关闭"""
                        else -> null
                    }
                } ?: """关闭"""

        /** 关闭 */
        @Composable fun `Close`(vararg args: Any?) = FYTxtConfig.observe { `Close`.fmt(args) }

        /** 测速 */
        val `TestDelay`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Test Delay"""
                        RootMLangTags.ZH -> """测速"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Group Item Order"""
                        RootMLangTags.ZH -> """组内排序"""
                        else -> null
                    }
                } ?: """组内排序"""

        /** 组内排序 */
        @Composable fun `SortMode`(vararg args: Any?) = FYTxtConfig.observe { `SortMode`.fmt(args) }

        /** 调整组内条目顺序 */
        val `SortModeSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Adjust item order inside each group"""
                        RootMLangTags.ZH -> """调整组内条目顺序"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Profile"""
                        RootMLangTags.ZH -> """添加订阅"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Provider"""
                        RootMLangTags.ZH -> """添加资源"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Override"""
                        RootMLangTags.ZH -> """添加覆写"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Chain"""
                        RootMLangTags.ZH -> """添加链式代理"""
                        else -> null
                    }
                } ?: """添加链式代理"""

        /** 添加链式代理 */
        @Composable fun `AddChain`(vararg args: Any?) = FYTxtConfig.observe { `AddChain`.fmt(args) }
    }

    object `Empty` {
        init {
            RootMLangGroups
        }

        /** 暂无节点 */
        val `NoNodes`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No nodes"""
                        RootMLangTags.ZH -> """暂无节点"""
                        else -> null
                    }
                } ?: """暂无节点"""

        /** 暂无节点 */
        @Composable fun `NoNodes`(vararg args: Any?) = FYTxtConfig.observe { `NoNodes`.fmt(args) }

        /** 请在配置页面加载配置文件 */
        val `Hint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Please load a profile in Config page"""
                        RootMLangTags.ZH -> """请在配置页面加载配置文件"""
                        else -> null
                    }
                } ?: """请在配置页面加载配置文件"""

        /** 请在配置页面加载配置文件 */
        @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }
    }

    object `Testing` {
        init {
            RootMLangGroups
        }

        /** 正在测试节点组：%s */
        val `Group`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Testing node group: %s"""
                        RootMLangTags.ZH -> """正在测试节点组：%s"""
                        else -> null
                    }
                } ?: """正在测试节点组：%s"""

        /** 正在测试节点组：%s */
        @Composable fun `Group`(vararg args: Any?) = FYTxtConfig.observe { `Group`.fmt(args) }

        /** 正在测试所有节点组... */
        val `All`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Testing all node groups..."""
                        RootMLangTags.ZH -> """正在测试所有节点组..."""
                        else -> null
                    }
                } ?: """正在测试所有节点组..."""

        /** 正在测试所有节点组... */
        @Composable fun `All`(vararg args: Any?) = FYTxtConfig.observe { `All`.fmt(args) }

        /** 测试请求已发送 */
        val `RequestSent`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Test request sent"""
                        RootMLangTags.ZH -> """测试请求已发送"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Test failed: %s"""
                        RootMLangTags.ZH -> """测试失败：%s"""
                        else -> null
                    }
                } ?: """测试失败：%s"""

        /** 测试失败：%s */
        @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

        /** 正在测试节点 */
        val `InProgress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Checking"""
                        RootMLangTags.ZH -> """检测中"""
                        else -> null
                    }
                } ?: """检测中"""

        /** 检测中 */
        @Composable
        fun `InProgress`(vararg args: Any?) = FYTxtConfig.observe { `InProgress`.fmt(args) }
    }

    object `Selection` {
        init {
            RootMLangGroups
        }

        /** 已切换到：%s */
        val `Switched`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Switched to: %s"""
                        RootMLangTags.ZH -> """已切换到：%s"""
                        else -> null
                    }
                } ?: """已切换到：%s"""

        /** 已切换到：%s */
        @Composable fun `Switched`(vararg args: Any?) = FYTxtConfig.observe { `Switched`.fmt(args) }

        /** 切换失败 */
        val `Failed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Switch failed"""
                        RootMLangTags.ZH -> """切换失败"""
                        else -> null
                    }
                } ?: """切换失败"""

        /** 切换失败 */
        @Composable fun `Failed`(vararg args: Any?) = FYTxtConfig.observe { `Failed`.fmt(args) }

        /** 切换失败：%s */
        val `Error`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Switch failed: %s"""
                        RootMLangTags.ZH -> """切换失败：%s"""
                        else -> null
                    }
                } ?: """切换失败：%s"""

        /** 切换失败：%s */
        @Composable fun `Error`(vararg args: Any?) = FYTxtConfig.observe { `Error`.fmt(args) }

        /** 当前 */
        val `Current`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Current"""
                        RootMLangTags.ZH -> """当前"""
                        else -> null
                    }
                } ?: """当前"""

        /** 当前 */
        @Composable fun `Current`(vararg args: Any?) = FYTxtConfig.observe { `Current`.fmt(args) }

        /** 当前节点 */
        val `CurrentNode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Current Node"""
                        RootMLangTags.ZH -> """当前节点"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d nodes"""
                        RootMLangTags.ZH -> """%d 个节点"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Latency"""
                        RootMLangTags.ZH -> """延迟"""
                        else -> null
                    }
                } ?: """延迟"""

        /** 延迟 */
        @Composable fun `Latency`(vararg args: Any?) = FYTxtConfig.observe { `Latency`.fmt(args) }

        /** 超时 */
        val `Timeout`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Timed out"""
                        RootMLangTags.ZH -> """检测超时"""
                        else -> null
                    }
                } ?: """检测超时"""

        /** 检测超时 */
        @Composable fun `Timeout`(vararg args: Any?) = FYTxtConfig.observe { `Timeout`.fmt(args) }

        /** 待检测 */
        val `UnknownLatency`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Pending"""
                        RootMLangTags.ZH -> """待检测"""
                        else -> null
                    }
                } ?: """待检测"""

        /** 待检测 */
        @Composable
        fun `UnknownLatency`(vararg args: Any?) = FYTxtConfig.observe { `UnknownLatency`.fmt(args) }
    }

    object `SortMode` {
        init {
            RootMLangGroups
        }

        /** 默认顺序 */
        val `Default`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Default"""
                        RootMLangTags.ZH -> """默认顺序"""
                        else -> null
                    }
                } ?: """默认顺序"""

        /** 默认顺序 */
        @Composable fun `Default`(vararg args: Any?) = FYTxtConfig.observe { `Default`.fmt(args) }

        /** 按名称排序 */
        val `ByName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """By Name"""
                        RootMLangTags.ZH -> """按名称排序"""
                        else -> null
                    }
                } ?: """按名称排序"""

        /** 按名称排序 */
        @Composable fun `ByName`(vararg args: Any?) = FYTxtConfig.observe { `ByName`.fmt(args) }

        /** 按延迟排序 */
        val `ByLatency`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """By Latency"""
                        RootMLangTags.ZH -> """按延迟排序"""
                        else -> null
                    }
                } ?: """按延迟排序"""

        /** 按延迟排序 */
        @Composable
        fun `ByLatency`(vararg args: Any?) = FYTxtConfig.observe { `ByLatency`.fmt(args) }
    }

    object `DisplayMode` {
        init {
            RootMLangGroups
        }

        /** 单列详细 */
        val `SingleDetailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Single Detailed"""
                        RootMLangTags.ZH -> """单列详细"""
                        else -> null
                    }
                } ?: """单列详细"""

        /** 单列详细 */
        @Composable
        fun `SingleDetailed`(vararg args: Any?) = FYTxtConfig.observe { `SingleDetailed`.fmt(args) }

        /** 单列简洁 */
        val `SingleSimple`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Single Simple"""
                        RootMLangTags.ZH -> """单列简洁"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Double Detailed"""
                        RootMLangTags.ZH -> """双列详细"""
                        else -> null
                    }
                } ?: """双列详细"""

        /** 双列详细 */
        @Composable
        fun `DoubleDetailed`(vararg args: Any?) = FYTxtConfig.observe { `DoubleDetailed`.fmt(args) }

        /** 双列简洁 */
        val `DoubleSimple`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Double Simple"""
                        RootMLangTags.ZH -> """双列简洁"""
                        else -> null
                    }
                } ?: """双列简洁"""

        /** 双列简洁 */
        @Composable
        fun `DoubleSimple`(vararg args: Any?) = FYTxtConfig.observe { `DoubleSimple`.fmt(args) }
    }

    object `GroupStyle` {
        init {
            RootMLangGroups
        }

        /** 列表展开 */
        val `Inline`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Inline Expand"""
                        RootMLangTags.ZH -> """列表展开"""
                        else -> null
                    }
                } ?: """列表展开"""

        /** 列表展开 */
        @Composable fun `Inline`(vararg args: Any?) = FYTxtConfig.observe { `Inline`.fmt(args) }

        /** 浮窗展开 */
        val `Floating`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Floating Popup"""
                        RootMLangTags.ZH -> """浮窗展开"""
                        else -> null
                    }
                } ?: """浮窗展开"""

        /** 浮窗展开 */
        @Composable fun `Floating`(vararg args: Any?) = FYTxtConfig.observe { `Floating`.fmt(args) }
    }
}
