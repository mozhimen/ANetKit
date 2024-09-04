package com.mozhimen.netk.file.download.test

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.mvvmk.bases.activity.databinding.BaseActivityVDB
import com.mozhimen.kotlin.lintk.optin.OptInApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optin.OptInApiDeprecated_ThirdParty
import com.mozhimen.kotlin.lintk.optin.OptInApiInit_ByLazy
import com.mozhimen.kotlin.elemk.android.cons.CPermission
import com.mozhimen.manifestk.permission.ManifestKPermission
import com.mozhimen.manifestk.permission.annors.APermissionCheck
import com.mozhimen.kotlin.lintk.annors.AManifestRequire
import com.mozhimen.kotlin.utilk.kotlin.UtilKStrFile
import com.mozhimen.kotlin.utilk.kotlin.UtilKStrPath
import com.mozhimen.netk.file.download.DownloadRequest
import com.mozhimen.netk.file.download.annors.ADownloadEngine
import com.mozhimen.netk.file.download.annors.ANotificationVisibility
import com.mozhimen.netk.file.download.commons.IDownloadListener
import com.mozhimen.netk.file.download.test.databinding.ActivityNetkFileBinding
import java.io.File

@AManifestRequire(
    CPermission.READ_EXTERNAL_STORAGE,
    CPermission.WRITE_EXTERNAL_STORAGE,
    CPermission.INTERNET
)
@APermissionCheck(
    CPermission.READ_EXTERNAL_STORAGE,
    CPermission.WRITE_EXTERNAL_STORAGE,
    CPermission.INTERNET
)
class NetKFileActivity : BaseActivityVDB<ActivityNetkFileBinding>() {
//    private val _netKFile by lazy { NetKFileOkDownload(this) }
    private val _musicUrl = "http://192.168.2.6/construction-sites-images/voice/20221102/176f9197f0694591b16ffd47a0f117fe.wav"
    private val _musicPath by lazy_ofNone { UtilKStrPath.Absolute.Internal.getFiles() + "/netkfile/music.wav" }
    private var _downloadRequest: DownloadRequest? = null

//    private val _fileDownloadSingleListener = object : IFileDownloadSingleListener {
//        override fun onComplete(task: DownloadTask) {
//            UtilKLogWrapper.d(TAG, "onComplete: path ${task.uri?.path}")
//            UtilKLogWrapper.d(TAG, "onComplete: isFileExists ${task.uri.path?.let { UtilKStrFile.isFileExist(it) } ?: "null"}")
//            vdb.netkFileBtn1.isClickable = true
//        }
//
//        override fun onFail(task: DownloadTask, e: Exception?) {
//            e?.printStackTrace()
//            UtilKLogWrapper.e(TAG, "onFail fail msg: ${e?.message}")
//            vdb.netkFileBtn1.isClickable = true
//        }
//    }

    private val _downloadListener = object : IDownloadListener {
        override fun onDownloadStart() {
            UtilKLogWrapper.d(TAG, "onDownloadStart")
        }

        override fun onProgressUpdate(percent: Int) {
            UtilKLogWrapper.d(TAG, "onProgressUpdate: percent $percent")
        }

        override fun onDownloadComplete(uri: Uri) {
            UtilKLogWrapper.d(TAG, "onDownloadComplete: path ${uri.path}")
            UtilKLogWrapper.d(TAG, "onDownloadComplete: isFileExists ${uri.path?.let { UtilKStrFile.isFileExist(it) } ?: "null"}")
            vdb.netkFileBtn2.isClickable = true
        }

        override fun onDownloadFailed(e: Throwable) {
            e.printStackTrace()
            UtilKLogWrapper.e(TAG, "onDownloadFailed fail msg: ${e.message}")
            vdb.netkFileBtn2.isClickable = true
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        ManifestKPermission.requestPermissions(this) {
            if (it) {
                super.initData(savedInstanceState)
            }
        }
    }

    @OptIn(OptInApiCall_BindLifecycle::class, OptInApiInit_ByLazy::class, OptInApiDeprecated_ThirdParty::class)
    override fun initView(savedInstanceState: Bundle?) {
//        vdb.netkFileBtn1.setOnClickListener {
//            vdb.netkFileBtn1.isClickable = false
//            _netKFile.download().singleFileTask().start(_musicUrl, _musicPath, _fileDownloadSingleListener)
//        }

        vdb.netkFileBtn2.setOnClickListener {
            vdb.netkFileBtn2.isClickable = false
            _downloadRequest = createCommonRequest(_musicUrl, _musicPath)
            _downloadRequest!!.registerListener(_downloadListener)
            _downloadRequest!!.startDownload()
        }
    }

    private fun createCommonRequest(strUrl: String, strPathNameApk: String): DownloadRequest =
        DownloadRequest(this, strUrl, ADownloadEngine.EMBED)
            .setNotificationVisibility(ANotificationVisibility.HIDDEN)
            .setShowNotificationDisableTip(false)
            .setDestinationUri(Uri.fromFile(File(strPathNameApk)))
}