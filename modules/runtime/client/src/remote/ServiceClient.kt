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

package com.github.nomadboxlab.monadbox.remote

import android.content.Context
import android.os.Build
import com.github.nomadboxlab.monadbox.service.ClashManager
import com.github.nomadboxlab.monadbox.service.ProfileManager
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.github.nomadboxlab.monadbox.service.common.util.initializeServiceGlobal
import com.github.nomadboxlab.monadbox.service.remote.IClashManager
import com.github.nomadboxlab.monadbox.service.remote.IProfileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

object ServiceClient {
    private val mutex = Mutex()
    private var initialized = false
    private var localClashManager: ClashManager? = null
    private var runtimeClashManager: RuntimeClashManager? = null
    private var clashManager: IClashManager? = null
    private var profileManager: IProfileManager? = null

    suspend fun connect(ctx: Context) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val appContext = ctx.appContextOrSelf
                if (initialized && clashManager != null && profileManager != null) {
                    return@withLock
                }

                val startedAt = System.currentTimeMillis()

                try {
                    initializeServiceGlobal(appContext)
                    val local = ClashManager(appContext)
                    val runtime = RuntimeClashManager(appContext, local)
                    localClashManager = local
                    runtimeClashManager = runtime
                    clashManager = runtime
                    profileManager = ProfileManager(appContext)
                    initialized = true
                    val processName =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            android.app.Application.getProcessName()
                        } else {
                            appContext.packageName
                        }
                    Timber.d(
                        "ServiceClient gateway initialized in pid=${android.os.Process.myPid()}, process=$processName, cost=${System.currentTimeMillis() - startedAt}ms"
                    )
                } catch (e: Exception) {
                    runtimeClashManager?.close()
                    runtimeClashManager = null
                    localClashManager?.close()
                    localClashManager = null
                    initialized = false
                    clashManager = null
                    profileManager = null
                    Timber.e(e, "Failed to initialize local service gateway")
                    throw RuntimeGatewayException(
                        code = RuntimeGatewayErrorCode.CLIENT_INIT_FAILED,
                        message = "Failed to initialize local service gateway",
                        cause = e,
                    )
                }
            }
        }
    }

    suspend fun disconnect() {
        mutex.withLock {
            runtimeClashManager?.close()
            runtimeClashManager = null
            localClashManager?.close()
            localClashManager = null
            clashManager = null
            profileManager = null
            initialized = false
        }
    }

    suspend fun clash(): IClashManager {
        return clashManager
            ?: throw RuntimeGatewayException(
                code = RuntimeGatewayErrorCode.CLIENT_NOT_CONNECTED,
                message = "ServiceClient not connected",
            )
    }

    suspend fun profile(): IProfileManager {
        return profileManager
            ?: throw RuntimeGatewayException(
                code = RuntimeGatewayErrorCode.CLIENT_NOT_CONNECTED,
                message = "ServiceClient not connected",
            )
    }

    fun isConnected(): Boolean = initialized && clashManager != null && profileManager != null
}
