package com.mozhimen.netk.mqtt.mos

data class MQTTConnBean(
    var userName: String,
    var password: String,
    var clientId: String,
    var topicUrls: List<String>
)