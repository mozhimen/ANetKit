package com.mozhimen.netk.observer.helpers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozhimen.basick.elemk.android.content.bases.BaseConnectivityBroadcastReceiver
import com.mozhimen.basick.elemk.android.net.cons.CConnectivityManager
import com.mozhimen.basick.elemk.android.net.cons.CNetType
import com.mozhimen.basick.elemk.android.os.cons.CVersCode
import com.mozhimen.basick.lintk.annors.ANetType
import com.mozhimen.basick.lintk.optins.permission.OPermission_ACCESS_NETWORK_STATE
import com.mozhimen.basick.lintk.optins.permission.OPermission_ACCESS_WIFI_STATE
import com.mozhimen.basick.lintk.optins.permission.OPermission_INTERNET
import com.mozhimen.basick.taskk.handler.TaskKHandler
import com.mozhimen.basick.utilk.android.app.UtilKApplicationReflect
import com.mozhimen.basick.utilk.android.net.UtilKNet
import com.mozhimen.basick.utilk.android.net.eNetType2strNetType
import com.mozhimen.basick.utilk.android.net.networkCapabilities2netType
import com.mozhimen.basick.utilk.android.util.it
import com.mozhimen.basick.utilk.android.util.wt
import com.mozhimen.basick.utilk.bases.IUtilK
import com.mozhimen.netk.observer.commons.INetKObserver
import com.mozhimen.netk.observer.utils.NetKObserverUtil
import java.lang.reflect.Method
import java.util.HashMap

/**
 * @ClassName NetworkCallbackImpl
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/9/27 10:52
 * @Version 1.0
 */
@OPermission_INTERNET
@OPermission_ACCESS_WIFI_STATE
@OPermission_ACCESS_NETWORK_STATE
@RequiresApi(CVersCode.V_21_5_L)
class NetworkCallbackProxy : ConnectivityManager.NetworkCallback(), IUtilK, INetKObserver {
//    companion object {
//        @JvmStatic
//        val instance = INSTANCE.holder
//    }

    //////////////////////////////////////////////////////////////////////////////////

    private val _liveNetType: MutableLiveData<@ANetType String> = MutableLiveData()// 网络状态
    private val _checkManMap = HashMap<Any, Method>()// 观察者，key=类、value=方法
    private val _netStatusReceiver = NetStatusReceiver()// 网络状态广播监听

    //////////////////////////////////////////////////////////////////////////////////

    init {
        val intentFilter = IntentFilter().apply { addAction(CConnectivityManager.CONNECTIVITY_ACTION/*"android.net.conn.CONNECTIVITY_CHANGE"*/) }
        UtilKApplicationReflect.instance.applicationContext.registerReceiver(_netStatusReceiver, intentFilter)
    }

    //////////////////////////////////////////////////////////////////////////////////

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        "onAvailable: 网络已连接".it(TAG)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        "onLost: 网络已断开连接".it(TAG)
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        val type = networkCapabilities.networkCapabilities2netType().eNetType2strNetType()
        "onCapabilitiesChanged: 网络连接改变 $type network $network networkCapabilities $networkCapabilities".wt(TAG)// 表明此网络连接成功验证
        if (type == _liveNetType.value) return
        TaskKHandler.post {
            post(type)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    override fun getNetType(): String =
        getLiveNetType().value ?: CNetType.UNKNOWN


    override fun getLiveNetType(): LiveData<String> =
        _liveNetType


    override fun register(obj: Any) {
        val clz = obj.javaClass
        if (!_checkManMap.containsKey(clz)) {
            val method = NetKObserverUtil.findAnnotationMethod(clz) ?: return
            _checkManMap[obj] = method
            post(obj, method, UtilKNet.getActiveNetType().eNetType2strNetType())
        }
    }

    override fun unRegister(obj: Any) {
        _checkManMap.remove(obj)
    }

    override fun unRegisterAll() {
        _checkManMap.clear()
    }

    //////////////////////////////////////////////////////////////////////////////////

    // 执行
    private fun post(str: @ANetType String) {
        _liveNetType.postValue(str)
        val set: Set<Any> = _checkManMap.keys
        for (obj in set) {
            val method = _checkManMap[obj] ?: continue
            post(obj, method, str)
        }
    }

    // 反射执行
    private fun post(obj: Any, method: Method, type: @ANetType String) {
        try {
            method.invoke(obj, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    @OPermission_ACCESS_NETWORK_STATE
    @OPermission_ACCESS_WIFI_STATE
    @OPermission_INTERNET
    inner class NetStatusReceiver : BaseConnectivityBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            context ?: return
            val type = UtilKNet.getActiveNetType().eNetType2strNetType()
            if (type == _liveNetType.value) return
            post(type)
        }
    }

//    private object INSTANCE {
//        val holder = NetworkCallbackProxy()
//    }
}