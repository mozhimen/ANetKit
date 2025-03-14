package com.mozhimen.netk.netty.commons

import com.mozhimen.netk.netty.mos.Message

/**
 * @ClassName IConnectClientListener
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/13
 * @Version 1.0
 */
interface IConnectClientListener {

    /**
     * 开始连接
     */
    fun onServerConnectStart()

    /**
     * 已连接
     */
    fun onServerConnect()

    /**
     * 断开连接
     */
    fun onServerDisconnect(onPurpose: Boolean)

    /**
     * 连接失败
     */
    fun onServerConnectFail()

    /**
     * 初始化，重连接
     */
    fun onServerReconnect()

    /**
     * 接收到服务端消息
     */
    fun onServerReceiveMessage(message: Message?)
}