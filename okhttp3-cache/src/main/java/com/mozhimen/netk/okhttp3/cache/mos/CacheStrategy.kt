package com.mozhimen.netk.okhttp3.cache.mos

import com.mozhimen.netk.okhttp3.cache.annors.ACacheMode

/**
 * @author : Aleyn
 * @date : 2022/06/24 19:54
 */
data class CacheStrategy constructor(
    var cacheKey: String = "", //缓存key
    val cacheTime: Long = -1, //过期时间  默认-1 不过期
    @ACacheMode val cacheMode: String? = null //缓存模式
)