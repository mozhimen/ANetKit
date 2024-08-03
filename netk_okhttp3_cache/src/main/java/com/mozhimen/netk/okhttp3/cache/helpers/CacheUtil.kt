package com.mozhimen.netk.okhttp3.cache.helpers

import com.mozhimen.netk.okhttp3.cache.NetKOkhttp3Cache
import com.mozhimen.netk.okhttp3.cache.annors.ACacheMode
import com.mozhimen.netk.okhttp3.cache.cons.CCacheHeaders
import com.mozhimen.netk.okhttp3.cache.mos.CacheStrategy
import okhttp3.Request
import okio.Buffer
import java.io.EOFException

/**
 * @author : Aleyn
 * @date : 2022/06/23 14:58
 */
object CacheUtil {
    @JvmStatic
    fun removeCacheHeader(request: Request): Request {
        return request.newBuilder()
            .removeHeader(CCacheHeaders.HEADER_CACHE_MODE)
            .removeHeader(CCacheHeaders.HEADER_CACHE_TIME)
            .removeHeader(CCacheHeaders.HEADER_CUSTOM_CACHE_KEY)
            .build()
    }

    /**
     * 获取缓存策略
     */
    @JvmStatic
    fun getCacheStrategy(request: Request): CacheStrategy? {
        var cacheMode = NetKOkhttp3Cache.cacheMode
        request.header(CCacheHeaders.HEADER_CACHE_MODE).takeUnless {
            it.isNullOrBlank()
        }?.let {
            cacheMode = it
        }
        if (cacheMode == ACacheMode.NETWORK) return null

        var cacheValid = NetKOkhttp3Cache.cacheTime
        request.header(CCacheHeaders.HEADER_CACHE_TIME).takeUnless {
            it.isNullOrBlank()
        }?.let {
            cacheValid = it.toLong() * 1000
        }

        var cacheKey = request.header(CCacheHeaders.HEADER_CUSTOM_CACHE_KEY).orEmpty()
        if (cacheKey.isBlank()) {
            cacheKey = buildCacheKey(request)
        }
        return CacheStrategy(cacheKey, cacheValid, cacheMode)
    }

    /**
     * 生成缓存Key
     */
    fun buildCacheKey(request: Request): String {
        val requestBody = request.body ?: return request.url.toString()
        val buffer = Buffer()
        requestBody.writeTo(buffer)

        val contentType = requestBody.contentType()
        val charset = contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8

        if (isProbablyUtf8(buffer)) {
            val questParam = buffer.readString(charset)
            buffer.close()
            if (questParam.isBlank()) return request.url.toString()
            val builder = request.url.newBuilder()
            kotlin.runCatching {
                builder.addQueryParameter("${request.method.lowercase()}param", questParam)
                return builder.build().toString()
            }.onFailure {
                return ""
            }
        }
        return request.url.toString()
    }

    fun isProbablyUtf8(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = buffer.size.coerceAtMost(64)
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (_: EOFException) {
            return false
        }
    }
}