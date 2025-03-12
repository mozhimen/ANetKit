package com.mozhimen.netk.netty

import com.mozhimen.kotlin.utilk.kotlin.bytes2str
import com.mozhimen.kotlin.utilk.kotlin.str2bytes
import io.netty.buffer.ByteBuf

/**
 * @ClassName ByteBufUtil
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/12
 * @Version 1.0
 */
object UtilByteBuf {
    @JvmStatic
    fun writeString(byteBuf: ByteBuf, str: String, len: Int) {
        try {
            //每个字符串之前需要加一个int，用于指定这个字符串的长度！！！
            byteBuf.writeIntLE(len)
            byteBuf.writeBytes(str.str2bytes()/*getBytes(StandardCharsets.UTF_8)*/)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun readString(byteBuf: ByteBuf, len: Int): String? {
        val bytes = ByteArray(len)
        try {
            byteBuf.readBytes(bytes)
            return bytes.bytes2str()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}