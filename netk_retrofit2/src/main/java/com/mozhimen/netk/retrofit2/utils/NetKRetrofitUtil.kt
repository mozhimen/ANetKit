package com.mozhimen.netk.retrofit2.utils

import com.mozhimen.netk.retrofit2.impls.FlowCallAdapterFactory
import com.mozhimen.serialk.moshi.UtilKMoshiWrapper
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
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
    fun getDefaultRetrofit(
        url: String,
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory = MoshiConverterFactory.create(UtilKMoshiWrapper.moshiBuilder),
        callAdapterFactory: CallAdapter.Factory? = null
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient).apply {
                addConverterFactory(converterFactory)
                if (callAdapterFactory != null)
                    addCallAdapterFactory(callAdapterFactory)
            }
            .build()
}