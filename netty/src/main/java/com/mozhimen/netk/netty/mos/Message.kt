package com.mozhimen.netk.netty.mos

/**
 * @ClassName Message
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/13
 * @Version 1.0
 */
/**
 * Describe:
 * 协议：IntString
 * Int : 包体大小（四个字节）
 * String(JSONString)
 */
data class Message(
    var body: String? = "",
    var len: Int = 0,
)