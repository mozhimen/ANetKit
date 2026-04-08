package com.mozhimen.netk.okhttp3.cache

import com.mozhimen.kotlin.utilk.kotlin.megaBytes
import com.mozhimen.netk.okhttp3.cache.annors.ACacheMode
import com.mozhimen.netk.okhttp3.cache.commons.IOkhttp3Cache
import com.mozhimen.netk.okhttp3.cache.helpers.Entry
import com.mozhimen.netk.okhttp3.cache.impls.ResponseBodyCache
import com.mozhimen.netk.okhttp3.cache.utils.CacheUtil
import okhttp3.*
import okhttp3.internal.cache.CacheRequest
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.closeQuietly
import okio.*
import java.io.Closeable
import java.io.File
import java.io.Flushable
import java.io.IOException
import java.util.*

/**
 * @author : Aleyn
 * @date : 2022/06/24 18:26
 */
class NetKOkhttp3Cache(
    directory: File,
    maxSize: Long = 10L.megaBytes() // 最大缓存大小, 默认 10M
) : Closeable, Flushable, IOkhttp3Cache {

    companion object {
        /**
         * 全局缓存模式
         */
        var cacheMode = ACacheMode.NETWORK
            private set

        /**
         * 全局缓存时长
         */
        var cacheTime = 10 * 1000L
            private set

        /**
         * 是否使用过期数据
         */
        var useExpiredData = false
            private set

        fun setCacheModel(@ACacheMode cacheMode: String) = apply {
            this.cacheMode = cacheMode
        }

        fun setCacheTime(cacheTime: Long) = apply {
            this.cacheTime = cacheTime
        }

        /**
         * 仅对只读取缓存模式生效
         */
        fun useExpiredData(useExpiredData: Boolean) = apply {
            this.useExpiredData = useExpiredData
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    val diskLruCache: DiskLruCache =
        CacheUtil.getDiskLruCache(directory, 1, 2, maxSize)

    val isClosed: Boolean
        get() = diskLruCache.isClosed()

    @get:Synchronized
    var writeSuccessCount = 0

    @get:Synchronized
    var writeAbortCount = 0

    ///////////////////////////////////////////////////////////////////////////////

    override fun getCache(cacheKey: String, request: Request): Response? {
        val key = CacheUtil.encodeUtf8_md5_hex(cacheKey)
        val snapshot: DiskLruCache.Snapshot = try {
            diskLruCache[key] ?: return null
        } catch (e: IOException) {
            return null
        }

        val entry: Entry = try {
            Entry(snapshot.getSource(Entry.ENTRY_METADATA))
        } catch (_: IOException) {
            snapshot.closeQuietly()
            return null
        }

        val response = entry.response(request, snapshot)
        if (!entry.matches(request, response)) {
            response.body?.closeQuietly()
            return null
        }
        return response
    }

    override fun getCache_ofExpiration(cacheKey: String, cacheTime: Long, request: Request): Response? {
        val cacheResponse = getCache(cacheKey, request)
        if (cacheResponse != null) {
            val responseMillis = cacheResponse.receivedResponseAtMillis
            val now = System.currentTimeMillis()
            if (cacheTime == -1000L || now - responseMillis <= cacheTime) {
                return cacheResponse
            } else {
                cacheResponse.body?.closeQuietly()
            }
        }
        return null
    }

    override fun putCache(cacheKey: String, response: Response): CacheRequest? {
        if (hasVaryAll(response)) return null
        val entry = Entry(response)
        var editor: DiskLruCache.Editor? = null
        return try {
            val redKey = CacheUtil.encodeUtf8_md5_hex(cacheKey)
            editor = diskLruCache.edit(redKey) ?: return null
            entry.writeTo(editor)
            RealCacheRequest(editor)
        } catch (_: IOException) {
            abortQuietly(editor)
            null
        }
    }

    override fun removeCache(cacheKey: String) {
        diskLruCache.remove(CacheUtil.encodeUtf8_md5_hex(cacheKey))
    }

    override fun removeAll() {
        diskLruCache.evictAll()
    }

    override fun flush() {
        try {
            diskLruCache.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun close() {
        try {
            diskLruCache.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    fun initialize() {
        try {
            diskLruCache.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun delete() {
        try {
            diskLruCache.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun writeAbortCount(): Int =
        writeAbortCount

    @Synchronized
    fun writeSuccessCount(): Int =
        writeSuccessCount

    ///////////////////////////////////////////////////////////////////////////////

    private fun hasVaryAll(response: Response): Boolean =
        "*" in varyFields(response.headers)

    private fun varyFields(headers: Headers): Set<String> {
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

    private fun abortQuietly(editor: DiskLruCache.Editor?) {
        try {
            editor?.abort()
        } catch (_: IOException) {
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    private inner class RealCacheRequest(
        private val editor: DiskLruCache.Editor
    ) : CacheRequest {
        private val cacheOut: Sink = editor.newSink(ResponseBodyCache.ENTRY_BODY)
        private val body: Sink
        var done = false

        init {
            this.body = object : ForwardingSink(cacheOut) {
                @Throws(IOException::class)
                override fun close() {
                    synchronized(this@NetKOkhttp3Cache) {
                        if (done) return
                        done = true
                        writeSuccessCount++
                    }
                    super.close()
                    editor.commit()
                }
            }
        }

        override fun abort() {
            synchronized(this@NetKOkhttp3Cache) {
                if (done) return
                done = true
                writeAbortCount++
            }
            cacheOut.closeQuietly()
            try {
                editor.abort()
            } catch (_: IOException) {
            }
        }

        override fun body(): Sink = body
    }
}