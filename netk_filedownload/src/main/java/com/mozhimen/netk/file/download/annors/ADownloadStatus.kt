package com.mozhimen.netk.file.download.annors

import androidx.annotation.IntDef
import com.mozhimen.kotlin.elemk.android.app.cons.CDownloadManager

/**
 *
 * @author by chiclaim@google.com
 */
@IntDef(
    value = [
        CDownloadManager.STATUS_PENDING,
        CDownloadManager.STATUS_PAUSED,
        CDownloadManager.STATUS_RUNNING,
        CDownloadManager.STATUS_SUCCESSFUL,
        CDownloadManager.STATUS_FAILED,
        CDownloadManager.STATUS_UNKNOWN
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class ADownloadStatus
