package com.mozhimen.netk.retrofit2.test.mos

/**
 * @ClassName BaseRes
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/8/2
 * @Version 1.0
 */
data class BaseRes<T>(
    val errorMsg: String,
    val errorCode: Int,
    val data: T?
)