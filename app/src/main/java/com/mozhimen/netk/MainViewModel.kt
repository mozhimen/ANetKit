package com.mozhimen.netk

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mozhimen.basick.elemk.androidx.lifecycle.bases.BaseViewModel
import com.mozhimen.netk.commons.INetKListener
import com.mozhimen.netk.customs.RxJavaResponse
import com.mozhimen.netk.mos.NetKResponse
import com.mozhimen.netk.mos.Weather
import io.reactivex.schedulers.Schedulers

/**
 * @ClassName MainViewModel
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/10/25 0:29
 * @Version 1.0
 */
class MainViewModel : BaseViewModel() {
    val uiWeather1 = MutableLiveData<String>()
    private var _lastTime1 = System.currentTimeMillis()
    fun getRealtimeWeatherAsync() {
        _lastTime1 = System.currentTimeMillis()
        ApiFactorys.createAsync(Apis::class.java).getRealTimeWeatherAsync("121.321504,31.194874").enqueue(object : INetKListener<Weather> {
            override fun onSuccess(response: NetKResponse<Weather>) {
                val duration = System.currentTimeMillis() - _lastTime1
                Log.i(TAG, "getRealtimeWeatherAsync onSuccess duration: $duration")
                uiWeather1.postValue(response.data?.result?.realtime?.temperature.toString() + " " + duration)
            }

            override fun onFail(throwable: Throwable) {
                Log.e(TAG, "getRealtimeWeatherAsync onFail ${throwable.message}")
            }
        })
    }

    val uiWeather3 = MutableLiveData<String>()
    private var _lastTime3 = System.currentTimeMillis()
    fun getRealTimeWeatherRxJava() {
        _lastTime3 = System.currentTimeMillis()
        ApiFactorys.createRxJava(Apis::class.java).getRealTimeWeatherRxJava("121.321504,31.194874").subscribeOn(Schedulers.io()).subscribe(
            object : RxJavaResponse<Weather>() {
                override fun onSuccess(response: NetKResponse<Weather>) {
                    val duration = System.currentTimeMillis() - _lastTime3
                    Log.i(TAG, "getRealTimeWeatherRxJava onSuccess duration $duration")
                    uiWeather3.postValue(response.data?.result?.realtime?.temperature.toString() + " " + duration)
                }

                override fun onFailed(code: Int, message: String?) {
                    Log.e(TAG, "getRealTimeWeatherRxJava onFail ${message ?: "msg loss"}")
                }
            }
        )
    }
}