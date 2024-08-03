package com.mozhimen.netk.okhttp3.impls

import okhttp3.internal.cache.CacheRequest
import okhttp3.internal.discard
import okhttp3.internal.http.ExchangeCodec
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.Source
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @ClassName CacheWriteSource
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/8/2
 * @Version 1.0
 */
class CacheWriteSource constructor(
    private val _bufferedSource: BufferedSource,
    private val _cacheRequest: CacheRequest,
    private val _bufferedSink: BufferedSink
) : Source {
    private var _cacheRequestClosed = false

    ////////////////////////////////////////////////////////////////

    @Throws(IOException::class)
    override fun read(sink: Buffer, byteCount: Long): Long {
        val bytesRead: Long
        try {
            bytesRead = _bufferedSource.read(sink, byteCount)
        } catch (e: IOException) {
            if (!_cacheRequestClosed) {
                _cacheRequestClosed = true
                _cacheRequest.abort()
            }
            throw e
        }

        if (bytesRead == -1L) {
            if (!_cacheRequestClosed) {
                _cacheRequestClosed = true
                _bufferedSink.close()
            }
            return -1
        }

        sink.copyTo(_bufferedSink.buffer, sink.size - bytesRead, bytesRead)
        _bufferedSink.emitCompleteSegments()
        return bytesRead
    }

    override fun timeout() =
        _bufferedSource.timeout()

    @Throws(IOException::class)
    override fun close() {
        if (!_cacheRequestClosed &&
            !discard(ExchangeCodec.DISCARD_STREAM_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        ) {
            _cacheRequestClosed = true
            _cacheRequest.abort()
        }
        _bufferedSource.close()
    }
}