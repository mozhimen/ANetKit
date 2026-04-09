package com.mozhimen.netk.retrofit2.test

import android.annotation.SuppressLint
import android.os.Bundle
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDBVM
import com.mozhimen.netk.retrofit2.test.databinding.ActivityNetkHttpBinding
import com.mozhimen.permissionk.PermissionK

class NetKRetrofitActivity : BaseActivityVDBVM<ActivityNetkHttpBinding, NetKRetrofitViewModel>() {
    override fun initData(savedInstanceState: Bundle?) {
        PermissionK.requestPermissions(this) {
            if (it) {
                super.initData(savedInstanceState)
            }
        }
    }

    override fun bindViewVM(vdb: ActivityNetkHttpBinding) {
        vdb.vm = vm
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        vdb.netkBtn1GetWeather.setOnClickListener {
            vm.getRealtimeWeatherCoroutine()
        }

        vdb.netkBtn2GetWeather.setOnClickListener {
            vm.getRealtimeWeatherCoroutineSync()
        }

        vdb.netkBtn3GetWeather.setOnClickListener {
            vm.getRealtimeWeatherRetrofitCache()
        }

        vdb.netkBtn4GetWeather.setOnClickListener {
            vm.getRealtimeWeatherOkhttp3Cache()
        }
    }
}