package com.mozhimen.netk.observer.utils

import com.mozhimen.netk.observer.annors.ANetKObserver
import java.lang.reflect.Method

/**
 * @ClassName NetKObserverUtil
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/2/18 21:41
 * @Version 1.0
 */
object NetKObserverUtil {
    // 查找监听的方法
    @JvmStatic
    fun findAnnotationMethod(clazz: Class<*>): Method? {
        val methods = clazz.methods
        for (method in methods) {
            method.getAnnotation(ANetKObserver::class.java) ?: continue// 看是否有注解
            val genericReturnType = method.genericReturnType.toString()// 判断返回类型
            if ("void" != genericReturnType)
                throw RuntimeException("The return type of the method【${method.name}】 must be void!")// 方法的返回类型必须为void
            val parameterTypes = method.genericParameterTypes// 检查参数，必须有一个String型的参数
            if (parameterTypes.size != 1 || parameterTypes[0].toString() != "class ${String::class.java.name}")
                throw RuntimeException("The parameter types size of the method【${method.name}】must be one (type name must be java.lang.String)!")
            return method
        }
        return null
    }
}