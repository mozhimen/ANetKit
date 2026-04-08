package com.mozhimen.netk.cos.utils

import com.tencent.cos.xml.CosXmlService
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.model.PresignedUrlRequest
import com.tencent.qcloud.core.http.RequestBodySerializer

/**
 * @ClassName COSUrlUtil
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/12/22 0:03
 * @Version 1.0
 */
fun CosXmlService.getPresignDownloadUrl(bucket: String, cosPath: String, expirationTime: Int): String =
    COSUrlUtil.getPresignDownloadUrl(this, bucket, cosPath, expirationTime)

fun CosXmlService.getPresignUploadUrl(bucket: String, cosPath: String): String =
    COSUrlUtil.getPresignUploadUrl(this, bucket, cosPath)

////////////////////////////////////////////////////////////////////////////////

object COSUrlUtil {
    /**
     * 获取预签名下载链接
     */
    @JvmStatic
    fun getPresignDownloadUrl(cosXmlService: CosXmlService, bucket: String, cosPath: String, expirationTime: Int): String {
        //.cssg-snippet-body-start:[get-presign-download-url]
        try {
//            val bucket = "examplebucket-1250000000" //存储桶名称
//            val cosPath = "exampleobject" //即对象在存储桶中的位置标识符。
            val method = "GET" //请求 HTTP 方法.
            val presignedUrlRequest = PresignedUrlRequest(bucket, cosPath)
            presignedUrlRequest.setRequestMethod(method)
            presignedUrlRequest.setSignKeyTime(expirationTime)// 设置签名有效期为 60s，注意这里是签名有效期，您需要自行保证密钥有效期/*2592000*/一个月
            presignedUrlRequest.addNoSignHeader("Host")// 设置不签名 Host
            return cosXmlService.getPresignedURL(presignedUrlRequest) ?: ""
        } catch (e: CosXmlClientException) {
            e.printStackTrace()
        }
        return ""
        //.cssg-snippet-body-end
    }

    /**
     * 获取预签名上传链接
     */
    fun getPresignUploadUrl(cosXmlService: CosXmlService, bucket: String, cosPath: String): String {
        //.cssg-snippet-body-start:[get-presign-upload-url]
        try {
//            val bucket = "examplebucket-1250000000" //存储桶名称
//            val cosPath = "exampleobject" //即对象在存储桶中的位置标识符。
            val method = "PUT" //请求 HTTP 方法
            val presignedUrlRequest: PresignedUrlRequest = object : PresignedUrlRequest(bucket, cosPath) {
                @Throws(CosXmlClientException::class)
                override fun getRequestBody(): RequestBodySerializer {
                    //用于计算 put 等需要带上 body 的请求的签名 URL
                    return RequestBodySerializer.string("text/plain", "this is test")
                }
            }
            presignedUrlRequest.setRequestMethod(method)
            presignedUrlRequest.setSignKeyTime(60)// 设置签名有效期为 60s，注意这里是签名有效期，您需要自行保证密钥有效期
            presignedUrlRequest.addNoSignHeader("Host")// 设置不签名 Host
            return cosXmlService.getPresignedURL(presignedUrlRequest) ?: ""
        } catch (e: CosXmlClientException) {
            e.printStackTrace()
        }
        return ""
        //.cssg-snippet-body-end
    }
    // .cssg-methods-pragma
}