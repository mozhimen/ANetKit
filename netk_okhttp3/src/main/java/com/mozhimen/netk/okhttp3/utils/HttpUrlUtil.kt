package com.mozhimen.netk.okhttp3.utils

import okhttp3.HttpUrl

/**
 * @ClassName HttpUrlUtil
 * @Description //http://127.0.0.1/test/upload/img?userName=xiaoming&userPassword=12345
 * @Author mozhimen
 * @Date 2024/12/30
 * @Version 1.0
 */
fun HttpUrl.gainScheme(): String =
    HttpUrlUtil.gainScheme(this)

fun HttpUrl.gainUserName(): String =
    HttpUrlUtil.gainUserName(this)

fun HttpUrl.gainPassword(): String =
    HttpUrlUtil.gainPassword(this)

fun HttpUrl.gainHost(): String =
    HttpUrlUtil.gainHost(this)

fun HttpUrl.gainPort(): Int =
    HttpUrlUtil.gainPort(this)

fun HttpUrl.gainPathSegments(): List<String?>? =
    HttpUrlUtil.gainPathSegments(this)

fun HttpUrl.gainFragment(): String? =
    HttpUrlUtil.gainFragment(this)

fun HttpUrl.gainEncodedUsername(): String =
    HttpUrlUtil.gainEncodedUsername(this)

fun HttpUrl.gainEncodedPassword(): String =
    HttpUrlUtil.gainEncodedPassword(this)

fun HttpUrl.gainPathSize(): Int =
    HttpUrlUtil.gainPathSize(this)

fun HttpUrl.gainEncodePath(): String =
    HttpUrlUtil.gainEncodePath(this)

fun HttpUrl.gainEncodedPathSegments(): List<String> =
    HttpUrlUtil.gainEncodedPathSegments(this)

fun HttpUrl.gainEncodeQuery(): String? =
    HttpUrlUtil.gainEncodeQuery(this)

fun HttpUrl.gainQuery(): String? =
    HttpUrlUtil.gainQuery(this)

fun HttpUrl.gainQuerySize(): Int =
    HttpUrlUtil.gainQuerySize(this)

fun HttpUrl.gainQueryParameterNames(): Set<String> =
    HttpUrlUtil.gainQueryParameterNames(this)

fun HttpUrl.gainEncodedFragment(): String? =
    HttpUrlUtil.gainEncodedFragment(this)

/////////////////////////////////////////////////////////////////////////////

object HttpUrlUtil {
    //http
    @JvmStatic
    fun gainScheme(httpUrl: HttpUrl): String =
        httpUrl.scheme

    ///
    @JvmStatic
    fun gainUserName(httpUrl: HttpUrl): String =
        httpUrl.username

    ///
    @JvmStatic
    fun gainPassword(httpUrl: HttpUrl): String =
        httpUrl.password

    //127.0.0.1
    @JvmStatic
    fun gainHost(httpUrl: HttpUrl): String =
        httpUrl.host

    //..->80
    @JvmStatic
    fun gainPort(httpUrl: HttpUrl): Int =
        httpUrl.port

    //["test","upload","img"]
    @JvmStatic
    fun gainPathSegments(httpUrl: HttpUrl): List<String?>? =
        httpUrl.pathSegments

    //http:// host/#abc -> "abc"
    @JvmStatic
    fun gainFragment(httpUrl: HttpUrl): String? =
        httpUrl.fragment

    @JvmStatic
    fun gainEncodedUsername(httpUrl: HttpUrl): String =
        httpUrl.encodedUsername

    @JvmStatic
    fun gainEncodedPassword(httpUrl: HttpUrl): String =
        httpUrl.encodedPassword

    @JvmStatic
    fun gainPathSize(httpUrl: HttpUrl): Int =
        httpUrl.pathSize

    // /test/upload/img
    @JvmStatic
    fun gainEncodePath(httpUrl: HttpUrl): String =
        httpUrl.encodedPath

    //["test","upload","img"]
    @JvmStatic
    fun gainEncodedPathSegments(httpUrl: HttpUrl): List<String> =
        httpUrl.encodedPathSegments

    //userName=xiaoming&userPassword=12345
    @JvmStatic
    fun gainEncodeQuery(httpUrl: HttpUrl): String? =
        httpUrl.encodedQuery

    //userName=xiaoming&userPassword=12345
    @JvmStatic
    fun gainQuery(httpUrl: HttpUrl): String? =
        httpUrl.query

    //2
    @JvmStatic
    fun gainQuerySize(httpUrl: HttpUrl): Int =
        httpUrl.querySize

    //["userName","userPassword"]
    @JvmStatic
    fun gainQueryParameterNames(httpUrl: HttpUrl): Set<String> =
        httpUrl.queryParameterNames

    ///
    @JvmStatic
    fun gainEncodedFragment(httpUrl: HttpUrl): String? =
        httpUrl.encodedFragment
}