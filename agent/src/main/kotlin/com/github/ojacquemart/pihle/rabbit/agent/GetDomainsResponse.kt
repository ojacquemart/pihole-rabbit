package com.githuh.pihole.rabbit.com.github.ojacquemart.pihle.rabbit.agent

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
        val groups: List<Int>,
    )
}