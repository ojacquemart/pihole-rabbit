package com.github.pihole.rabbit

import com.github.ojacquemart.pihole.rabbit.agent.PiHoleConfig
import com.github.pihole.rabbit.com.github.ojacquemart.pihole.rabbit.agent.AgentConfig
import com.github.pihole.rabbit.com.github.ojacquemart.pihole.rabbit.agent.GroupsStore
import com.github.pihole.rabbit.com.github.ojacquemart.pihole.rabbit.agent.SupabaseConfig
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun main() {
    // TODO: try to generate a crontab and see if it could be executed
    // >> it needs crontab -l > /tmp/crontab.txt
    // >> create a file with the content of the new elements
    // >> crontab /tmp/crontab.txt to update the crontab
    // TODO: read a config file
    // TODO: think about a way to execute the main function forever (while true?)
    // TODO: try to generate a jar and test it on the Raspberry Pi
    // TODO: rework the groups assembler
    // TODO: investigate the possible use of dsl

    val piholeConfig = PiHoleConfig(
        baseUrl = "http://192.168.68.50",
        password = null,
    )
    val supabaseConfig = SupabaseConfig(
        url = "https://tbexvdlkfgpgwbcwahqq.supabase.co",
        key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRiZXh2ZGxrZmdwZ3diY3dhaHFxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDk2NzI1NzAsImV4cCI6MjAyNTI0ODU3MH0.9Z0zpnJYmkq8iWTfw7uljAk3fUTau4i52QM23gHyr-k",
    )
    val config = AgentConfig(
        pihole = piholeConfig,
        supabase = supabaseConfig,
    )

    val store = GroupsStore(config)
    store.upsert()

    println("Listening for changes...")

    val channel = config.supabaseClient.channel("foobarqix_foobaro")

    // TODO: create constants for the primary key and the table name and the schema
    val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
        table = "pihole_groups"
        filter("id", FilterOperator.EQ, 2)
    }

    coroutineScope {
        launch {
            changeFlow.collect {
                println("Change detected")
                // TODO: figure out to deserialize the record using the kotlin serialization library
                println(it.record)
            }
        }

        channel.subscribe()
    }
}
