package com.githuh.pihole.rabbit

import com.githuh.pihole.rabbit.com.github.ojacquemart.pihle.rabbit.agent.PiHoleClient
import com.githuh.pihole.rabbit.com.github.ojacquemart.pihle.rabbit.agent.PiHoleConfig

suspend fun main() {
    // TODO: try to generate a crontab and see if it could be executed
    // TODO: read a config file
    // TODO: think about a way to execute the main function forever (while true?)
    // TODO: try to generate a jar and test it on the Raspberry Pi

    val foo = PiHoleClient(
        PiHoleConfig(
            baseUrl = "http://192.168.68.50",
            password = null,
        )
    )
    println(foo.getGroups())
}