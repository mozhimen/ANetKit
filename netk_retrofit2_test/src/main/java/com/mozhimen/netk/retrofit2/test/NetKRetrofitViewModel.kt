package com.mozhimen.netk.retrofit2.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mozhimen.basick.elemk.androidx.lifecycle.bases.BaseViewModel
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.wrapper.UtilKRes
import com.mozhimen.netk.retrofit2.test.customs.ApiFactory
import com.mozhimen.netk.retrofit2.helpers.NetKHelper
import com.mozhimen.netk.retrofit2.helpers.asNetKRes
import com.mozhimen.netk.retrofit2.helpers.asNetKResSync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Response
import kotlin.coroutines.resume

/**
 * @ClassName NetKViewModel
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/5/11 23:44
 * @Version 1.0
 */
class NetKRetrofitViewModel : BaseViewModel() {

    val uiWeather1 = MutableLiveData("")
    val uiWeather2 = MutableLiveData("")
    val uiWeather3 = MutableLiveData("")
    val uiWeather4 = MutableLiveData("")

    fun getRealtimeWeatherCoroutine() {
        val time = System.currentTimeMillis()
        viewModelScope.launch(Dispatchers.IO) {
            NetKHelper.createFlow { ApiFactory.apis.get_ofCoroutine() }.asNetKRes(
                onSuccess = { data ->
                    uiWeather1.postValue(data.id.toString() + " ${System.currentTimeMillis() - time}")
                }, onFail = { code, msg ->
                    uiWeather1.postValue("$code $msg ${System.currentTimeMillis() - time}")
                })
        }
    }

    suspend fun getRealtimeWeatherCoroutine1(): String = suspendCancellableCoroutine { coroutine ->
        viewModelScope.launch(Dispatchers.IO) {
            val time = System.currentTimeMillis()
            NetKHelper.createFlow { ApiFactory.apis.get_ofCoroutine() }.asNetKRes(
                onSuccess = { data ->
                    coroutine.resume(data.id.toString() + " ${System.currentTimeMillis() - time}")
                }, onFail = { code, msg ->
                    coroutine.resume("$code $msg ${System.currentTimeMillis() - time}")
                })
        }
    }

    fun getRealtimeWeatherCoroutineSync() {
        viewModelScope.launch(Dispatchers.IO) {
            val time = System.currentTimeMillis()
            val res = NetKHelper.createFlow { ApiFactory.apis.get_ofCoroutine() }.asNetKResSync()
            if (res.bean != null) {
                uiWeather2.postValue(res.bean!!.id.toString() + " ${System.currentTimeMillis() - time}")
            } else {
                uiWeather2.postValue("${res.code} ${res.msg} ${System.currentTimeMillis() - time}")
            }
        }
    }

    fun getRealtimeWeatherRetrofitCache() {
        viewModelScope.launch(Dispatchers.IO) {
//            val time= System.currentTimeMillis()
//            val res = NetKHelper.createFlow { ApiFactory.apisCache.get_ofCache() }.asNetKResSync()
//            if (res.bean?.body() != null) {
//                uiWeather3.postValue(res.bean!!.body()!!.id.toString() + " ${System.currentTimeMillis() - time}")
//            } else {
//                uiWeather3.postValue("${res.code} ${res.msg} ${System.currentTimeMillis() - time}")
//            }
            ApiFactory.apisRetrofitCache.get_ofRetrofitCache().let { response ->
                UtilKLogWrapper.d(TAG, "getRealtimeWeatherRetrofitCache: response $response")
                val currentResponse: Response? = response.raw()
                val cacheResponse: Response? = currentResponse?.cacheResponse
                val isFromCache = currentResponse?.receivedResponseAtMillis == cacheResponse?.receivedResponseAtMillis
                val validityMillis = currentResponse?.receivedResponseAtMillis ?: (0 + 1 - System.currentTimeMillis())
                uiWeather3.postValue(
                    response.body().toString() + UtilKRes.gainString(
                        if (isFromCache) {
                            R.string.main_cachehit_description
                        } else {
                            R.string.main_cachemiss_description
                        }
                    ).format(validityMillis)
                )
            }
        }
    }

    fun getRealtimeWeatherOkhttp3Cache() {
        viewModelScope.launch(Dispatchers.IO) {
            val time= System.currentTimeMillis()
            val res = NetKHelper.createFlow { ApiFactory.apisOkHttp3Cache.get_ofOkhttp3Cache("AleynText", "Aleyn123") }.asNetKResSync()
            if (res.bean != null) {
                uiWeather4.postValue(res.bean!!.data.toString() + " ${System.currentTimeMillis() - time}")
            } else {
                uiWeather4.postValue("${res.code} ${res.msg} ${System.currentTimeMillis() - time}")
            }
//            ApiFactory.apisOkHttp3Cache.get_ofOkhttp3Cache().let { response ->
//                UtilKLogWrapper.d(TAG, "getRealtimeWeatherOkhttp3Cache: response $response")
//                val currentResponse: Response? = response.raw()
//                val cacheResponse: Response? = currentResponse?.cacheResponse
//                val isFromCache = currentResponse?.receivedResponseAtMillis == cacheResponse?.receivedResponseAtMillis
//                val validityMillis = currentResponse?.receivedResponseAtMillis ?: (0 + 1 - System.currentTimeMillis())
//                uiWeather4.postValue(
//                    response.body()?.title + UtilKRes.gainString(
//                        if (isFromCache) {
//                            R.string.main_cachehit_description
//                        } else {
//                            R.string.main_cachemiss_description
//                        }
//                    ).format(validityMillis)
//                )
//            }
        }
    }
}