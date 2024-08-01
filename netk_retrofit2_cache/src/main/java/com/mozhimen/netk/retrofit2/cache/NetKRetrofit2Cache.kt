package com.mozhimen.netk.retrofit2.cache

import com.mozhimen.basick.elemk.commons.IA_BListener
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.netk.retrofit2.cache.annors.ACacheHeader
import com.mozhimen.netk.retrofit2.cache.impls.InterceptorANetKRetrofit2Cache
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @ClassName ANetKRetrofit2Cache
 * @Description {
 *             CacheControl.Builder()
 *                 .maxAge(it.value, it.unit)
 *                 .build()
 *         }
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/7/30 20:04
 * @Version 1.0
 */
fun Retrofit.supportCache(
    cache: Cache,
    cacheControl: IA_BListener<ACacheHeader, CacheControl> = {
        CacheControl.Builder()
            .maxAge(it.value, it.unit)
            .build()
    }
): Retrofit =
    NetKRetrofit2Cache.supportCache(this, cache, cacheControl)

/////////////////////////////////////////////////////////////////////////////

object NetKRetrofit2Cache : IUtilK {
    @JvmStatic
    fun supportCache(
        retrofit: Retrofit,
        cache: Cache
    ): Retrofit =
        supportCache(retrofit, cache) {
            CacheControl.Builder()
                .maxAge(it.value, it.unit)
                .build()
        }

    @JvmStatic
    fun supportCache(
        retrofit: Retrofit,
        cache: Cache,
        cacheControl: IA_BListener<ACacheHeader, CacheControl>
    ): Retrofit {
        val okHttpClient = retrofit.callFactory().let { callFactory ->
            check(callFactory is OkHttpClient) { "RetrofitCache only works with OkHttp as Http Client!" }
            UtilKLogWrapper.d(TAG, "supportCache: ")
            callFactory.newBuilder()
                .cache(cache)
                .addNetworkInterceptor(InterceptorANetKRetrofit2Cache(cacheControl))
                .build()
        }
        return retrofit.newBuilder()
            .client(okHttpClient)
            .build()
    }
}