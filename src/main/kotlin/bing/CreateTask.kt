package bing

import cn.zhxu.okhttps.HTTP
import cn.zhxu.okhttps.WHttpTask
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit


val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 10809))

val url = "wss://sydney.bing.com"
var http = HTTP.builder()
    .config { builder ->
        builder.pingInterval(15, TimeUnit.SECONDS)
        builder.proxy(proxy)
    }
    .baseUrl(url)
    .build()

val task : WHttpTask = http.webSocket("/sydney/ChatHub")










