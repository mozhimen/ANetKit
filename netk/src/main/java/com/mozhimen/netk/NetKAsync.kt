package com.mozhimen.netk

import com.mozhimen.kotlin.lintk.annors.AManifestRequire
import com.mozhimen.kotlin.elemk.android.cons.CPermission
import com.mozhimen.netk.commons.INetKFactory
import com.mozhimen.netk.commons.INetKInterceptor
import com.mozhimen.netk.helpers.MethodParser
import com.mozhimen.netk.helpers.Scheduler
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 * @ClassName NetK
 * @Description TODO
 * @Author mozhimen
 * @Date 2021/9/26 20:46
 * @Version 1.0
 */
@AManifestRequire(CPermission.INTERNET)
open class NetKAsync constructor(
    private val _baseUrl: String,
    private val _factory: INetKFactory
) {
    private var _interceptors = mutableListOf<INetKInterceptor>()
    private var _methodServices = ConcurrentHashMap<Method, MethodParser>()
    private val _scheduler by lazy {
        Scheduler(_factory, _interceptors)
    }

    fun addInterceptor(interceptor: INetKInterceptor) {
        _interceptors.add(interceptor)
    }

    fun addInterceptors(interceptors: Array<INetKInterceptor>) {
        _interceptors.addAll(interceptors)
    }

    /**
     * interface ApiService{
     *  @Headers("auth-token:token","accountId:123456")
     *  @BaseUrl("http://api.mozhimen.top/as/")
     *  @Post("/cities/{province}")
     *  @Get("/cities")
     *  fun listCities(@Path("province") pronvice:Int ,@Filed("page") page:Int): CallK<JsonObject>
     * }
     * @param service Class<T>
     * @return T
     */
    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service),
            object : InvocationHandler {
                override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
                    var methodParser = _methodServices[method]
                    if (methodParser == null) {
                        methodParser = MethodParser.parse(_baseUrl, method)
                        _methodServices[method] = methodParser
                    }

                    //bugFix: 此处应当考虑methodParser复用,每次调用都应当解析入参
                    val newRequest = methodParser.newRequest(method, args)
                    return _scheduler.newCall(newRequest)
                }
            }) as T
    }
}