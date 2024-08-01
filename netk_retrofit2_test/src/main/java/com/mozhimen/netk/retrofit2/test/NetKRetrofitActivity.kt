package com.mozhimen.netk.retrofit2.test

import android.annotation.SuppressLint
import android.os.Bundle
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVDBVM
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.basick.manifestk.permission.ManifestKPermission
import com.mozhimen.basick.manifestk.permission.annors.APermissionCheck
import com.mozhimen.netk.retrofit2.test.databinding.ActivityNetkHttpBinding

@APermissionCheck(CPermission.INTERNET)
class NetKRetrofitActivity : BaseActivityVDBVM<ActivityNetkHttpBinding, NetKRetrofitViewModel>() {
    override fun initData(savedInstanceState: Bundle?) {
        ManifestKPermission.requestPermissions(this) {
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