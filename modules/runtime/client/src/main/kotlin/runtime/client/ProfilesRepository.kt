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

package com.github.nomadboxlab.monadbox.runtime.client

import android.content.Context
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.remote.ServiceClient
import com.github.nomadboxlab.monadbox.remote.asRuntimeGatewayException
import com.github.nomadboxlab.monadbox.service.remote.IFetchObserver
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ProfilesRepository(
    private val context: Context,
    private val mutationCoordinator: RuntimeMutationCoordinator,
) : ProfilesProvider {
    override suspend fun createProfile(type: Profile.Type, name: String, source: String): UUID {
        Timber.d("Creating profile: type=$type, name=$name")
        return runGatewayCall("Failed to create profile") {
            ServiceClient.connect(context)
            ServiceClient.profile().create(type, name, source)
        }
    }

    override suspend fun cloneProfile(uuid: UUID): UUID {
        Timber.d("Cloning profile: uuid=$uuid")
        return runGatewayCall("Failed to clone profile") {
            ServiceClient.connect(context)
            ServiceClient.profile().clone(uuid)
        }
    }

    override suspend fun deleteProfile(uuid: UUID) {
        Timber.d("Deleting profile: uuid=$uuid")
        runGatewayCall("Failed to delete profile") {
            ServiceClient.connect(context)
            ServiceClient.profile()
                .queryByUUID(uuid)
                ?.takeIf { it.active }
                ?.let { activeProfile ->
                    ServiceClient.profile().clearActive(activeProfile)
                    mutationCoordinator.notifyActiveProfileChanged()
                }
            ServiceClient.profile().delete(uuid)
        }
    }

    override suspend fun queryAllProfiles(): List<Profile> {
        return withContext(Dispatchers.IO) { queryAllProfilesInternal() }
    }

    override suspend fun queryActiveProfile(ensureDefault: Boolean): Profile? {
        return withContext(Dispatchers.IO) {
            val activeProfile = queryActiveProfileInternal()
            if (activeProfile != null || !ensureDefault) {
                return@withContext activeProfile
            }

            val fallbackProfile =
                queryAllProfilesInternal().firstOrNull() ?: return@withContext null
            Timber.i(
                "Recovered missing active profile by selecting first profile: uuid=%s",
                fallbackProfile.uuid,
            )

            runGatewayCall("Failed to set default active profile") {
                ServiceClient.connect(context)
                ServiceClient.profile().setActive(fallbackProfile)
            }
            mutationCoordinator.notifyActiveProfileChanged()

            queryProfileByUUIDInternal(fallbackProfile.uuid) ?: fallbackProfile.copy(active = true)
        }
    }

    override suspend fun queryProfileByUUID(uuid: UUID): Profile? {
        return withContext(Dispatchers.IO) { queryProfileByUUIDInternal(uuid) }
    }

    override suspend fun setActiveProfile(uuid: UUID) {
        withContext(Dispatchers.IO) {
            val startedAt = System.currentTimeMillis()
            Timber.d("Setting active profile: uuid=$uuid")
            runGatewayCall("Failed to set active profile") {
                ServiceClient.connect(context)

                val previousActiveProfile = ServiceClient.profile().queryActive()
                val profile =
                    ServiceClient.profile().queryByUUID(uuid) ?: error("Profile not found: $uuid")

                ServiceClient.profile().setActive(profile)

                mutationCoordinator.notifyActiveProfileChanged()

                Timber.d(
                    "Active profile applied: uuid=$uuid cost=${System.currentTimeMillis() - startedAt}ms"
                )
                previousActiveProfile
            }
        }
    }

    override suspend fun clearActiveProfile(profile: Profile) {
        Timber.d("Clearing active profile: uuid=${profile.uuid}")
        runGatewayCall("Failed to clear active profile") {
            ServiceClient.connect(context)
            ServiceClient.profile().clearActive(profile)
        }

        mutationCoordinator.notifyActiveProfileChanged()
    }

    override suspend fun reorderProfiles(uuids: List<UUID>) {
        Timber.d("Reordering profiles: count=${uuids.size}")
        runGatewayCall("Failed to reorder profiles") {
            ServiceClient.connect(context)
            ServiceClient.profile().reorder(uuids)
        }
    }

    override suspend fun updateProfile(uuid: UUID, callback: IFetchObserver?) {
        Timber.d("Updating profile: uuid=$uuid")
        runGatewayCall("Failed to update profile") {
            ServiceClient.connect(context)
            ServiceClient.profile().update(uuid, callback)
            mutationCoordinator.notifyProfileContentChangedIfActive(uuid)
        }
    }

    override suspend fun patchProfile(uuid: UUID, name: String, source: String, interval: Long) {
        Timber.d("Patching profile: uuid=$uuid")
        runGatewayCall("Failed to patch profile") {
            ServiceClient.connect(context)
            ServiceClient.profile().patch(uuid, name, source, interval)
            mutationCoordinator.notifyProfileContentChangedIfActive(uuid)
        }
    }

    suspend fun queryAll(): List<Profile> {
        return queryAllProfiles()
    }

    suspend fun queryActive(): Profile? {
        return queryActiveProfile()
    }

    private suspend fun queryAllProfilesInternal(): List<Profile> {
        return runGatewayCall("Failed to query all profiles") {
            ServiceClient.connect(context)
            ServiceClient.profile().queryAll()
        }
    }

    private suspend fun queryActiveProfileInternal(): Profile? {
        return runGatewayCall("Failed to query active profile") {
            ServiceClient.connect(context)
            ServiceClient.profile().queryActive()
        }
    }

    private suspend fun queryProfileByUUIDInternal(uuid: UUID): Profile? {
        return runGatewayCall("Failed to query profile by uuid") {
            ServiceClient.connect(context)
            ServiceClient.profile().queryByUUID(uuid)
        }
    }

    private suspend inline fun <T> runGatewayCall(defaultMessage: String, block: () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            throw e.asRuntimeGatewayException(
                RuntimeGatewayErrorCode.CLIENT_OPERATION_FAILED,
                defaultMessage,
            )
        }
    }
}
