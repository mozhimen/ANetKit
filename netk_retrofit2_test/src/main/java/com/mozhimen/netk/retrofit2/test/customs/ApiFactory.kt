package com.mozhimen.netk.retrofit2.test.customs

import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.basick.utilk.kotlin.megaBytes
import com.mozhimen.netk.okhttp3.cache.NetKOkhttp3Cache
import com.mozhimen.netk.okhttp3.cache.impls.InterceptorOkhttp3Cache
import com.mozhimen.netk.retrofit2.NetKRetrofit
import com.mozhimen.netk.retrofit2.cache.impls.InterceptorRetrofit2Cache

/**
 * @ClassName ApiFactory
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2021/12/13 22:16
 * @Version 1.0
 */
object ApiFactory : IUtilK {
    val netKRetrofit by lazy { NetKRetrofit("http://jsonplaceholder.typicode.com") }

    val netKRetrofitCache by lazy {
        //方式1
//        NetKRetrofitUtil.getRetrofit_ofMoshi(
//            "http://www.randomnumberapi.com/", OkHttpClient.Builder()
//                .addNetworkInterceptor(InterceptorUtil.getHttpLoggingInterceptor(TAG))
//                .build()
//        )
//            .supportCache(Cache(directory = File(UtilKFileDir.Internal.getCache(), "retrofit"), 10 * 1024))

        //方式2
        NetKRetrofit("http://www.randomnumberapi.com", cacheSize = 10L.megaBytes(), networkInterceptors = listOf(InterceptorRetrofit2Cache()))
    }

    val netKOkHttp3Cache by lazy {
        //全局配置
//        CacheManager.setCacheModel(CacheMode.READ_CACHE_NETWORK_PUT)// 设置全局缓存模式
//            .setCacheTime(15 * 1000) // 设置全局 过期时间 (毫秒)
//            .useExpiredData(true)// 缓存过期时是否继续使用，仅对 ONLY_CACHE 生效
        NetKRetrofit("https://www.wanandroid.com", interceptors = listOf(InterceptorOkhttp3Cache(NetKOkhttp3Cache(NetKRetrofit.cacheFolder))))
    }

    /////////////////////////////////////////////////////////////////

    //示例1
    val apis: Apis = netKRetrofit.create(Apis::class.java)

    //示例2
    val apis1: Apis = netKRetrofit.create()

    //示例:缓存
    val apisRetrofitCache: Apis = netKRetrofitCache.create(Apis::class.java)

    //示例:缓存
    val apisOkHttp3Cache: Apis = netKOkHttp3Cache.create(Apis::class.java)

    /////////////////////////////////////////////////////////////////

//    private val _baseUrl get() = Config.restfulSP.baseUrl
//
//    val bizApi: BizApis by lazy {  NetKHttp(_baseUrl).create(BizApis::class.java) }
}