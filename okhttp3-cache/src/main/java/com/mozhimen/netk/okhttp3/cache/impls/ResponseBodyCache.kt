package com.mozhimen.netk.okhttp3.cache.impls

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.toLongOrDefault
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import java.io.IOException

/**
 * @ClassName CacheResponseBody
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/8/2
 * @Version 1.0
 */
class ResponseBodyCache(
    val snapshot: DiskLruCache.Snapshot,
    private val contentType: String?,
    private val contentLength: String?
) : ResponseBody() {

    companion object{
        const val ENTRY_BODY = 1
    }

    //////////////////////////////////////////////////////////////

    private val bodySource: BufferedSource

    //////////////////////////////////////////////////////////////

    init {
        val source = snapshot.getSource(ENTRY_BODY)
        bodySource = object : ForwardingSource(source) {
            @Throws(IOException::class)
            override fun close() {
                snapshot.close()
                super.close()
            }
        }.buffer()
    }

    //////////////////////////////////////////////////////////////

    override fun contentType(): MediaType? =
        contentType?.toMediaTypeOrNull()

    override fun contentLength(): Long =
        contentLength?.toLongOrDefault(-1L) ?: -1L

    override fun source(): BufferedSource =
        bodySource
}