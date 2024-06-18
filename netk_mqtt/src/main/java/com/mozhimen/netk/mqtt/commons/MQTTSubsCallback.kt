package com.mozhimen.netk.mqtt.commons

import android.util.Log
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken


/**
 * @ClassName MQTTSubsCallback
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
class MQTTSubsCallback(private val _url: String) : IMqttActionListener, IUtilK {

    override fun onSuccess(asyncActionToken: IMqttToken) {
        Log.d(TAG, "subscribe: onSuccess: topicUrl: $_url")
    }

    override fun onFailure(
        asyncActionToken: IMqttToken,
        exception: Throwable
    ) {
        //消息订阅失败 index
        exception.printStackTrace()
        UtilKLogWrapper.e(TAG, "subscribe: onFailure: topicUrl: 消息订阅失败$_url ${exception.message ?: ""}")
    }
}