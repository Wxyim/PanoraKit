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
 *
 */

package com.github.nomadboxlab.monadbox.feature.profiles.usecase

import android.app.Application
import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.core.model.CompileRequest
import com.github.nomadboxlab.monadbox.core.model.ConfigurationOverride
import com.github.nomadboxlab.monadbox.data.repository.ActiveProfileOverrideReloader
import com.github.nomadboxlab.monadbox.feature.editor.screen.ConfigPreviewSaveDecision
import com.github.nomadboxlab.monadbox.feature.editor.screen.ConfigPreviewSaveOutcome
import com.github.nomadboxlab.monadbox.feature.editor.screen.ConfigPreviewSavePhase
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionExecutor
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionFailurePresentation
import com.github.nomadboxlab.monadbox.presentation.runtime.getOrThrowHandled
import com.github.nomadboxlab.monadbox.runtime.client.ProfilesRepository
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import com.github.nomadboxlab.monadbox.service.runtime.util.sendProfileChanged
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import timber.log.Timber

class SaveProfileWithRollbackUseCase(
    private val application: Application,
    private val profilesRepository: ProfilesRepository,
    private val activeProfileOverrideReloader: ActiveProfileOverrideReloader,
    private val runtimeActionExecutor: RuntimeActionExecutor,
) {
    private companion object {
        const val REMOTE_FETCH_POLL_INTERVAL_MS = 120L
        const val LOCAL_UNVALIDATED_MARKER_FILE = ".local-unvalidated-profile"
        const val PROFILE_SAVE_STAGING_DIR = "profile-save-staging"
        const val PROFILE_SAVE_ROLLBACK_DIR = "profile-save-rollback"
    }

    private val profileGuiJson = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    suspend fun saveContent(
        uuid: UUID,
        content: String,
        onPhaseChanged: (ConfigPreviewSavePhase) -> Unit = {},
        decisionProvider: () -> ConfigPreviewSaveDecision = { ConfigPreviewSaveDecision.Continue },
        stopRuntime: suspend () -> Unit = {},
    ): ConfigPreviewSaveOutcome {
        val profile =
            profilesRepository.queryProfileByUUID(uuid) ?: error("Profile not found: $uuid")
        val liveConfigFile = resolveProfileConfigFile(uuid)
        val liveProfileDir =
            liveConfigFile.parentFile ?: error("Profile directory not found: $uuid")
        val stagingDir = createProfileSaveWorkspaceDirectory(uuid, PROFILE_SAVE_STAGING_DIR)
        var rollbackDir: File? = null

        try {
            cleanupOldProfileSaveWorkspaceDirectories(
                uuid = uuid,
                bucket = PROFILE_SAVE_STAGING_DIR,
                keep = stagingDir,
            )

            withContext(Dispatchers.IO) {
                if (liveProfileDir.exists()) {
                    liveProfileDir.copyRecursively(stagingDir, overwrite = true)
                } else {
                    stagingDir.mkdirs()
                }
            }

            val stagedConfigFile = stagingDir.resolve(liveConfigFile.name)

            onPhaseChanged(ConfigPreviewSavePhase.LocalSaving)
            withContext(Dispatchers.IO) {
                stagedConfigFile.parentFile?.mkdirs()
                stagedConfigFile.writeText(content)
            }

            onPhaseChanged(ConfigPreviewSavePhase.Validating)
            validateStagedProfileConfig(uuid, stagingDir, stagedConfigFile)

            if (profile.requiresRemoteResourceFetchForSave()) {
                onPhaseChanged(ConfigPreviewSavePhase.FetchingRemoteResources)
                when (decisionProvider()) {
                    ConfigPreviewSaveDecision.ContinueEditing -> {
                        return ConfigPreviewSaveOutcome.ResumeEditing
                    }

                    ConfigPreviewSaveDecision.SaveLocally -> {
                        return saveProfileConfigLocally(
                            uuid = uuid,
                            liveConfigFile = liveConfigFile,
                            content = content,
                            stopRuntime = stopRuntime,
                        )
                    }

                    ConfigPreviewSaveDecision.Continue -> Unit
                }

                val interruptedOutcome =
                    awaitRemoteResourceFetch(
                        uuid = uuid,
                        profile = profile,
                        stagingDir = stagingDir,
                        liveConfigFile = liveConfigFile,
                        content = content,
                        decisionProvider = decisionProvider,
                        stopRuntime = stopRuntime,
                    )
                if (interruptedOutcome != null) {
                    return interruptedOutcome
                }
            }

            rollbackDir = createProfileSaveWorkspaceDirectory(uuid, PROFILE_SAVE_ROLLBACK_DIR)
            cleanupOldProfileSaveWorkspaceDirectories(
                uuid = uuid,
                bucket = PROFILE_SAVE_ROLLBACK_DIR,
                keep = rollbackDir,
            )
            snapshotProfileDirectory(liveProfileDir, rollbackDir)

            commitStagedProfileDirectory(stagingDir, liveProfileDir)
            application.sendProfileChanged(uuid)

            if (!activeProfileOverrideReloader.reapplyIfActiveProfile(uuid.toString())) {
                Timber.w("Override reapply skipped for profile (missing configs?): %s", uuid)
            }

            onPhaseChanged(ConfigPreviewSavePhase.ApplyingRuntime)
            val runtimeInterruptedOutcome =
                awaitRuntimeReloadOrUndo(
                    uuid = uuid,
                    liveProfileDir = liveProfileDir,
                    rollbackDir = rollbackDir,
                    decisionProvider = decisionProvider,
                )
            if (runtimeInterruptedOutcome != null) {
                return runtimeInterruptedOutcome
            }
            setLocalUnvalidatedMarker(uuid, false)
            return ConfigPreviewSaveOutcome.Saved
        } finally {
            if (stagingDir.exists()) {
                stagingDir.deleteRecursively()
            }
            if (rollbackDir?.exists() == true) {
                rollbackDir.deleteRecursively()
            }
        }
    }

    suspend fun loadConfigForGui(uuid: UUID): ConfigurationOverride {
        val liveConfigFile = resolveProfileConfigFile(uuid)
        if (!liveConfigFile.exists()) {
            error(MLang.ProfilesPage.Message.ProfileFileNotExist)
        }

        return withContext(Dispatchers.IO) { parseProfileConfigYaml(liveConfigFile.readText()) }
    }

    fun hasGuiConfigChanges(
        originalConfig: ConfigurationOverride,
        updatedConfig: ConfigurationOverride,
    ): Boolean {
        return buildProfileGuiOverrideContent(originalConfig, updatedConfig) != "{}"
    }

    suspend fun saveGuiContent(
        uuid: UUID,
        originalConfig: ConfigurationOverride,
        updatedConfig: ConfigurationOverride,
        onPhaseChanged: (ConfigPreviewSavePhase) -> Unit = {},
        decisionProvider: () -> ConfigPreviewSaveDecision = { ConfigPreviewSaveDecision.Continue },
        stopRuntime: suspend () -> Unit = {},
    ): ConfigPreviewSaveOutcome {
        if (!hasGuiConfigChanges(originalConfig, updatedConfig)) {
            return ConfigPreviewSaveOutcome.Saved
        }

        val liveConfigFile = resolveProfileConfigFile(uuid)
        if (!liveConfigFile.exists()) {
            error(MLang.ProfilesPage.Message.ProfileFileNotExist)
        }

        val overrideContent = buildProfileGuiOverrideContent(originalConfig, updatedConfig)
        val updatedYaml =
            withContext(Dispatchers.IO) {
                applyProfileGuiDiffToYaml(
                    originalYaml = liveConfigFile.readText(),
                    diffJson = overrideContent,
                )
            }

        return saveContent(
            uuid = uuid,
            content = updatedYaml,
            onPhaseChanged = onPhaseChanged,
            decisionProvider = decisionProvider,
            stopRuntime = stopRuntime,
        )
    }

    private suspend fun validateStagedProfileConfig(
        uuid: UUID,
        profileDir: File,
        configFile: File,
    ) {
        val result =
            withContext(Dispatchers.Default) {
                Clash.compilePreview(
                    CompileRequest(
                        profileUuid = uuid.toString(),
                        profileDir = profileDir.absolutePath,
                        profilePath = configFile.absolutePath,
                        outputPath = profileDir.resolve("runtime.yaml").absolutePath,
                    )
                )
            }
        if (!result.success) {
            error(
                result.error?.takeIf { it.isNotBlank() } ?: MLang.Component.Editor.Error.SaveFailed
            )
        }
    }

    private suspend fun commitLocalConfigContent(configFile: File, content: String) {
        withContext(Dispatchers.IO) {
            configFile.parentFile?.mkdirs()
            configFile.writeText(content)
        }
    }

    private suspend fun saveProfileConfigLocally(
        uuid: UUID,
        liveConfigFile: File,
        content: String,
        stopRuntime: suspend () -> Unit,
    ): ConfigPreviewSaveOutcome {
        commitLocalConfigContent(liveConfigFile, content)
        setLocalUnvalidatedMarker(uuid, true)
        stopRuntime()
        application.sendProfileChanged(uuid)
        return ConfigPreviewSaveOutcome.SavedLocally
    }

    private suspend fun commitStagedProfileDirectory(stagingDir: File, liveProfileDir: File) {
        withContext(Dispatchers.IO) {
            if (liveProfileDir.exists()) {
                liveProfileDir.deleteRecursively()
            }
            stagingDir.copyRecursively(liveProfileDir, overwrite = true)
        }
    }

    private suspend fun snapshotProfileDirectory(sourceDir: File, snapshotDir: File) {
        withContext(Dispatchers.IO) {
            if (snapshotDir.exists()) {
                snapshotDir.deleteRecursively()
            }
            snapshotDir.mkdirs()
            if (sourceDir.exists()) {
                sourceDir.copyRecursively(snapshotDir, overwrite = true)
            }
        }
    }

    private suspend fun restoreProfileDirectory(snapshotDir: File, liveProfileDir: File) {
        withContext(Dispatchers.IO) {
            if (liveProfileDir.exists()) {
                liveProfileDir.deleteRecursively()
            }
            snapshotDir.copyRecursively(liveProfileDir, overwrite = true)
        }
    }

    private suspend fun awaitRemoteResourceFetch(
        uuid: UUID,
        profile: Profile,
        stagingDir: File,
        liveConfigFile: File,
        content: String,
        decisionProvider: () -> ConfigPreviewSaveDecision,
        stopRuntime: suspend () -> Unit,
    ): ConfigPreviewSaveOutcome? = coroutineScope {
        val remoteFetch =
            async(Dispatchers.IO) {
                Clash.fetchAndValid(stagingDir, profile.source, false) {}.await()
            }

        while (!remoteFetch.isCompleted) {
            when (decisionProvider()) {
                ConfigPreviewSaveDecision.ContinueEditing -> {
                    remoteFetch.cancel()
                    runCatching { remoteFetch.await() }
                    return@coroutineScope ConfigPreviewSaveOutcome.ResumeEditing
                }

                ConfigPreviewSaveDecision.SaveLocally -> {
                    remoteFetch.cancel()
                    runCatching { remoteFetch.await() }
                    return@coroutineScope saveProfileConfigLocally(
                        uuid = uuid,
                        liveConfigFile = liveConfigFile,
                        content = content,
                        stopRuntime = stopRuntime,
                    )
                }

                ConfigPreviewSaveDecision.Continue -> delay(REMOTE_FETCH_POLL_INTERVAL_MS)
            }
        }

        remoteFetch.await()
        null
    }

    private suspend fun awaitRuntimeReloadOrUndo(
        uuid: UUID,
        liveProfileDir: File,
        rollbackDir: File,
        decisionProvider: () -> ConfigPreviewSaveDecision,
    ): ConfigPreviewSaveOutcome? = coroutineScope {
        val runtimeReload = async {
            runtimeActionExecutor
                .reloadIfActiveProfile(
                    operation = "profiles:save-config",
                    profileId = uuid,
                    presentation =
                        RuntimeActionFailurePresentation.Runtime(
                            fallbackMessage = MLang.ProfilesVM.Error.Unknown
                        ),
                )
                .getOrThrowHandled()
        }

        while (!runtimeReload.isCompleted) {
            when (decisionProvider()) {
                ConfigPreviewSaveDecision.ContinueEditing -> {
                    runtimeReload.cancel()
                    runCatching { runtimeReload.await() }
                    rollbackCommittedProfileSave(uuid, rollbackDir, liveProfileDir)
                    return@coroutineScope ConfigPreviewSaveOutcome.ResumeEditing
                }

                ConfigPreviewSaveDecision.SaveLocally,
                ConfigPreviewSaveDecision.Continue -> delay(REMOTE_FETCH_POLL_INTERVAL_MS)
            }
        }

        runtimeReload.await()
        null
    }

    private suspend fun rollbackCommittedProfileSave(
        uuid: UUID,
        rollbackDir: File,
        liveProfileDir: File,
    ) {
        restoreProfileDirectory(rollbackDir, liveProfileDir)
        application.sendProfileChanged(uuid)
        if (!activeProfileOverrideReloader.reapplyIfActiveProfile(uuid.toString())) {
            Timber.w("Override reapply skipped during save rollback: %s", uuid)
        }
        runtimeActionExecutor
            .reloadIfActiveProfile(
                operation = "profiles:undo-save-config",
                profileId = uuid,
                presentation =
                    RuntimeActionFailurePresentation.Runtime(
                        fallbackMessage = MLang.ProfilesVM.Error.Unknown
                    ),
            )
            .getOrThrowHandled()
    }

    private fun buildProfileGuiOverrideContent(
        originalConfig: ConfigurationOverride,
        updatedConfig: ConfigurationOverride,
    ): String {
        val originalElement =
            profileGuiJson.encodeToJsonElement(ConfigurationOverride.serializer(), originalConfig)
        val updatedElement =
            profileGuiJson.encodeToJsonElement(ConfigurationOverride.serializer(), updatedConfig)
        val diffElement = diffJsonElement(originalElement, updatedElement) ?: JsonObject(emptyMap())
        return profileGuiJson.encodeToString(JsonElement.serializer(), diffElement)
    }

    private fun diffJsonElement(original: JsonElement?, updated: JsonElement?): JsonElement? {
        if (original == updated) {
            return null
        }

        return when {
            original is JsonObject && updated is JsonObject -> {
                val keys = original.keys + updated.keys
                val changed =
                    buildMap<String, JsonElement> {
                        keys.forEach { key ->
                            diffJsonElement(original[key], updated[key])?.let { put(key, it) }
                        }
                    }
                changed.takeIf(Map<String, JsonElement>::isNotEmpty)?.let(::JsonObject)
            }

            original is JsonArray && updated is JsonArray -> updated
            updated == null -> JsonNull
            else -> updated
        }
    }

    private fun parseProfileConfigYaml(yamlText: String): ConfigurationOverride {
        val rootElement = yamlValueToJsonElement(loadProfileConfigYamlRoot(yamlText))
        val configElement =
            if (rootElement is JsonObject) {
                sanitizeJsonElementForSerializer(rootElement, ConfigurationOverride.serializer())
                    as? JsonObject ?: JsonObject(emptyMap())
            } else {
                JsonObject(emptyMap())
            }

        return runCatching {
                profileGuiJson.decodeFromJsonElement(
                    ConfigurationOverride.serializer(),
                    configElement,
                )
            }
            .getOrElse { error ->
                Clash.inspectCompiledConfig(yamlText)
                    ?: throw IllegalStateException(
                        error.message ?: MLang.ProfilesPage.Message.ReadProfileFailed,
                        error,
                    )
            }
    }

    private fun <T> sanitizeJsonElementForSerializer(
        element: JsonElement,
        serializer: KSerializer<T>,
    ): JsonElement? = sanitizeJsonElementForDescriptor(element, serializer.descriptor)

    private fun sanitizeJsonElementForDescriptor(
        element: JsonElement,
        descriptor: SerialDescriptor,
    ): JsonElement? {
        if (element is JsonNull) {
            return JsonNull
        }
        if (descriptor.serialName.startsWith("kotlinx.serialization.json.")) {
            return element
        }

        return when (descriptor.kind) {
            StructureKind.CLASS,
            StructureKind.OBJECT -> sanitizeJsonObject(element as? JsonObject, descriptor)

            StructureKind.LIST ->
                sanitizeJsonArray(
                    element = element as? JsonArray,
                    elementDescriptor = descriptor.getElementDescriptor(0),
                )

            StructureKind.MAP ->
                sanitizeJsonMap(
                    element = element as? JsonObject,
                    valueDescriptor = descriptor.getElementDescriptor(1),
                )

            SerialKind.ENUM -> (element as? JsonPrimitive)?.let { JsonPrimitive(it.content) }
            PrimitiveKind.STRING -> (element as? JsonPrimitive)?.let { JsonPrimitive(it.content) }
            PrimitiveKind.BOOLEAN ->
                (element as? JsonPrimitive)?.booleanOrNull?.let { value -> JsonPrimitive(value) }

            PrimitiveKind.BYTE,
            PrimitiveKind.SHORT,
            PrimitiveKind.INT ->
                (element as? JsonPrimitive)?.intOrNull?.let { value -> JsonPrimitive(value) }

            PrimitiveKind.LONG ->
                (element as? JsonPrimitive)?.longOrNull?.let { value -> JsonPrimitive(value) }

            PrimitiveKind.FLOAT,
            PrimitiveKind.DOUBLE ->
                (element as? JsonPrimitive)?.doubleOrNull?.let { value -> JsonPrimitive(value) }

            PrimitiveKind.CHAR ->
                (element as? JsonPrimitive)?.content?.singleOrNull()?.let { value ->
                    JsonPrimitive(value.toString())
                }

            else -> element.takeIf { it is JsonPrimitive }
        }
    }

    private fun sanitizeJsonObject(
        element: JsonObject?,
        descriptor: SerialDescriptor,
    ): JsonElement? {
        if (element == null) {
            return null
        }

        val content =
            buildMap<String, JsonElement> {
                for (index in 0 until descriptor.elementsCount) {
                    val key = descriptor.getElementName(index)
                    val childElement = element[key] ?: continue
                    sanitizeJsonElementForDescriptor(
                            element = childElement,
                            descriptor = descriptor.getElementDescriptor(index),
                        )
                        ?.let { sanitizedChild -> put(key, sanitizedChild) }
                }
            }

        if (content.isEmpty() && element.isNotEmpty()) {
            return null
        }
        return JsonObject(content)
    }

    private fun sanitizeJsonArray(
        element: JsonArray?,
        elementDescriptor: SerialDescriptor,
    ): JsonElement? {
        if (element == null) {
            return null
        }

        val content =
            element.mapNotNull { childElement ->
                sanitizeJsonElementForDescriptor(childElement, elementDescriptor)
            }

        if (content.isEmpty() && element.isNotEmpty()) {
            return null
        }
        return JsonArray(content)
    }

    private fun sanitizeJsonMap(
        element: JsonObject?,
        valueDescriptor: SerialDescriptor,
    ): JsonElement? {
        if (element == null) {
            return null
        }

        val content =
            buildMap<String, JsonElement> {
                element.forEach { (key, childElement) ->
                    sanitizeJsonElementForDescriptor(childElement, valueDescriptor)?.let {
                        put(key, it)
                    }
                }
            }

        if (content.isEmpty() && element.isNotEmpty()) {
            return null
        }
        return JsonObject(content)
    }

    private fun applyProfileGuiDiffToYaml(originalYaml: String, diffJson: String): String {
        val diffElement = profileGuiJson.parseToJsonElement(diffJson)
        if (diffElement !is JsonObject || diffElement.isEmpty()) {
            return originalYaml
        }

        val root = loadProfileConfigYamlRoot(originalYaml)
        applyJsonDiffToYamlMap(root, diffElement)
        return dumpProfileConfigYaml(root)
    }

    private fun loadProfileConfigYamlRoot(yamlText: String): MutableMap<String, Any?> {
        val loaded =
            yamlText.takeIf { it.isNotBlank() }?.let { Yaml().load<Any?>(it) }
                ?: emptyMap<String, Any?>()
        val mutableValue = toMutableYamlValue(loaded)
        @Suppress("UNCHECKED_CAST")
        return when (mutableValue) {
            is MutableMap<*, *> -> mutableValue as MutableMap<String, Any?>
            else -> linkedMapOf()
        }
    }

    private fun toMutableYamlValue(value: Any?): Any? {
        return when (value) {
            is Map<*, *> -> {
                linkedMapOf<String, Any?>().apply {
                    value.forEach { (key, childValue) ->
                        key?.toString()?.let { put(it, toMutableYamlValue(childValue)) }
                    }
                }
            }

            is List<*> ->
                value.mapTo(mutableListOf()) { childValue -> toMutableYamlValue(childValue) }
            else -> value
        }
    }

    private fun yamlValueToJsonElement(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is Map<*, *> -> {
                val content =
                    buildMap<String, JsonElement> {
                        value.forEach { (key, childValue) ->
                            key?.toString()?.let { put(it, yamlValueToJsonElement(childValue)) }
                        }
                    }
                JsonObject(content)
            }

            is List<*> -> JsonArray(value.map(::yamlValueToJsonElement))
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Int -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is Short -> JsonPrimitive(value.toInt())
            is Byte -> JsonPrimitive(value.toInt())
            else -> JsonPrimitive(value.toString())
        }
    }

    private fun applyJsonDiffToYamlMap(target: MutableMap<String, Any?>, diff: JsonObject) {
        diff.forEach { (key, value) ->
            when (value) {
                JsonNull -> target.remove(key)

                is JsonObject -> {
                    val currentValue = toMutableYamlValue(target[key])
                    @Suppress("UNCHECKED_CAST")
                    val childMap =
                        when (currentValue) {
                            is MutableMap<*, *> -> currentValue as MutableMap<String, Any?>
                            else -> linkedMapOf()
                        }
                    applyJsonDiffToYamlMap(childMap, value)
                    if (childMap.isEmpty()) {
                        target.remove(key)
                    } else {
                        target[key] = childMap
                    }
                }

                else -> target[key] = jsonElementToYamlValue(value)
            }
        }
    }

    private fun jsonElementToYamlValue(element: JsonElement): Any? {
        return when (element) {
            JsonNull -> null
            is JsonObject -> {
                linkedMapOf<String, Any?>().apply {
                    element.forEach { (key, childElement) ->
                        put(key, jsonElementToYamlValue(childElement))
                    }
                }
            }

            is JsonArray -> {
                element.mapTo(mutableListOf()) { childElement ->
                    jsonElementToYamlValue(childElement)
                }
            }
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.booleanOrNull != null -> element.booleanOrNull
                    element.longOrNull != null -> {
                        val longValue = element.longOrNull!!
                        if (longValue in Int.MIN_VALUE..Int.MAX_VALUE) {
                            longValue.toInt()
                        } else {
                            longValue
                        }
                    }

                    element.doubleOrNull != null -> element.doubleOrNull
                    else -> element.content
                }
            }
        }
    }

    private fun dumpProfileConfigYaml(root: MutableMap<String, Any?>): String {
        if (root.isEmpty()) {
            return ""
        }

        val dumperOptions =
            DumperOptions().apply {
                defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                indent = 4
                indicatorIndent = 2
                isPrettyFlow = true
                splitLines = false
            }
        return Yaml(dumperOptions).dump(root).trimEnd() + "\n"
    }

    private fun createProfileSaveWorkspaceDirectory(uuid: UUID, bucket: String): File {
        val root = application.cacheDir.resolve(bucket)
        val dir = root.resolve("${uuid}-${System.currentTimeMillis()}")
        dir.mkdirs()
        return dir
    }

    private fun cleanupOldProfileSaveWorkspaceDirectories(uuid: UUID, bucket: String, keep: File) {
        val root = application.cacheDir.resolve(bucket)
        root
            .listFiles()
            ?.filter { it != keep && it.name.startsWith("$uuid-") }
            ?.forEach { runCatching { it.deleteRecursively() } }
    }

    private fun Profile.requiresRemoteResourceFetchForSave(): Boolean {
        return type == Profile.Type.Url
    }

    private fun getImportedProfileDirectory(uuid: UUID): File {
        return File(application.filesDir, "imported/$uuid")
    }

    private fun getClashProfileDirectory(uuid: UUID): File {
        return File(application.filesDir, "clash/profiles/$uuid")
    }

    private fun resolveProfileConfigFile(uuid: UUID): File {
        val importedFile = getImportedProfileDirectory(uuid).resolve("config.yaml")
        return if (importedFile.exists()) {
            importedFile
        } else {
            getClashProfileDirectory(uuid).resolve("config.yaml")
        }
    }

    private fun resolveLocalUnvalidatedMarkerFile(uuid: UUID): File {
        val configFile = resolveProfileConfigFile(uuid)
        val profileDir = configFile.parentFile ?: getImportedProfileDirectory(uuid)
        return profileDir.resolve(LOCAL_UNVALIDATED_MARKER_FILE)
    }

    private suspend fun setLocalUnvalidatedMarker(uuid: UUID, needed: Boolean) {
        withContext(Dispatchers.IO) {
            val markerFile = resolveLocalUnvalidatedMarkerFile(uuid)
            if (needed) {
                markerFile.parentFile?.mkdirs()
                if (!markerFile.exists()) {
                    markerFile.writeText("pending-remote-validation")
                }
            } else if (markerFile.exists()) {
                markerFile.delete()
            }
        }
    }
}
