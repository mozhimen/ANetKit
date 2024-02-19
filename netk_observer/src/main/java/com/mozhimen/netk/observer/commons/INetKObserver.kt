package com.mozhimen.netk.observer.commons

import androidx.lifecycle.LiveData
import com.mozhimen.basick.lintk.annors.ANetType

/**
 * @ClassName INetKObserver
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/9/27 14:13
 * @Version 1.0
 */
interface INetKObserver {
    fun getNetTypes(): Set<String>
    fun getLiveNetTypes(): LiveData<Set<@ANetType String>>// 获取状态
    fun register(obj: Any)// 注册
    fun unRegister(obj: Any)// 取消注册
    fun unRegisterAll()// 取消所有注册
}