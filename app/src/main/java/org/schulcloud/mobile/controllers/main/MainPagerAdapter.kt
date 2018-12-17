package org.schulcloud.mobile.controllers.main

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import org.schulcloud.mobile.utils.mutableLiveDataOf
import kotlin.properties.Delegates


sealed class MainPagerAdapter<P : MainFragment<P, VM>, VM : ViewModel>(fragment: P) :
        FragmentPagerAdapter(fragment.childFragmentManager) {
    open var parent: P? = fragment

    abstract val tabs: List<Tab<*, P, VM>>

    private val fragmentsToAdd: MutableList<Pair<Int, InnerMainFragment<*, P, VM>>> = mutableListOf()

    private val _fragments: MutableLiveData<List<InnerMainFragment<*, P, VM>?>> = mutableLiveDataOf(emptyList())
    val fragments: LiveData<List<InnerMainFragment<*, P, VM>?>>
        get() = _fragments


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        @Suppress("UNCHECKED_CAST")
        val childFragment = super.instantiateItem(container, position) as InnerMainFragment<*, P, VM>
        fragmentsToAdd += position to childFragment
        return childFragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        super.destroyItem(container, position, obj)

        _fragments.value = (_fragments.value?.toMutableList() ?: ArrayList(count))
                .apply { this[position] = null }
    }

    override fun finishUpdate(container: ViewGroup) {
        super.finishUpdate(container)

        for ((pos, fragment) in fragmentsToAdd) {
            _fragments.value = (_fragments.value?.toMutableList() ?: ArrayList(count))
                    .apply {
                        while (size < pos + 1) add(null)
                        this[pos] = fragment
                    }

            @Suppress("UNCHECKED_CAST")
            val newParent = fragment.getParentFragment() as? P
            if (parent != newParent) {
                parent = newParent
                onParentFragmentChanged()
            }
        }
        fragmentsToAdd.clear()
    }

    open fun onParentFragmentChanged() {}
}

class SimplePagerAdapter<P : MainFragment<P, VM>, VM : ViewModel>(
    fragment: P,
    override val tabs: List<Tab<*, P, VM>>
) : MainPagerAdapter<P, VM>(fragment) {

    override fun getItem(position: Int): Fragment? {
        return if (position in tabs.indices) tabs[position].fragment() else null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position in tabs.indices) tabs[position].title else null
    }

    override fun getCount() = tabs.size
}

fun <P : MainFragment<P, VM>, VM : ViewModel> List<Tab<*, P, VM>>.toPagerAdapter(fragment: P): MainPagerAdapter<P, VM> {
    return SimplePagerAdapter(fragment, this)
}

class LiveDataPagerAdapter<P : MainFragment<P, VM>, VM : ViewModel>(
    fragment: P,
    private val tabsLiveData: LiveData<List<Tab<*, P, VM>>>
) : MainPagerAdapter<P, VM>(fragment) {
    override var parent by Delegates.observable<P?>(null) { _, _, new ->
        new ?: return@observable
        tabsLiveData.observe(new, Observer {
            tabs = it ?: emptyList()
            notifyDataSetChanged()
        })
    }

    override var tabs: List<Tab<*, P, VM>> = emptyList()

    init {
        parent = fragment
    }


    override fun getItem(position: Int): Fragment? {
        return if (position in tabs.indices) tabs[position].fragment() else null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position in tabs.indices) tabs[position].title else null
    }

    override fun getCount() = tabs.size
}

fun <P : MainFragment<P, VM>, VM : ViewModel> LiveData<List<Tab<*, P, VM>>>.toPagerAdapter(fragment: P): MainPagerAdapter<P, VM> {
    return LiveDataPagerAdapter(fragment, this)
}


data class Tab<F : InnerMainFragment<F, P, VM>, P : MainFragment<P, VM>, VM : ViewModel>(
    val title: String,
    val fragment: () -> F
)
