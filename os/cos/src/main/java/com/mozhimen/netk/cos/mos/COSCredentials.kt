package com.mozhimen.netk.cos.mos

import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials
import com.tencent.qcloud.core.auth.SessionQCloudCredentials

/**
 * @ClassName COSCredentials
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/12/21 22:35
 * @Version 1.0
 */
data class COSCredentials(
    val tmpSecretId: String = "",
    val tmpSecretKey: String = "",
    val sessionToken: String = "",
    val startTime: Long = System.currentTimeMillis(),//临时密钥有效截止时间戳，单位是秒
    val expiredTime: Long = System.currentTimeMillis() //临时密钥有效截止时间戳，单位是秒
) {
    fun toQCloudLifecycleCredentials(): QCloudLifecycleCredentials =
        SessionQCloudCredentials(
            tmpSecretId,
            tmpSecretKey,
            sessionToken,
            startTime,
            expiredTime
        )
}