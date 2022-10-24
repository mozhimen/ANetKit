package com.mozhimen.netk

import com.mozhimen.netk.customs.AsyncFactory
import com.mozhimen.netk.customs.AsyncInterceptorLog


/**
 * @ClassName ApiFactory
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2021/12/13 22:16
 * @Version 1.0
 */
object ApiFactorys {
    private val _baseUrl = "https://api.caiyunapp.com/v2.5/cIecnVlovchAFYIk/"

    private val _netkAsync: NetKAsync by lazy { NetKAsync(_baseUrl, AsyncFactory(_baseUrl)) }
    private val _netkRxJava: NetKRxJava by lazy { NetKRxJava(_baseUrl) }

    init {
        _netkAsync.addInterceptor(AsyncInterceptorLog())
    }

    fun <T> createAsync(api: Class<T>): T {
        return _netkAsync.create(api)
    }

    fun <T> createRxJava(api: Class<T>): T {
        return _netkRxJava.create(api)
    }
}