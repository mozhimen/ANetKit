package com.mozhimen.netk.retrofit2.cache.test.commons

import com.mozhimen.netk.retrofit2.cache.annors.ACacheHeader
import retrofit2.Response
import retrofit2.http.GET
import java.util.concurrent.TimeUnit


interface RandomApi {
    @GET("api/v1.0/random")
    @ACacheHeader(1, unit = TimeUnit.MINUTES)
    suspend fun randomNumber(): Response<List<Int>>
}
