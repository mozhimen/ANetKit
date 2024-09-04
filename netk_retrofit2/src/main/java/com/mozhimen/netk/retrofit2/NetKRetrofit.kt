package com.mozhimen.netk.retrofit2

import com.mozhimen.kotlin.elemk.android.util.cons.CLog
import com.mozhimen.kotlin.elemk.javax.net.bases.BaseHostnameVerifier
import com.mozhimen.kotlin.elemk.javax.net.bases.BaseX509TrustManager
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.kotlin.utilk.java.io.UtilKFileDir
import com.mozhimen.kotlin.utilk.javax.net.UtilKSSLSocketFactory
import com.mozhimen.netk.retrofit2.utils.NetKRetrofitUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import okhttp3.Cache
import java.io.File

/**
 * @ClassName CoroutineFactory
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/5/12 16:01
 * @Version 1.0
 */
open class NetKRetrofit constructor(
    baseUrl: String,
    connectTimeoutSecond: Long = 15,
    readTimeoutSecond: Long = 15,
    cacheSize: Long = 0L,
    interceptors: List<Interceptor> = emptyList(),
    networkInterceptors: List<Interceptor> = emptyList()
) : BaseUtilK() {

    companion object{
        val cacheFolder: File by lazy { File(UtilKFileDir.Internal.getCache(), "netk_retrofit_cache") }
    }

    /////////////////////////////////////////////////////////////////////////

    private val _okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            connectTimeout(connectTimeoutSecond, TimeUnit.SECONDS)
            readTimeout(readTimeoutSecond, TimeUnit.SECONDS)
            sslSocketFactory(UtilKSSLSocketFactory.get_ofTLS(), BaseX509TrustManager())
            hostnameVerifier(BaseHostnameVerifier())
            if (cacheSize > 0L)
                cache(Cache(cacheFolder, cacheSize))
            if (interceptors.isNotEmpty())
                for (interceptor in interceptors)
                    addInterceptor(interceptor)
            if (networkInterceptors.isNotEmpty())
                for (networkInterceptor in networkInterceptors)
                    addNetworkInterceptor(networkInterceptor)
            if (BuildConfig.DEBUG)
                addInterceptor(HttpLoggingInterceptor { msg ->
                    UtilKLogWrapper.pringln_ofLongLog(CLog.VERBOSE, TAG, msg)
                }.also { it.level = HttpLoggingInterceptor.Level.BODY })
        }.build()
    }

    @get:Synchronized
    @set:Synchronized
    private var _retrofit: Retrofit? = null
        get() {
            if (field != null)
                return field
            return NetKRetrofitUtil.getRetrofit_ofMoshi(baseUrl, _okHttpClient).also { field = it }
        }

    /////////////////////////////////////////////////////////////////////////

    val okHttpClient: OkHttpClient
        get() = _okHttpClient

    val retrofit: Retrofit
        get() = _retrofit!!

    @get:Synchronized
    @set:Synchronized
    var baseUrl: String = baseUrl
        set(value) {
            _retrofit = NetKRetrofitUtil.getRetrofit_ofMoshi(value, _okHttpClient)
            field = value
        }

    /////////////////////////////////////////////////////////////////////////

    @Synchronized
    fun <SERVICE : Any> create(service: Class<SERVICE>): SERVICE {
        return _retrofit!!.create(service) as SERVICE
    }

    inline fun <reified SERVICE : Any> create(): SERVICE {
        return create(SERVICE::class.java)
    }
}