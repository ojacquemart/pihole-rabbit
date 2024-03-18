package com.github.ojacquemart.pihole.rabbit.agent

import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

@WireMockTest
class PiHoleClientTest {

    @BeforeEach
    fun setUp() = Pihole.stubs()

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = ["password"])
    fun `should get a token from the pihole`(password: String?, runtimeInfo: WireMockRuntimeInfo) = runBlocking {
        val client = PiHoleClient(
            config = PiHoleConfig(
                baseUrl = "http://localhost:${runtimeInfo.httpPort}",
                password = password,
            )
        )

        assertNotNull(client.lazyToken.await())

        password
            ?.let { verify(1, postRequestedFor(urlEqualTo(PiHoleClient.LOGIN_FILE))) }
            ?: verify(1, getRequestedFor(urlEqualTo(PiHoleClient.INDEX_FILE)))
    }

    @Test
    fun `should get main groups`(runtimeInfo: WireMockRuntimeInfo) = runBlocking {
        val client = createClient(runtimeInfo)

        val groups = client.getGroups()
        assertEquals(4, groups.data.size)

        val names = groups.data.map { it.name }
        assertEquals(
            listOf(
                "Default",
                "foo",
                "bar",
                "qix",
            ),
            names
        )
    }

    @Test
    fun `should get clients groups`(runtimeInfo: WireMockRuntimeInfo) = runBlocking {
        val client = createClient(runtimeInfo)

        val clients = client.getClients()
        assertEquals(4, clients.data.size)

        val names = clients.data.map { it.comment }
        assertEquals(
            listOf(
                "media-player",
                "foo-macbook",
                "firetv",
                "console",
            ),
            names
        )
    }

    @Test
    fun `should get domains groups`(runtimeInfo: WireMockRuntimeInfo) = runBlocking {
        val client = createClient(runtimeInfo)

        val domains = client.getDomains()
        assertEquals(3, domains.data.size)

        val comments = domains.data.map { it.comment }
        assertEquals(
            listOf(
                "no-youtube",
                "no-twitch",
                "no-instagram",
            ),
            comments
        )
    }

    private fun createClient(runtimeInfo: WireMockRuntimeInfo) =
        PiHoleClient(
            config = PiHoleConfig(
                baseUrl = "http://localhost:${runtimeInfo.httpPort}",
                password = "password",
            )
        )

}