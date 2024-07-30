package com.mozhimen.netk.retrofit2.test.customs

import com.mozhimen.netk.retrofit2.test.mos.ArticleRes
import retrofit2.http.GET
import retrofit2.http.Headers

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
    @GET("/posts/2")
    suspend fun get_ofCache(): ArticleRes?
}