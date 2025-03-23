package com.mozhimen.netk.netty.mos

import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @ClassName MessageBuffer
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/20
 * @Version 1.0
 */
open class MessageBuffer @JvmOverloads constructor(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size) {
    protected var mBuffer: ByteBuffer = ByteBuffer.wrap(bytes, offset, length)

    ////////////////////////////////////////////////////////////////////////////////////////

    init {
        mBuffer.order(ByteOrder.LITTLE_ENDIAN)
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    fun popInt(): Int {
        try {
            return mBuffer.getInt()
        } catch (e: BufferUnderflowException) {
            e.printStackTrace()
        }
        return 0
    }
}
