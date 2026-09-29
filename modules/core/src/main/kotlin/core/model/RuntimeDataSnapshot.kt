package com.github.nomadboxlab.monadbox.core.model

import kotlinx.serialization.Serializable

@Serializable
data class RuntimeDataSnapshot(
    val configuration: UiConfiguration = UiConfiguration(),
    val providers: List<Provider> = emptyList(),
    val proxyGroups: List<ProxyGroup> = emptyList(),
)
