package com.mozhimen.netk.retrofit2.impls

import com.mozhimen.kotlin.elemk.commons.IA_Listener
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.coroutines.resumeWithException

/**
 * @ClassName FlowCallAdapterFactory
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/9/6
 * @Version 1.0
 */
class FlowCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (Flow::class.java != getRawType(returnType)) {
            return null
        }

        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                "Flow return type must be parameterized as Flow<Foo>"
            )
        }

        val responseType = getParameterUpperBound(0, returnType)
        val rawDeferredType = getRawType(responseType)

        return if (rawDeferredType == Response::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException(
                    "Response must be parameterized as Response<Foo> or Response<out Foo>"
                )
            }
            ResponseCallAdapter<Any>(getParameterUpperBound(0, responseType))
        } else {
            BodyCallAdapter<Any>(responseType)
        }
    }

    inner class BodyCallAdapter<T>(private val _responseType: Type) : CallAdapter<T, Flow<T>> {
        override fun responseType(): Type {
            return _responseType
        }

        override fun adapt(call: Call<T>): Flow<T> = flow {
            emit(suspendCancellableCoroutine { continuation ->
                call.registerCallback(continuation) { response ->
                    continuation.resumeWith(kotlin.runCatching {
                        if (response.isSuccessful) {
                            response.body() ?: throw NullPointerException("Response body is null: $response")
                        } else {
                            throw HttpException(response)
                        }
                    })
                }

                call.registerOnCancellation(continuation)
            })
        }
    }

    inner class ResponseCallAdapter<T>(private val _responseType: Type) : CallAdapter<T, Flow<Response<T>>> {
        override fun responseType(): Type {
            return _responseType
        }

        override fun adapt(call: Call<T>): Flow<Response<T>> = flow {
            emit(suspendCancellableCoroutine { continuation ->
                call.registerCallback(continuation) { response ->
                    continuation.resumeWith(kotlin.runCatching {
                        if (response.isSuccessful) {
                            response
                        } else {
                            throw HttpException(response)
                        }
                    })
                }

                call.registerOnCancellation(continuation)
            })
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    private fun Call<*>.registerOnCancellation(
        continuation: CancellableContinuation<*>
    ) {
        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (e: Exception) {
                // Ignore cancel exception
            }
        }
    }

    private fun <T> Call<T>.registerCallback(
        continuation: CancellableContinuation<*>,
        onSuccess: IA_Listener<Response<T>>
    ) {
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                onSuccess.invoke(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }

        })
    }
}


