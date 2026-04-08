package com.mozhimen.netk.cos.utils

import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.elemk.commons.IAB_Listener
import com.mozhimen.kotlin.elemk.commons.IA_Listener
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.tencent.cos.xml.CosXmlService
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.exception.CosXmlServiceException
import com.tencent.cos.xml.listener.CosXmlResultListener
import com.tencent.cos.xml.model.CosXmlRequest
import com.tencent.cos.xml.model.CosXmlResult
import com.tencent.cos.xml.transfer.COSXMLUploadTask.COSXMLUploadTaskResult
import com.tencent.cos.xml.transfer.TransferConfig
import com.tencent.cos.xml.transfer.TransferManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


/**
 * @ClassName TransferUploadUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/12/22
 * @Version 1.0
 */
//fun CosXmlService.transferUploadFile(
//    bucket: String,
//    cosPath: String,
//    strFilePathName: String,
//    onComplete: IA_Listener<Boolean>,
//    onProgress: IAB_Listener<Long, Long>? = null
//) {
//    TransferUploadUtil.transferUploadFile(this, bucket, cosPath, strFilePathName, onComplete, onProgress)
//}

object TransferUploadUtil : BaseUtilK() {
    private val _transferConfig by lazy { TransferConfig.Builder().build() }// 初始化 TransferConfig，这里使用默认配置，如果需要定制，请参考 SDK 接口文档

    fun getTransferConfig(): TransferConfig =
        _transferConfig

    ////////////////////////////////////////////////////////////////

    @JvmStatic
    suspend fun transferUploadFile_onBack(cosXmlService: CosXmlService, bucket: String, cosPath: String, strFilePathName: String, onProgress: IAB_Listener<Long, Long>? = null): Boolean =
        suspendCancellableCoroutine { continuation ->
            transferUploadFile(cosXmlService, bucket, cosPath, strFilePathName, { res ->
                continuation.resume(res)
            }, onProgress)
        }

    /**
     * 高级接口上传对象
     */
    @JvmStatic
    fun transferUploadFile(
        cosXmlService: CosXmlService,
        bucket: String,// 存储桶名称，由bucketname-appid 组成，appid必须填入，可以在COS控制台查看存储桶名称。 https://console.cloud.tencent.com/cos5/bucket
        cosPath: String,/*//对象在存储桶中的位置标识符，即称对象键*/
        strFilePathName: String,//本地文件的绝对路径
        onComplete: IA_Listener<Boolean>,
        onProgress: IAB_Listener<Long, Long>? = null,
    ) {
        val transferManager = TransferManager(cosXmlService, _transferConfig)// 初始化 TransferManager
        val uploadId: String? = null//若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
        val cosXmlUploadTask = transferManager.upload(bucket, cosPath, strFilePathName, uploadId)// 上传文件
        cosXmlUploadTask.setCosXmlProgressListener { complete, target ->//设置上传进度回调
            // todo Do something to update progress...
            UtilKLogWrapper.v(TAG, "transferUploadFileOnBack: setCosXmlProgressListener complete $complete target $target")
            onProgress?.invoke(complete, target)
        }
        cosXmlUploadTask.setCosXmlResultListener(object : CosXmlResultListener {
            //设置返回结果回调
            override fun onSuccess(request: CosXmlRequest, result: CosXmlResult) {
                val uploadTaskResult = result as COSXMLUploadTaskResult
                UtilKLogWrapper.d(TAG, "onSuccess: uploadTaskResult $uploadTaskResult")
                onComplete.invoke(true)
            }

            // 如果您使用 kotlin 语言来调用，请注意回调方法中的异常是可空的，否则不会回调 onFail 方法，即：
            // clientException 的类型为 CosXmlClientException?，serviceException 的类型为 CosXmlServiceException?
            override fun onFail(
                request: CosXmlRequest,
                clientException: CosXmlClientException?,
                serviceException: CosXmlServiceException?,
            ) {
                clientException?.printStackTrace() ?: serviceException?.printStackTrace()
                UtilKLogWrapper.w(TAG, "onFail: ")
                onComplete.invoke(false)
            }
        })
        cosXmlUploadTask.setTransferStateListener {//设置任务状态回调, 可以查看任务过程
            // todo notify transfer state
            UtilKLogWrapper.d(TAG, "transferUploadFile: state $it")
        }
    }
}