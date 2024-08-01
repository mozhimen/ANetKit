package com.mozhimen.netk.retrofit2.test.customs

import com.mozhimen.netk.okhttp3.cache.CacheMode
import com.mozhimen.netk.okhttp3.cache.CacheStrategy
import com.mozhimen.netk.retrofit2.cache.annors.ACacheHeader
import com.mozhimen.netk.retrofit2.test.mos.ArticleRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

/**
 * @ClassName TestApi
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2021/12/13 22:19
 * @Version 1.0
 */
interface Apis {

    //121.321504,31.194874
    @GET("/posts/2")
    suspend fun get_ofCoroutine(): ArticleRes?

    //121.321504,31.194874
//    @Headers("Cache-Control:public, only-if-cached, max-stale=120")
    @GET("api/v1.0/random")
    @ACacheHeader(1, TimeUnit.MINUTES, override = true)
    suspend fun get_ofRetrofitCache(): Response<List<Int>>

    @GET("/posts/2")
    suspend fun get_ofOkhttp3Cache(
        @Header(CacheStrategy.CACHE_MODE) cacheMode: String = CacheMode.READ_CACHE_NETWORK_PUT,
        @Header(CacheStrategy.CACHE_TIME) cacheTime: String = "100"
    ): ArticleRes?
}