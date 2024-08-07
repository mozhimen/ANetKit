package com.mozhimen.netk.lan.netty.client.client;


import com.mozhimen.netk.lan.netty.client.DemoMessage;

public interface IConnectionListener {

    /**
     * 开始连接
     */
    void onStartConnect();

    /**
     * 已连接
     */
    void onConnect();

    /**
     * 断开连接
     */
    void onDisConnect(boolean onPurpose);

    /**
     * 连接失败
     */
    void onConnectFailure();

    /**
     * 初始化，重连接
     */
    void onReInit();

    void onReceiveMessage(DemoMessage message);
}
