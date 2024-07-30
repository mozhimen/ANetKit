package com.mozhimen.netk.retrofit2.cache

import com.mozhimen.netk.retrofit2.cache.annors.ANetKCache
import com.mozhimen.netk.retrofit2.cache.impls.InterceptorCache
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ResponseCacheExtension {

    @JvmStatic
    @JvmOverloads
    fun setup(
        retrofit: Retrofit,
        cache: Cache,
        cacheControl: (annotation: ANetKCache) -> CacheControl = {
            CacheControl.Builder()
                .maxAge(it.value, it.unit)
                .build()
        }
    ): Retrofit {
        val okHttpClient = retrofit.callFactory().let { callFactory ->
            check(callFactory is OkHttpClient) { "RetrofitCache only works with OkHttp as Http Client!" }
            callFactory.newBuilder()
                .addNetworkInterceptor(InterceptorCache(cacheControl))
                .cache(cache)
                .build()
        }
        return retrofit.newBuilder()
            .client(okHttpClient)
            .build()
    }
}

fun Retrofit.responseCache(
    cache: Cache,
    cacheControl: (annotation: ANetKCache) -> CacheControl = {
        CacheControl.Builder()
            .maxAge(it.value, it.unit)
            .build()
    }
) = ResponseCacheExtension.setup(this, cache, cacheControl)
