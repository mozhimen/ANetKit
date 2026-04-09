package com.mozhimen.netk.file.test

import android.os.Bundle
import android.util.Log
import com.liulishuo.okdownload.DownloadTask
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.lintk.optins.api.OApiInit_ByLazy
import com.mozhimen.netk.file.okdownload.commons.IFileDownloadSingleListener
import com.mozhimen.kotlin.utilk.kotlin.UtilKStrFile
import com.mozhimen.kotlin.utilk.kotlin.UtilKStrPath
import com.mozhimen.netk.file.okdownload.NetKFileOkDownload
import com.mozhimen.netk.file.test.databinding.ActivityNetkFileBinding
import com.mozhimen.permissionk.PermissionK
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDB

class NetKFileActivity : BaseActivityVDB<ActivityNetkFileBinding>() {
    private val _netKFile by lazy { NetKFileOkDownload(this) }
    private val _musicUrl = "http://192.168.2.6/construction-sites-images/voice/20221102/176f9197f0694591b16ffd47a0f117fe.wav"
    private val _musicPath by lazy { UtilKStrPath.Absolute.Internal.getFiles() + "/netkfile/music.wav" }
//    private var _downloadRequest: DownloadRequest? = null

    private val _fileDownloadSingleListener = object : IFileDownloadSingleListener {
        override fun onComplete(task: DownloadTask) {
            Log.d(TAG, "onComplete: path ${task.uri?.path}")
            Log.d(TAG, "onComplete: isFileExists ${task.uri.path?.let { UtilKStrFile.isFileExist(it) } ?: "null"}")
            vdb.netkFileBtn1.isClickable = true
        }

        override fun onFail(task: DownloadTask, e: Exception?) {
            e?.printStackTrace()
            Log.e(TAG, "onFail fail msg: ${e?.message}")
            vdb.netkFileBtn1.isClickable = true
        }
    }

//    private val _downloadListener = object : IDownloadListener {
//        override fun onDownloadStart() {
//            Log.d(TAG, "onDownloadStart")
//        }
//
//        override fun onProgressUpdate(percent: Int) {
//            Log.d(TAG, "onProgressUpdate: percent $percent")
//        }
//
//        override fun onDownloadComplete(uri: Uri) {
//            Log.d(TAG, "onDownloadComplete: path ${uri.path}")
//            Log.d(TAG, "onDownloadComplete: isFileExists ${uri.path?.let { UtilKStrFile.isFileExist(it) } ?: "null"}")
//            vb.netkFileBtn2.isClickable = true
//        }
//
//        override fun onDownloadFailed(e: Throwable) {
//            e.printStackTrace()
//            Log.e(TAG, "onDownloadFailed fail msg: ${e.message}")
//            vb.netkFileBtn2.isClickable = true
//        }
//    }

    override fun initData(savedInstanceState: Bundle?) {
        PermissionK.requestPermissions(this) {
            if (it) {
                super.initData(savedInstanceState)
            }
        }
    }

    @OptIn(OApiCall_BindViewLifecycle::class, OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    override fun initView(savedInstanceState: Bundle?) {
        vdb.netkFileBtn1.setOnClickListener {
            vdb.netkFileBtn1.isClickable = false
            _netKFile.download().singleFileTask().start(_musicUrl, _musicPath, _fileDownloadSingleListener)
        }

//        vb.netkFileBtn2.setOnClickListener {
//            vb.netkFileBtn2.isClickable = false
//            _downloadRequest = createCommonRequest(_musicUrl, _musicPath)
//            _downloadRequest!!.registerListener(_downloadListener)
//            _downloadRequest!!.startDownload()
//        }
    }

//    private fun createCommonRequest(strUrl: String, strPathNameApk: String): DownloadRequest =
//        DownloadRequest(this, strUrl, ADownloadEngine.EMBED)
//            .setNotificationVisibility(ANotificationVisibility.HIDDEN)
//            .setShowNotificationDisableTip(false)
//            .setDestinationUri(Uri.fromFile(File(strPathNameApk)))
}