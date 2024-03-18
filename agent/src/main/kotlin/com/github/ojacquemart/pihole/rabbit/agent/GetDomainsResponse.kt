package com.github.ojacquemart.pihole.rabbit.agent

import kotlinx.serialization.Serializable

@Serializable
data class GetDomainsResponse(
    val data: List<Domain>,
) {

    @Serializable
    data class Domain(
        val id: Int,
        val type: Int,
        val domain: String,
        val comment: String?,
        val enabled: Int,
        val groups: List<Int>,
    )
}