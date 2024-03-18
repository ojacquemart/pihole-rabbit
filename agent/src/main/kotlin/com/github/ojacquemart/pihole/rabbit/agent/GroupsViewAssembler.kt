package com.github.ojacquemart.pihole.rabbit.agent

import org.slf4j.LoggerFactory

data class GroupsViewAssembler(
    val config: PiHoleConfig,
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(GroupsViewAssembler::class.java)
    }

    private val client = PiHoleClient(config)

    suspend fun assemble(): GroupsView {
        // TODO: try to call those three methods in parallel
        val groups = client.getGroups()
        val clients = client.getClients()
        val domains = client.getDomains()

        val groupsById = groups.data
            .associateBy { it.id }
        val groupsOfClients = clients.data
            .flatMap { it.groups }
            .mapNotNull { groupsById[it] }
            .distinct()

        val domainsView = domains.data
            .map { domain ->
                LOGGER.trace("Building domain: {}", domain.id)

                val clientsView = groupsOfClients
                    .map { client ->
                        GroupsView.ClientView(
                            id = client.id,
                            name = client.name,
                            enabled = domain.groups.contains(client.id),
                        )
                    }

                GroupsView.DomainView(
                    id = domain.id,
                    name = domain.comment,
                    enabled = domain.enabled == 1,
                    clients = clientsView,
                )
            }

        LOGGER.trace("Domain views resolved: {}", domainsView)

        return GroupsView(domainsView)
    }
}

data class GroupsView(
    val domains: List<DomainView>,
) {
    data class ClientView(
        val id: Int,
        val name: String,
        val enabled: Boolean,
    )

    data class DomainView(
        val id: Int,
        val name: String?,
        val enabled: Boolean?,
        val clients: List<ClientView>,
    )
}
