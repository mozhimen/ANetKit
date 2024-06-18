package com.mozhimen.netk.mqtt

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.basick.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.basick.lintk.optins.OApiInit_ByLazy
import com.mozhimen.netk.mqtt.bases.BaseNetKMQTTService
import com.mozhimen.servicek.ServiceKProxy
import com.mozhimen.servicek.bases.BaseServiceResCallback


/**
 * @ClassName PrefabKMQTTServiceDelegate
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
@OApiInit_ByLazy
@OApiCall_BindLifecycle
class NetKMQTTServiceProxy<A, S>(
    activity: A,
    clazz: Class<S>,
    resListener: BaseServiceResCallback
) : ServiceKProxy<A>(activity, clazz, resListener) where A : AppCompatActivity, A : LifecycleOwner, S : BaseNetKMQTTService