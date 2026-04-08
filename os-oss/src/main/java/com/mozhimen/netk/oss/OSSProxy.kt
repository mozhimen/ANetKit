package com.mozhimen.netk.oss

import android.annotation.SuppressLint
import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.kotlin.utilk.java.text.UtilKSimpleDateFormat
import com.mozhimen.kotlin.utilk.java.util.UtilKDate
import com.mozhimen.kotlin.utilk.java.util.date2longDate
import com.mozhimen.netk.os.basic.commons.IOSProxy
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * @ClassName OSSUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/11/23 10:46
 * @Version 1.0
 */
class OSSProxy(private val _okHttpClient: OkHttpClient) : BaseUtilK(), IOSProxy {
    private var _ossClient: OSSClient? = null
    private val _clientConfiguration: ClientConfiguration by lazy {
        ClientConfiguration().apply {
            maxConcurrentRequest = 3// 设置最大并发数。
            socketTimeout = 10000// 设置Socket层传输数据的超时时间。
            connectionTimeout = 10000// 设置建立连接的超时时间。
            maxErrorRetry = 2// 请求失败后最大的重试次数。
//            val cnameExcludeList: MutableList<String> = ArrayList()// 列表中的元素将跳过CNAME解析。
//            cnameExcludeList.add("cname")
//            customCnameExcludeList = cnameExcludeList
//            proxyHost = "yourProxyHost"// 代理服务器主机地址。
//            proxyPort = 8080// 代理服务器端口
//            setUserAgentMark("yourUserAgent")// 用户代理中HTTP的User-Agent头。
//            isHttpDnsEnable = true// 是否开启httpDns。
//            isCheckCRC64 = true// 是否开启CRC校验。
//            isFollowRedirectsEnable = true// 是否开启HTTP重定向。
            okHttpClient = _okHttpClient// 设置自定义OkHttpClient。
        }
    }
    private val _simpleDateFormat: SimpleDateFormat by lazy {
        UtilKSimpleDateFormat.get("yyyy-MM-dd'T'HH:mm:ss").apply { timeZone = TimeZone.getTimeZone("UTC") }
    }

    ///////////////////////////////////////////////////////////////////////////

    fun getOSClient(): OSSClient? =
        _ossClient

    /**
     * var accessKeyId = "yourAccessKeyId"// 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
     * var accessKeySecret = "yourAccessKeySecret"
     * var securityToken = "yourSecurityToken"// 从STS服务获取的安全令牌（SecurityToken）。
     */
    fun initOSClient(accessKeyId: String, endPoint: String, accessKeySecret: String, securityToken: String): OSSClient? {
        return try {
            OSSClient(_context.applicationContext, endPoint, OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken), _clientConfiguration)// 创建OSSClient实例。
        } catch (e: Exception) {
            null
        }.also { _ossClient = it }
    }

    fun updateOSClient(accessKeyId: String, accessKeySecret: String, securityToken: String) {
        try {
            _ossClient?.updateCredentialProvider(OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken))// 创建OSSClient实例。
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    @SuppressLint("SimpleDateFormat")
    override fun strTimeExpiration2longTimeExpiration(strDate: String): Long? =
        try {
            _simpleDateFormat.parse(strDate)?.date2longDate()
        } catch (e: Exception) {
            null
        }
}