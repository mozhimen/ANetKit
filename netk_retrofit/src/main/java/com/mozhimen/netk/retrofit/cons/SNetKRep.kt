package com.mozhimen.netk.retrofit.cons

/**
 * @ClassName NetKRep
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/10/22 18:42
 * @Version 1.0
 */
sealed class SNetKRep<out T> {
    object Uninitialized : SNetKRep<Nothing>()

    object Loading : SNetKRep<Nothing>()

    object Empty : SNetKRep<Nothing>()

    data class MSuccess<R>(val data: R) : SNetKRep<R>()

    data class MError(val exception: Throwable) : SNetKRep<Nothing>()
}