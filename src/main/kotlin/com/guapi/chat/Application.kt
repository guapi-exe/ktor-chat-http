package com.guapi.chat

import com.guapi.chat.plugins.configureRouting
import com.guapi.chat.plugins.configureSecurity
import com.guapi.chat.plugins.configureSockets
import com.guapi.chat.plugins.configureTemplating
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureTemplating()
    configureSockets()
    configureRouting()
}
