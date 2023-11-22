package com.s1092790.eenglish

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

class HttpRequest {
    fun sendPOST(url: String?, requestBody: RequestBody?, callback: OnCallback) {
        // 创建 OkHttpClient 实例
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

        // 创建 Request 对象
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + Chat.YOUR_KEY)
            .post(requestBody)
            .build()

        // 发送请求并处理回调
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailCall(e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val res = response.body()?.string()
                callback.onOKCall(res)
            }
        })
    }

    interface OnCallback {
        fun onOKCall(response: String?)
        fun onFailCall(error: String?)
    }
}
