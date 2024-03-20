package com.github.pihole.rabbit.com.github.ojacquemart.pihole.rabbit.agent

import com.github.ojacquemart.pihole.rabbit.agent.PiHoleConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

data class AgentConfig(
    val pihole: PiHoleConfig,
    val supabase: SupabaseConfig,
) {

    val supabaseClient = createSupabaseClient(
        supabaseUrl = supabase.url,
        supabaseKey = supabase.key,
    ) {
        install(Auth)
        install(Postgrest)
    }

}
