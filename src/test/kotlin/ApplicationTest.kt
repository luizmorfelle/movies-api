package com.univali

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/") {
            parameter("movie", "Kingdom of the Planet")
            parameter("year", "2024")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

}
