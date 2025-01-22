package com.mozhimen.netk.okhttp3.interceptor.helpers

import okhttp3.CacheControl
import java.util.concurrent.TimeUnit

/**
 * @ClassName CacheUtil
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/7/29
 * @Version 1.0
 */
object CacheUtil {
    @JvmStatic
    fun getHeaderCacheOnline(timeSeconds: Int): String =
        "Cache-Control:public, max-age=${timeSeconds}"

    @JvmStatic
    fun getCacheControl_ofMaxAge(timeSeconds: Int): CacheControl =
        CacheControl.Builder().maxAge(timeSeconds, TimeUnit.SECONDS).build()

    @JvmStatic
    fun getHeaderCacheOffline(timeSeconds: Int): String =
        "Cache-Control:public, only-if-cached, max-stale=$timeSeconds"
}