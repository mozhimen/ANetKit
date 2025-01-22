package com.mozhimen.netk.mqtt.test

import com.mozhimen.basick.postk.crypto.PostKCryptoDES
import com.mozhimen.basick.postk.crypto.mos.MCryptoDESConfig
import com.mozhimen.netk.mqtt.annors.AConnType
import com.mozhimen.netk.mqtt.bases.BaseNetKMQTTService
import com.mozhimen.netk.mqtt.commons.IMQTTGenConnBeanListener
import com.mozhimen.netk.mqtt.commons.IMQTTSubsResListener
import org.eclipse.paho.client.mqttv3.MqttMessage


/**
 * @ClassName MQTTService
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
class MQTTService : BaseNetKMQTTService() {
    companion object {
        const val header_register = "/v1/dn/dr/"//注册
        const val header_login = "/v1/dn/dl/"//登录
        const val header_policy_change = "/v1/dn/stg/"//策略变化通知
        const val header_data_issue = "/v1/dn/da/"//设备数据下发
        const val header_voice = "/v1/de/voice/"//设备语音数据发送
    }

    private val _topicRegisterUrl: String
        get() = "$header_register${Config.loginTenantId}/${Config.mqttResClientId}"
    private val _topicLoginUrl: String
        get() = "$header_login${Config.loginTenantId}/${Config.mqttResClientId}"
    private val _topicPolicyChangeUrl: String
        get() = "$header_policy_change${Config.loginTenantId}/${Config.mqttResClientId}"
    private val _topicDataIssueUrl: String
        get() = "$header_data_issue${Config.loginTenantId}/${Config.mqttResClientId}"
    private val _topicVoiceUrl: String
        get() = "$header_voice${Config.loginTenantId}/${Config.mqttResClientId}"

    //val tmp = "${Config.loginTenantId}###${Config.loginTenantAccessKey}###0$connType"

    private object Config {
        const val deviceUuid = ""
        const val loginTenantId = ""
        const val loginTenantAccessKey = ""
        const val mqttResClientId = ""
        const val mqttResPwd = ""
        const val mqtt_public_key = ""
    }

    override fun getSubsResListener(): IMQTTSubsResListener {
        return object : IMQTTSubsResListener {
            override fun onTopicPush(topic: String, payload: String, message: MqttMessage) {
//                when {
//                    topic.contains(MQTTCmd.header_register) -> {
//                        if (_isRegister) return
//                        _isRegister = true
//
//                        Log.d(TAG, "_mqttCallback: messageArrived: header_register")
//
//                        val json = UtilKEncryptDES.decrypt(payload, Config.loginTenantAccessKey)
//                        Log.d(TAG, "_mqttCallback messageArrived: $json")
//
//                        val mqttBaseBean = json.fromJson<MQTTBaseBean>()
//                        if (mqttBaseBean != null && mqttBaseBean.code == "0000") {
//                            val dataBean = mqttBaseBean.data
//                            dataBean?.let {
//                                Config.mqttResPwd = it.accessKey
//                                Config.mqttResClientId = it.deviceId
//                                startConnectMqtt(MQTTCmd.conn_type_login.also { s -> _connType = s })
//                            }
//                        }
//                    }
//                    topic.contains(MQTTCmd.header_login) -> {
//                        if (_isLogin) return
//                        _isLogin = true
//
//                        Log.d(TAG, "_mqttCallback: messageArrived: header_login")
//
//                        val json = UtilKEncryptDES.decrypt(payload, MQTTCmd.mqtt_public_key)
//                        val mqttBaseBean = json.fromJson<MQTTBaseBean>()
//                        if (mqttBaseBean != null && mqttBaseBean.code == "0000") {
//                            //执行登录成功逻辑
//                            _mqttDataListener?.onGetData(UtilKJson.t2Json(MQTTCommBean(true, "服务连接成功")))
//                        }
//                    }
//                    topic.contains(MQTTCmd.header_policy_change) -> {
//                        Log.d(TAG, "_mqttCallback: messageArrived: header_policy_change")
//
//                        _owner.lifecycleScope.launch(Dispatchers.IO) {
//                            SplashRepository.saveRemoteChangeData(SplashRepository.AChangeData.rule_time)
//                        }
//                    }
//                    topic.contains(MQTTCmd.header_data_issue) -> {
//                        Log.d(TAG, "_mqttCallback: messageArrived: header_data_issue")
//
//                        _owner.lifecycleScope.launch(Dispatchers.IO) {
//                            SplashRepository.saveRemoteChangeData(SplashRepository.AChangeData.tower_device_issue_res)
//                        }
//                    }
//                    topic.contains(MQTTCmd.header_voice) -> {
//                        Log.d(TAG, "_mqttCallback: messageArrived: header_voice")
//
//                        _owner.lifecycleScope.launch(Dispatchers.IO) {
//                            val resString = UtilKEncryptDES.decrypt(payload, Config.deviceKey)
//                            Log.d(TAG, "_mqttCallback messageArrived: header_voice resString: $resString")
//                            //a40df7927201499fb8a08ff6972dc753###18###/voice/20221103/0f3f5b5dd75e40e8baa1275ac2b523e2.wav --->for example: /voice/20221103/0f3f5b5dd75e40e8baa1275ac2b523e2.wav
//                            val stringArray = resString.split("###")
//                            val url = stringArray.last()
//                            if (url.endsWith("wav")) {
//                                _voiceManager.startPlay((Config.audioBaseUrl + url).also {
//                                    Log.d(TAG, "messageArrived: header_voice audioUrl $it")
//                                })
//                            }
//                        }
//                    }
//                }
            }
        }
    }

    override fun getGenConnBeanListener(): IMQTTGenConnBeanListener {
        return object : IMQTTGenConnBeanListener {
            override fun onGetBaseUrl(): String {
                return ""
            }

            override fun onGetUseName(connType: Int): String {
                return ""//UtilKEncryptDES.encrypt(tmp, MQTTCmd.mqtt_public_key)
            }

            override fun onGetPassword(connType: Int): String {
                return if (connType == AConnType.REGISTER) Config.mqtt_public_key else PostKCryptoDES.with(MCryptoDESConfig(secretKey = Config.loginTenantAccessKey)).encryptWithBase64(Config.mqttResPwd)
            }

            override fun onGetClientId(connType: Int): String {
                return if (connType == AConnType.REGISTER) Config.deviceUuid else Config.mqttResClientId
            }

            override fun onGetTopics(connType: Int): List<String> {
                return if (connType == AConnType.REGISTER) listOf(_topicRegisterUrl) else listOf(_topicLoginUrl, _topicPolicyChangeUrl, _topicDataIssueUrl, _topicVoiceUrl)
            }
        }
    }
}