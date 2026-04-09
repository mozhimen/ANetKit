package com.mozhimen.netk.observer.test

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mozhimen.kotlin.lintk.optins.manifest.application.OApplication_USES_CLEAR_TEXT_TRAFFIC
import com.mozhimen.kotlin.elemk.android.cons.CPermission
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.android.util.d
import com.mozhimen.kotlin.utilk.java.net.UtilKHttpURLConnectionWrapper
import com.mozhimen.kotlin.utilk.java.net.UtilKNetworkInterface
import com.mozhimen.kotlin.utilk.wrapper.UtilKNet
import com.mozhimen.netk.observer.NetKObserver
import com.mozhimen.netk.observer.annors.ANetKObserver
import com.mozhimen.netk.observer.test.databinding.ActivityNetkObserverBinding
import com.mozhimen.permissionk.PermissionK
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDB
import kotlinx.coroutines.launch

/**
 * @ClassName NetKObserverActivity
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/1/15 20:47
 * @Version 1.0
 */
class NetKObserverActivity : BaseActivityVDB<ActivityNetkObserverBinding>()/*, INetKObserverOwner*/ {
    override fun initData(savedInstanceState: Bundle?) {
        PermissionK.requestPermissions(this, arrayOf(CPermission.ACCESS_FINE_LOCATION)) {
            if (it) {
                super.initData(savedInstanceState)
            }
        }
    }

    @OptIn(OApplication_USES_CLEAR_TEXT_TRAFFIC::class)
    @SuppressLint("MissingPermission")
    override fun initView(savedInstanceState: Bundle?) {
        NetKObserver.instance.register(this)
        vdb.netkObserverTxt.setOnClickListener {
            UtilKLogWrapper.i(TAG,"${UtilKNet.getStrIP()}")
            lifecycleScope.launch {
                UtilKHttpURLConnectionWrapper.getStrIPOnBack().d(TAG)
            }
        }
    }

    @ANetKObserver
    fun onNetChange(types: Set<String>) {
        vdb.netkObserverTxt.text = types.joinToString { it }
    }

    override fun onDestroy() {
        NetKObserver.instance.unRegister(this)
        super.onDestroy()
    }

//    override fun onChanged(types: Set<String>) {
//        UtilKLogWrapper.d(TAG, "onChanged: $types")
//    }
}