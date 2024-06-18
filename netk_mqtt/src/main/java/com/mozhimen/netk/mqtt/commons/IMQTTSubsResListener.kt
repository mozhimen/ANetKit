package com.mozhimen.netk.mqtt.commons

import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * @ClassName IMQTTSubsResListener
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
interface IMQTTSubsResListener {
    fun onTopicPush(topic: String, payload: String, message: MqttMessage)
}