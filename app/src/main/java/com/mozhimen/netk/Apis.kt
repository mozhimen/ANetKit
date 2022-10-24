package com.mozhimen.netk

import com.mozhimen.netk.mos.Weather
import com.mozhimen.netk.annors._Path
import com.mozhimen.netk.annors.methods._GET
import com.mozhimen.netk.commons.INetKCall
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @ClassName TestApi
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2021/12/13 22:19
 * @Version 1.0
 */
interface Apis {
    //121.321504,31.194874
    @_GET("{loc}/realtime.json")
    fun getRealTimeWeatherAsync(@_Path("loc") loc: String): INetKCall<Weather>

    //121.321504,31.194874
    @GET("{loc}/realtime.json")
    fun getRealTimeWeatherRxJava(@Path("loc") loc: String): Observable<Weather>
}