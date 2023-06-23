package bingimage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import log.logger
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class bingimage {


    val cookie = ""//建议直接吧请求的全部cookie复制下来，试了单_U和其他的都不行。
    suspend fun CreateimageRespone(name: String): Response? {

        val urlEncodedPrompt = withContext(Dispatchers.IO) {
            URLEncoder.encode(name, "UTF-8")
        }
        val url = "https://edgeservices.bing.com/images/create?q=${urlEncodedPrompt}&rt=4&FORM=GENCRE"
        val client = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(false) //禁用自动重定向
            .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 10809)))
            .build()
        try {
            val formBody = FormBody.Builder()
                .add("q", name)
                .add("qs", "ds") //创建伪请求表单数据
                .build()
            val request = Request.Builder()
                .url(url)
                .post(formBody)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Origin","https://edgeservices.bing.com")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .header("Cookie", cookie)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-Forwarded-For", "1.1.1.1")
                .build()
            val response = client.newCall(request).execute()
            if (response.code == 302) {
                val location = response.header("Location")
                logger.info { location }
            }
            return response
        } catch (e:Error){
            logger.error { e }
            return null
        }
    }
    suspend fun createimage(name: String){
        val urlEncodedPrompt = withContext(Dispatchers.IO) {
            URLEncoder.encode(name, "UTF-8")
        }
        val response = CreateimageRespone(name)
        logger.info { response }
        if(response == null){
            logger.error { "请求错误" }
            delay(1000*3)
            createimage(name)
            return
        }
        if(response.code != 302){
            logger.error { "请求错误${response.code}" }
            delay(1000*3)
            createimage(name)
            return
        }
        val text = response.body?.string()
        logger.info { text }
        if (text != null) {
            if("this prompt has been blocked" in text.toLowerCase() ||
                "此提示已被阻止" in text.toLowerCase() ||
                "content warning" in text.toLowerCase()
            ){
                logger.error { "被bing阻止的关键词" }
                return
            }
        }
        val redirectUrl = response.headers.get("Location")?.replace("&nfy=1", "")
        logger.info { redirectUrl }
        val requestId = redirectUrl?.split("id=")?.get(1)
        val pollingUrl = "https://edgeservices.bing.com/images/create/async/results/${requestId}?q=${urlEncodedPrompt}"
        logger.info { pollingUrl }
        try {
            withTimeout(1000 * 60 * 3) {
                var imagelist = getimage(pollingUrl)
                while (true) {
                    if (imagelist == null) {
                        logger.info { "new ping" }
                        delay(1000 * 5)
                        imagelist = getimage(pollingUrl)
                    }else if(imagelist.size != 0 ){
                        logger.info { imagelist }
                        return@withTimeout
                    }
                    delay(1000)
                }
            }
        }catch (e:Error){
            logger.error { "err:$e" }
        }
    }

    suspend fun getimage(url: String): List<String>? {
        val client = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(60,TimeUnit.SECONDS)
            .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 10809)))
            .build()
        val request = Request.Builder()
            .url(url)
            .get()
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            .header("Origin","https://edgeservices.bing.com")
            .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
            .header("Cookie", cookie)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("X-Forwarded-For", "1.1.1.1")
            .build()
        val response = client.newCall(request).execute()
        logger.info { response }
        val text: String? = response.body?.string()
        val regex = Regex("""src="([^"]+)"""")
        var imageLinks: List<String>? = null
        if(text != null) {
            imageLinks = regex.findAll(text).map { it.value.replace("src=","",true) }.toList()
        }
        return if(text != null && !imageLinks.isNullOrEmpty()){
            imageLinks
        }else{
            logger.info { "err:$text" }
            null
        }
    }

}
