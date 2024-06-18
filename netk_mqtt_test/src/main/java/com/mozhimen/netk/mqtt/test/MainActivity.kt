package com.mozhimen.netk.mqtt.test

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVDB
import com.mozhimen.basick.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.basick.lintk.optins.OApiInit_ByLazy
import com.mozhimen.netk.mqtt.NetKMQTTServiceProxy
import com.mozhimen.netk.mqtt.test.databinding.ActivityMainBinding
import com.mozhimen.servicek.bases.BaseServiceResCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivityVDB<ActivityMainBinding>() {

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    private val _prefabKServiceDelegate: NetKMQTTServiceProxy<MainActivity, MQTTService> by lazy {
        NetKMQTTServiceProxy(this, MQTTService::class.java,
            object : BaseServiceResCallback() {
                override fun onResString(resString: String?) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        vdb.demoTxt.text = resString ?: "loss"
                    }
                }
            })
    }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class)
    override fun initView(savedInstanceState: Bundle?) {
        _prefabKServiceDelegate.bindLifecycle(this)
    }
}