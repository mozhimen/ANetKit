package com.mozhimen.netk.okhttp3.interceptor.impls

import com.mozhimen.kotlin.lintk.optins.permission.OPermission_ACCESS_NETWORK_STATE
import com.mozhimen.kotlin.utilk.wrapper.UtilKNet
import com.mozhimen.netk.okhttp3.interceptor.annors.AInterceptor
import com.mozhimen.netk.okhttp3.interceptor.commons.IInterceptor
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import kotlin.math.abs


/**
 * @ClassName InterceptorCahceOffline
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/7/30
 * @Version 1.0
 */
@AInterceptor
class InterceptorCacheOffline(
    private val _expiredTimeOffline: Int = 7 * 24 * 60 * 60,
    private val _checkNetTime: Long = 2000L
) : IInterceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (request.method == "GET")
            if (_isConnected) {
//            val tempCacheControl = CacheControl.Builder()
//                .onlyIfCached()
//                .maxStale(_maxStale, TimeUnit.MINUTES)
//                .build()
//            request = request.newBuilder()
//                .cacheControl(tempCacheControl)
//                .build()
                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build()
            } else {
                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=$_expiredTimeOffline")
                    .build()
            }
        return chain.proceed(request)
    }

    private var _lastTime = System.currentTimeMillis()

    @OptIn(OPermission_ACCESS_NETWORK_STATE::class)
    private var _isConnected: Boolean = UtilKNet.isConnected_ofActive()
        get() {
            val time = System.currentTimeMillis()
            return if (abs(time - _lastTime) > _checkNetTime) {
                _lastTime = time
                UtilKNet.isConnected_ofActive().also { field = it }
            } else {
                field
            }
        }
}