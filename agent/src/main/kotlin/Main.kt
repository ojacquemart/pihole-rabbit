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
        val description: String,
    )
}

suspend fun main() {
    // TODO: transform this into a PiholeClient class
    // TODO: consider the need to do a login request according to the "login-box" div presence
    // TODO: write tests
    // TODO: use kotlinx.serialization to parse the JSON responses
    // TODO: check about this SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
    // TODO: try to generate a jar and test it on the Raspberry Pi

    val baseUrl = "http://192.168.68.60"

    val client = HttpClient(CIO) {
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
    val response: HttpResponse = client.get("$baseUrl/admin/index.php")

    val body: String = response.body()

    // login box is present if not logged in
    val loginResponse = client.submitForm(
        url = "$baseUrl/admin/login.php",
        formParameters = parameters {
            append("pw", "foobarqix@pihole")
        }
    )

    println(loginResponse.status)
    println(loginResponse.headers)
    val loginBody = loginResponse.bodyAsText()

    val tokenDiv = """<div id="token" hidden>"""
    val indexOfTokenDiv = loginBody.indexOf(tokenDiv) + tokenDiv.length
    val indexOfEndTokenDiv = loginBody.substring(indexOfTokenDiv).indexOf("</div>")
    val token = loginBody.substring(indexOfTokenDiv, indexOfTokenDiv + indexOfEndTokenDiv)
    println(token)

    val groups = client.submitForm(
        url = "$baseUrl/admin/scripts/pi-hole/php/groups.php",
        formParameters = parameters {
            append("action", "get_groups")
            append("token", token)
        }
    )

    println(groups.status)
    val r = groups.body<GetGroupsResponse>()
    println(r)
}