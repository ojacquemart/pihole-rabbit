package com.github.ojacquemart.pihole.rabbit.agent

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