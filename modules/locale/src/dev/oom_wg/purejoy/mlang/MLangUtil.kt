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

object MLangUtil {
    init {
        RootMLangGroups
    }

    object `Error` {
        init {
            RootMLangGroups
        }

        /** 未知错误 */
        val `UnknownError`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unknown error"""
                        RootMLangTags.ZH -> """未知错误"""
                        else -> null
                    }
                } ?: """未知错误"""

        /** 未知错误 */
        @Composable
        fun `UnknownError`(vararg args: Any?) = FYTxtConfig.observe { `UnknownError`.fmt(args) }
    }
}
