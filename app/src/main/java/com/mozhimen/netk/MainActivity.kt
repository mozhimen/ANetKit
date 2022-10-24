package com.mozhimen.netk

import android.Manifest
import android.os.Bundle
import com.mozhimen.basick.basek.BaseKActivity
import com.mozhimen.componentk.permissionk.PermissionK
import com.mozhimen.componentk.permissionk.annors.PermissionKAnnor
import com.mozhimen.netk.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers

@PermissionKAnnor([Manifest.permission.INTERNET])
class MainActivity : BaseKActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override fun initData(savedInstanceState: Bundle?) {
        PermissionK.initPermissions(this) {
            if (it) {
                initView(savedInstanceState)
            }
        }
    }

    override fun injectVM() {
        vb.vm = vm
    }

    override fun initView(savedInstanceState: Bundle?) {
        vb.netkBtnGetWeather.setOnClickListener {
            vm.getRealtimeWeatherAsync()
        }


        vb.netkBtn2GetWeather.setOnClickListener {
            vm.getRealTimeWeatherRxJava()
        }
    }
}