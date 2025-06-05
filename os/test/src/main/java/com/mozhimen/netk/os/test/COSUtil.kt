//package com.mozhimen.netk.os.test
//
//import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
//import com.mozhimen.kotlin.elemk.commons.IAB_Listener
//import com.mozhimen.kotlin.elemk.commons.IA_Listener
//import com.mozhimen.kotlin.utilk.commons.IUtilK
//import com.mozhimen.kotlin.utilk.kotlin.getSplitFirstIndexToStart
//import com.mozhimen.kotlin.utilk.kotlin.text.removeStart_ofSeparator
//import com.mozhimen.netk.cos.mos.COSCredentials
//import com.mozhimen.netk.cos.COSProxy
//import com.mozhimen.netk.cos.commons.IFetchCredentialsListener
//import com.mozhimen.netk.cos.utils.getPresignDownloadUrl
//import com.mozhimen.netk.cos.utils.transferUploadFile
//import com.tencent.cos.xml.CosXmlService
//import kotlinx.coroutines.runBlocking
//
///**
// * @ClassName ImageUtil
// * @Description TODO
// * @Author Mozhimen & Kolin Zhao
// * @Date 2023/11/23 11:24
// * @Version 1.0
// */
//fun String.restoreStrSignatureUrl(): String =
//    replace(Repository.getCdnHost(), "").getSplitFirstIndexToStart("?q-sign-algorithm").replace("%3A", ":")
//
//fun String.getCdnSignatureStrUrl(tag: String): String =
//    if (DataSourceLocale.getCdnHost().also { UtilKLogWrapper.d("COSUtil>>>>>", "getCdnSignatureStrUrl: $it") }.isNotEmpty() && startsWith(DataSourceLocale.getCdnHost())) {
//        restoreStrSignatureUrl().removeStart_ofSeparator().getSignatureStrUrl(tag) ?: ""
//    } else {
//        removeStart_ofSeparator().getSignatureStrUrl(tag) ?: ""
//    }
//
//fun String.getSignatureStrUrl(tag: String): String? =
//    COSUtil.getSignatureStrUrl(tag, this)
//
//fun String.transferUploadFile(strFilePathName: String, onComplete: IA_Listener<Boolean>, onProgress: IAB_Listener<Long, Long>? = null) {
//    COSUtil.transferUploadFile(this, strFilePathName, onComplete, onProgress)
//}
//
//object COSUtil : IUtilK {
//
//    ////////////////////////////////////////////////////////////////////////////////
//
//    const val SIGN_URL_EXPIRATION_TIME_SECOND = 300_000
//    const val SIGN_URL_EXPIRATION_TIME_MILLISECOND = SIGN_URL_EXPIRATION_TIME_SECOND * 1000L
//    const val URL_EXPIRATION_BUFFER_TIME_MILLISECOND = 60_000L
//
//    ////////////////////////////////////////////////////////////////////////////////
//
//    private var _bucket = "xxxx-xxx-xxxxxxxxxx"
//    private var _region = "ap-singapore"
//    private var _protocol = ""
//    private var _longTimeExpiration = 0L
//    private val _cosProxy by lazy { COSProxy() }
//    private val _iFetchCredentialsListener = object : IFetchCredentialsListener {
//        override fun onFetchNewCredentials(): COSCredentials {
//            return runBlocking {
//                Repository.getSignatureOnBack()?.toCOSCredentials() ?: COSCredentials()
//            }
//        }
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @JvmStatic
//    fun getSignatureStrUrl(tag: String, url: String): String? {
//        UtilKLogWrapper.v(TAG, "getSignatureStrUrl: sta ----------------------------------------------------------------<")
//        UtilKLogWrapper.v(TAG, "getSignatureStrUrl: tag $tag origin_ url $url")
//        return getService()?.getPresignDownloadUrl(_bucket, url, SIGN_URL_EXPIRATION_TIME_SECOND)?.apply { UtilKLogWrapper.v(TAG, "getSignatureStrUrl: tag $tag presign url $this") }?.replaceCdnHost()
//            .also {
//                UtilKLogWrapper.v(TAG, "getSignatureStrUrl: tag $tag cdnhost url $it")
//                UtilKLogWrapper.v(TAG, "getSignatureStrUrl: end ---------------------------------------------------------------->")
//            }
//    }
//
//    @JvmStatic
//    fun transferUploadFile(cosPath: String, strFilePathName: String, onComplete: IA_Listener<Boolean>, onProgress: IAB_Listener<Long, Long>? = null) {
//        getService()?.transferUploadFile(_bucket, cosPath, strFilePathName, onComplete, onProgress)
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @JvmStatic
//    fun getService(): CosXmlService? {
//        if (_cosProxy.getService() == null) {
//            initService()
//        } /*else if (System.currentTimeMillis() + 5 * 60_000 >= _longTimeExpiration) {
//            initOSSClient()
//        }*/
//        return _cosProxy.getService()
//    }
//
//    @JvmStatic
//    fun initService(): CosXmlService? =
//        runBlocking {
//            Repository.getSignatureOnBack()
//        }?.let {
//            _bucket = it.bucket
//            _longTimeExpiration = _cosProxy.strTimeExpiration2longTimeExpiration(it.expiration) ?: it.expiredTime
//            _cosProxy.initService(it.region, _iFetchCredentialsListener)
//        }
//
//    ////////////////////////////////////////////////////////////////////////////////
//
//    private fun SignatureRes.toCOSCredentials(): COSCredentials =
//        COSCredentials(
//            this.credentials.tmpSecretId,
//            this.credentials.tmpSecretKey,
//            this.credentials.sessionToken,
//            this.startTime,
//            this.expiredTime
//        )
//
//    private fun String.replaceCdnHost(): String {
//        if (_protocol.isEmpty()) {
//            _protocol = this.getSplitFirstIndexToStart(":")
//        }
//        if (DataSourceLocale.getCdnHost().isEmpty())
//            return this
//        return this.replaceFirst(getOriginHost(_protocol), DataSourceLocale.getCdnHost())
//    }
//
//    private fun getOriginHost(protocol: String): String =
//        "$protocol://${_bucket}.cos.${_region}.myqcloud.com"
//
//
////    @JvmStatic
////    fun getOriginHost(protocol: String): String =
////        "$protocol://$BUCKET_NAME.$END_POINT"
////
////    @JvmStatic
////    fun updateOSSClient() =
////        runBlocking {
////            Repository.getSignatureOnBack()
////        }?.let {
////            _longTimeExpiration = _cosProxy.strTimeExpiration2longTimeExpiration(it.expiration) ?: it.expiredTime.toLong()
////            _cosProxy.initService(it.accessKeyId, it.accessKeySecret, it.securityToken)
////        }
////
////    @JvmStatic
////    fun initOSSClient(): OSSClient? =
////        runBlocking {
////            Repository.getSignatureOnBack()
////        }?.let {
////            _longTimeExpiration = _cosProxy.strTimeExpiration2longTimeExpiration(it.expiration) ?: System.currentTimeMillis()
////            _cosProxy.initOSSClient(it.accessKeyId, END_POINT, it.accessKeySecret, it.securityToken)
////        }
////
////    @JvmStatic
////    fun getSignatureStrUrl(url: String): String? {
////        UtilKLogWrapper.d(TAG, "getSignatureStrUrl: url $url")
////        return getOSSClient()?.presignConstrainedObjectURL(BUCKET_NAME, url, 1800_000)?.apply { UtilKLogWrapper.d(TAG, "getSignatureStrUrl: origin url $this") }?.replaceCdnHost()
////            .also { UtilKLogWrapper.v(TAG, "getSignatureStrUrl: $it") }
////    }
////
////    @JvmStatic
////    fun getOSSClient(): OSSClient? {
////        if (_cosProxy.getOSSClient() == null) {
////            initOSSClient()
////        } else if (System.currentTimeMillis() + 5 * 60_000 >= _longTimeExpiration) {
////            updateOSSClient()
////        }
////        return _cosProxy.getOSSClient()
////    }
////
////    @JvmStatic
////    fun String.replaceCdnHost(): String {
////        if (_protocol.isEmpty()) {
////            _protocol = this.getSplitFirstIndexToStart(":")
////        }
////        return this.replaceFirst(getOriginHost(_protocol), DataSourceLocale.cdnHost)
////    }
//}
//
