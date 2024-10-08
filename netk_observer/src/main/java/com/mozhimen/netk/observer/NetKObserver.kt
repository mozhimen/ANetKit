package com.mozhimen.netk.observer

import android.net.NetworkRequest
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.mozhimen.kotlin.elemk.android.os.cons.CVersCode
import com.mozhimen.kotlin.lintk.annors.ANetType
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_ACCESS_NETWORK_STATE
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_INTERNET
import com.mozhimen.kotlin.utilk.android.app.UtilKApplicationWrapper
import com.mozhimen.kotlin.utilk.android.net.UtilKConnectivityManager
import com.mozhimen.kotlin.utilk.android.os.UtilKBuildVersion
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.netk.observer.commons.INetKObserver
import com.mozhimen.netk.observer.helpers.NetworkCallbackProxy

/**
 * @ClassName NetKObserver
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/9/27 14:08
 * @Version 1.0
 */
@OptIn(OPermission_ACCESS_NETWORK_STATE::class, OPermission_INTERNET::class)
@RequiresApi(CVersCode.V_21_5_L)
class NetKObserver : BaseUtilK(), INetKObserver {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    private val _networkCallbackProxy by lazy { NetworkCallbackProxy() }

    ////////////////////////////////////////////////////////////////////////////////////////////

    init {
        if (UtilKBuildVersion.isAfterV_24_7_N())
            UtilKConnectivityManager.registerDefaultNetworkCallback(_context, _networkCallbackProxy)
        else
            UtilKConnectivityManager.registerNetworkCallback(_context, NetworkRequest.Builder().build(), _networkCallbackProxy)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    fun init() {
        //无操作, 初始化
    }

    override fun getNetTypes(): Set<String> =
        _networkCallbackProxy.getNetTypes()

    override fun getLiveNetTypes(): LiveData<Set<@ANetType String>> =
        _networkCallbackProxy.getLiveNetTypes()

    override fun register(obj: Any) {
        _networkCallbackProxy.register(obj)
    }

    override fun unRegister(obj: Any) {
        _networkCallbackProxy.unRegister(obj)
    }

    override fun unRegisterAll() {
        _networkCallbackProxy.unRegisterAll()
        UtilKConnectivityManager.unregisterNetworkCallback(UtilKApplicationWrapper.instance.applicationContext, _networkCallbackProxy)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    private object INSTANCE {
        val holder = NetKObserver()
    }
}