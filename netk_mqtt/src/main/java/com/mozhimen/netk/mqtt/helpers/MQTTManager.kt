package com.mozhimen.netk.mqtt.helpers

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.basick.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.basick.lintk.optins.OApiInit_ByLazy
import com.mozhimen.basick.lintk.optins.permission.OPermission_ACCESS_NETWORK_STATE
import com.mozhimen.basick.taskk.temps.TaskKPollInfinite
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.basick.utilk.wrapper.UtilKNet
import com.mozhimen.netk.mqtt.annors.AConnType
import com.mozhimen.netk.mqtt.commons.IMQTTDataListener
import com.mozhimen.netk.mqtt.commons.IMQTTGenConnBeanListener
import com.mozhimen.netk.mqtt.commons.IMQTTListener
import com.mozhimen.netk.mqtt.commons.IMQTTSubsResListener
import com.mozhimen.netk.mqtt.commons.MQTTSubsCallback
import com.mozhimen.netk.mqtt.mos.MQTTCommBean
import com.mozhimen.netk.mqtt.mos.MQTTConnBean
import com.mozhimen.serialk.moshi.UtilKMoshiWrapper
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTManager(
    private val _context: Context, private val _owner: LifecycleOwner,
    private val _mqttDataListener: IMQTTDataListener,
    private val _mqttSubsResListener: IMQTTSubsResListener,
    private val _mqttGenConnBeanListener: IMQTTGenConnBeanListener,
) : IMQTTListener, IUtilK {

    private var _connType: Int
        get() = MQTTSP.mqttConnType
        set(value) {
            MQTTSP.mqttConnType = value
        }
    private var _isRegister = false
    private var _isLogin = false

    private var _mqttConnBean: MQTTConnBean? = null
    private var _mqttClient: MqttAndroidClient? = null
    private var _mqttOptions: MqttConnectOptions? = null
        get() {
            if (field != null) return field
            val mqttOptions = MqttConnectOptions()
            mqttOptions.isCleanSession = true// 清除缓存
            mqttOptions.connectionTimeout = 10// 设置超时时间，单位：秒
            mqttOptions.keepAliveInterval = 20// 心跳包发送间隔，单位：秒
            return mqttOptions.also { field = it }
        }

    private val _iMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.i(TAG, "onSuccess: connect")

            if (_mqttClient == null) {
                UtilKLogWrapper.w(TAG, "_iMqttActionListener _mqttClient为空~")
                return
            }
            if (_mqttConnBean == null) {
                UtilKLogWrapper.w(TAG, "_iMqttActionListener _mqttConnBean为空~")
                return
            }

            try {
                for (topicUrl in _mqttConnBean!!.topicUrls) {
                    // 订阅topic话题
                    _mqttClient!!.subscribe(topicUrl, 1, null, MQTTSubsCallback(topicUrl))
                }
            } catch (e: MqttException) {
                e.printStackTrace()
                UtilKLogWrapper.e(TAG, "_iMqttActionListener MqttException: ${e.message ?: ""}")
            }
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            exception?.printStackTrace()
            UtilKLogWrapper.e(TAG, "_iMqttActionListener: connType $_connType onFailure: ${exception?.message ?: ""}")
            if (_connType == AConnType.REGISTER) {
                _mqttDataListener.onGetData(UtilKMoshiWrapper.t2strJson_ofMoshi(MQTTCommBean(false, "MQTT连接失败,${exception?.message ?: "请检查网络"}")))//"MQTT连接失败,请检查网络"
            } else {
                _mqttDataListener.onGetData(UtilKMoshiWrapper.t2strJson_ofMoshi(MQTTCommBean(false, "登录失败,请在后台配置设备信息")))
            }
            startConnectTask()
        }
    }

    private val _mqttCallback: MqttCallback = object : MqttCallback {
        override fun connectionLost(cause: Throwable?) {
            cause?.printStackTrace()
            UtilKLogWrapper.e(TAG, "_mqttCallback: connectionLost")
            _mqttDataListener.onGetData(UtilKMoshiWrapper.t2strJson_ofMoshi(false, "后台服务失去连接,请检查网络"))//失去连接 //onCallback("${MQTTCmd.pre_fail}###后台服务失去连接,请检查网络")//失去连接
            startConnectTask()
        }

        override fun messageArrived(topic: String, message: MqttMessage) {
            try {
                val payload = String(message.payload)
                Log.d(TAG, "_mqttCallback: messageArrived: topic: $topic, message: $payload")
                stopConnectTask()
                _mqttSubsResListener.onTopicPush(topic, payload, message)
            } catch (e: Exception) {
                e.printStackTrace()
                UtilKLogWrapper.e(TAG, "_mqttCallback: messageArrived: throw ${e.message ?: ""}")
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            Log.d(TAG, "_mqttCallback: deliveryComplete")
        }
    }//MQTT监听并且接受消息
    @OptIn(OApiCall_BindLifecycle::class, OApiInit_ByLazy::class)
    private var _connectTask: TaskKPollInfinite? = null
        get() {
            if (field != null) return field
            val connectTask = TaskKPollInfinite().apply { bindLifecycle(_owner) }
            return connectTask.also { field = connectTask }
        }

    private fun genMqttConnBean(connType: Int) {
        try {
            val userName = _mqttGenConnBeanListener.onGetUseName(connType)
            val password = _mqttGenConnBeanListener.onGetUseName(connType)
            val clientId = _mqttGenConnBeanListener.onGetClientId(connType)
            val topics = _mqttGenConnBeanListener.onGetTopics(connType)
            _mqttConnBean = MQTTConnBean(userName, password, clientId, topics)
            Log.d(TAG, "genMqttConnBean: _mqttConnBean $_mqttConnBean")
        } catch (e: Exception) {
            e.printStackTrace()
            UtilKLogWrapper.e(TAG, "pkgMqttConnBean Exception ${e.message ?: ""}")
        }
    }

    private fun startConnectMqtt(connType: Int) {
        disconnectMqtt()
        genMqttConnBean(connType)

        _mqttClient = MqttAndroidClient(_context, _mqttGenConnBeanListener.onGetBaseUrl(), _mqttConnBean!!.clientId)// 设置MQTT监听并且接受消息
        _mqttClient!!.setCallback(_mqttCallback)
        _mqttOptions!!.userName = _mqttConnBean!!.userName        // 用户名
        _mqttOptions!!.password = _mqttConnBean!!.password.toCharArray()// 密码 将字符串转换为字符串数组
        connectMqtt(connType)
    }

    @OptIn(OPermission_ACCESS_NETWORK_STATE::class)
    private fun connectMqtt(connType: Int) {
        try {
            if (!UtilKNet.hasConnected()) {
                _mqttDataListener.onGetData(UtilKMoshiWrapper.t2strJson_ofMoshi(MQTTCommBean(false, "亲,网络丢失了")))//断网情况下
                UtilKLogWrapper.w(TAG, "connectMqtt: connType $connType 断网~")
                return
            }
            if (_mqttClient == null) {
                UtilKLogWrapper.w(TAG, "connectMqtt: connType $connType _mqttClient为空~")
                return
            }
            if (_mqttClient!!.isConnected) {
                UtilKLogWrapper.w(TAG, "connectMqtt: connType $connType _mqttClient已连接~")
                return
            }
            _mqttClient!!.connect(_mqttOptions, null, _iMqttActionListener)
        } catch (e: Exception) {
            e.printStackTrace()
            UtilKLogWrapper.e(TAG, "mqttDoConnection connType $connType MqttException ${e.message ?: ""}")
        }
    }

    private fun disconnectMqtt() {
        try {
            _mqttClient?.let {
                if (_mqttClient!!.isConnected) {
                    _mqttClient!!.unregisterResources()
                    _mqttClient!!.disconnect()
                }
                _mqttClient!!.close()
                _mqttClient = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(OApiCall_BindLifecycle::class, OApiInit_ByLazy::class)
    private fun startConnectTask() {
        if (_connectTask!!.isActive()) return
        _connectTask!!.start(30000) {
            connectMqtt(_connType)
        }
    }

    @OptIn(OApiCall_BindLifecycle::class, OApiInit_ByLazy::class)
    private fun stopConnectTask() {
        if (_connectTask != null) {
            if (_connectTask!!.isActive()) {
                _connectTask!!.cancel()
                _connectTask = null
            }
        }
    }

    override fun start() {
        startConnectMqtt(_connType)
    }

    override fun stop() {
        stopConnectTask()
        disconnectMqtt()
        _isLogin = false
        _isRegister = false
        _connType = AConnType.REGISTER
    }
}