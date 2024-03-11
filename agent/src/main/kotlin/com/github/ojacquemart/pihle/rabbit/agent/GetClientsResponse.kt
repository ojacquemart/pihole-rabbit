package com.githuh.pihole.rabbit.com.github.ojacquemart.pihle.rabbit.agent

import kotlinx.serialization.Serializable

@Serializable
data class GetClientsResponse(
    val data: List<Client>,
) {

    @Serializable
    data class Client(
        val id: String,
        val comment: String,
        val groups: List<Int>,
    )
}