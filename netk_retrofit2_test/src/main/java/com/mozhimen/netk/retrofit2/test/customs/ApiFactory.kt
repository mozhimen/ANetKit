package com.mozhimen.netk.retrofit2.test.customs

import com.mozhimen.basick.utilk.kotlin.megaBytes
import com.mozhimen.netk.retrofit2.NetKRetrofit
import com.mozhimen.netk.retrofit2.cache.impls.InterceptorANetKRetrofit2Cache

/**
 * @ClassName ApiFactory
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2021/12/13 22:16
 * @Version 1.0
 */
object ApiFactory {
    private val _baseUrl = "http://jsonplaceholder.typicode.com"

    val netKRetrofit = NetKRetrofit(_baseUrl)

    val netKRetrofitCache = NetKRetrofit(_baseUrl, cacheSize = 10L.megaBytes(), networkInterceptors = listOf(InterceptorANetKRetrofit2Cache()))

    //示例1
    val apis: Apis = netKRetrofit.create(Apis::class.java)

    //示例2
    val apis1: Apis = netKRetrofit.create()

    //示例:缓存
    val apisCache:Apis = netKRetrofitCache.create(Apis::class.java)

    /////////////////////////////////////////////////////////////////

//    private val _baseUrl get() = Config.restfulSP.baseUrl
//
//    val bizApi: BizApis by lazy {  NetKHttp(_baseUrl).create(BizApis::class.java) }
}