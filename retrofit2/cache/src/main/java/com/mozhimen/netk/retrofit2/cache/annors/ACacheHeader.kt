package com.mozhimen.netk.retrofit2.cache.annors

import java.util.concurrent.TimeUnit

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ACacheHeader constructor(
    val value: Int,
    /**
     * [TimeUnit] for the cache to stay
     */
    val unit: TimeUnit = TimeUnit.SECONDS,
    /**
     * Override server-side response headers?
     * default: `false`
     */
    val override: Boolean = false
)
