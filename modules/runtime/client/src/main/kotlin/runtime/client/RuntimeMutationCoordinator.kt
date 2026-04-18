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

package com.github.nomadboxlab.monadbox.runtime.client

import android.content.Context
import android.content.Intent
import com.github.nomadboxlab.monadbox.remote.ServiceClient
import com.github.nomadboxlab.monadbox.runtime.client.root.RootTunReloadDispatcher
import com.github.nomadboxlab.monadbox.runtime.client.root.RootTunReloadReason
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeOverrideChangeNotifier
import com.github.nomadboxlab.monadbox.service.common.constants.Intents
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.github.nomadboxlab.monadbox.service.root.RootTunRuntimeRecovery
import com.github.nomadboxlab.monadbox.service.root.RootTunStateStore
import java.util.UUID

internal fun shouldScheduleRootTunReloadForActiveProfile(
    isRootTunActive: Boolean,
    activeProfileId: UUID?,
    changedProfileId: UUID,
): Boolean = isRootTunActive && activeProfileId == changedProfileId

class RuntimeMutationCoordinator(
    context: Context,
    private val rootTunReloadDispatcher: RootTunReloadDispatcher,
) : RuntimeOverrideChangeNotifier {
    private val appContext = context.appContextOrSelf
    private val rootTunStateStore by lazy { RootTunStateStore(appContext) }

    fun notifyActiveProfileChanged() {
        broadcastOverrideChanged()
        scheduleRootTunReloadIfActive(RootTunReloadReason.PROFILE_CHANGED)
    }

    override fun notifyOverrideBindingsChanged() {
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
        val status =
            RootTunRuntimeRecovery.recoverStaleTransition(
                context = appContext,
                status = rootTunStateStore.snapshot(),
            )
        return status.state.isActive || status.runtimeReady
    }
}
