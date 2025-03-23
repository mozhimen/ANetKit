package com.mozhimen.netk.netty.helpers

import com.mozhimen.netk.netty.mos.Message
import com.mozhimen.netk.netty.mos.MessageBuffer
import com.mozhimen.netk.netty.utils.UtilByteBuf.readString
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import kotlin.concurrent.Volatile

/**
 * @ClassName MessageDecoder
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/20
 * @Version 1.0
 */
/**
 * Describe: 解码
 */
class MessageDecoder : ByteToMessageDecoder() {
    /**
     * 在工作线程中回调
     */
    override fun decode(channelHandlerContext: ChannelHandlerContext, byteBuf: ByteBuf, list: MutableList<Any>) {
        //记录读取的位置
        byteBuf.markReaderIndex()

        //判断缓冲区是否可读
        if (!byteBuf.isReadable) {
            byteBuf.resetReaderIndex()
            return
        }

        //如果可读消息不足四个字节，那么重置指针位置，返回
        if (byteBuf.readableBytes() < LENGTH_BYTES.size) {
            byteBuf.resetReaderIndex()
            return
        }
        byteBuf.readBytes(LENGTH_BYTES)
        val messageBuffer = MessageBuffer(LENGTH_BYTES)
        packetSize = messageBuffer.popInt()
        if (packetSize < 0 || packetSize > MAX_PACK_SIZE) {
            throw RuntimeException("Invalid packet, size = $packetSize")
        }

        //如果可读字节数不足，那么重置指针位置，返回
        if (byteBuf.readableBytes() < packetSize - LENGTH_BYTES.size) {
            byteBuf.resetReaderIndex()
            return
        }
        val readBytes = byteBuf.readBytes(packetSize)

        val message = Message()
        message.len = packetSize
        message.body = readString(readBytes, packetSize)!!

        packetSize = 0
        list.add(message)
    }

    companion object {
        @Volatile
        private var LENGTH_BYTES = ByteArray(4)
        private const val MAX_PACK_SIZE = 1024 * 1024 * 64

        @Volatile
        private var packetSize = 0
    }
}