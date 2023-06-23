package com.guapi.chat.plugins

import bing.CreateChat
import com.fasterxml.jackson.databind.JsonNode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/CreateChat") {
            val chatjson: JsonNode = CreateChat()
            call.respondText(chatjson.toString())
        }
    }
}
