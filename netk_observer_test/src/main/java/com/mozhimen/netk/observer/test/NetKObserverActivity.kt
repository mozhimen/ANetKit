package com.mozhimen.netk.observer.test

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVB
import com.mozhimen.basick.lintk.optins.application.OApplication_USES_CLEAR_TEXT_TRAFFIC
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.basick.manifestk.permission.ManifestKPermission
import com.mozhimen.basick.utilk.android.util.dt
import com.mozhimen.basick.utilk.java.net.UtilKHttpURLConnection
import com.mozhimen.basick.utilk.java.net.UtilKNetworkInterface
import com.mozhimen.netk.observer.NetKObserver
import com.mozhimen.netk.observer.annors.ANetKObserver
import com.mozhimen.netk.observer.test.databinding.ActivityNetkObserverBinding
import kotlinx.coroutines.launch

/**
 * @ClassName NetKObserverActivity
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/1/15 20:47
 * @Version 1.0
 */
class NetKObserverActivity : BaseActivityVB<ActivityNetkObserverBinding>()/*, INetKObserverOwner*/ {
    override fun initData(savedInstanceState: Bundle?) {
        ManifestKPermission.requestPermissions(this, arrayOf(CPermission.ACCESS_FINE_LOCATION)) {
            if (it) {
                super.initData(savedInstanceState)
            }
        }
    }

    @OptIn(OApplication_USES_CLEAR_TEXT_TRAFFIC::class)
    @SuppressLint("MissingPermission")
    override fun initView(savedInstanceState: Bundle?) {
        NetKObserver.instance.register(this)
        vb.netkObserverTxt.setOnClickListener {
            UtilKNetworkInterface.printStrIp()
            lifecycleScope.launch {
                UtilKHttpURLConnection.getStrIpOnBack().dt(TAG)
            }
        }
    }

    @ANetKObserver
    fun onNetChange(types: Set<String>) {
        vb.netkObserverTxt.text = types.joinToString { it }
    }

    override fun onDestroy() {
        NetKObserver.instance.unRegister(this)
        super.onDestroy()
    }

//    override fun onChanged(types: Set<String>) {
//        Log.d(TAG, "onChanged: $types")
//    }
}