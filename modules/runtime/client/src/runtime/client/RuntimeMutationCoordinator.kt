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

package com.github.yumelira.yumebox.runtime.client

import android.content.Context
import android.content.Intent
import com.github.yumelira.yumebox.remote.ServiceClient
import com.github.yumelira.yumebox.runtime.client.root.RootTunReloadDispatcher
import com.github.yumelira.yumebox.runtime.client.root.RootTunReloadReason
import com.github.yumelira.yumebox.service.common.constants.Intents
import com.github.yumelira.yumebox.service.common.util.appContextOrSelf
import com.github.yumelira.yumebox.service.root.RootTunStateStore
import java.util.UUID

internal fun shouldScheduleRootTunReloadForActiveProfile(
    isRootTunActive: Boolean,
    activeProfileId: UUID?,
    changedProfileId: UUID,
): Boolean = isRootTunActive && activeProfileId == changedProfileId

class RuntimeMutationCoordinator(
    context: Context,
    private val rootTunReloadDispatcher: RootTunReloadDispatcher,
) {
    private val appContext = context.appContextOrSelf
    private val rootTunStateStore by lazy { RootTunStateStore(appContext) }

    fun notifyActiveProfileChanged() {
        broadcastOverrideChanged()
        scheduleRootTunReloadIfActive(RootTunReloadReason.PROFILE_CHANGED)
    }

    fun notifyOverrideBindingsChanged() {
        broadcastOverrideChanged()
        scheduleRootTunReloadIfActive(RootTunReloadReason.PROFILE_OVERRIDE_CHANGED)
    }

    suspend fun notifyProfileContentChangedIfActive(profileId: UUID) {
        val rootTunActive = isRootTunActive()
        if (!rootTunActive) return

        ServiceClient.connect(appContext)
        val activeProfile = ServiceClient.profile().queryActive() ?: return
        if (
            shouldScheduleRootTunReloadForActiveProfile(
                isRootTunActive = rootTunActive,
                activeProfileId = activeProfile.uuid,
                changedProfileId = profileId,
            )
        ) {
            rootTunReloadDispatcher.schedule(RootTunReloadReason.PROFILE_CHANGED)
        }
    }

    private fun scheduleRootTunReloadIfActive(reason: RootTunReloadReason) {
        if (!isRootTunActive()) {
            return
        }
        rootTunReloadDispatcher.schedule(reason)
    }

    private fun broadcastOverrideChanged() {
        appContext.sendBroadcast(
            Intent(Intents.actionOverrideChanged(appContext.packageName))
                .setPackage(appContext.packageName)
        )
    }

    private fun isRootTunActive(): Boolean {
        val status = rootTunStateStore.snapshot()
        return status.state.isActive || status.runtimeReady
    }
}
