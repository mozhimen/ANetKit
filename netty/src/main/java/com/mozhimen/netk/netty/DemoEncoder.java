package com.mozhimen.netk.netty;

import com.mozhimen.netk.netty.mos.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.mozhimen.netk.netty.utils.UtilByteBuf;

/**
 * Describe: 编码
 */
public class DemoEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) {
        UtilByteBuf.writeString(byteBuf, message.getBody(), message.getLen());
    }

}
