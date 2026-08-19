/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 */

package com.github.nomadboxlab.monadbox.service.runtime.records

import com.github.nomadboxlab.monadbox.core.model.ProxyGroup
import com.github.nomadboxlab.monadbox.service.runtime.entity.Selection

/**
 * Applies the app's persisted selector state to a runtime snapshot.
 *
 * mihomo can briefly report the config default while providers are still
 * resolving or while a startup selector restore is being applied. The
 * persisted value is the authoritative value for the app during that
 * transition, so every runtime read path must use the same projection.
 */
internal object SelectionPresentation {
    fun apply(groups: List<ProxyGroup>, selections: List<Selection>): List<ProxyGroup> {
        if (groups.isEmpty() || selections.isEmpty()) return groups

        val selectedByGroup =
            selections
                .asSequence()
                .map { it.proxy.trim() to it.selected.trim() }
                .filter { (group, selected) -> group.isNotEmpty() && selected.isNotEmpty() }
                .toMap()

        if (selectedByGroup.isEmpty()) return groups

        return groups.map { group ->
            val selected = selectedByGroup[group.name.trim()] ?: return@map group
            val isValid =
                group.proxies.isEmpty() ||
                    group.proxies.any { proxy -> proxy.name.trim() == selected }
            if (isValid && group.now.trim() != selected) group.copy(now = selected) else group
        }
    }
}
