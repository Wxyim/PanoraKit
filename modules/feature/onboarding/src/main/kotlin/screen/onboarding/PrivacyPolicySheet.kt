/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.screen.onboarding

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.nomadboxlab.monadbox.feature.onboarding.R
import com.github.nomadboxlab.monadbox.presentation.component.AppActionBottomSheet
import com.github.nomadboxlab.monadbox.presentation.theme.PrivacyPolicySheetLayoutDefaults
import dev.oom_wg.purejoy.mlang.MLang
import java.util.Locale
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@SuppressLint("LocalContextResourcesRead")
@Composable
internal fun PrivacyPolicySheet(show: MutableState<Boolean>) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val policyText = remember {
        runCatching {
                // Policy: Simplified Chinese uses dedicated text; others fallback to English.
                val resourceId =
                    when (Locale.getDefault().language) {
                        "zh" -> R.raw.privacy_policy_zh
                        else -> R.raw.privacy_policy
                    }
                context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
            }
            .getOrElse { MLang.Onboarding.Sheet.LoadFailed }
    }

    AppActionBottomSheet(
        show = show.value,
        title = MLang.Onboarding.Sheet.PrivacyPolicyTitle,
        onDismissRequest = { show.value = false },
    ) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = PrivacyPolicySheetLayoutDefaults.MaxHeight)
                    .verticalScroll(scrollState)
        ) {
            Text(
                text = policyText,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurface,
            )
        }
    }
}
