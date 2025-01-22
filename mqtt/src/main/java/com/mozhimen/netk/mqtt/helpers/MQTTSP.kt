package com.mozhimen.netk.mqtt.helpers

import com.mozhimen.cachek.sharedpreferences.CacheKSP
import com.mozhimen.cachek.sharedpreferences.temps.CacheKSPVarPropertyInt


/**
 * @ClassName MQTTSP
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
object MQTTSP {
    private val _spMQTT = CacheKSP.instance.with("netk_mqtt_sp")
    var mqttConnType: Int by CacheKSPVarPropertyInt(_spMQTT,"mqttConnType")
}