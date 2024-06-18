package com.mozhimen.netk.mqtt.bases

import com.mozhimen.netk.mqtt.commons.IMQTTDataListener
import com.mozhimen.netk.mqtt.commons.IMQTTGenConnBeanListener
import com.mozhimen.netk.mqtt.commons.IMQTTListener
import com.mozhimen.netk.mqtt.commons.IMQTTSubsResListener
import com.mozhimen.netk.mqtt.helpers.MQTTManager
import com.mozhimen.servicek.bases.BaseLifecycleService2

/**
 * @ClassName MQTTService
 * @Description MQTTService
 * @Author Kolin Zhao / Mozhimen
 * @Date 2022/9/26 18:20
 * @Version 1.0
 */
abstract class BaseNetKMQTTService : BaseLifecycleService2(), IMQTTListener {

    private val _mqttDataListener: IMQTTDataListener = object : IMQTTDataListener {
        override fun onGetData(data: String) {
            invoke(data)
        }
    }
    private val _mattManager by lazy {
        MQTTManager(
            this, this,
            _mqttDataListener = _mqttDataListener,
            _mqttSubsResListener = getSubsResListener(),
            _mqttGenConnBeanListener = getGenConnBeanListener()
        )
    }

    abstract fun getSubsResListener(): IMQTTSubsResListener

    abstract fun getGenConnBeanListener(): IMQTTGenConnBeanListener

    override fun onCreate() {
        super.onCreate()
        start()
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }

    override fun start() {
        _mattManager.start()
    }

    override fun stop() {
        _mattManager.stop()
    }
}