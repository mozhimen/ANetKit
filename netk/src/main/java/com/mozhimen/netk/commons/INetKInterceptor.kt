package com.mozhimen.netk.commons

/**
 * @ClassName Interceptor
 * @Description TODO
 * @Author mozhimen
 * @Date 2021/9/26 20:39
 * @Version 1.0
 */
interface INetKInterceptor {
    val TAG: String
        get() = "${this.javaClass.simpleName}>>>>>"

    fun intercept(chain: INetKChain): Boolean
}