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

import dev.oom_wg.purejoy.mlang.MLang
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticDetail
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.Mark
import org.yaml.snakeyaml.error.MarkedYAMLException
import timber.log.Timber

object YamlDiagnosticsProvider {

    fun analyze(content: String): CodeAnalysisResult {
        val container = DiagnosticsContainer()
        if (content.isBlank()) {
            return CodeAnalysisResult(diagnostics = container)
        }

        return try {
            Yaml().load<Any?>(content)
            CodeAnalysisResult(diagnostics = container)
        } catch (error: MarkedYAMLException) {
            val detail =
                error.problem?.trim()?.takeIf(String::isNotBlank)
                    ?: error.context?.trim()?.takeIf(String::isNotBlank)
                    ?: error.message
                        ?.lineSequence()
                        ?.firstOrNull()
                        ?.trim()
                        ?.takeIf(String::isNotBlank)
                    ?: MLang.Component.Editor.Error.Unknown
            val (start, end) = resolveMarkedRange(content, error.problemMark ?: error.contextMark)
            container.addDiagnostic(
                DiagnosticRegion(
                    start,
                    end,
                    DiagnosticRegion.SEVERITY_ERROR,
                    0,
                    DiagnosticDetail(
                        briefMessage = MLang.Component.Editor.Error.YamlSyntaxError,
                        detailedMessage = detail,
                    ),
                )
            )
            CodeAnalysisResult(
                diagnostics = container,
                hasErrors = true,
                primaryMessage = MLang.Component.Editor.Error.ValidationFailed.format(detail),
            )
        } catch (error: Exception) {
            Timber.w(error, "YAML analysis failed")
            CodeAnalysisResult(
                diagnostics = container,
                hasErrors = true,
                primaryMessage =
                    MLang.Component.Editor.Error.ValidationFailed.format(
                        MLang.Component.Editor.Error.Unknown
                    ),
            )
        }
    }

    private fun resolveMarkedRange(content: String, mark: Mark?): Pair<Int, Int> {
        if (mark == null) {
            return 0 to content.length.coerceAtMost(1)
        }

        val lineStart = lineStartOffset(content, mark.line)
        val lineEnd = lineEndOffset(content, lineStart)
        val start = (lineStart + mark.column).coerceIn(0, content.length)
        val end =
            if (lineEnd <= start) {
                start
            } else {
                (start + 1).coerceAtMost(lineEnd)
            }
        return start to end
    }

    private fun lineStartOffset(content: String, targetLine: Int): Int {
        if (targetLine <= 0) {
            return 0
        }

        var currentLine = 0
        var index = 0
        while (index < content.length && currentLine < targetLine) {
            if (content[index] == '\n') {
                currentLine += 1
            }
            index += 1
        }
        return index
    }

    private fun lineEndOffset(content: String, lineStart: Int): Int {
        val nextLineFeed = content.indexOf('\n', startIndex = lineStart)
        return when {
            nextLineFeed < 0 -> content.length
            nextLineFeed > lineStart && content[nextLineFeed - 1] == '\r' -> nextLineFeed - 1
            else -> nextLineFeed
        }
    }
}
