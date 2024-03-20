package com.github.pihole.rabbit.com.github.ojacquemart.pihole.rabbit.agent

import com.github.ojacquemart.pihole.rabbit.agent.GroupsViewAssembler
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

class GroupsStore(
    private val config: AgentConfig,
) {

    @Serializable
    data class GroupsTable(
        val id: Int? = null,
        val content: GroupsView,
    )

    companion object {
        const val TABLE_NAME = "pihole_groups"

        private val logger = LoggerFactory.getLogger(GroupsStore::class.java)
    }

    suspend fun upsert() {
        logger.info("Trying to upsert groups")

        val assembler = GroupsViewAssembler(config.pihole)
        val groups = assembler.assemble()

        logger.debug("Upserting groups: {}", groups)
        config.supabaseClient
            .from(TABLE_NAME)
            .upsert(GroupsTable(id = 1, content = groups))
    }
}
