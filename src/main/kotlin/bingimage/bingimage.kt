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


    val cookie = "_IDET=MIExp=0; _EDGE_V=1; MUID=2B477EEE043F65EB13B46DC8055964D6; MUIDB=2B477EEE043F65EB13B46DC8055964D6; SRCHD=AF=NOFORM; SRCHUID=V=2&GUID=CE4AC420C1374469880536B7C51BB05C&dmnchg=1; ANON=A=0B9EBAA97E5C501ABE2DAC8AFFFFFFFF; SnrOvr=X=rebateson; _UR=QS=0&TQS=0; _tarLang=default=zh-Hans; _TTSS_IN=hist=WyJlbiIsImF1dG8tZGV0ZWN0Il0=; _TTSS_OUT=hist=WyJ6aC1IYW5zIl0=; ANIMIA=FRE=1; MicrosoftApplicationsTelemetryDeviceId=0a50099d-bd6f-466e-8198-709c0a9656bb; MMCASM=ID=C30104246A984F19932553661D64D2AD; _EDGE_CD=u=zh-hans; ABDEF=V=13&ABDV=13&MRNB=1686627886668&MRB=0; GC=HgWwVfbrEiWDPNwieQtmlxaqPrLPrAkNyOBG9gRtN_7i7xOUiZGP4pRb8xJWgUXmDF0F6h6bZO2vEtpU9zyEhg; SUID=A; SNRHOP=I=&TS=; WLS=C=2b2ce29b174453fa&N=pi; _HPVN=CS=eyJQbiI6eyJDbiI6NSwiU3QiOjAsIlFzIjowLCJQcm9kIjoiUCJ9LCJTYyI6eyJDbiI6NSwiU3QiOjAsIlFzIjowLCJQcm9kIjoiSCJ9LCJReiI6eyJDbiI6NSwiU3QiOjAsIlFzIjowLCJQcm9kIjoiVCJ9LCJBcCI6dHJ1ZSwiTXV0ZSI6dHJ1ZSwiTGFkIjoiMjAyMy0wNi0xNFQwMDowMDowMFoiLCJJb3RkIjowLCJHd2IiOjAsIkRmdCI6bnVsbCwiTXZzIjowLCJGbHQiOjAsIkltcCI6NDB9; USRLOC=HS=1&ELOC=LAT=35.977088928222656|LON=120.16619110107422|N=%E9%BB%84%E5%B2%9B%E5%8C%BA%EF%BC%8C%E5%B1%B1%E4%B8%9C%E7%9C%81|ELT=2|&CLOC=LAT=35.988976823537165|LON=120.1636586116564|A=733.4464586120832|TS=230614041116|SRC=W; ipv6=hit=1686712422129&t=4; _SS=SID=30B6A9F0E2C46AB436C7BAC0E3166B65&R=1028&RB=1028&GB=0&RG=0&RP=1028; SRCHHPGUSR=SRCHLANG=zh-Hans&PV=15.0.0&BRW=XW&BRH=M&CW=1652&CH=841&SCW=1652&SCH=841&DPR=1.5&UTC=480&DM=0&EXLTT=31&HV=1686712398&PRVCW=1652&PRVCH=841&BZA=0&cdxtone=Creative&cdxtoneopts=h3imaginative,clgalileo,gencontentv3; _EDGE_S=SID=30B6A9F0E2C46AB436C7BAC0E3166B65&mkt=en-us&ui=zh-cn; MSCC=NR; _RwBf=r=1&mta=0&rc=1028&rb=1028&gb=0&rg=0&pc=1028&mtu=0&rbb=0.0&g=0&cid=&clo=0&v=12&l=2023-06-13T07:00:00.0000000Z&lft=0001-01-01T00:00:00.0000000&aof=0&o=0&p=bingcopilotwaitlist&c=MY00IA&t=2433&s=2023-02-13T03:26:52.6386102+00:00&ts=2023-06-14T03:16:44.0485084+00:00&rwred=0&wls=2&lka=0&lkt=0&TH=&e=omPg8uzQM2-Sy5EQmtlP5EEM73jjzkkMROTKE0KqnzsPmz4WyfyPd7BOKKnwjJ9pXhhRTJHlM4rpzmg6yCJ3ew&A=0B9EBAA97E5C501ABE2DAC8AFFFFFFFF; _U=1edbP_nlyPWv_4ZWoB-n9FLTOrE_2dZC41oVJkpHEYVdmkW68-V_kUthIXyVa0BEAGdgSlUfbAVIS2u9EDyg8uq7aGZmugmvKAlviylkxmzZtJmGTatQhUa2JScEjG1qMFnDOdEID4S_P8AHLzRCSgT_mtZ_T6fcTZkI5lMD17xmYO38iW7ZKf_VaK-idq077GGZ6XFinbJS9LlfOUnMY6w; SRCHUSR=DOB=20230604&T=1686719934000; GI_FRE_COOKIE=gi_prompt=5&gi_sc=8&gi_fre=1"
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