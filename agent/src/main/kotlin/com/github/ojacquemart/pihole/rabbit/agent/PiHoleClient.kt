package com.github.ojacquemart.pihole.rabbit.agent

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
import io.ktor.util.reflect.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class PiHoleClient(
    private val config: PiHoleConfig,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(PiHoleClient::class.java)

        const val INDEX_FILE = "/admin/index.php"
        const val LOGIN_FILE = "/admin/login.php"

        const val GROUPS_FILE = "/admin/scripts/pi-hole/php/groups.php"
        const val GROUPS_ACTION = "groups"
        const val GROUPS_CLIENTS_ACTION = "clients"
        const val GROUPS_DOMAINS_ACTION = "domains"

        const val PARAM_TOKEN = "token"
        const val PARAM_PASSWORD = "pw"
        const val PARAM_ACTION = "action"
    }

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
    val lazyToken = GlobalScope.async(Dispatchers.Unconfined, start = CoroutineStart.LAZY) {
        getToken()
    }

    private suspend fun getToken(): String {
        logger.debug("Trying to get the token")

        return config.password?.let { getTokenAfterLogin() } ?: getTokenWithoutLogin()
    }

    private suspend fun getTokenAfterLogin(): String {
        val loginResponse = client.submitForm(
            url = "${config.baseUrl}$LOGIN_FILE",
            formParameters = parameters {
                append(PARAM_PASSWORD, config.password)
            }
        )

        return extractToken(loginResponse.bodyAsText())
    }

    private fun StringValuesBuilder.append(name: String, value: String?) {
        value?.let { append(name, it) }
    }

    private suspend fun getTokenWithoutLogin(): String {
        val response: HttpResponse = client.get("${config.baseUrl}$INDEX_FILE")
        val body = response.bodyAsText()

        return extractToken(body)
    }

    private fun extractToken(body: String): String {
        val tokenDiv = """<div id="$PARAM_TOKEN" hidden>"""
        val indexOfTokenDiv = body.indexOf(tokenDiv) + tokenDiv.length
        val indexOfEndTokenDiv = body.substring(indexOfTokenDiv).indexOf("</div>")
        val token = body.substring(indexOfTokenDiv, indexOfTokenDiv + indexOfEndTokenDiv)

        return token
    }

    suspend fun getGroups(): GetGroupsResponse = submitGroupsAction("get_$GROUPS_ACTION", typeInfo<GetGroupsResponse>())
    suspend fun getClients(): GetClientsResponse = submitGroupsAction("get_$GROUPS_CLIENTS_ACTION", typeInfo<GetClientsResponse>())
    suspend fun getDomains(): GetDomainsResponse = submitGroupsAction("get_$GROUPS_DOMAINS_ACTION", typeInfo<GetDomainsResponse>())

    private suspend fun <T> submitGroupsAction(action: String, typeInfo: TypeInfo): T {
        val token = lazyToken.await()
        logger.debug("Getting groups $action...")

        val groups = client.submitForm(
            url = "${config.baseUrl}$GROUPS_FILE",
            formParameters = parameters {
                append(PARAM_ACTION, action)
                append(PARAM_TOKEN, token)
            }
        )

        return groups.body(typeInfo)
    }
}