package com.mozhimen.netk.retrofit2.cache.impls

import com.mozhimen.kotlin.elemk.commons.IA_BListener
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.netk.okhttp3.interceptor.commons.IInterceptor
import com.mozhimen.netk.retrofit2.cache.annors.ACacheHeader
import com.mozhimen.netk.retrofit2.cache.cons.CCacheParams
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation

class InterceptorRetrofit2Cache constructor(
    private val _onGetCacheControl: IA_BListener<ACacheHeader, CacheControl> = {
        CacheControl.Builder()
            .maxAge(it.value, it.unit)
            .build()
    }
) : IInterceptor {

    private val _registration: MutableMap<Int, ACacheHeader> = mutableMapOf()

    ///////////////////////////////////////////////////////////////////

    override fun intercept(chain: Interceptor.Chain): Response {
        UtilKLogWrapper.d(TAG, "intercept: ")
        val request = chain.request()
        val response = chain.proceed(request)
        if (request.method == "GET") {
            findAnnotation(request)?.let { cacheHeader ->
                val cacheControl: CacheControl = _onGetCacheControl(cacheHeader)
                if (hasANetKCacheHeader(response) && !cacheHeader.override) {
                    UtilKLogWrapper.d(TAG, "intercept: return")
                    return response
                }
                UtilKLogWrapper.d(TAG, "intercept: add header cacheControl $cacheControl headers ${response.headers.names()}")
                return response.newBuilder()
                    .removeHeader(CCacheParams.HEADER_PRAGMA)
                    .removeHeader(CCacheParams.HEADER_CACHE_CONTROL)
                    .header(CCacheParams.HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build().also { UtilKLogWrapper.d(TAG, "intercept: headers ${it.headers.names()}") }
            }
        }
        return response
    }

    ///////////////////////////////////////////////////////////////////

    private fun findAnnotation(request: Request): ACacheHeader? {
        val key = request.url.hashCode()
        return _registration[key] ?: request.tag(Invocation::class.java)
            ?.method()
            ?.annotations
            ?.filterIsInstance<ACacheHeader>()
            ?.firstOrNull()
            ?.also { _registration[key] = it }
    }

    private fun hasANetKCacheHeader(response: Response): Boolean =
        response.headers.names().contains(CCacheParams.HEADER_CACHE_CONTROL)
}