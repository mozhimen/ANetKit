package com.mozhimen.netk.okhttp3.cache.annors

import androidx.annotation.StringDef

/**
 * @ClassName ACacheMode
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/8/2
 * @Version 1.0
 */
@StringDef(
    ACacheMode.NETWORK,
    ACacheMode.CACHE,
    ACacheMode.NETWORK__REFRESH_CACHE,
    ACacheMode.CACHE__NETWORK__REFRESH_CACHE_EXPIRATION,
    ACacheMode.NETWORK__CACHE
)
annotation class ACacheMode {
    companion object {
        /**
         * 只请求网络，默认 (不加缓存)
         */
        const val NETWORK = "NETWORK"

        /**
         * 只读取缓存(没有缓存抛出异常)
         */
        const val CACHE = "CACHE"

        /**
         * 请求完，写入缓存
         */
        const val NETWORK__REFRESH_CACHE = "NETWORK__REFRESH_CACHE"

        /**
         * 先请求网络，网络请求失败使用缓存  (网络请求成功，写入缓存)
         */
        const val NETWORK__CACHE = "NETWORK__CACHE"

        /**
         * 先读取缓存，缓存失效再请求网络更新缓存
         */
        const val CACHE__NETWORK__REFRESH_CACHE_EXPIRATION = "CACHE__NETWORK__REFRESH_CACHE_EXPIRATION"

        /**
         * 总是先读取缓存, 无论是否到失效时间都请求网络更新缓存
         */
//        const val CACHE__NETWORK__REFRESH_CACHE_ALWAYS = "CACHE__NETWORK__REFRESH_CACHE_ALWAYS"
    }
}
