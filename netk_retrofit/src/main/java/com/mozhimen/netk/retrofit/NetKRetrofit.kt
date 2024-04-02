package com.mozhimen.netk.retrofit

import com.mozhimen.basick.elemk.android.util.cons.CLog
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.bases.BaseUtilK
import com.mozhimen.basick.utilk.squareup.moshi.UtilKMoshi
import com.mozhimen.basick.utilk.squareup.moshi.UtilKMoshiWrapper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @ClassName CoroutineFactory
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/5/12 16:01
 * @Version 1.0
 */
open class NetKRetrofit(
    baseUrl: String,
    connectTimeoutSecond: Long = 15,
    readTimeoutSecond: Long = 15,
    interceptors: List<Interceptor> = emptyList()
) : BaseUtilK() {
    private val _interceptors: ArrayList<Interceptor> = ArrayList()
    private val _okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                connectTimeout(connectTimeoutSecond, TimeUnit.SECONDS)
                readTimeout(readTimeoutSecond, TimeUnit.SECONDS)
                addInterceptor(HttpLoggingInterceptor { msg -> UtilKLogWrapper.pringln_ofLongLog(CLog.VERBOSE, TAG, msg) }.also { it.level = HttpLoggingInterceptor.Level.BODY })
                if (_interceptors.isNotEmpty())
                    for (interceptor in _interceptors) addInterceptor(interceptor)
            }
        }.build()
    }

    private var _retrofit: Retrofit? = null
        get() {
            if (field != null) return field
            return initRetrofit(baseUrl).also { field = it }
        }

    /////////////////////////////////////////////////////////////////////////

    val okHttpClient: OkHttpClient
        get() = _okHttpClient

    val retrofit: Retrofit
        get() = _retrofit!!

    var baseUrl: String = baseUrl
        set(value) {
            field = value
            _retrofit = initRetrofit(value)
        }

    /////////////////////////////////////////////////////////////////////////

    init {
        if (interceptors.isNotEmpty()) this._interceptors.addAll(interceptors)
    }

    /////////////////////////////////////////////////////////////////////////

    @Synchronized
    fun <SERVICE : Any> create(service: Class<SERVICE>): SERVICE {
        if (_retrofit == null) {
            _retrofit = initRetrofit(baseUrl)
        }
        return _retrofit!!.create(service) as SERVICE
    }

    inline fun <reified SERVICE : Any> create(): SERVICE {
        return create(SERVICE::class.java)
    }

    /////////////////////////////////////////////////////////////////////////

    private fun initRetrofit(url: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .client(_okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(UtilKMoshiWrapper.moshiBuilder))
            .build()
}