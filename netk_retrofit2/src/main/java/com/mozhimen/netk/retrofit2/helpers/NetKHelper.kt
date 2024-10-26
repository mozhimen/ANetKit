package com.mozhimen.netk.retrofit2.helpers

import com.mozhimen.kotlin.elemk.commons.ISuspendAB_Listener
import com.mozhimen.kotlin.elemk.commons.ISuspendA_Listener
import com.mozhimen.kotlin.elemk.commons.ISuspend_AListener
import com.mozhimen.kotlin.elemk.mos.MResultIST
import com.mozhimen.kotlin.utilk.android.util.e
import com.mozhimen.kotlin.utilk.android.util.v
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.netk.retrofit2.cons.SNetKRes
import com.mozhimen.netk.retrofit2.cons.CResCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody

/**
 * @ClassName NetKHelper
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/10/26 11:01
 * @Version 1.0
 */
suspend fun <T> Flow<SNetKRes<T>>.asNetKRes(
    onSuccess: ISuspendA_Listener<T>,
    onFail: ISuspendAB_Listener<Int, String>/*onSuccess: suspend (data: R) -> Unit, onFail: suspend (code: Int, msg: String) -> Unit*/
) {
    NetKHelper.asNetRes(this, onSuccess, onFail)
}

suspend fun <T> Flow<SNetKRes<T>>.asNetKResSync(): MResultIST<T?> =
    NetKHelper.asNetKResSync(this)

//////////////////////////////////////////////////////////////////////////////////

object NetKHelper : BaseUtilK() {
    @JvmStatic
    fun <R> createFlow(invoke: ISuspend_AListener<R?>): Flow<SNetKRes<R>> = flow {
        emit(SNetKRes.Uninitialized)
        val result: R? = invoke()
        result?.let {
            emit(SNetKRes.MSuccess(result))
        } ?: emit(SNetKRes.Empty)
    }.onStart {
        emit(SNetKRes.Loading)
    }.catch { e ->
        emit(SNetKRes.MError(e))
    }.flowOn(Dispatchers.IO)

    @JvmStatic
    fun createStringFlow(invoke: ISuspend_AListener<ResponseBody?>): Flow<SNetKRes<String>> = flow {
        emit(SNetKRes.Uninitialized)
        val result: ResponseBody? = invoke()
        result?.let {
            emit(SNetKRes.MSuccess(result.string()))
        } ?: emit(SNetKRes.Empty)
    }.onStart {
        emit(SNetKRes.Loading)
    }.catch { e ->
        emit(SNetKRes.MError(e))
    }.flowOn(Dispatchers.IO)

    //////////////////////////////////////////////////////////////////////////////////

    @JvmStatic
    suspend fun <T> asNetRes(flow: Flow<SNetKRes<T>>, onSuccess: ISuspendA_Listener<T>, onFail: ISuspendAB_Listener<Int, String>) {
        flow.onEach {
            "asNetRes: ${it::class.simpleName}".v(TAG)
        }.collectLatest {
            when (it) {
                SNetKRes.Empty -> onFail(CResCode.Empty, "result is null")
                is SNetKRes.MSuccess -> onSuccess(it.data)
                is SNetKRes.MError -> {
                    val netKThrowable = NetKRepErrorParser.getThrowable(it.exception)
                    "asNetKRes: Error code ${netKThrowable.code} message ${netKThrowable.message}".e(TAG)
                    onFail(netKThrowable.code, netKThrowable.message)
                }

                else -> {}
            }
        }
    }

    @JvmStatic
    suspend fun <T> asNetKResSync(flow: Flow<SNetKRes<T>>): MResultIST<T?> {
        var res: MResultIST<T?> = MResultIST(CResCode.Empty, null, null)
        flow.onEach {
            "asNetKResSync: ${it::class.simpleName}".v(TAG)
        }.collectLatest {
            res = when (it) {
                SNetKRes.Empty -> MResultIST(CResCode.Empty, "result is null", null)
                is SNetKRes.MSuccess -> MResultIST(CResCode.SUCCESS, null, it.data)
                is SNetKRes.MError -> {
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