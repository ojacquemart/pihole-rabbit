package com.github.ojacquemart.pihole.rabbit.agent

import com.github.tomakehurst.wiremock.client.WireMock

object Pihole {
    fun stubs() {
        WireMock.stubFor(
            WireMock.get(PiHoleClient.INDEX_FILE)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withBodyFile("index.php")
                )
        )

        WireMock.stubFor(
            WireMock.post(PiHoleClient.LOGIN_FILE)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withBodyFile("login.php")
                )
        )

        stubGroups()
    }

    private fun stubGroups() {
        stubGroups("get_groups")
        stubGroups("get_clients")
        stubGroups("get_domains")
    }

    private fun stubGroups(action: String) {
        WireMock.stubFor(
            WireMock.post(PiHoleClient.GROUPS_FILE)
                .withFormParam("action", WireMock.equalTo(action))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("groups_$action.php")
                )
        )
    }
}