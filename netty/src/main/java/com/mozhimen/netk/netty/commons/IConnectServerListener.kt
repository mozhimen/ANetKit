package com.mozhimen.netk.netty.commons

import com.mozhimen.netk.netty.mos.Message
import io.netty.channel.Channel

/**
 * @ClassName IConnectServerListener
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/13
 * @Version 1.0
 */
interface IConnectServerListener {
    /**
     * 客户端建立连接
     */
    fun onClientConnect(channel: Channel?)

    /**
     * 客户端断开连接
     */
    fun onClientDisconnect(channel: Channel?)

    /**
     * 接收到客户端消息
     */
    fun onClientReceiveMessage(message: Message?, channel: Channel?)
}