package com.mozhimen.netk.observer.test

import android.os.Bundle
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVB
import com.mozhimen.netk.observer.NetKObserver
import com.mozhimen.netk.observer.annors.ANetKObserver
import com.mozhimen.netk.observer.test.databinding.ActivityNetkObserverBinding

/**
 * @ClassName NetKObserverActivity
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/1/15 20:47
 * @Version 1.0
 */
class NetKObserverActivity : BaseActivityVB<ActivityNetkObserverBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        NetKObserver.instance.register(this)
    }

    @ANetKObserver
    fun onNetChange(net: String) {
        vb.netkObserverTxt.text = net
    }

    override fun onDestroy() {
        NetKObserver.instance.unRegister(this)
        super.onDestroy()
    }
}