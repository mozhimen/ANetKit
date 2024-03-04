package com.mozhimen.netk

import android.Manifest
import android.os.Bundle
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVBVM
import com.mozhimen.basick.manifestk.annors.AManifestKRequire
import com.mozhimen.basick.manifestk.permission.ManifestKPermission
import com.mozhimen.basick.manifestk.permission.annors.APermissionCheck
import com.mozhimen.netk.databinding.ActivityMainBinding

@AManifestKRequire(Manifest.permission.INTERNET)
@APermissionCheck(Manifest.permission.INTERNET)
class MainActivity : BaseActivityVBVM<ActivityMainBinding, MainViewModel>() {
    override fun initData(savedInstanceState: Bundle?) {
        ManifestKPermission.requestPermissions(this) {
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

    override fun bindViewVM(vb: ActivityMainBinding) {
        vdb.vm = vm
    }
}