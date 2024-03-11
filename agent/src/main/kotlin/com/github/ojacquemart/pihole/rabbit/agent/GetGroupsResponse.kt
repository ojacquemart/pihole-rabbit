package com.github.ojacquemart.pihole.rabbit.agent

import kotlinx.serialization.Serializable

@Serializable
data class GetGroupsResponse(
    val data: List<Group>,
) {

    @Serializable
    data class Group(
        val id: Int,
        val enabled: Int,
        val name: String,
        val description: String?,
    )
}