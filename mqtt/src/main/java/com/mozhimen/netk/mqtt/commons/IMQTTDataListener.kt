package com.mozhimen.netk.mqtt.commons

interface IMQTTDataListener {
    fun onGetData(data: String)
}