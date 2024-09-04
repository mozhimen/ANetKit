package com.mozhimen.netk.retrofit2.cache.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mozhimen.kotlin.utilk.java.io.UtilKFileDir
import com.mozhimen.netk.retrofit2.cache.supportCache
import com.mozhimen.netk.retrofit2.cache.test.commons.RandomApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

//@HiltViewModel
class MainViewModel /*@Inject*/ constructor(/*private val api: RandomApi*/) : ViewModel() {
    private val _retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        Retrofit.Builder()
            .baseUrl("http://www.randomnumberapi.com/")
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(loggingInterceptor)
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .supportCache(Cache(directory = File(UtilKFileDir.Internal.getCache(), "retrofit"), 10 * 1024))
    }

    private val _channelState = Channel<ViewState>(capacity = Channel.Factory.CONFLATED).apply {
        viewModelScope.launch { send(ViewState.Initial) }
    }

    val state = _channelState.consumeAsFlow()

    suspend fun updateNumber() {
        withContext(Dispatchers.IO) {
            _retrofit.create(RandomApi::class.java).randomNumber().let { response ->
                val number = response.body()?.first()
                if (number != null) {
                    val currentResponse: Response? = response.raw()
                    val cachedResponse: Response? = currentResponse?.cacheResponse
                    _channelState.send(
                        ViewState.Update(
                            number,
                            currentResponse?.receivedResponseAtMillis == cachedResponse?.receivedResponseAtMillis,
                            currentResponse?.receivedResponseAtMillis ?: (0 + 1 - System.currentTimeMillis())
                        )
                    )
                } else {
                    _channelState.send(ViewState.Error(IllegalStateException("No number received!")))
                }
            }

        }
    }

    sealed class ViewState {
        object Initial : ViewState()
        data class Update constructor(val value: Int, val fromCache: Boolean, val validityMillis: Long) : ViewState()
        data class Error(val error: Throwable) : ViewState()
    }
}
