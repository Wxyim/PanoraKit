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

package com.github.nomadboxlab.monadbox.presentation.component

import com.github.nomadboxlab.monadbox.presentation.util.*
import kotlinx.serialization.json.JsonElement

typealias OpenRuleListEditor =
    (
        title: String,
        values: OverrideListModeValues<List<String>>,
        availableModes: List<OverrideListEditorMode>,
        selectedMode: OverrideListEditorMode,
        referenceCatalog: OverrideReferenceCatalog,
        onValueChange: (OverrideListModeValues<List<String>>) -> Unit,
    ) -> Unit

typealias OpenStructuredObjectListEditor =
    (
        type: OverrideStructuredObjectType,
        title: String,
        values: OverrideListModeValues<List<Map<String, JsonElement>>>,
        availableModes: List<OverrideListEditorMode>,
        selectedMode: OverrideListEditorMode,
        referenceCatalog: OverrideReferenceCatalog,
        onValueChange: (OverrideListModeValues<List<Map<String, JsonElement>>>) -> Unit,
    ) -> Unit

typealias OpenObjectMapEditor =
    (
        type: OverrideStructuredMapType,
        title: String,
        values: OverrideListModeValues<Map<String, Map<String, JsonElement>>>,
        availableModes: List<OverrideListEditorMode>,
        selectedMode: OverrideListEditorMode,
        onValueChange: (OverrideListModeValues<Map<String, Map<String, JsonElement>>>) -> Unit,
    ) -> Unit

typealias OpenSubRulesEditor =
    (
        title: String,
        values: OverrideListModeValues<Map<String, List<String>>>,
        availableModes: List<OverrideListEditorMode>,
        selectedMode: OverrideListEditorMode,
        referenceCatalog: OverrideReferenceCatalog,
        onValueChange: (OverrideListModeValues<Map<String, List<String>>>) -> Unit,
    ) -> Unit
