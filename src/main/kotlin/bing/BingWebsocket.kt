package bing

import cn.zhxu.okhttps.WebSocket
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import log.logger
import java.util.*


class bingwebsocket {

    suspend fun createws(): WebSocket {
        var wsopen = false
        val ws: WebSocket = task.listen()
        task.setOnOpen { WebSocket, data ->
            WebSocket.send("{\"protocol\":\"json\",\"version\":1}")
            WebSocket.send("{\"type\":6}")
            logger.info{"wsopen:$data"}
            wsopen = true
        }
        task.heatbeat(15, 15)
        task.pingSupplier {
            "{\"type\":6}"
        }
        task.setOnClosed { WebSocket, data ->
            logger.info{"close:$data"}
            ws.cancel()
        }
        task.setOnException { WebSocket, data ->
            logger.error{"err:$data"}
        }

        while (true) {
            if(wsopen)
                return ws
            delay(1000)
        }
    }
    suspend fun getmessage(): String? {
        var state: Int = 200
        var bingmessage: String? = null
        task.setOnMessage { Websocket, data ->
            val body = data.toString().split("")[0]
            val objectMapper = ObjectMapper()
            val event = objectMapper.readTree(body)
            logger.info{"event:$event"}
            if (event.size() == 0) {
                return@setOnMessage;
            }
            if(event == null) {
                return@setOnMessage
            }
            if(event.get("type") == null) {
                return@setOnMessage
            }
            logger.info{"eventtype:${event.get("type").toString().toInt()}"}
            when(event.get("type").toString().toInt()){
                1 -> {
                    val messages = event.get("arguments")?.get(0)?.get("messages")
                    val message = messages?.get(0)
                    if (message?.get("contentType").toString() == "IMAGE") {
                        val imageTag = messages?.filter{ "IMAGE" in it?.get("contentType").toString() }?.get(0)?.get("text")
                        logger.info{imageTag}
                    }
                    state = 200
                    return@setOnMessage;
                }
                2 -> {
                    val result = event.get("item")?.get("result")
                    val messages = event.get("item")?.get("messages")
                    var message = messages?.get(1)
                    if ("bot" !in  message?.get("author").toString() ) {
                        state = 400
                        if (result != null) {
                            if ( "maximum context length" in result.get("exception").toString()) {
                                bingmessage = "state:$state;对话长度太长啦！超出8193token，请结束对话重新开始"
                                logger.info{"对话长度太长啦！超出8193token，请结束对话重新开始"}
                            } else if (result.get("value").toString() == "Throttled") {
                                logger.info{"该账户的SERP请求已被限流"}
                                bingmessage = "state:$state;该账户的SERP请求已被限流"
                            }
                        } else {
                            logger.info{"wtf no author."}
                            bingmessage = "state:$state;wtf no author."
                        }
                    }
                    if("Success" in result?.get("value").toString()) {
                        println(result?.get("value"))
                        val suggestions = messages?.get(1)?.get("suggestedResponses")
                        var suggestionstr = ""
                        if (suggestions != null) {
                            val size = suggestions.size()
                            var i = 0
                            while (size > i ){
                                suggestionstr += "建议${i+1}:${suggestions.get(i).get("text")}\n"
                                i++
                            }
                        }
                        state = 100
                        val str = "state:$state;${messages?.get(1)?.get("text")}\n${suggestionstr}"
                        bingmessage = str
                    }
                }
                7 -> {
                    logger.error{"error:$event"}
                    state = 400
                    return@setOnMessage
                }
                else -> {
                    state = 200
                    return@setOnMessage
                }
            }
        }
        while (true){
            if( state == 400 ){
                return bingmessage
            }
            if( state == 100){
                return bingmessage
            }
            else if(bingmessage != null){
                return bingmessage
            }
            delay(1000)
        }
    }
    suspend fun sendbing(message: String,clientId: String, conversationId: String, conversationSignature: String, invocationId: Int ): String {
        val message = "{"+
                "'arguments':[" +
                "{" +
                "'source':'cib'," +
                "'optionsSets':[" +
                "'nlu_direct_response_filter'," +
                "'deepleo'," +
                "'disable_emoji_spoken_text'," +
                "'responsible_ai_policy_235'," +
                "'enablemm'," +
                "'h3imaginative'," +
                "'dtappid'," +
                "'cricinfo'," +
                "'cricinfov2'," +
                "'dv3sugg'" +
                "]," +
                "'sliceIds':[" +
                "'222dtappid'," +
                "'225cricinfo'," +
                "'224locals0'" +
                "]," +
                "'traceId':'${genRanHex(32)}'," +
                "'isStartOfSession':${invocationId == 0}," +
                "'message':{" +
                "'locale':'zh-CN'," +
                "'market':'zh-CN'," +
                "'region':'HK'," +
                "'location':'lat:47.639557;long:-122.128159;re=1000m;'," +
                "'locationHints':[" +
                "{" +
                "'Center':{" +
                "'Latitude':39.971031896331," +
                "'Longitude':116.33522679576237" +
                "}," +
                "'RegionType':2," +
                "'SourceType':11" +
                "}," +
                "{" +
                "'country':'Hong Kong'," +
                "'timezoneoffset':8," +
                "'countryConfidence':9," +
                "'Center':{" +
                "'Latitude':22.15," +
                "'Longitude':114.1" +
                "}," +
                "'RegionType':2," +
                "'SourceType':1" +
                "}" +
                "]," +
                "'author':'user'," +
                "'inputMethod':'Keyboard'," +
                "'text':'$message'," +
                "'messageType':'SearchQuery'" +
                "}," +
                "'conversationSignature':$conversationSignature," +
                "'participant':{" +
                "'id':$clientId" +
                "}," +
                "'conversationId':$conversationId" +
                "}" +
                "],"+
                "'invocationId':'${invocationId}'," +
                "'target':'chat'," +
                "'type':4" +
                "}\u001E"



        val ws = createws()
        val newmessage = message.replace("'","\"")
        ws.send(newmessage)
        logger.info{"send:$newmessage"}
        var bingmessage = withTimeoutOrNull(1000*60*2) {
            getmessage()
        }
        if(bingmessage == null){
            bingmessage = "state:$500;超时错误"
        }
        cleanupWebSocketConnection(ws)
        logger.info{"clean"}
        return bingmessage

    }
    fun cleanupWebSocketConnection(webSocket: WebSocket): Boolean {
        return webSocket.cancel()
    }
    fun genRanHex(size: Int): String {
        val random = Random()
        val hexChars = "0123456789abcdef"
        val sb = StringBuilder()
        for (i in 1..size) {
            sb.append(hexChars[random.nextInt(16)])
        }
        return sb.toString()
    }
}
