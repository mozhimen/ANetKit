package com.mozhimen.netk.mqtt.mos

data class MQTTBaseBean(
    val code: String?,
    val data: DataBean?,
    val msg: String?,
    val success: Boolean?
)

data class DataBean(
    val accessKey: String,
    val deviceId: String
)