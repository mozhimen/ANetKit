package com.mozhimen.netk.okhttp3.impls

import com.mozhimen.netk.okhttp3.utils.NetKOkhttp3Util
import okhttp3.CipherSuite
import okhttp3.Handshake
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.TlsVersion
import okhttp3.internal.EMPTY_HEADERS
import okhttp3.internal.addHeaderLenient
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.http.StatusLine
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.toByteString
import okio.Source
import okio.buffer
import java.io.IOException
import java.security.cert.Certificate
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.util.ArrayList
import java.util.TreeSet

/**
 * @ClassName Entry
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/8/2
 * @Version 1.0
 */
class Entry {

    companion object {
        const val ENTRY_METADATA = 0
        private val SENT_MILLIS = "${Platform.get().getPrefix()}-Sent-Millis"
        private val RECEIVED_MILLIS = "${Platform.get().getPrefix()}-Received-Millis"
    }

    /////////////////////////////////////////////////////////////////////////////

    private val url: HttpUrl
    private val varyHeaders: Headers
    private val requestMethod: String
    private val protocol: Protocol
    private val code: Int
    private val message: String
    private val responseHeaders: Headers
    private val handshake: Handshake?
    private val sentRequestMillis: Long
    private val receivedResponseMillis: Long
    private val isHttps: Boolean get() = url.scheme == "https"

    /////////////////////////////////////////////////////////////////////////////

    @Throws(IOException::class)
    constructor(rawSource: Source) {
        try {
            val source = rawSource.buffer()
            val urlLine = source.readUtf8LineStrict()
            url = urlLine.toHttpUrlOrNull()
                ?: throw IOException("Cache corruption for $urlLine")
            requestMethod = source.readUtf8LineStrict()
            val varyHeadersBuilder = Headers.Builder()
            val varyRequestHeaderLineCount = readInt(source)
            for (i in 0 until varyRequestHeaderLineCount) {
                addHeaderLenient(varyHeadersBuilder, source.readUtf8LineStrict())
            }
            varyHeaders = varyHeadersBuilder.build()

            val statusLine = StatusLine.parse(source.readUtf8LineStrict())
            protocol = statusLine.protocol
            code = statusLine.code
            message = statusLine.message
            val responseHeadersBuilder = Headers.Builder()
            val responseHeaderLineCount = readInt(source)
            for (i in 0 until responseHeaderLineCount) {
                addHeaderLenient(responseHeadersBuilder, source.readUtf8LineStrict())
            }
            val sendRequestMillisString = responseHeadersBuilder[SENT_MILLIS]
            val receivedResponseMillisString = responseHeadersBuilder[RECEIVED_MILLIS]
            responseHeadersBuilder.removeAll(SENT_MILLIS)
            responseHeadersBuilder.removeAll(RECEIVED_MILLIS)
            sentRequestMillis = sendRequestMillisString?.toLong() ?: 0L
            receivedResponseMillis = receivedResponseMillisString?.toLong() ?: 0L
            responseHeaders = responseHeadersBuilder.build()

            if (isHttps) {
                val blank = source.readUtf8LineStrict()
                if (blank.isNotEmpty()) {
                    throw IOException("expected \"\" but was \"$blank\"")
                }
                val cipherSuiteString = source.readUtf8LineStrict()
                val cipherSuite = CipherSuite.forJavaName(cipherSuiteString)
                val peerCertificates = readCertificateList(source)
                val localCertificates = readCertificateList(source)
                val tlsVersion = if (!source.exhausted()) {
                    TlsVersion.forJavaName(source.readUtf8LineStrict())
                } else {
                    TlsVersion.SSL_3_0
                }
                handshake =
                    Handshake.get(tlsVersion, cipherSuite, peerCertificates, localCertificates)
            } else {
                handshake = null
            }
        } finally {
            rawSource.close()
        }
    }

    constructor(response: Response) {
        this.url = response.request.url
        this.varyHeaders = NetKOkhttp3Util.varyHeaders(response)
        this.requestMethod = response.request.method
        this.protocol = response.protocol
        this.code = response.code
        this.message = response.message
        this.responseHeaders = response.headers
        this.handshake = response.handshake
        this.sentRequestMillis = response.sentRequestAtMillis
        this.receivedResponseMillis = response.receivedResponseAtMillis
    }

    /////////////////////////////////////////////////////////////////////////////

    fun matches(request: Request, response: Response): Boolean {
        return url == request.url &&
                requestMethod == request.method &&
                NetKOkhttp3Util.varyMatches(response, varyHeaders, request)
    }

    fun response(request: Request, snapshot: DiskLruCache.Snapshot): Response {
        val contentType = responseHeaders["Content-Type"]
        val contentLength = responseHeaders["Content-Length"]
        return Response.Builder()
            .request(request)
            .protocol(protocol)
            .code(code)
            .message(message)
            .headers(responseHeaders)
            .body(CacheResponseBody(snapshot, contentType, contentLength))
            .handshake(handshake)
            .sentRequestAtMillis(sentRequestMillis)
            .receivedResponseAtMillis(receivedResponseMillis)
            .build()
    }

    @Throws(IOException::class)
    fun writeTo(editor: DiskLruCache.Editor) {
        editor.newSink(ENTRY_METADATA).buffer().use { sink ->
            sink.writeUtf8(url.toString()).writeByte('\n'.code)
            sink.writeUtf8(requestMethod).writeByte('\n'.code)
            sink.writeDecimalLong(varyHeaders.size.toLong()).writeByte('\n'.code)
            for (i in 0 until varyHeaders.size) {
                sink.writeUtf8(varyHeaders.name(i))
                    .writeUtf8(": ")
                    .writeUtf8(varyHeaders.value(i))
                    .writeByte('\n'.code)
            }

            sink.writeUtf8(StatusLine(protocol, code, message).toString())
                .writeByte('\n'.code)
            sink.writeDecimalLong((responseHeaders.size + 2).toLong()).writeByte('\n'.code)
            for (i in 0 until responseHeaders.size) {
                sink.writeUtf8(responseHeaders.name(i))
                    .writeUtf8(": ")
                    .writeUtf8(responseHeaders.value(i))
                    .writeByte('\n'.code)
            }
            sink.writeUtf8(SENT_MILLIS)
                .writeUtf8(": ")
                .writeDecimalLong(sentRequestMillis)
                .writeByte('\n'.code)
            sink.writeUtf8(RECEIVED_MILLIS)
                .writeUtf8(": ")
                .writeDecimalLong(receivedResponseMillis)
                .writeByte('\n'.code)

            if (isHttps) {
                sink.writeByte('\n'.code)
                sink.writeUtf8(handshake!!.cipherSuite.javaName).writeByte('\n'.code)
                writeCertList(sink, handshake.peerCertificates)
                writeCertList(sink, handshake.localCertificates)
                sink.writeUtf8(handshake.tlsVersion.javaName).writeByte('\n'.code)
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////

    @Throws(IOException::class)
    private fun readCertificateList(source: BufferedSource): List<Certificate> {
        val length = readInt(source)
        if (length == -1) return emptyList() // OkHttp v1.2 used -1 to indicate null.

        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val result = ArrayList<Certificate>(length)
            for (i in 0 until length) {
                val line = source.readUtf8LineStrict()
                val bytes = Buffer()
                bytes.write(line.decodeBase64()!!)
                result.add(certificateFactory.generateCertificate(bytes.inputStream()))
            }
            return result
        } catch (e: CertificateException) {
            throw IOException(e.message)
        }
    }

    @Throws(IOException::class)
    private fun writeCertList(sink: BufferedSink, certificates: List<Certificate>) {
        try {
            sink.writeDecimalLong(certificates.size.toLong()).writeByte('\n'.code)
            for (element in certificates) {
                val bytes = element.encoded
                val line = bytes.toByteString().base64()
                sink.writeUtf8(line).writeByte('\n'.code)
            }
        } catch (e: CertificateEncodingException) {
            throw IOException(e.message)
        }
    }

    @Throws(IOException::class)
    private fun readInt(source: BufferedSource): Int {
        try {
            val result = source.readDecimalLong()
            val line = source.readUtf8LineStrict()
            if (result < 0L || result > Integer.MAX_VALUE || line.isNotEmpty()) {
                throw IOException("expected an int but was \"$result$line\"")
            }
            return result.toInt()
        } catch (e: NumberFormatException) {
            throw IOException(e.message)
        }
    }
}