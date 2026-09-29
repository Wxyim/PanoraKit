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

package com.github.nomadboxlab.monadbox.core.bridge

import android.os.Build
import androidx.annotation.Keep
import com.github.nomadboxlab.monadbox.core.Global

@Keep
object Bridge {
    @Volatile private var loaded = false

    @Synchronized
    internal fun ensureLoaded() {
        if (loaded) {
            return
        }

        System.loadLibrary("bridge")

        val ctx = Global.application

        val home = ctx.filesDir.resolve("clash").apply { mkdirs() }.absolutePath
        val versionName =
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "unknown"
        val sdkVersion = Build.VERSION.SDK_INT

        nativeInit(home, versionName, sdkVersion)
        loaded = true
    }

    private external fun nativeInit(home: String, versionName: String, sdkVersion: Int)

    external fun nativeCompilePreview(requestJson: String): String

    external fun nativeCompileToFile(requestJson: String): String

    external fun nativeInspectCompiledConfig(yamlText: String): String?

    external fun nativeInspectCompiledGroups(
        yamlText: String,
        profileDir: String,
        excludeNotSelectable: Boolean,
    ): String?

    external fun nativeInspectSourceGroups(
        yamlText: String,
        profileDir: String,
        excludeNotSelectable: Boolean,
        includeGlobal: Boolean,
    ): String?

    external fun nativeLoadCompiledConfig(
        path: kotlinx.coroutines.CompletableDeferred<Unit>,
        configPath: String,
    )

    external fun nativeReset()

    external fun nativeForceGc()

    external fun nativeSuspend(suspend: Boolean)

    external fun nativeQueryTunnelState(): String

    external fun nativeQueryTrafficNow(): Long

    external fun nativeQueryTrafficTotal(): Long

    external fun nativeQueryTrafficSnapshot(): LongArray

    external fun nativeQueryRuntimeSnapshot(): String

    /**
     * Content hash of the payload [nativeQueryRuntimeSnapshot] would return.
     *
     * The payload is every proxy group plus providers and configuration, and the client polls it
     * every couple of seconds. An unchanged hash means unchanged payload text, so the caller can
     * keep the snapshot it already decoded instead of paying for the marshal, the JNI string copy
     * and the deserialization again.
     */
    external fun nativeQueryRuntimeSnapshotStamp(): Long

    external fun nativeQueryConnections(): String

    external fun nativeNotifyDnsChanged(dnsList: String)

    external fun nativeNotifyTimeZoneChanged(name: String, offset: Int)

    external fun nativeNotifyInstalledAppChanged(uidList: String)

    /** Returns 0 on success, non-zero when the native TUN stack failed to start. */
    external fun nativeStartTun(
        fd: Int,
        stack: String,
        gateway: String,
        portal: String,
        dns: String,
        cb: TunInterface,
    ): Int

    external fun nativeStopTun()

    external fun nativeStartRootTun(configJson: String): String?

    external fun nativeStopRootTun()

    external fun nativeStartHttp(listenAt: String): String?

    external fun nativeStopHttp()

    external fun nativeSubscribeLogcat(callback: LogcatInterface)

    /**
     * Detaches every log subscriber that [nativeSubscribeLogcat] registered.
     *
     * The native side keeps the callback alive until it is told to detach, so closing the Kotlin
     * channel alone used to leave a native subscriber behind that kept decoding every core log line
     * and delivered a duplicate copy of each line to the channels that were still open.
     */
    external fun nativeUnsubscribeLogcat()

    external fun nativeFetchAndValid(
        completable: FetchCallback,
        path: String,
        url: String,
        force: Boolean,
    )

    external fun nativeQueryProviders(): String

    external fun nativeUpdateProvider(
        completable: kotlinx.coroutines.CompletableDeferred<Unit>,
        type: String,
        name: String,
    )

    external fun nativeQueryConfiguration(): String

    external fun nativeSetCustomUserAgent(userAgent: String)

    external fun nativeQueryGroupNames(excludeNotSelectable: Boolean): String

    external fun nativeQueryGroup(name: String, sort: String): String?

    external fun nativeQueryGroups(excludeNotSelectable: Boolean, sort: String): String

    external fun nativeHealthCheck(
        completable: kotlinx.coroutines.CompletableDeferred<Unit>,
        name: String,
    )

    external fun nativeHealthCheckProxy(
        completable: kotlinx.coroutines.CompletableDeferred<String>,
        proxyName: String,
    )

    external fun nativeHealthCheckAll()

    external fun nativePatchSelector(selector: String, name: String): Boolean

    external fun nativeSetMode(mode: String): Boolean

    external fun nativeCloseConnection(id: String): Boolean

    external fun nativeCloseAllConnections()
}
