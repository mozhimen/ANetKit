package com.mozhimen.netk.retrofit2.test.customs

import com.mozhimen.kotlin.utilk.commons.IUtilK
import com.mozhimen.kotlin.utilk.kotlin.megaBytes
import com.mozhimen.netk.okhttp3.cache.NetKOkhttp3Cache
import com.mozhimen.netk.okhttp3.cache.impls.InterceptorOkhttp3Cache
import com.mozhimen.netk.retrofit2.NetKRetrofit2
import com.mozhimen.netk.retrofit2.cache.impls.InterceptorRetrofit2Cache
import com.mozhimen.serialk.moshi.UtilKMoshiWrapper
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * @ClassName ApiFactory
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2021/12/13 22:16
 * @Version 1.0
 */
object ApiFactory : IUtilK {
    val netKRetrofit2 by lazy { NetKRetrofit2("http://jsonplaceholder.typicode.com", _converterFactory =  MoshiConverterFactory.create(UtilKMoshiWrapper.moshiBuilder)) }

    val netKRetrofit2Cache by lazy {
        //方式1
//        NetKRetrofitUtil.getRetrofit_ofMoshi(
//            "http://www.randomnumberapi.com/", OkHttpClient.Builder()
//                .addNetworkInterceptor(InterceptorUtil.getHttpLoggingInterceptor(TAG))
//                .build()
//        )
//            .supportCache(Cache(directory = File(UtilKFileDir.Internal.getCache(), "retrofit"), 10 * 1024))

        //方式2
        NetKRetrofit2("http://www.randomnumberapi.com", cacheSize = 10L.megaBytes(), networkInterceptors = listOf(InterceptorRetrofit2Cache()),_converterFactory =  MoshiConverterFactory.create(UtilKMoshiWrapper.moshiBuilder))
    }

    val netKOkHttp3Cache by lazy {
        //全局配置
//        CacheManager.setCacheModel(CacheMode.READ_CACHE_NETWORK_PUT)// 设置全局缓存模式
//            .setCacheTime(15 * 1000) // 设置全局 过期时间 (毫秒)
//            .useExpiredData(true)// 缓存过期时是否继续使用，仅对 ONLY_CACHE 生效
        NetKRetrofit2("https://www.wanandroid.com", interceptors = listOf(InterceptorOkhttp3Cache(NetKOkhttp3Cache(NetKRetrofit2.cacheFolder))),_converterFactory =  MoshiConverterFactory.create(UtilKMoshiWrapper.moshiBuilder))
    }

    /////////////////////////////////////////////////////////////////

    //示例1
    val apis: Apis = netKRetrofit2.create(Apis::class.java)

    //示例2
    val apis1: Apis = netKRetrofit2.create()

    //示例:缓存
    val apisRetrofitCache: Apis = netKRetrofit2Cache.create(Apis::class.java)

    //示例:缓存
    val apisOkHttp3Cache: Apis = netKOkHttp3Cache.create(Apis::class.java)

    /////////////////////////////////////////////////////////////////

//    private val _baseUrl get() = Config.restfulSP.baseUrl
//
//    val bizApi: BizApis by lazy {  NetKHttp(_baseUrl).create(BizApis::class.java) }
}