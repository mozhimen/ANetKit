package com.mozhimen.netk.mqtt.annors

import androidx.annotation.IntDef


/**
 * @ClassName AConnType
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
@IntDef(value = [AConnType.LOGIN, AConnType.REGISTER])
annotation class AConnType {
    companion object {
        const val REGISTER = 1
        const val LOGIN = 2
    }
}
