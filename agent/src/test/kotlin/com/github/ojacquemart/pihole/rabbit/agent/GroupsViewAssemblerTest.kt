package com.github.ojacquemart.pihole.rabbit.agent

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@WireMockTest
class GroupsViewAssemblerTest {

    @BeforeEach
    fun setUp() = Pihole.stubs()

    @Test
    fun `assemble groups clients and domain all together`(runtimeInfo: WireMockRuntimeInfo) = runBlocking {
        val config = PiHoleConfig("http://localhost:${runtimeInfo.httpPort}", "password")

        val groupsViewAssembler = GroupsViewAssembler(config)
        val view = groupsViewAssembler.assemble()

        val expectedView = GroupsView(
            domains = listOf(
                GroupsView.DomainView(
                    id = 1,
                    name = "no-youtube",
                    enabled = true,
                    clients = listOf(
                        GroupsView.ClientView(
                            id = 2,
                            name = "foo",
                            enabled = true,
                        ),
                        GroupsView.ClientView(
                            id = 3,
                            name = "bar",
                            enabled = true,
                        ),
                        GroupsView.ClientView(
                            id = 8,
                            name = "qix",
                            enabled = false,
                        ),
                    )
                ),
                GroupsView.DomainView(
                    id = 3,
                    name = "no-twitch",
                    enabled = true,
                    clients = listOf(

                        GroupsView.ClientView(
                            id = 2,
                            name = "foo",
                            enabled = false,
                        ),
                        GroupsView.ClientView(
                            id = 3,
                            name = "bar",
                            enabled = false,
                        ),
                        GroupsView.ClientView(
                            id = 8,
                            name = "qix",
                            enabled = true,
                        ),
                    )
                ),
                GroupsView.DomainView(
                    id = 4,
                    name = "no-instagram",
                    enabled = true,
                    clients = listOf(


                        GroupsView.ClientView(
                            id = 2,
                            name = "foo",
                            enabled = true,
                        ),
                        GroupsView.ClientView(
                            id = 3,
                            name = "bar",
                            enabled = true,
                        ),
                        GroupsView.ClientView(
                            id = 8,
                            name = "qix",
                            enabled = false,
                        ),
                    )
                ),
            )
        )

        assertEquals(expectedView, view)
    }

}