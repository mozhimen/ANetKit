package com.mozhimen.netk.observer.annors

/**
 * @ClassName ANetKObserver
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/9/27 14:01
 * @Version 1.0
 */
/**
 *     @ANetKObserver
 *     fun onNetChange(net: String) {
 *         vb.netkObserverTxt.text = net
 *     }
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ANetKObserver
