package com.mozhimen.netk.connection

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.basick.elemk.android.content.bases.BaseBroadcastReceiverProxy
import com.mozhimen.basick.elemk.android.content.bases.BaseConnectivityBroadcastReceiver
import com.mozhimen.basick.elemk.android.net.cons.CConnectivityManager
import com.mozhimen.basick.elemk.commons.IConnectionListener
import com.mozhimen.basick.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.basick.lintk.optins.OApiInit_ByLazy
import com.mozhimen.basick.lintk.optins.permission.OPermission_ACCESS_NETWORK_STATE


/**
 * @ClassName SenseKNetConnProxy
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/2/13 15:41
 * @Version 1.0
 */
@OptIn(OPermission_ACCESS_NETWORK_STATE::class)
@OApiCall_BindLifecycle
@OApiInit_ByLazy
class NetKConnectionProxy<C>(
    context: C,
    private val _listener: IConnectionListener,
    receiver: BaseConnectivityBroadcastReceiver = BaseConnectivityBroadcastReceiver(),
) : BaseBroadcastReceiverProxy<C>(context, receiver, arrayOf(CConnectivityManager.CONNECTIVITY_ACTION)) where C : Context, C : LifecycleOwner {

    init {
        (_receiver as BaseConnectivityBroadcastReceiver).registerListener(_listener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        (_receiver as BaseConnectivityBroadcastReceiver).unRegisterListener(_listener)
        super.onDestroy(owner)
    }
}