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

package com.github.nomadboxlab.monadbox.screen.about

import androidx.compose.runtime.Composable
import com.github.nomadboxlab.monadbox.R
import com.github.nomadboxlab.monadbox.feature.about.OpenSourceLicensesScreenBody
import com.github.nomadboxlab.monadbox.presentation.component.NavigationBackIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang

@Composable
@Destination<RootGraph>
fun OpenSourceLicensesScreen(navigator: DestinationsNavigator) {
    OpenSourceLicensesScreenBody(
        aboutLibrariesRawResId = R.raw.aboutlibraries,
        navigationIcon = {
            NavigationBackIcon(
                navigator = navigator,
                contentDescription = MLang.Component.Navigation.Back,
            )
        },
        onBack = { navigator.popBackStack() },
    )
}
