package com.mozhimen.netk.file.okdownload

import android.content.Context
import com.liulishuo.okdownload.core.ExtOkDownload
import com.mozhimen.kotlin.lintk.optins.OApiInit_InApplication
import com.mozhimen.netk.okdownload.ext.NetKOkDownloadExt

/**
 * @ClassName NetKFileOkDownloadMgr
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/11/24 21:25
 * @Version 1.0
 */
@OApiInit_InApplication
object NetKFileOkDownloadMgr {
    /**
     * fix bug of #415
     */
    @JvmStatic
    fun init(context: Context) {
        NetKOkDownloadExt.init(context)
    }
}