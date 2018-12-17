package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.material.tabs.TabLayout
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.utils.zipLater
import org.schulcloud.mobile.views.LiveViewPager


abstract class TabbedMainFragment<F : MainFragment<F, VM>, VM : ViewModel> : MainFragment<F, VM>() {
    companion object {
        val TAG = TabbedMainFragment::class.simpleName
    }

    abstract val pagerAdapter: MainPagerAdapter<F, VM>
    lateinit var pager: LiveViewPager
        private set
    var tabLayout: TabLayout? = null
        private set

    final override val currentInnerFragment: LiveData<Int?>
    private val currentPositionAddFunc: (LiveData<Int>) -> Unit
    override val innerFragments
        get() = pagerAdapter.fragments

    init {
        val (position, addFunc) = zipLater<Int>()
        currentInnerFragment = position.map { it }
        currentPositionAddFunc = addFunc
    }


    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager = view.findViewById(R.id.viewPager)
        // Subscribe currentInnerFragment to pager position
        currentPositionAddFunc(pager.currentItemLiveData)
        pager.adapter = pagerAdapter

        tabLayout = view.findViewById(R.id.tabLayout)
        tabLayout?.setupWithViewPager(pager)

        mainViewModel.toolbarColors.observe(this, Observer {
            tabLayout?.setTabTextColors(it.textColorSecondary, it.textColor)
            tabLayout?.setSelectedTabIndicatorColor(it.textColor)
        })
    }
}
