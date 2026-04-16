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

object MLangConnection {
    init {
        RootMLangGroups
    }

    /** 连接 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Connections"""
                    RootMLangTags.ZH -> """连接"""
                    else -> null
                }
            } ?: """连接"""

    /** 连接 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    /** 查看当前活动连接 */
    val `Summary`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """View active connections"""
                    RootMLangTags.ZH -> """查看当前活动连接"""
                    else -> null
                }
            } ?: """查看当前活动连接"""

    /** 查看当前活动连接 */
    @Composable fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }

    object `Tab` {
        init {
            RootMLangGroups
        }

        /** 活动中 */
        val `Active`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Active"""
                        RootMLangTags.ZH -> """活动中"""
                        else -> null
                    }
                } ?: """活动中"""

        /** 活动中 */
        @Composable fun `Active`(vararg args: Any?) = FYTxtConfig.observe { `Active`.fmt(args) }

        /** 已关闭 */
        val `Closed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Closed"""
                        RootMLangTags.ZH -> """已关闭"""
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
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Search"""
                    RootMLangTags.ZH -> """搜索"""
                    else -> null
                }
            } ?: """搜索"""

    /** 搜索 */
    @Composable fun `Search`(vararg args: Any?) = FYTxtConfig.observe { `Search`.fmt(args) }

    /** 搜索主机、进程... */
    val `SearchHint`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Search host, process..."""
                    RootMLangTags.ZH -> """搜索主机、进程..."""
                    else -> null
                }
            } ?: """搜索主机、进程..."""

    /** 搜索主机、进程... */
    @Composable fun `SearchHint`(vararg args: Any?) = FYTxtConfig.observe { `SearchHint`.fmt(args) }

    /** 排序: */
    val `SortBy`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Sort by:"""
                    RootMLangTags.ZH -> """排序:"""
                    else -> null
                }
            } ?: """排序:"""

    /** 排序: */
    @Composable fun `SortBy`(vararg args: Any?) = FYTxtConfig.observe { `SortBy`.fmt(args) }

    object `Sort` {
        init {
            RootMLangGroups
        }

        /** 时间 */
        val `Time`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Time"""
                        RootMLangTags.ZH -> """时间"""
                        else -> null
                    }
                } ?: """时间"""

        /** 时间 */
        @Composable fun `Time`(vararg args: Any?) = FYTxtConfig.observe { `Time`.fmt(args) }

        /** 上传 */
        val `Upload`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Upload"""
                        RootMLangTags.ZH -> """上传"""
                        else -> null
                    }
                } ?: """上传"""

        /** 上传 */
        @Composable fun `Upload`(vararg args: Any?) = FYTxtConfig.observe { `Upload`.fmt(args) }

        /** 下载 */
        val `Download`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Download"""
                        RootMLangTags.ZH -> """下载"""
                        else -> null
                    }
                } ?: """下载"""

        /** 下载 */
        @Composable fun `Download`(vararg args: Any?) = FYTxtConfig.observe { `Download`.fmt(args) }

        /** 主机 */
        val `Host`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Host"""
                        RootMLangTags.ZH -> """主机"""
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
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Loading..."""
                    RootMLangTags.ZH -> """加载中..."""
                    else -> null
                }
            } ?: """加载中..."""

    /** 加载中... */
    @Composable fun `Loading`(vararg args: Any?) = FYTxtConfig.observe { `Loading`.fmt(args) }

    /** 暂无活动连接 */
    val `Empty`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """No active connections"""
                    RootMLangTags.ZH -> """暂无活动连接"""
                    else -> null
                }
            } ?: """暂无活动连接"""

    /** 暂无活动连接 */
    @Composable fun `Empty`(vararg args: Any?) = FYTxtConfig.observe { `Empty`.fmt(args) }

    /** 没有匹配的连接 */
    val `NoResults`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """No matching connections"""
                    RootMLangTags.ZH -> """没有匹配的连接"""
                    else -> null
                }
            } ?: """没有匹配的连接"""

    /** 没有匹配的连接 */
    @Composable fun `NoResults`(vararg args: Any?) = FYTxtConfig.observe { `NoResults`.fmt(args) }

    object `RelativeTime` {
        init {
            RootMLangGroups
        }

        /** 刚刚 */
        val `JustNow`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Just now"""
                        RootMLangTags.ZH -> """刚刚"""
                        else -> null
                    }
                } ?: """刚刚"""

        /** 刚刚 */
        @Composable fun `JustNow`(vararg args: Any?) = FYTxtConfig.observe { `JustNow`.fmt(args) }

        /** %d分钟前 */
        val `MinutesAgo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d min ago"""
                        RootMLangTags.ZH -> """%d分钟前"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d hr ago"""
                        RootMLangTags.ZH -> """%d小时前"""
                        else -> null
                    }
                } ?: """%d小时前"""

        /** %d小时前 */
        @Composable fun `HoursAgo`(vararg args: Any?) = FYTxtConfig.observe { `HoursAgo`.fmt(args) }

        /** %d天前 */
        val `DaysAgo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d d ago"""
                        RootMLangTags.ZH -> """%d天前"""
                        else -> null
                    }
                } ?: """%d天前"""

        /** %d天前 */
        @Composable fun `DaysAgo`(vararg args: Any?) = FYTxtConfig.observe { `DaysAgo`.fmt(args) }
    }

    object `Detail` {
        init {
            RootMLangGroups
        }

        /** 连接信息 */
        val `Info`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Connection Info"""
                        RootMLangTags.ZH -> """连接信息"""
                        else -> null
                    }
                } ?: """连接信息"""

        /** 连接信息 */
        @Composable fun `Info`(vararg args: Any?) = FYTxtConfig.observe { `Info`.fmt(args) }

        /** 协议 */
        val `Protocol`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Protocol"""
                        RootMLangTags.ZH -> """协议"""
                        else -> null
                    }
                } ?: """协议"""

        /** 协议 */
        @Composable fun `Protocol`(vararg args: Any?) = FYTxtConfig.observe { `Protocol`.fmt(args) }

        /** 源应用 */
        val `SourceApp`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Source App"""
                        RootMLangTags.ZH -> """源应用"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Package"""
                        RootMLangTags.ZH -> """包名"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Process"""
                        RootMLangTags.ZH -> """进程"""
                        else -> null
                    }
                } ?: """进程"""

        /** 进程 */
        @Composable fun `Process`(vararg args: Any?) = FYTxtConfig.observe { `Process`.fmt(args) }

        /** 源地址 */
        val `SourceAddress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Source"""
                        RootMLangTags.ZH -> """源地址"""
                        else -> null
                    }
                } ?: """源地址"""

        /** 源地址 */
        @Composable
        fun `SourceAddress`(vararg args: Any?) = FYTxtConfig.observe { `SourceAddress`.fmt(args) }

        /** 目标地址 */
        val `DestinationAddress`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Destination"""
                        RootMLangTags.ZH -> """目标地址"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Duration"""
                        RootMLangTags.ZH -> """连接时长"""
                        else -> null
                    }
                } ?: """连接时长"""

        /** 连接时长 */
        @Composable fun `Duration`(vararg args: Any?) = FYTxtConfig.observe { `Duration`.fmt(args) }

        /** 上传 */
        val `Upload`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Upload"""
                        RootMLangTags.ZH -> """上传"""
                        else -> null
                    }
                } ?: """上传"""

        /** 上传 */
        @Composable fun `Upload`(vararg args: Any?) = FYTxtConfig.observe { `Upload`.fmt(args) }

        /** 下载 */
        val `Download`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Download"""
                        RootMLangTags.ZH -> """下载"""
                        else -> null
                    }
                } ?: """下载"""

        /** 下载 */
        @Composable fun `Download`(vararg args: Any?) = FYTxtConfig.observe { `Download`.fmt(args) }

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

        /** 类型 */
        val `Type`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Type"""
                        RootMLangTags.ZH -> """类型"""
                        else -> null
                    }
                } ?: """类型"""

        /** 类型 */
        @Composable fun `Type`(vararg args: Any?) = FYTxtConfig.observe { `Type`.fmt(args) }

        /** 内容 */
        val `Content`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Content"""
                        RootMLangTags.ZH -> """内容"""
                        else -> null
                    }
                } ?: """内容"""

        /** 内容 */
        @Composable fun `Content`(vararg args: Any?) = FYTxtConfig.observe { `Content`.fmt(args) }
    }
}
