/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.feature.editor.diagnostic

import io.github.rosemoe.sora.lang.diagnostic.DiagnosticDetail
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer

object YamlDiagnosticsProvider {

    fun analyze(content: String): DiagnosticsContainer {
        val container = DiagnosticsContainer()
        if (content.isBlank()) return container

        val lines = content.lines()
        var offset = 0

        for ((lineIndex, line) in lines.withIndex()) {
            checkTabIndentation(line, offset, lineIndex, container)
            offset += line.length + 1
        }

        return container
    }

    private fun checkTabIndentation(
        line: String,
        lineOffset: Int,
        lineIndex: Int,
        container: DiagnosticsContainer,
    ) {
        val tabIndex = line.indexOf('\t')
        if (tabIndex >= 0 && line.substring(0, tabIndex).all { it == ' ' || it == '\t' }) {
            container.addDiagnostic(
                DiagnosticRegion(
                    lineOffset + tabIndex,
                    lineOffset + tabIndex + 1,
                    DiagnosticRegion.SEVERITY_WARNING,
                    0,
                    DiagnosticDetail(
                        briefMessage = "Tab indentation",
                        detailedMessage =
                            "Line ${lineIndex + 1}: YAML uses spaces for indentation, not tabs",
                    ),
                )
            )
        }
    }
}
