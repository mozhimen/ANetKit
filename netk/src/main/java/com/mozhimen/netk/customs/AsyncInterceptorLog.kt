package com.mozhimen.netk.customs

import android.util.Log
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.netk.annors.methods._METHOD
import com.mozhimen.netk.commons.INetKChain
import com.mozhimen.netk.commons.INetKInterceptor

/**
 * @ClassName MsgInterceptor
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/5/11 23:02
 * @Version 1.0
 */
class AsyncInterceptorLog : INetKInterceptor {

    override fun intercept(chain: INetKChain): Boolean {
        val request = chain.request()
        val response = chain.response()
        if (chain.isRequestPeriod) {
            UtilKLogWrapper.d(TAG, "intercept endPointUrl ${chain.request().endPointUrl()}")
        }
        if (!chain.isRequestPeriod && chain.response() != null) {
            val outputBuilder = StringBuilder()
            val httpMethod: String =
                when (request.httpMethod) {
                    _METHOD._GET -> "GET"
                    _METHOD._POST -> "POST"
                    _METHOD._PUT -> "PUT"
                    _METHOD._DELETE -> "DELETE"
                    else -> "UNKNOW"
                }
            val requestUrl: String = request.endPointUrl()
            outputBuilder.append("\nrequestUrl>>>>>>$requestUrl\n")
            outputBuilder.append("\nmethod>>>>>>$httpMethod\n")

            if (request.headers != null) {
                outputBuilder.append("headers>>>>>\n")
                request.headers!!.forEach(action = {
                    outputBuilder.append(it.key + ": " + it.value)
                    outputBuilder.append("\n")
                })
            }

            if (request.parameters != null && request.parameters!!.isNotEmpty()) {
                outputBuilder.append("parameters>>>>>\n")
                request.parameters!!.forEach(action = {
                    outputBuilder.append(it.key + ":" + it.value + "\n")
                })
            }

            outputBuilder.append("response>>>>>\n")
            outputBuilder.append(response?.rawData + "\n")

            UtilKLogWrapper.d(TAG, "intercept builder $outputBuilder")
        }
        return false
    }
}