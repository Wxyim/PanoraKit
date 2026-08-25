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

package com.github.nomadboxlab.monadbox.service.runtime.util

import android.content.Context
import android.content.Intent
import com.github.nomadboxlab.monadbox.service.common.constants.Intents
import java.io.File
import java.util.*

val Context.importedDir: File
    get() = filesDir.resolve("imported")

val Context.processingDir: File
    get() = filesDir.resolve("processing")

/**
 * Latest modification time among the profile's real configuration files.
 *
 * Preview compilation and runtime starts write derived artifacts
 * (runtime.yaml and its .fingerprint sidecar) into the profile directory.
 * Counting them would make the profile's updatedAt/fingerprint drift on every
 * preview, invalidating the cached proxy-page preview and forcing a recompile
 * whenever the page is revisited. They are excluded so the timestamp only
 * changes when the user's actual config content changes.
 */
val File.directoryLastModified: Long?
    get() {
        return walk()
            .filter { file -> file.isFile && !file.isDerivedRuntimeArtifact() }
            .map { it.lastModified() }
            .maxOrNull()
    }

private fun File.isDerivedRuntimeArtifact(): Boolean {
    return name == "runtime.yaml" || name.endsWith(".fingerprint")
}

fun Context.sendBroadcastSelf(intent: Intent) {
    sendBroadcast(intent.setPackage(this.packageName))
}

fun Context.sendProfileChanged(uuid: UUID) {
    val intent =
        Intent(Intents.ACTION_PROFILE_CHANGED).putExtra(Intents.EXTRA_UUID, uuid.toString())

    sendBroadcastSelf(intent)
}

fun Context.sendProfileLoaded(uuid: UUID) {
    val intent = Intent(Intents.ACTION_PROFILE_LOADED).putExtra(Intents.EXTRA_UUID, uuid.toString())

    sendBroadcastSelf(intent)
}

fun Context.sendOverrideChanged() {
    val intent = Intent(Intents.ACTION_OVERRIDE_CHANGED)

    sendBroadcastSelf(intent)
}

fun Context.sendServiceRecreated() {
    sendBroadcastSelf(Intent(Intents.ACTION_SERVICE_RECREATED))
}

fun Context.sendClashStarted() {
    sendBroadcastSelf(Intent(Intents.ACTION_CLASH_STARTED))
}

fun Context.sendClashStopped(reason: String?) {
    sendBroadcastSelf(
        Intent(Intents.ACTION_CLASH_STOPPED).putExtra(Intents.EXTRA_STOP_REASON, reason)
    )
}
