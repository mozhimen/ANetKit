package com.mozhimen.netk.okhttp3.utils

import okhttp3.RequestBody
import okio.Buffer

/**
 * @ClassName RequestBodyUtil
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/12/30
 * @Version 1.0
 */
fun RequestBody.requestBody2string():String =
    RequestBodyUtil.requestBody2string(this)

///////////////////////////////////////////////////////////////

object RequestBodyUtil {
    @JvmStatic
    fun requestBody2string(requestBody: RequestBody): String =
        try {
            val copy: RequestBody = requestBody
            val buffer = Buffer()
            copy.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            ""
        }
}