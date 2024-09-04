package com.mozhimen.netk.okhttp3.cache.impls

import android.util.Log
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.netk.okhttp3.cache.NetKOkhttp3Cache
import com.mozhimen.netk.okhttp3.cache.annors.ACacheMode
import com.mozhimen.netk.okhttp3.cache.commons.IOkhttp3Cache
import com.mozhimen.netk.okhttp3.cache.helpers.CacheUtil
import com.mozhimen.netk.okhttp3.cache.mos.CacheStrategy
import com.mozhimen.netk.okhttp3.impls.CacheWriteSource
import com.mozhimen.netk.okhttp3.interceptor.commons.IInterceptor
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.EMPTY_RESPONSE
import okhttp3.internal.cache.CacheRequest
import okhttp3.internal.closeQuietly
import okhttp3.internal.http.RealResponseBody
import okio.buffer
import java.io.IOException
import java.net.HttpURLConnection

/**
 * @author : Aleyn
 * @date : 2022/06/23 14:45
 */
class InterceptorOkhttp3Cache(
    private val _cache: IOkhttp3Cache
) : IInterceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val initialRequest = chain.request()
        val strategy = CacheUtil.getCacheStrategy(initialRequest)
        val newRequest = CacheUtil.removeCacheHeader(initialRequest)

        if (strategy == null || strategy.cacheMode == ACacheMode.NETWORK) // ONLY_NETWORK 直接请求网络
            return chain.proceed(newRequest)

        // ONLY_CACHE 只读取缓存
        if (strategy.cacheMode == ACacheMode.CACHE) {
            // 只读缓存模式,缓存为空,返回错误响应
            return (
                    if (NetKOkhttp3Cache.useExpiredData)
                        _cache.getCache(strategy.cacheKey, newRequest)
                    else
                        _cache.getCache_ofExpiration(strategy.cacheKey, strategy.cacheTime, newRequest)
                    )
                ?: Response.Builder()
                    .request(chain.request())
//                    .protocol(Protocol.HTTP_1_1)
                    .code(HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
                    .message("no cached data")
                    .body(EMPTY_RESPONSE)
                    .sentRequestAtMillis(-1L)
                    .receivedResponseAtMillis(System.currentTimeMillis())
                    .build()
        }

        //先读取缓存，缓存失效再请求网络更新缓存
        if (strategy.cacheMode == ACacheMode.CACHE__NETWORK__REFRESH_CACHE_EXPIRATION) {
            val cacheResponse = _cache.getCache_ofExpiration(strategy.cacheKey, strategy.cacheTime, newRequest)
            if (cacheResponse != null){
                UtilKLogWrapper.d(TAG, "intercept: CACHE__NETWORK__REFRESH_CACHE_EXPIRATION cache cacheResponse $cacheResponse")
                return cacheResponse
            }
        }

        //NETWORK__REFRESH_CACHE, NETWORK__CACHE, CACHE__NETWORK__REFRESH_CACHE_EXPIRATION
        val response = chain.proceed(newRequest)
        try {
            if (response.isSuccessful)
                return getCacheWriteResponse(_cache.putCache(strategy.cacheKey, response), response)
            if (strategy.cacheMode == ACacheMode.NETWORK__CACHE)
                return _cache.getCache_ofExpiration(strategy.cacheKey, strategy.cacheTime, newRequest) ?: response
            return response
        } catch (e: Throwable) {
            if (strategy.cacheMode == ACacheMode.NETWORK__CACHE)
                return _cache.getCache_ofExpiration(strategy.cacheKey, strategy.cacheTime, newRequest) ?: response//throw e
            return response//throw e
        }
    }

    @Throws(IOException::class)
    private fun getCacheWriteResponse(cacheRequest: CacheRequest?, response: Response): Response {
        if (cacheRequest == null) return response
        val cacheBodyUnbuffered = cacheRequest.body()
        val source = response.body!!.source()
        val cacheBody = cacheBodyUnbuffered.buffer()
        val cacheWritingSource = CacheWriteSource(source, cacheRequest, cacheBody)
        val contentType = response.header("Content-Type")
        val contentLength = response.body!!.contentLength()
        return response.newBuilder()
            .body(RealResponseBody(contentType, contentLength, cacheWritingSource.buffer()))
            .build()
    }
}