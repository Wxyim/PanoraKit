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

package com.github.nomadboxlab.monadbox.feature.editor.diagnostic

import dev.oom_wg.purejoy.mlang.MLang
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticDetail
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

object JsonDiagnosticsProvider {

    fun analyze(content: String): CodeAnalysisResult {
        val container = DiagnosticsContainer()

        if (content.isBlank()) {
            val detail = MLang.Component.Editor.Error.MissingValue
            addDiagnostic(
                container = container,
                start = 0,
                end = 0,
                briefMessage = MLang.Component.Editor.Error.JsonSyntaxError,
                detailedMessage = detail,
            )
            return CodeAnalysisResult(
                diagnostics = container,
                hasErrors = true,
                primaryMessage = MLang.Component.Editor.Error.ValidationFailed.format(detail),
            )
        }

        val trimmed = content.trim()

        try {

            when {
                trimmed.startsWith("{") -> JSONObject(trimmed)
                trimmed.startsWith("[") -> JSONArray(trimmed)
                else -> {
                    val detail = MLang.Component.Editor.Error.JsonRootExpected
                    addDiagnostic(
                        container = container,
                        start = 0,
                        end = content.length.coerceAtMost(1),
                        briefMessage = MLang.Component.Editor.Error.JsonSyntaxError,
                        detailedMessage = detail,
                    )
                    return CodeAnalysisResult(
                        diagnostics = container,
                        hasErrors = true,
                        primaryMessage =
                            MLang.Component.Editor.Error.ValidationFailed.format(detail),
                    )
                }
            }
        } catch (e: JSONException) {
            val detailMessage = formatErrorMessage(e.message)
            val diagnostic = parseJsonException(e, content, detailMessage)
            if (diagnostic != null) {
                container.addDiagnostic(diagnostic)
            }
            return CodeAnalysisResult(
                diagnostics = container,
                hasErrors = true,
                primaryMessage = MLang.Component.Editor.Error.ValidationFailed.format(detailMessage),
            )
        } catch (e: Exception) {
            Timber.w(e, "JSON analysis failed")
            return CodeAnalysisResult(
                diagnostics = container,
                hasErrors = true,
                primaryMessage =
                    MLang.Component.Editor.Error.ValidationFailed.format(
                        MLang.Component.Editor.Error.Unknown
                    ),
            )
        }

        return CodeAnalysisResult(diagnostics = container)
    }

    private fun parseJsonException(
        e: JSONException,
        content: String,
        detailMessage: String,
    ): DiagnosticRegion? {
        val message = e.message ?: return null
        val indexPattern = "character (\\d+)".toRegex()
        val match = indexPattern.find(message)

        val errorIndex = match?.groupValues?.get(1)?.toIntOrNull() ?: 0

        val safeIndex =
            if (content.isEmpty()) {
                0
            } else {
                errorIndex.coerceIn(0, content.length - 1)
            }
        val endIndex =
            if (content.isEmpty()) {
                0
            } else {
                (safeIndex + 1).coerceAtMost(content.length)
            }

        return DiagnosticRegion(
            safeIndex,
            endIndex,
            DiagnosticRegion.SEVERITY_ERROR,
            0,
            DiagnosticDetail(
                briefMessage = MLang.Component.Editor.Error.JsonSyntaxError,
                detailedMessage = detailMessage,
            ),
        )
    }

    private fun formatErrorMessage(message: String?): String {
        if (message.isNullOrBlank()) {
            return MLang.Component.Editor.Error.Unknown
        }

        return when {
            message.contains("Unterminated") -> MLang.Component.Editor.Error.Unterminated
            message.contains("Expected") -> {

                val expectedPattern = "Expected (\\S+)".toRegex()
                val expected =
                    expectedPattern.find(message)?.groupValues?.get(1)
                        ?: MLang.Component.Editor.Error.Unknown
                MLang.Component.Editor.Error.Expected.format(expected)
            }
            message.contains("No value") -> MLang.Component.Editor.Error.MissingValue
            message.contains("Duplicate") -> MLang.Component.Editor.Error.DuplicateKey
            else -> message
        }
    }

    private fun addDiagnostic(
        container: DiagnosticsContainer,
        start: Int,
        end: Int,
        briefMessage: String,
        detailedMessage: String,
    ) {
        container.addDiagnostic(
            DiagnosticRegion(
                start,
                end.coerceAtLeast(start),
                DiagnosticRegion.SEVERITY_ERROR,
                0,
                DiagnosticDetail(briefMessage = briefMessage, detailedMessage = detailedMessage),
            )
        )
    }
}
