package bing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import log.logger
import java.net.InetSocketAddress
import java.net.Proxy


val cookie = ""//自己卡bing的cooike中的_U
val MUID = ""//MUID
public suspend fun CreateChat(): JsonNode {
    try {
        val client = HttpClient() {
            engine {
                proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 10809))
            }
            install(ContentNegotiation) {
                jackson()
            }
        }
        val url = "https://edgeservices.bing.com/edgesvc/turing/conversation/create"
        val httpResponse: HttpResponse = client.get(url) {
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
                append(HttpHeaders.Cookie,"_U=${cookie};MUID=${MUID}")
                append(HttpHeaders.ContentType, "application/json")
                append(HttpHeaders.Referrer, "https://www.bing.com/search?q=Bing+AI&showconv=1&FORM=hpcodx")
                append(HttpHeaders.XForwardedFor, "1.1.1.1")
            }
        }
        val text = httpResponse.bodyAsText()
        val objectMapper = ObjectMapper()
        val json = objectMapper.readTree(text)
        logger.info{json}
        return json
    } catch (e: Exception) {
        e.printStackTrace()
        return error("创建失败:$e")
    }
}








