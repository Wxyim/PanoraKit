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

package com.github.nomadboxlab.monadbox.core.model

import android.os.Parcel
import android.os.Parcelable
import com.github.nomadboxlab.monadbox.core.util.createListFromParcelSlice
import com.github.nomadboxlab.monadbox.core.util.writeToParcelSlice
import kotlinx.serialization.Serializable

@Serializable
data class ProxyGroup(
    val name: String = "",
    val type: Proxy.Type,
    val proxies: List<Proxy>,
    val now: String,
    val hidden: Boolean = false,
    val icon: String? = null,
) : Parcelable {
    class SliceProxyList(data: List<Proxy>) : List<Proxy> by data, Parcelable {
        constructor(parcel: Parcel) : this(Proxy.createListFromParcelSlice(parcel, 0, 50))

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            writeToParcelSlice(dest, flags)
        }

        companion object CREATOR : Parcelable.Creator<SliceProxyList> {
            override fun createFromParcel(parcel: Parcel): SliceProxyList {
                return SliceProxyList(parcel)
            }

            override fun newArray(size: Int): Array<SliceProxyList?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(
        parcel: Parcel
    ) : this(
        type = Proxy.Type.entries[parcel.readInt()],
        proxies = SliceProxyList(parcel),
        now = parcel.readString().orEmpty(),
        hidden = if (parcel.dataAvail() > 0) parcel.readInt() != 0 else false,
        icon = if (parcel.dataAvail() > 0) parcel.readString() else null,
        name = if (parcel.dataAvail() > 0) parcel.readString().orEmpty() else "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type.ordinal)
        SliceProxyList(proxies).writeToParcel(parcel, 0)
        parcel.writeString(now)
        parcel.writeInt(if (hidden) 1 else 0)
        parcel.writeString(icon)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProxyGroup> {
        override fun createFromParcel(parcel: Parcel): ProxyGroup {
            return ProxyGroup(parcel)
        }

        override fun newArray(size: Int): Array<ProxyGroup?> {
            return arrayOfNulls(size)
        }
    }
}
