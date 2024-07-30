package com.mozhimen.netk.retrofit2.test.customs

import com.mozhimen.basick.manifestk.annors.AManifestKRequire
import com.mozhimen.basick.manifestk.cons.CApplication
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.basick.utilk.kotlin.megaBytes
import com.mozhimen.netk.okhttp3.interceptor.impls.InterceptorCacheOffline
import com.mozhimen.netk.okhttp3.interceptor.impls.InterceptorCacheOnline
import com.mozhimen.netk.retrofit2.NetKRetrofit

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

    val netKRetrofitCache = NetKRetrofit(_baseUrl, cacheSize = 10L.megaBytes(), interceptors = listOf(InterceptorCacheOffline()))

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