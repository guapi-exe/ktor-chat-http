package bing

import com.fasterxml.jackson.databind.JsonNode
import log.logger
suspend fun main(){
    val chatjson: JsonNode = CreateChat()
    val message = bingwebsocket().sendbing("画一个猫",chatjson.get("clientId").toString(),chatjson.get("conversationId").toString(),chatjson.get("conversationSignature").toString(),0)
    val state = message.split(";")[0].split(":")[1].toInt()
    val send = message.split(";")[1]
    logger.info { "ConcentionState:$state" }
    logger.info{"messagebing:$send"}
}
