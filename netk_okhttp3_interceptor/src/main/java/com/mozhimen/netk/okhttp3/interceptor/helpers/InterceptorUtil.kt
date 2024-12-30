package com.mozhimen.netk.okhttp3.interceptor.helpers

import com.mozhimen.kotlin.elemk.android.util.cons.CLog
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import okhttp3.logging.HttpLoggingInterceptor

/**
 * @ClassName InterceptorUtil
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/7/31
 * @Version 1.0
 */
object InterceptorUtil {
    @JvmStatic
    fun get_ofHttpLogging(tag: String): HttpLoggingInterceptor =
        HttpLoggingInterceptor { msg -> UtilKLogWrapper.pringln_ofLongLog(CLog.VERBOSE, tag, msg) }.also { it.level = HttpLoggingInterceptor.Level.BODY }
}