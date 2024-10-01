package com.mozhimen.netk.connection.test

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import androidx.core.app.ActivityCompat
import com.mozhimen.kotlin.elemk.android.content.cons.CPackageManager
import com.mozhimen.kotlin.elemk.android.net.cons.ENetType
import com.mozhimen.bindk.bases.activity.databinding.BaseActivityVDB
import com.mozhimen.kotlin.elemk.commons.IConnectionListener
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_ACCESS_FINE_LOCATION
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_ACCESS_NETWORK_STATE
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_ACCESS_WIFI_STATE
import com.mozhimen.kotlin.elemk.android.cons.CPermission
import com.mozhimen.manifestk.permission.ManifestKPermission
import com.mozhimen.kotlin.utilk.android.net.UtilKNet
import com.mozhimen.kotlin.utilk.android.net.eNetType2strNetType
import com.mozhimen.netk.connection.NetKConnectionProxy
import com.mozhimen.netk.connection.test.databinding.ActivityNetkConnectionBinding

/**
 * @ClassName NetKConnActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/2/13 15:36
 * @Version 1.0
 */
class NetKConnectionActivity : BaseActivityVDB<ActivityNetkConnectionBinding>() {
    @OptIn(OApiCall_BindLifecycle::class, OApiInit_ByLazy::class, OPermission_ACCESS_NETWORK_STATE::class)
    private val _netKConnectionProxy: NetKConnectionProxy<NetKConnectionActivity> by lazy_ofNone { NetKConnectionProxy(this, _netKConnListener).apply { bindLifecycle(this@NetKConnectionActivity) } }

    @OptIn(OPermission_ACCESS_WIFI_STATE::class, OPermission_ACCESS_FINE_LOCATION::class)
    private val _netKConnListener = object : IConnectionListener {
        override fun onDisconnect() {
            vdb.netkConnTxt.text = "断网了"
        }

        @SuppressLint("SetTextI18n")
        override fun onConnect(types: Set<ENetType>) {
            val stringBuilder = StringBuilder()
            types.forEach {
                UtilKLogWrapper.d(TAG, "onConnect: ${it.eNetType2strNetType()}")
                when (it) {
                    ENetType.MOBILE_4G -> stringBuilder.append("移动网络4G").append(",")
                    ENetType.MOBILE_3G -> stringBuilder.append("移动网络3G").append(",")
                    ENetType.MOBILE_2G -> stringBuilder.append("移动网络2G").append(",")
                    ENetType.MOBILE -> stringBuilder.append("移动网络").append(",")
                    ENetType.WIFI -> {
                        stringBuilder.append("WIFI").append(",")
                        if (ActivityCompat.checkSelfPermission(this@NetKConnectionActivity, Manifest.permission.ACCESS_FINE_LOCATION) == CPackageManager.PERMISSION_GRANTED) {
                            stringBuilder.append("risi ${UtilKNet.getWifiStrength()}").append(",")
                        }
                    }

                    ENetType.VPN -> stringBuilder.append("VPN").append(",")
                    ENetType.UNKNOWN -> stringBuilder.append("未知连接").append(",")

                    else -> stringBuilder.append("其他").append(",")
                }
            }

            vdb.netkConnTxt.text = "有连接 ${stringBuilder.substring(0, stringBuilder.length - 1)}"
        }
    }

    @OptIn(OApiCall_BindLifecycle::class, OApiInit_ByLazy::class)
    override fun initView(savedInstanceState: Bundle?) {
        ManifestKPermission.requestPermissions(this, arrayOf(CPermission.ACCESS_FINE_LOCATION)) {
            if (it) {
                _netKConnectionProxy.bindLifecycle(this)
            }
        }
    }
}