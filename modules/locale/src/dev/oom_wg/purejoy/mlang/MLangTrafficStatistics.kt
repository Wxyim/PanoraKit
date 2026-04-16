/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

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

object MLangTrafficStatistics {
    init {
        RootMLangGroups
    }

    /** 流量统计与最近请求 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Traffic Statistics & Recent Requests"""
                    RootMLangTags.ZH -> """流量统计与最近请求"""
                    else -> null
                }
            } ?: """流量统计与最近请求"""

    /** 流量统计与最近请求 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    /** 查看流量概览与最近请求 */
    val `EntrySummary`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """View traffic overview and recent requests"""
                    RootMLangTags.ZH -> """查看流量概览与最近请求"""
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
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Traffic Overview"""
                    RootMLangTags.ZH -> """流量概览"""
                    else -> null
                }
            } ?: """流量概览"""

    /** 流量概览 */
    @Composable
    fun `OverviewTitle`(vararg args: Any?) = FYTxtConfig.observe { `OverviewTitle`.fmt(args) }

    object `Detail` {
        init {
            RootMLangGroups
        }

        /** 请求与站点流量 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Requests & Site Traffic"""
                        RootMLangTags.ZH -> """请求与站点流量"""
                        else -> null
                    }
                } ?: """请求与站点流量"""

        /** 请求与站点流量 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 在“最近请求”和“站点流量”之间切换查看 */
        val `Summary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Switch between recent requests and site-level traffic aggregation"""
                        RootMLangTags.ZH -> """在“最近请求”和“站点流量”之间切换查看"""
                        else -> null
                    }
                } ?: """在“最近请求”和“站点流量”之间切换查看"""

        /** 在“最近请求”和“站点流量”之间切换查看 */
        @Composable fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
    }

    object `TargetSites` {
        init {
            RootMLangGroups
        }

        /** 站点流量 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Site Traffic"""
                        RootMLangTags.ZH -> """站点流量"""
                        else -> null
                    }
                } ?: """站点流量"""

        /** 站点流量 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 按目标域名或目标 IP 聚合上下行流量 */
        val `Summary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Aggregate upload and download traffic by destination host or IP"""
                        RootMLangTags.ZH -> """按目标域名或目标 IP 聚合上下行流量"""
                        else -> null
                    }
                } ?: """按目标域名或目标 IP 聚合上下行流量"""

        /** 按目标域名或目标 IP 聚合上下行流量 */
        @Composable fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }

        /** 暂无目标网站流量记录 */
        val `Empty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No destination traffic yet"""
                        RootMLangTags.ZH -> """暂无目标网站流量记录"""
                        else -> null
                    }
                } ?: """暂无目标网站流量记录"""

        /** 暂无目标网站流量记录 */
        @Composable fun `Empty`(vararg args: Any?) = FYTxtConfig.observe { `Empty`.fmt(args) }

        /** %d 项 */
        val `Count`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d"""
                        RootMLangTags.ZH -> """%d 项"""
                        else -> null
                    }
                } ?: """%d 项"""

        /** %d 项 */
        @Composable fun `Count`(vararg args: Any?) = FYTxtConfig.observe { `Count`.fmt(args) }

        /** 上行 %s */
        val `Upload`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Up %s"""
                        RootMLangTags.ZH -> """上行 %s"""
                        else -> null
                    }
                } ?: """上行 %s"""

        /** 上行 %s */
        @Composable fun `Upload`(vararg args: Any?) = FYTxtConfig.observe { `Upload`.fmt(args) }

        /** 下行 %s */
        val `Download`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Down %s"""
                        RootMLangTags.ZH -> """下行 %s"""
                        else -> null
                    }
                } ?: """下行 %s"""

        /** 下行 %s */
        @Composable fun `Download`(vararg args: Any?) = FYTxtConfig.observe { `Download`.fmt(args) }
    }

    object `RecentRequests` {
        init {
            RootMLangGroups
        }

        /** 最近请求 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Recent Requests"""
                        RootMLangTags.ZH -> """最近请求"""
                        else -> null
                    }
                } ?: """最近请求"""

        /** 最近请求 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 按时间顺序查看最近的网络访问与命中链路 */
        val `Summary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Browse recent network requests and matched proxy chains in chronological order"""
                        RootMLangTags.ZH -> """按时间顺序查看最近的网络访问与命中链路"""
                        else -> null
                    }
                } ?: """按时间顺序查看最近的网络访问与命中链路"""

        /** 按时间顺序查看最近的网络访问与命中链路 */
        @Composable fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }

        /** 暂无最近请求记录 */
        val `Empty`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No recent requests"""
                        RootMLangTags.ZH -> """暂无最近请求记录"""
                        else -> null
                    }
                } ?: """暂无最近请求记录"""

        /** 暂无最近请求记录 */
        @Composable fun `Empty`(vararg args: Any?) = FYTxtConfig.observe { `Empty`.fmt(args) }

        /** 未知请求 */
        val `UnknownRequest`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unknown Request"""
                        RootMLangTags.ZH -> """未知请求"""
                        else -> null
                    }
                } ?: """未知请求"""

        /** 未知请求 */
        @Composable
        fun `UnknownRequest`(vararg args: Any?) = FYTxtConfig.observe { `UnknownRequest`.fmt(args) }

        /** %d 条 */
        val `Count`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d"""
                        RootMLangTags.ZH -> """%d 条"""
                        else -> null
                    }
                } ?: """%d 条"""

        /** %d 条 */
        @Composable fun `Count`(vararg args: Any?) = FYTxtConfig.observe { `Count`.fmt(args) }
    }

    object `Status` {
        init {
            RootMLangGroups
        }

        /** 进行中 */
        val `Active`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Active"""
                        RootMLangTags.ZH -> """进行中"""
                        else -> null
                    }
                } ?: """进行中"""

        /** 进行中 */
        @Composable fun `Active`(vararg args: Any?) = FYTxtConfig.observe { `Active`.fmt(args) }

        /** 已结束 */
        val `Closed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Closed"""
                        RootMLangTags.ZH -> """已结束"""
                        else -> null
                    }
                } ?: """已结束"""

        /** 已结束 */
        @Composable fun `Closed`(vararg args: Any?) = FYTxtConfig.observe { `Closed`.fmt(args) }
    }

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
    }

    object `TimeRange` {
        init {
            RootMLangGroups
        }

        /** 今日 */
        val `Today`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Today"""
                        RootMLangTags.ZH -> """今日"""
                        else -> null
                    }
                } ?: """今日"""

        /** 今日 */
        @Composable fun `Today`(vararg args: Any?) = FYTxtConfig.observe { `Today`.fmt(args) }

        /** 本周 */
        val `Week`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """This Week"""
                        RootMLangTags.ZH -> """本周"""
                        else -> null
                    }
                } ?: """本周"""

        /** 本周 */
        @Composable fun `Week`(vararg args: Any?) = FYTxtConfig.observe { `Week`.fmt(args) }
    }

    object `Summary` {
        init {
            RootMLangGroups
        }

        /** 今日流量 */
        val `TodayTraffic`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Today's Traffic"""
                        RootMLangTags.ZH -> """今日流量"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """This Week's Traffic"""
                        RootMLangTags.ZH -> """本周流量"""
                        else -> null
                    }
                } ?: """本周流量"""

        /** 本周流量 */
        @Composable
        fun `WeekTraffic`(vararg args: Any?) = FYTxtConfig.observe { `WeekTraffic`.fmt(args) }
    }

    object `Compare` {
        init {
            RootMLangGroups
        }

        /** 较昨日 +%s */
        val `MoreThanYesterday`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """vs yesterday +%s"""
                        RootMLangTags.ZH -> """较昨日 +%s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """vs yesterday %s"""
                        RootMLangTags.ZH -> """较昨日 %s"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Same as yesterday"""
                        RootMLangTags.ZH -> """与昨日持平"""
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
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Last 7 days stats"""
                        RootMLangTags.ZH -> """近 7 天统计"""
                        else -> null
                    }
                } ?: """近 7 天统计"""

        /** 近 7 天统计 */
        @Composable
        fun `WeekStats`(vararg args: Any?) = FYTxtConfig.observe { `WeekStats`.fmt(args) }
    }

    object `Chart` {
        init {
            RootMLangGroups
        }

        /** 4 小时 */
        val `Hourly`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """4 Hours"""
                        RootMLangTags.ZH -> """4 小时"""
                        else -> null
                    }
                } ?: """4 小时"""

        /** 4 小时 */
        @Composable fun `Hourly`(vararg args: Any?) = FYTxtConfig.observe { `Hourly`.fmt(args) }

        /** 按天 */
        val `Daily`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Daily"""
                        RootMLangTags.ZH -> """按天"""
                        else -> null
                    }
                } ?: """按天"""

        /** 按天 */
        @Composable fun `Daily`(vararg args: Any?) = FYTxtConfig.observe { `Daily`.fmt(args) }
    }
}
