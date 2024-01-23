package com.mozhimen.netk.retrofit.commons

/**
 * @ClassName NetKRep
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/10/22 18:42
 * @Version 1.0
 */
sealed class NetKRep<out T> {
    object Uninitialized : NetKRep<Nothing>()

    object Loading : NetKRep<Nothing>()

    object Empty : NetKRep<Nothing>()

    data class MSuccess<R>(val data: R) : NetKRep<R>()

    data class MError(val exception: Throwable) : NetKRep<Nothing>()
}