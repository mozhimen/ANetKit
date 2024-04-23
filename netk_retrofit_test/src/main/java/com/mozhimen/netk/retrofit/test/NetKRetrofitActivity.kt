package com.mozhimen.netk.retrofit.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import androidx.lifecycle.lifecycleScope
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVBVM
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.basick.manifestk.permission.ManifestKPermission
import com.mozhimen.basick.manifestk.permission.annors.APermissionCheck
import com.mozhimen.basick.manifestk.annors.AManifestKRequire
import com.mozhimen.basick.manifestk.cons.CApplication
import com.mozhimen.netk.retrofit.test.databinding.ActivityNetkHttpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AManifestKRequire(CPermission.INTERNET, CApplication.USES_CLEAR_TEXT_TRAFFIC)
@APermissionCheck(CPermission.INTERNET)
class NetKRetrofitActivity : BaseActivityVBVM<ActivityNetkHttpBinding, NetKRetrofitViewModel>() {
    override fun initData(savedInstanceState: Bundle?) {
        ManifestKPermission.requestPermissions(this) {
            if (it) {
                super.initData(savedInstanceState)
            }
        }
    }

    override fun bindViewVM(vb: ActivityNetkHttpBinding) {
        UtilKLogWrapper.d(TAG, "bindViewVM: ")
        vdb.vm = vm
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {

        vdb.netkBtn1GetWeather.setOnClickListener {
            vm.getRealtimeWeatherCoroutine()
        }

        vdb.netkBtn2GetWeather.setOnClickListener {
            val time = System.currentTimeMillis()
            lifecycleScope.launch(Dispatchers.IO) {
                vm.getRealtimeWeatherCoroutineSync().bean?.let { bean ->
                    withContext(Dispatchers.Main) {
                        vdb.netkTxt2.text = bean.result.realtime.temperature.toString() + " ${System.currentTimeMillis() - time}"
                    }
                }
            }
        }
    }
}