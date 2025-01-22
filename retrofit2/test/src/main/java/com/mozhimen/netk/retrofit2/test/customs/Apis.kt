package com.mozhimen.netk.retrofit2.test.customs

import com.mozhimen.netk.okhttp3.cache.annors.ACacheMode
import com.mozhimen.netk.okhttp3.cache.cons.CCacheHeaders
import com.mozhimen.netk.retrofit2.cache.annors.ACacheHeader
import com.mozhimen.netk.retrofit2.test.mos.ArticleRes
import com.mozhimen.netk.retrofit2.test.mos.BaseRes
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
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
    @GET("/api/v1.0/random")
    @ACacheHeader(1, TimeUnit.MINUTES, override = true)
    suspend fun get_ofRetrofitCache(): Response<List<Int>>

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun get_ofOkhttp3Cache(
        @Field("username") username: String,
        @Field("password") password: String,
        @Header(CCacheHeaders.HEADER_CACHE_MODE) cacheMode: String = ACacheMode.CACHE__NETWORK__REFRESH_CACHE_EXPIRATION,
        @Header(CCacheHeaders.HEADER_CACHE_TIME) cacheTime: String = "100"
    ): BaseRes<Any>?

    @Headers(
        "${CCacheHeaders.HEADER_CACHE_TIME}:100",
        "${CCacheHeaders.HEADER_CACHE_MODE}:${ACacheMode.CACHE__NETWORK__REFRESH_CACHE_EXPIRATION}"
    )
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun get_ofOkhttp3Cache2(
        @Field("username") username: String,
        @Field("password") password: String,
    ): BaseRes<Any>?
}