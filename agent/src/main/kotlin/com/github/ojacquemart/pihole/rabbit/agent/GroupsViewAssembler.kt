package com.github.ojacquemart.pihole.rabbit.agent

import com.github.pihole.rabbit.com.github.ojacquemart.pihole.rabbit.agent.GroupsView
import org.slf4j.LoggerFactory

data class GroupsViewAssembler(
    val config: PiHoleConfig,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(GroupsViewAssembler::class.java)
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
                logger.trace("Building domain: {}", domain.id)

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
                    type = domain.type,
                    clients = clientsView,
                )
            }

        logger.trace("Domains view resolved: {}", domainsView)

        return GroupsView(domainsView)
    }
}
