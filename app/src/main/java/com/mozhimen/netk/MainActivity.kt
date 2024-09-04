package com.mozhimen.netk

import android.Manifest
import android.os.Bundle
import com.mozhimen.kotlin.elemk.androidx.appcompat.bases.databinding.BaseActivityVBVM
import com.mozhimen.kotlin.lintk.annors.AManifestRequire
import com.mozhimen.manifestk.permission.ManifestKPermission
import com.mozhimen.manifestk.permission.annors.APermissionCheck
import com.mozhimen.netk.databinding.ActivityMainBinding

@AManifestRequire(Manifest.permission.INTERNET)
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