package com.mozhimen.netk.retrofit2.cache.test

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.mozhimen.bindk.bases.activity.viewbinding.BaseActivityVBVM
import com.mozhimen.netk.retrofit2.cache.test.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

//@AndroidEntryPoint
class MainActivity : BaseActivityVBVM<ActivityMainBinding, MainViewModel>() {

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        vm.state.flowWithLifecycle(this.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    is MainViewModel.ViewState.Initial -> {
                        vb.textNumber.text = ""
                        vb.textWhereFrom.text = getText(R.string.main_default_description)
                    }

                    is MainViewModel.ViewState.Update -> {
                        vb.refresh.isRefreshing = false
                        vb.textNumber.text = "${it.value}"
                        vb.textWhereFrom.text = getText(
                            if (it.fromCache) {
                                R.string.main_cachehit_description
                            } else {
                                R.string.main_cachemiss_description
                            }
                        ).toString().format(it.validityMillis)

                    }

                    is MainViewModel.ViewState.Error -> {
                        vb.refresh.isRefreshing = false
                        vb.textNumber.text = "-1"
                        vb.textWhereFrom.text = "${it.error.message}"
                    }
                }
            }
            .launchIn(lifecycleScope)

        vb.refresh.setOnRefreshListener {
            lifecycleScope.launch { vm.updateNumber() }
        }
    }
}
