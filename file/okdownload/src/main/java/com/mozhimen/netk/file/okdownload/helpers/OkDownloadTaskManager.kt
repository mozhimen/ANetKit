package com.mozhimen.netk.file.okdownload.helpers

import androidx.lifecycle.LifecycleOwner
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy

/**
 * @ClassName NetKFileDownload
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/11/1 21:52
 * @Version 1.0
 */
class OkDownloadTaskManager(owner: LifecycleOwner) {
    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    private val _okDownloadSingleTask by lazy { OkDownloadSingleTask().apply { bindLifecycle(owner) } }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    fun singleFileTask(): OkDownloadSingleTask =
        _okDownloadSingleTask
}