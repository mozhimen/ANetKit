package com.mozhimen.netk.retrofit2.cons

/**
 * @ClassName NetKRep
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/10/22 18:42
 * @Version 1.0
 */
sealed class SNetKRes<out T> {
    object Uninitialized : SNetKRes<Nothing>()

    object Loading : SNetKRes<Nothing>()

    object Empty : SNetKRes<Nothing>()

    data class MSuccess<R>(val data: R) : SNetKRes<R>()

    data class MError(val exception: Throwable) : SNetKRes<Nothing>()
}