package com.github.pihole.rabbit

import com.github.ojacquemart.pihole.rabbit.agent.PiHoleClient
import com.github.ojacquemart.pihole.rabbit.agent.PiHoleConfig

suspend fun main() {
    // TODO: try to generate a crontab and see if it could be executed
    // >> it needs crontab -l > /tmp/crontab.txt
    // >> create a file with the content of the new elements
    // >> crontab /tmp/crontab.txt to update the crontab
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
    println(foo.getClients())
    println(foo.getDomains())
}