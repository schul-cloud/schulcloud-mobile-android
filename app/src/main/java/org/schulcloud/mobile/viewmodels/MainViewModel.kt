package org.schulcloud.mobile.viewmodels

import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.utils.SingleLiveEvent
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class MainViewModel : BaseViewModel() {
    val config: MutableLiveData<MainFragmentConfig> = MutableLiveData()
    val title: MutableLiveData<String> = MutableLiveData()
    val toolbarColors: MutableLiveData<ToolbarColors> = MutableLiveData()
    val onOptionsItemSelected: SingleLiveEvent<MenuItem> = SingleLiveEvent()
    val onFabClicked: SingleLiveEvent<Void> = SingleLiveEvent()
}

data class ToolbarColors(
    @ColorInt val color: Int,
    @ColorInt val textColor: Int,
    @ColorInt val textColorSecondary: Int,
    @ColorInt val statusBarColor: Int
)
