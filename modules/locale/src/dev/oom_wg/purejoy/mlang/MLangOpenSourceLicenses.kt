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

object MLangOpenSourceLicenses {
    init {
        RootMLangGroups
    }

    /** 开源许可证 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Open Source Licenses"""
                    RootMLangTags.ZH -> """开源许可证"""
                    else -> null
                }
            } ?: """开源许可证"""

    /** 开源许可证 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `LicenseSheet` {
        init {
            RootMLangGroups
        }

        /** 暂无许可证内容 */
        val `NoContent`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No license content"""
                        RootMLangTags.ZH -> """暂无许可证内容"""
                        else -> null
                    }
                } ?: """暂无许可证内容"""

        /** 暂无许可证内容 */
        @Composable
        fun `NoContent`(vararg args: Any?) = FYTxtConfig.observe { `NoContent`.fmt(args) }
    }
}
