package com.github.pihole.rabbit.com.github.ojacquemart.pihole.rabbit.agent

import kotlinx.serialization.Serializable

@Serializable
data class GroupsView(
    val domains: List<DomainView>,
) {
    @Serializable
    data class DomainView(
        val id: Int,
        val name: String?,
        val type: Int?,
        val enabled: Boolean?,
        val clients: List<ClientView>,
    )

    @Serializable
    data class ClientView(
        val id: Int,
        val name: String,
        val enabled: Boolean,
    )
}
