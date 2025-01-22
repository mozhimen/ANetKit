package com.mozhimen.netk.okhttp3.interceptor.bases

import com.mozhimen.netk.okhttp3.interceptor.commons.IInterceptor
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @ClassName HostSelectionInterceptorA
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/7/3
 * @Version 1.0
 */
abstract class BaseInterceptorHostSelection : IInterceptor {
    protected open var _httpUrl: HttpUrl? = null//HttpUrl.parse(BuildConfig.DEVELOPMENT_BASE_URL);

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (_httpUrl != null) {
            try {
                val newUrl: HttpUrl = request.url.newBuilder()
                    .scheme(_httpUrl!!.scheme)
                    .host(_httpUrl!!.toUrl().toURI().host)
                    .port(_httpUrl!!.port)
                    .build();
                request = request.newBuilder()
                    .url(newUrl)
                    .build()
            } catch (_: Exception) {
            }
        }
        return chain.proceed(request)
    }
}