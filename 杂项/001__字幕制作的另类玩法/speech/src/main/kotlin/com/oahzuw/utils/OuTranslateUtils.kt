package com.oahzuw.utils

import cn.yiiguxing.plugin.translate.trans.tk
import com.alibaba.fastjson.JSON
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.IOException
import java.net.URI


// 代码参考: https://github.com/YiiGuxing/TranslationPlugin, 基于这里面的Google改的, 主要是使用tk算法

fun doTranslate(word: String): String {
    var word = word.replace(" ", "%20")
    var url = "https://translate.google.cn/translate_a/single?client=gtx&dt=t&dt=bd&dt=rm&dj=1&sl=en&tl=zh-CN&hl=zh-CN&tk=${word.tk()}&q=$word"
    var result = ""
    JSON.parseObject(doGet(url)).getJSONArray("sentences").iterator().forEach {
        var trans = JSON.parseObject(it.toString()).getString("trans")
        if (!trans.isNullOrBlank()) {
            result += trans
        }
    }
    return result
}

fun doGet(url: String): String {
    val httpGet = HttpGet()
    val httpClient = HttpClients.createDefault()
    httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
    var response: CloseableHttpResponse? = null
    //设置请求和传输超时时间
    val requestConfig = RequestConfig.custom().setSocketTimeout(1000 * 60).setConnectTimeout(1000 * 60).build()
    httpGet.config = requestConfig
    try {
        httpGet.uri = URI(url)
        response = httpClient.execute(httpGet)
        return if (response!!.statusLine.statusCode == 200) {
            EntityUtils.toString(response.entity, "UTF-8")
        } else {
            ""
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    } finally {
        close(httpClient, response)
    }
}

/**
 * 通用关流方法
 *
 * @param httpClient
 * @param httpResponse
 */
fun close(httpClient: CloseableHttpClient?, httpResponse: CloseableHttpResponse?) {
    var httpClient = httpClient
    var httpResponse = httpResponse
    if (httpResponse != null) {
        try {
            httpResponse.close()
        } catch (e: IOException) {
        }
    }
    if (httpClient != null) {
        try {
            httpClient.close()
        } catch (e: IOException) {
        }
    }
}