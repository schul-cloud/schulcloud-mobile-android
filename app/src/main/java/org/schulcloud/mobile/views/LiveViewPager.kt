package org.schulcloud.mobile.views

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import org.schulcloud.mobile.utils.mutableLiveDataOf

class LiveViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    companion object {
        val TAG: String = LiveViewPager::class.java.simpleName

        private const val POS_OFFSET_THRESHOLD = 0.5f
    }

    private val _currentItemLiveData: MutableLiveData<Int> = mutableLiveDataOf(0)
    val currentItemLiveData: LiveData<Int>
        get() = _currentItemLiveData

    init {
        addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val pos = if (positionOffset > POS_OFFSET_THRESHOLD) position + 1 else position
                if (_currentItemLiveData.value != pos)
                    _currentItemLiveData.value = pos
            }

            override fun onPageSelected(position: Int) {}
        })
    }
}
