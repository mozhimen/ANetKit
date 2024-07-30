package com.mozhimen.netk.retrofit2.utils

import com.mozhimen.serialk.moshi.UtilKMoshiWrapper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * @ClassName NetKRetrofitUtil
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/7/30
 * @Version 1.0
 */
object NetKRetrofitUtil {
    @JvmStatic
    fun getRetrofit_ofMoshi(url: String, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(UtilKMoshiWrapper.moshiBuilder))
            .build()
}