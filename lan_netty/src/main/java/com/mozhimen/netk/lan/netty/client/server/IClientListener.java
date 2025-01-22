package com.mozhimen.netk.lan.netty.client.server;

import com.mozhimen.netk.lan.netty.client.DemoMessage;

import io.netty.channel.Channel;

public interface IClientListener {

    /**
     * 客户端建立连接
     *
     */
    void onClientConnect(Channel channel);

    /**
     * 客户端断开连接
     */
    void onClientDisConnect(Channel channel);

    /**
     * 接收到客户端消息
     */
    void onClientMessage(DemoMessage message, Channel channel);

}
