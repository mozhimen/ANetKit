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
import com.mozhimen.basick.lintk.optins.permission.OPermission_INTERNET


/**
 * @ClassName SenseKNetConnProxy
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
@OApiCall_BindLifecycle
@OApiInit_ByLazy
class NetKConnectionProxy<C> : BaseBroadcastReceiverProxy<C> where C : Context, C : LifecycleOwner {

    private val _listener: IConnectionListener

    @OptIn(OPermission_ACCESS_NETWORK_STATE::class, OPermission_INTERNET::class)
    constructor(
        context: C,
        listener: IConnectionListener,
        receiver: BaseConnectivityBroadcastReceiver = BaseConnectivityBroadcastReceiver(),
    ) : super(context, receiver, arrayOf(CConnectivityManager.CONNECTIVITY_ACTION)) {
        _listener = listener
        (_receiver as BaseConnectivityBroadcastReceiver).registerListener(_listener)
    }

    @OptIn(OPermission_ACCESS_NETWORK_STATE::class, OPermission_INTERNET::class)
    override fun onDestroy(owner: LifecycleOwner) {
        (_receiver as BaseConnectivityBroadcastReceiver).unRegisterListener(_listener)
        super.onDestroy(owner)
    }
}