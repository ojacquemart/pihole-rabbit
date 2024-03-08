import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.githuh.pihole.rabbit.com.github.ojacquemart.pihle.rabbit.agent.PiHoleClient
import com.githuh.pihole.rabbit.com.github.ojacquemart.pihle.rabbit.agent.PiHoleConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

@WireMockTest
class PiHoleClientTest {

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

        stubFor(
            get(PiHoleClient.INDEX_FILE)
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBodyFile("index.php")
                )
        )
        stubFor(
            post(PiHoleClient.LOGIN_FILE)
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBodyFile("login.php")
                )
        )

        assertNotNull(client.lazyToken.await())

        password
            ?.let { verify(1, postRequestedFor(urlEqualTo(PiHoleClient.LOGIN_FILE))) }
            ?: verify(1, getRequestedFor(urlEqualTo(PiHoleClient.INDEX_FILE)))
    }

}