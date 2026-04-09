package com.mozhimen.netk

import android.os.Bundle
import com.mozhimen.netk.databinding.ActivityMainBinding
import com.mozhimen.permissionk.PermissionK
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDBVM

class MainActivity : BaseActivityVDBVM<ActivityMainBinding, MainViewModel>() {
    override fun initData(savedInstanceState: Bundle?) {
        PermissionK.requestPermissions(this) {
            if (it) {
                super.initData(savedInstanceState)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        vdb.netkBtnGetWeather.setOnClickListener {
            vm.getRealtimeWeatherAsync()
        }

        vdb.netkBtn2GetWeather.setOnClickListener {
            vm.getRealTimeWeatherRxJava()
        }
    }

    override fun bindViewVM(vdb: ActivityMainBinding) {
        this.vdb.vm = vm
    }
}