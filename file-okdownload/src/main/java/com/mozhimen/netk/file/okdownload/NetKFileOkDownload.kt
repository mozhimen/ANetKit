package com.mozhimen.netk.file.okdownload

import androidx.lifecycle.LifecycleOwner
import com.mozhimen.netk.file.okdownload.helpers.OkDownloadTaskManager


/**
 * @ClassName NetKFile
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
class NetKFileOkDownload(owner: LifecycleOwner) {

    private val _okDownloadTaskManager by lazy { OkDownloadTaskManager(owner) }

    fun download(): OkDownloadTaskManager =
        _okDownloadTaskManager

//    companion object {
//        @JvmStatic
//        val instance = NetKFileDownloadProvider.holder
//    }
//
//    private object NetKFileDownloadProvider {
//        val holder = NetKFileDownload()
//    }
//
//    private val _netKFileDownload: NetKFileDownload by lazy { NetKFileDownload() }
//
//    fun download(): NetKFileDownload {
//        return _netKFileDownload
//    }

//    object Download {
//        @JvmStatic
//        fun start(url: String, filePath: String, fileName: String, threadCount: Int): DownloadManger {
//            val downloadManger: DownloadManger = DownloadManger.getInstance(context)
//            downloadManger.init(url, path, name, childTaskCount)
//            return downloadManger
//        }
//
//        @JvmStatic
//        fun start(url: String, file: File, threadCount: Int): DownloadManger {
//            val downloadManger: DownloadManger = DownloadManger.getInstance(context)
//            downloadManger.init(url, path, name, childTaskCount)
//            return downloadManger
//        }
//    }

}