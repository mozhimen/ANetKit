package com.mozhimen.netk.file.okdownload.helpers

import androidx.lifecycle.LifecycleOwner
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.lintk.optins.api.OApiInit_ByLazy

/**
 * @ClassName NetKFileDownload
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/11/1 21:52
 * @Version 1.0
 */
class OkDownloadTaskManager(owner: LifecycleOwner) {
    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class, OApiCall_BindViewLifecycle::class)
    private val _okDownloadSingleTask by lazy { OkDownloadSingleTask().apply { bindLifecycle(owner) } }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class, OApiCall_BindViewLifecycle::class)
    fun singleFileTask(): OkDownloadSingleTask =
        _okDownloadSingleTask
}