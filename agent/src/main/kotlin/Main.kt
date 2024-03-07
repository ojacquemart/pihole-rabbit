package com.githuh.pihole.rabbit

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GetGroupsResponse(
    val data: List<Group>,
) {

    @Serializable
    data class Group(
        val id: Int,
        val enabled: Int,
        val name: String,
        val description: String?,
    )
}

class PiHoleConfig(
    val baseUrl: String,
    val password: String? = null,
)

class PiHoleClient(
    private val config: PiHoleConfig,
) {

    private val client = HttpClient(CIO) {
        followRedirects = true

        install(HttpRedirect) {
            // needed to follow the redirect after a successful login request
            checkHttpMethod = false
        }

        install(HttpCookies) {
            // needed to pass the "PHPSESSID" cookie through the requests
            storage = AcceptAllCookiesStorage()
        }

        install(ContentNegotiation) {
            // needed to parse the JSON responses
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val lazyToken = GlobalScope.async(Dispatchers.Unconfined, start = CoroutineStart.LAZY) {
        getToken()
    }

    private suspend fun getToken(): String {
        val response: HttpResponse = client.get("${config.baseUrl}/admin/index.php")

        val body = response.bodyAsText()
        if (body.contains("login-card")) {
            return getTokenAfterLogin()
        }

        return extractToken(body)
    }

    private suspend fun getTokenAfterLogin(): String {
        val loginResponse = client.submitForm(
            url = "${config.baseUrl}/admin/login.php",
            formParameters = parameters {
                append("pw", config.password)
            }
        )

        return extractToken(loginResponse.bodyAsText())
    }

    private fun StringValuesBuilder.append(name: String, value: String?) {
        value?.let { append(name, it) }
    }

    private fun extractToken(body: String): String {
        val tokenDiv = """<div id="token" hidden>"""
        val indexOfTokenDiv = body.indexOf(tokenDiv) + tokenDiv.length
        val indexOfEndTokenDiv = body.substring(indexOfTokenDiv).indexOf("</div>")
        val token = body.substring(indexOfTokenDiv, indexOfTokenDiv + indexOfEndTokenDiv)

        return token
    }

    suspend fun getGroups(): GetGroupsResponse {
        val token = lazyToken.await()

        val groups = client.submitForm(
            url = "${config.baseUrl}/admin/scripts/pi-hole/php/groups.php",
            formParameters = parameters {
                append("action", "get_groups")
                append("token", token)
            }
        )

        return groups.body<GetGroupsResponse>()
    }
}

suspend fun main() {
    // TODO: check about this SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
    // TODO: write tests
    // TODO: try to generate a jar and test it on the Raspberry Pi

    val foo = PiHoleClient(
        PiHoleConfig(
            baseUrl = "http://192.168.68.50",
            password = null,
        )
    )
    println(foo.getGroups())
}