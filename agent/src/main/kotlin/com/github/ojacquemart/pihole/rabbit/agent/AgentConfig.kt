package com.github.pihole.rabbit.com.github.ojacquemart.pihole.rabbit.agent

import com.github.ojacquemart.pihole.rabbit.agent.PiHoleConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

data class AgentConfig(
    val pihole: PiHoleConfig,
    val supabase: SupabaseConfig,
) {

    val supabaseClient = createSupabaseClient(
        supabaseUrl = supabase.url,
        supabaseKey = supabase.key,
    ) {
        defaultLogLevel = LogLevel.DEBUG

        install(Postgrest)
        install(Realtime)
    }

}
