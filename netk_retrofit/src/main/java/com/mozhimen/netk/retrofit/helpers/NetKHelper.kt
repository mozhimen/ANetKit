package com.mozhimen.netk.retrofit.helpers

import com.mozhimen.basick.elemk.commons.ISuspendAB_Listener
import com.mozhimen.basick.elemk.commons.ISuspendA_Listener
import com.mozhimen.basick.elemk.commons.ISuspend_AListener
import com.mozhimen.basick.elemk.mos.MResultIST
import com.mozhimen.basick.utilk.android.util.e
import com.mozhimen.basick.utilk.android.util.v
import com.mozhimen.basick.utilk.bases.BaseUtilK
import com.mozhimen.netk.retrofit.cons.SNetKRep
import com.mozhimen.netk.retrofit.cons.CResCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * @ClassName NetKHelper
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/10/26 11:01
 * @Version 1.0
 */
suspend fun <T> Flow<SNetKRep<T>>.asNetKRes(
    onSuccess: ISuspendA_Listener<T>,
    onFail: ISuspendAB_Listener<Int, String>/*onSuccess: suspend (data: R) -> Unit, onFail: suspend (code: Int, msg: String) -> Unit*/
) {
    NetKHelper.asNetRes(this, onSuccess, onFail)
}

suspend fun <T> Flow<SNetKRep<T>>.asNetKResSync(): MResultIST<T?> =
    NetKHelper.asNetKResSync(this)

object NetKHelper : BaseUtilK() {
    @JvmStatic
    fun <R> createFlow(invoke: ISuspend_AListener<R?>): Flow<SNetKRep<R>> = flow {
        emit(SNetKRep.Uninitialized)
        val result: R? = invoke()
        result?.let {
            emit(SNetKRep.MSuccess(result))
        } ?: emit(SNetKRep.Empty)
    }.onStart {
        emit(SNetKRep.Loading)
    }.catch { e ->
        emit(SNetKRep.MError(e))
    }.flowOn(Dispatchers.IO)

    @JvmStatic
    suspend fun <T> asNetRes(flow: Flow<SNetKRep<T>>, onSuccess: ISuspendA_Listener<T>, onFail: ISuspendAB_Listener<Int, String>) {
        flow.onEach {
            "asNetRes: ${it::class.simpleName}".v(TAG)
        }.collectLatest {
            when (it) {
                SNetKRep.Empty -> onFail(CResCode.Empty, "result is null")
                is SNetKRep.MSuccess -> onSuccess(it.data)
                is SNetKRep.MError -> {
                    val netKThrowable = NetKRepErrorParser.getThrowable(it.exception)
                    "asNetKRes: Error code ${netKThrowable.code} message ${netKThrowable.message}".e(TAG)
                    onFail(netKThrowable.code, netKThrowable.message)
                }

                else -> {}
            }
        }
    }

    @JvmStatic
    suspend fun <T> asNetKResSync(flow: Flow<SNetKRep<T>>): MResultIST<T?> {
        var res: MResultIST<T?> = MResultIST(CResCode.Empty, null, null)
        flow.onEach {
            "asNetKResSync: ${it::class.simpleName}".v(TAG)
        }.collectLatest {
            res = when (it) {
                SNetKRep.Empty -> MResultIST(CResCode.Empty, "result is null", null)
                is SNetKRep.MSuccess -> MResultIST(CResCode.SUCCESS, null, it.data)
                is SNetKRep.MError -> {
                    val netKThrowable = NetKRepErrorParser.getThrowable(it.exception)
                    "asNetKResSync: Error code ${netKThrowable.code} message ${netKThrowable.message}".e(TAG)
                    MResultIST(netKThrowable.code, netKThrowable.message, null)
                }

                else -> MResultIST(CResCode.UNKNOWN, "result is error", null)
            }
        }
        return res
    }
}