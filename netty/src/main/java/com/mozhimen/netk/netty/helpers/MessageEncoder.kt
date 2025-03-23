package com.mozhimen.netk.netty.helpers

import com.mozhimen.netk.netty.mos.Message
import com.mozhimen.netk.netty.utils.UtilByteBuf
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * @ClassName MessageEncoder
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/20
 * @Version 1.0
 */
/**
 * Describe: 编码
 */
class MessageEncoder : MessageToByteEncoder<Message>() {
    override fun encode(channelHandlerContext: ChannelHandlerContext, message: Message, byteBuf: ByteBuf) {
        UtilByteBuf.writeString(byteBuf, message.body, message.len)
    }
}