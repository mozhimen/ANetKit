package com.mozhimen.netk.observer.commons

/**
 * @ClassName INetKObserverOwner
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/2/19
 * @Version 1.0
 */
interface INetKObserverOwner {
    fun onChanged(types: Set<String>)
}