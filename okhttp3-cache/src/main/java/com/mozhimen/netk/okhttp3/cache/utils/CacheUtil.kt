package com.mozhimen.netk.okhttp3.cache.utils

import com.mozhimen.netk.okhttp3.cache.NetKOkhttp3Cache
import com.mozhimen.netk.okhttp3.cache.annors.ACacheMode
import com.mozhimen.netk.okhttp3.cache.cons.CCacheHeaders
import com.mozhimen.netk.okhttp3.cache.mos.CacheStrategy
import okhttp3.Request
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.io.FileSystem
import okhttp3.internal.threadFactory
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import java.io.EOFException
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

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

    @JvmStatic
    fun encodeUtf8_md5_hex(str: String): String =
        str.encodeUtf8().md5().hex()

    @JvmStatic
    fun getDiskLruCache(
        directory: File,
        appVersion: Int,
        valueCount: Int,
        maxSize: Long
    ): DiskLruCache =
        DiskLruCache(FileSystem.SYSTEM, directory, appVersion, valueCount, maxSize, TaskRunner.INSTANCE)

    /**
     * OkHttp 4.0.0 版本开始用 Kotlin 重构， DiskLruCache 的构造函数被 internal 来修饰了，导致kotlin 无法直接创建，坑爹啊。
     * 不过 Java 可以无视 Kotlin 的 internal 关键字，可以直接过编译期
     * 这里为了版本兼容没有用 Java 过度 ，还是统一反射创建
     */
    @JvmStatic
    fun getDiskLruCache_ofReflect(
        directory: File,
        appVersion: Int,
        valueCount: Int,
        maxSize: Long
    ): DiskLruCache {
        val clazzDiskLruCache = DiskLruCache::class.java
        return try {
            val clazzTaskRunner = Class.forName("okhttp3.internal.concurrent.TaskRunner")
            val constructorDiskLruCache = clazzDiskLruCache.getConstructor(FileSystem::class.java, File::class.java, Int::class.java, Int::class.java, Long::class.java, clazzTaskRunner)
            constructorDiskLruCache.newInstance(FileSystem.SYSTEM, directory, appVersion, valueCount, maxSize, TaskRunner.INSTANCE)
        } catch (e: Exception) {
            try {
                val constructorDiskLruCache = clazzDiskLruCache.getConstructor(FileSystem::class.java, File::class.java, Int::class.java, Int::class.java, Long::class.java, Executor::class.java)
                val threadPoolExecutor = ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, LinkedBlockingQueue(), threadFactory("OkHttp DiskLruCache", true))
                constructorDiskLruCache.newInstance(FileSystem.SYSTEM, directory, appVersion, valueCount, maxSize, threadPoolExecutor)
            } catch (e: Exception) {
                throw IllegalArgumentException("Please use okhttp 4.0.0 or later")
            }
        }
    }
}