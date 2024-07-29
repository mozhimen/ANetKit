package com.mozhimen.netk.retrofit2.utils

/**
 * @ClassName CacheUtil
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/7/29
 * @Version 1.0
 */
object CacheUtil {
    @JvmStatic
    fun getHeaderCache(timeSeconds: Long): String =
        "Cache-Control:public,max-age=${timeSeconds}"
}