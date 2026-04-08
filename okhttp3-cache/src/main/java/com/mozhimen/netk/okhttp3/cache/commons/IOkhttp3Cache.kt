package com.mozhimen.netk.okhttp3.cache.commons

import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.cache.CacheRequest
import java.io.IOException

/**
 * @author : Aleyn
 * @date : 2022/06/23 16:41
 */
interface IOkhttp3Cache {
    fun getCache(cacheKey: String, request: Request): Response?
    fun getCache_ofExpiration(cacheKey: String, cacheTime: Long, request: Request): Response?
    fun putCache(cacheKey: String, response: Response): CacheRequest?
    fun removeCache(cacheKey: String)
    fun removeAll()
}