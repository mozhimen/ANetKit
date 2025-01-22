package com.mozhimen.netk.lan.netty.client;

/**
 * Describe:
 * 协议：IntString
 * Int : 包体大小（四个字节）
 * String(JSONString)
 */
public class DemoMessage {

    private int messageSize;

    private String body;

    public int getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
