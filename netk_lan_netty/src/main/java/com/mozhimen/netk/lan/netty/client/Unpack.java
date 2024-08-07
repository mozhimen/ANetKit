package com.mozhimen.netk.lan.netty.client;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Unpack {

    protected ByteBuffer mBuffer;


    public Unpack(byte[] bytes, int offset, int length) {
        mBuffer = ByteBuffer.wrap(bytes, offset, length);
        mBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public Unpack(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    public int popInt() {
        try {
            return mBuffer.getInt();
        } catch (BufferUnderflowException bEx) {
            bEx.printStackTrace();
        }
        return 0;
    }
}
