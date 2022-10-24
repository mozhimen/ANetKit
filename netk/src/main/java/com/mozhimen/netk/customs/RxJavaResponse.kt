package com.mozhimen.netk.customs

import com.mozhimen.basick.extsk.toJson
import com.mozhimen.netk.commons.INetKConverter
import com.mozhimen.netk.helpers.StatusParser
import com.mozhimen.netk.mos.NetKResponse
import com.mozhimen.netk.mos.NetKThrowable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @ClassName RxJavaResponse
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/5/13 17:07
 * @Version 1.0
 */
abstract class RxJavaResponse<T : Any>(private val _converter: INetKConverter = AsyncConverter()) :
    Observer<T> {
    abstract fun onSuccess(response: NetKResponse<T>)
    abstract fun onFailed(code: Int, message: String?)

    override fun onNext(value: T) {
        try {
            val response: NetKResponse<T> = parseResponse(value)
            if (response.isSuccessful()) {
                onSuccess(response)
            } else {
                onFailed(0, "数据请求失败")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            val ex = StatusParser.getThrowable(e)
            onFailed(ex.code, ex.message)
        }
    }

    override fun onSubscribe(d: Disposable) {}

    override fun onComplete() {}

    override fun onError(e: Throwable) {
        e.printStackTrace()
        val ex: NetKThrowable = if (e is NetKThrowable) e else StatusParser.getThrowable(e)
        onFailed(ex.code, ex.message)
    }

    private fun <T> parseResponse(value: T): NetKResponse<T> {
        val rawData: String = value!!::class.toJson() /*UtilKJson.t2Json(value)*/
        return _converter.convert(rawData, getParentGenericTypeClazz(this@RxJavaResponse, 0)!!)
    }

    private fun getParentGenericTypeClazz(obj: Any, index: Int = 0): Class<*>? =
        getParentGenericType(obj, index) as? Class<*>?

    private fun getParentGenericType(obj: Any, index: Int = 0): Type? =
        obj::class.java
            .genericSuperclass
            .let { it as ParameterizedType }
            .actualTypeArguments.filterIsInstance<Class<*>>()
            .run {
                if (this.isNotEmpty() && index in this.indices)
                    this[index]
                else
                    null
            }
}