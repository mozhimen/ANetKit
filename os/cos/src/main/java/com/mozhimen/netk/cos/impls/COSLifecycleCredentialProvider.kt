package com.mozhimen.netk.cos.impls

import com.mozhimen.netk.cos.commons.IFetchCredentialsListener
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials
import com.tencent.qcloud.core.common.QCloudClientException

/**
 * @ClassName COSServerCredentialProvider
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/12/21 23:12
 * @Version 1.0
 */
class COSLifecycleCredentialProvider(
    private val _fetchCredentialsListener: IFetchCredentialsListener
) : BasicLifecycleCredentialProvider() {
    @Throws(QCloudClientException::class)
    override fun fetchNewCredentials(): QCloudLifecycleCredentials {

        // 首先从您的临时密钥服务器获取包含了密钥信息的响应
        // 临时密钥生成和使用指引参见https://cloud.tencent.com/document/product/436/14048

//            val tmpSecretId = "临时密钥 secretId"
//            val tmpSecretKey = "临时密钥 secretKey"
//            val sessionToken = "临时密钥 TOKEN"
//            val expiredTime = 1556183496L //临时密钥有效截止时间戳，单位是秒
//
//            /*强烈建议返回服务器时间作为签名的开始时间，用来避免由于用户手机本地时间偏差过大导致的签名不正确 */
//            // 返回服务器时间作为签名的起始时间
//            val startTime = 1556182000L //临时密钥有效起始时间，单位是秒

        // 最后返回临时密钥信息对象
        return _fetchCredentialsListener.onFetchNewCredentials().toQCloudLifecycleCredentials()
    }
}