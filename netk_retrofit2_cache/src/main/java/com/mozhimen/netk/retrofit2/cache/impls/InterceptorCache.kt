package com.mozhimen.netk.retrofit2.cache.impls

import com.mozhimen.netk.retrofit2.cache.annors.ANetKCache
import com.mozhimen.netk.retrofit2.cache.cons.CNetKCacheParams
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation

class InterceptorCache constructor(
    private val _onGetCacheControl: (annotation: ANetKCache) -> CacheControl = {
        CacheControl.Builder()
            .maxAge(it.value, it.unit)
            .build()
    }
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (request.method == "GET") {
            findAnnotation(request)?.let { cacheInfo ->
                val cacheControl: CacheControl = _onGetCacheControl(cacheInfo)
                if (containsCachingInformation(response) && !cacheInfo.override) {
                    return response
                }
                return response.newBuilder()
                    .removeHeader(CNetKCacheParams.CACHE_PRAGMA)
                    .removeHeader(CNetKCacheParams.CACHE_CONTROL)
                    .header(CNetKCacheParams.CACHE_CONTROL, cacheControl.toString())
                    .build()
            }
        }
        return response
    }

    private val registration: MutableMap<Int, ANetKCache> = mutableMapOf()

    private fun findAnnotation(
        request: Request
    ): ANetKCache? {
        val key = request.url.hashCode()
        return registration[key] ?: request.tag(Invocation::class.java)
            ?.method()
            ?.annotations
            ?.filterIsInstance<ANetKCache>()
            ?.firstOrNull()
            ?.also { registration[key] = it }
    }

    private fun containsCachingInformation(response: Response): Boolean =
        response.headers.names().contains(CNetKCacheParams.CACHE_CONTROL)
}