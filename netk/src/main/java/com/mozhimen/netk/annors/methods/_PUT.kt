package com.mozhimen.netk.annors.methods

/**
 * @ClassName PUTK
 * @Description TODO
 * @Author Kolin Zhao / Mozhimen
 * @Version 1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class _PUT(val value: String, val formPost: Boolean = false)
