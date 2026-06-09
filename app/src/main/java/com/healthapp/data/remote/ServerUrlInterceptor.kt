package com.healthapp.data.remote

import com.healthapp.util.ServerConfig
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 动态重写服务器URL的拦截器
 * 支持用户在APP内自定义服务器地址
 */
@Singleton
class ServerUrlInterceptor @Inject constructor(
    private val serverConfig: ServerConfig
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // 只重写发往占位地址的请求
        if (original.url.host != PLACEHOLDER_HOST) {
            return chain.proceed(original)
        }

        val serverUrl = serverConfig.getServerUrl()
        val newBase = serverUrl.toHttpUrl()

        val newUrl = original.url.newBuilder()
            .scheme(newBase.scheme)
            .host(newBase.host)
            .port(newBase.port)
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

    companion object {
        const val PLACEHOLDER_HOST = "pulsevita-server.local"
        const val PLACEHOLDER_URL = "https://$PLACEHOLDER_HOST/"
    }
}
