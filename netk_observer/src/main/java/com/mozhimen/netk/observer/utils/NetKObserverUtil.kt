package com.mozhimen.netk.observer.utils

import android.util.Log
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.netk.observer.annors.ANetKObserver
import java.lang.reflect.Method

/**
 * @ClassName NetKObserverUtil
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/2/18 21:41
 * @Version 1.0
 */
object NetKObserverUtil : IUtilK {
    @JvmStatic
    fun findMethodOfChild(clazz: Class<*>): Method? {
        val methods = clazz.methods
        for (method in methods) {
            if (method.name != "onChanged") continue
            val genericReturnType = method.genericReturnType.toString()// 判断返回类型
            if ("void" != genericReturnType)
                throw RuntimeException("The return type of the method < ${method.name} > must be void!")// 方法的返回类型必须为void
            val parameterTypes = method.genericParameterTypes// 检查参数，必须有一个String型的参数
            UtilKLogWrapper.d(TAG, "findAnnotationMethod: ${parameterTypes[0]}?=${Set::class.java.name}<${String::class.java.name}>")
            if (parameterTypes.size != 1 || parameterTypes[0].toString() != "${Set::class.java.name}<${String::class.java.name}>")
                throw RuntimeException("The parameter types size of the method < ${method.name} > must be one (type name must be java.util.Set<java.lang.String>)!")
            return method
        }
        return null
    }

    // 查找监听的方法
    @JvmStatic
    fun findMethodOfAnnotation(clazz: Class<*>): Method? {
        val methods = clazz.methods
        for (method in methods) {
            method.getAnnotation(ANetKObserver::class.java) ?: continue// 看是否有注解
            val genericReturnType = method.genericReturnType.toString()// 判断返回类型
            if ("void" != genericReturnType)
                throw RuntimeException("The return type of the method < ${method.name} > must be void!")// 方法的返回类型必须为void
            val parameterTypes = method.genericParameterTypes// 检查参数，必须有一个String型的参数
            UtilKLogWrapper.d(TAG, "findAnnotationMethod: ${parameterTypes[0]}?=${Set::class.java.name}<${String::class.java.name}>")
            if (parameterTypes.size != 1 || parameterTypes[0].toString() != "${Set::class.java.name}<${String::class.java.name}>")
                throw RuntimeException("The parameter types size of the method < ${method.name} > must be one (type name must be java.util.Set<java.lang.String>)!")
            return method
        }
        return null
    }
}