package com.mozhimen.netk.mqtt.commons

import com.mozhimen.netk.mqtt.annors.AConnType


/**
 * @ClassName IMQTTGenConnBeanListener
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
interface IMQTTGenConnBeanListener {
    fun onGetBaseUrl(): String
    fun onGetUseName(@AConnType connType: Int): String
    fun onGetPassword(@AConnType connType: Int): String
    fun onGetClientId(@AConnType connType: Int): String
    fun onGetTopics(@AConnType connType: Int): List<String>
}