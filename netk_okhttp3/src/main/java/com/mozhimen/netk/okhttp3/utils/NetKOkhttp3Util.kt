package com.mozhimen.netk.okhttp3.utils

import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_HEADERS
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.io.FileSystem
import okhttp3.internal.threadFactory
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import java.io.File
import java.util.TreeSet
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @ClassName NetKOkhttp3Util
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/8/2
 * @Version 1.0
 */
fun RequestBody.requestBody2string():String =
    NetKOkhttp3Util.requestBody2string(this)

///////////////////////////////////////////////////////////////

object NetKOkhttp3Util {
    @JvmStatic
    fun requestBody2string(requestBody: RequestBody): String =
        try {
            val copy: RequestBody = requestBody
            val buffer = Buffer()
            copy.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            ""
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

    @JvmStatic
    fun varyHeaders(response: Response): Headers {
        val requestHeaders = response.networkResponse!!.request.headers
        val responseHeaders = response.headers
        return varyHeaders(requestHeaders, responseHeaders)
    }

    @JvmStatic
    fun varyHeaders(requestHeaders: Headers, responseHeaders: Headers): Headers {
        val varyFields = varyFields(responseHeaders)
        if (varyFields.isEmpty()) return EMPTY_HEADERS

        val result = Headers.Builder()
        for (i in 0 until requestHeaders.size) {
            val fieldName = requestHeaders.name(i)
            if (fieldName in varyFields) {
                result.add(fieldName, requestHeaders.value(i))
            }
        }
        return result.build()
    }

    @JvmStatic
    fun varyFields(headers: Headers): Set<String> {
        var result: MutableSet<String>? = null
        for (i in 0 until headers.size) {
            if (!"Vary".equals(headers.name(i), ignoreCase = true)) {
                continue
            }

            val value = headers.value(i)
            if (result == null) {
                result = TreeSet(String.CASE_INSENSITIVE_ORDER)
            }
            for (varyField in value.split(',')) {
                result.add(varyField.trim())
            }
        }
        return result ?: emptySet()
    }

    @JvmStatic
    fun varyMatches(
        cachedResponse: Response,
        cachedRequest: Headers,
        newRequest: Request
    ): Boolean {
        return varyFields(cachedResponse.headers).none {
            cachedRequest.values(it) != newRequest.headers(it)
        }
    }

    @JvmStatic
    fun hasVaryAll(response: Response): Boolean =
        "*" in varyFields(response.headers)
}