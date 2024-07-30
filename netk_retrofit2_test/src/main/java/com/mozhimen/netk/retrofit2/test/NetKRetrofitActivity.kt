package com.mozhimen.netk.retrofit2.test

import android.annotation.SuppressLint
import android.os.Bundle
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import androidx.lifecycle.lifecycleScope
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVDBVM
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.basick.manifestk.permission.ManifestKPermission
import com.mozhimen.basick.manifestk.permission.annors.APermissionCheck
import com.mozhimen.netk.retrofit2.test.databinding.ActivityNetkHttpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            vm.getRealtimeWeatherCache()
        }
    }
}