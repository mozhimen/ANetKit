package com.mozhimen.netk.okhttp3.interceptor.impls

import com.mozhimen.netk.okhttp3.interceptor.annors.AInterceptor
import com.mozhimen.netk.okhttp3.interceptor.annors.ANetworkInterceptor
import com.mozhimen.netk.okhttp3.interceptor.commons.IInterceptor
import okhttp3.Interceptor
import okhttp3.Response


/**
 * @ClassName InterceptorCache
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/7/30
 * @Version 1.0
 */
@ANetworkInterceptor
class InterceptorCacheOnline(private val _cacheSeconds: Long = 120) : IInterceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain
                .request().newBuilder()
                .removeHeader("Pragma")
//                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=${_cacheSeconds}")
                .build()
        )
}