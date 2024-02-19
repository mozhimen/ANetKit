package com.mozhimen.netk.observer.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozhimen.basick.elemk.android.content.bases.BaseConnectivityBroadcastReceiver
import com.mozhimen.basick.elemk.android.net.cons.CConnectivityManager
import com.mozhimen.basick.elemk.android.os.cons.CVersCode
import com.mozhimen.basick.lintk.annors.ANetType
import com.mozhimen.basick.lintk.optins.permission.OPermission_ACCESS_NETWORK_STATE
import com.mozhimen.basick.taskk.handler.TaskKHandler
import com.mozhimen.basick.utilk.android.app.UtilKApplicationReflect
import com.mozhimen.basick.utilk.android.net.UtilKNet
import com.mozhimen.basick.utilk.android.net.eNetType2strNetType
import com.mozhimen.basick.utilk.android.net.networkCapabilities2netTypes
import com.mozhimen.basick.utilk.bases.IUtilK
import com.mozhimen.netk.observer.commons.INetKObserver
import com.mozhimen.netk.observer.commons.INetKObserverOwner
import com.mozhimen.netk.observer.utils.NetKObserverUtil
import java.lang.reflect.Method

/**
 * @ClassName NetworkCallbackImpl
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/9/27 10:52
 * @Version 1.0
 */
@SuppressLint("UnspecifiedRegisterReceiverFlag")
@OPermission_ACCESS_NETWORK_STATE
@RequiresApi(CVersCode.V_21_5_L)
class NetworkCallbackProxy : ConnectivityManager.NetworkCallback(), IUtilK, INetKObserver {

    private val _liveNetTypes: MutableLiveData<Set<@ANetType String>> = MutableLiveData()// 网络状态
    private val _invokeMethods = HashMap<Any, Method>()// 观察者，key=类、value=方法
    private val _networkStatusReceiver = NetworkStatusReceiver()// 网络状态广播监听

    //////////////////////////////////////////////////////////////////////////////////

    init {
        val intentFilter = IntentFilter().apply {
            addAction(CConnectivityManager.CONNECTIVITY_ACTION/*"android.net.conn.CONNECTIVITY_CHANGE"*/)
        }
        UtilKApplicationReflect.instance.applicationContext.registerReceiver(_networkStatusReceiver, intentFilter)
    }

    //////////////////////////////////////////////////////////////////////////////////

    //网络连接成功回调
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Log.d(TAG, "onAvailable: 网络连接成功回调")
    }

    //网络状态变化
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        val types = networkCapabilities.networkCapabilities2netTypes().map { it.eNetType2strNetType() }.toSet()
        Log.w(TAG, "onCapabilitiesChanged: 网络状态变化 $types network $network networkCapabilities $networkCapabilities")// 表明此网络连接成功验证
        if (types == _liveNetTypes.value) return
        TaskKHandler.post {
            post(types)
        }
    }

    //网络连接属性变化
    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties)
        Log.i(TAG, "onLinkPropertiesChanged: 网络连接属性变化")
    }

    //访问的网络阻塞状态发生变化
    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        super.onBlockedStatusChanged(network, blocked)
        Log.d(TAG, "onBlockedStatusChanged: 访问的网络阻塞状态发生变化")
    }

    //网络已断开连接
    override fun onLost(network: Network) {
        super.onLost(network)
        Log.e(TAG, "onLost: 网络已断开连接")
    }

    //网络连接超时或网络不可达
    override fun onUnavailable() {
        super.onUnavailable()
        Log.e(TAG, "onUnavailable: 网络连接超时或网络不可达")
    }

    //网络正在丢失连接
    override fun onLosing(network: Network, maxMsToLive: Int) {
        super.onLosing(network, maxMsToLive)
        Log.e(TAG, "onLosing: 网络正在丢失连接")
    }

    //////////////////////////////////////////////////////////////////////////////////

    override fun getNetTypes(): Set<String> =
        getLiveNetTypes().value ?: emptySet()


    override fun getLiveNetTypes(): LiveData<Set<@ANetType String>> =
        _liveNetTypes

    override fun register(obj: Any) {
        val clz = obj.javaClass
        if (!_invokeMethods.containsKey(clz)) {
            val method = try {
                if (obj is INetKObserverOwner) NetKObserverUtil.findMethodOfChild(clz) ?: NetKObserverUtil.findMethodOfAnnotation(clz) ?: return
                else NetKObserverUtil.findMethodOfAnnotation(clz) ?: return
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            _invokeMethods[obj] = method
            val types = UtilKNet.getActiveNetTypes().map { it.eNetType2strNetType() }.toSet()
            Log.d(TAG, "register: types $types")
            post(obj, method, types)
        }
    }

    override fun unRegister(obj: Any) {
        _invokeMethods.remove(obj)
    }

    override fun unRegisterAll() {
        _invokeMethods.clear()
    }

    //////////////////////////////////////////////////////////////////////////////////

    // 执行
    private fun post(types: Set<@ANetType String>) {
        _liveNetTypes.postValue(types)
        val set: Set<Any> = _invokeMethods.keys
        for (obj in set) {
            val method = _invokeMethods[obj] ?: continue
            post(obj, method, types)
        }
    }

    // 反射执行
    private fun post(obj: Any, method: Method, types: Set<@ANetType String>) {
        try {
            method.invoke(obj, types)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    inner class NetworkStatusReceiver : BaseConnectivityBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            context ?: return
            val types: Set<String> = UtilKNet.getActiveNetTypes().map { it.eNetType2strNetType() }.toSet()
            if (types == _liveNetTypes.value) return
            Log.d(TAG, "onReceive: types $types")
            post(types)
        }
    }

}