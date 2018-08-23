package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.MainViewModel
import kotlin.properties.Delegates

abstract class MainFragment : BaseFragment() {
    protected val mainActivity: MainActivity get() = activity as MainActivity
    protected val mainViewModel: MainViewModel
        get() = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

    protected val navController: NavController
        get() = findNavController(this)
    protected lateinit var config: LiveData<MainFragmentConfig>
        private set

    protected abstract fun provideConfig(): LiveData<MainFragmentConfig>

    open var url: String? = null
    private var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
        new?.isRefreshing = isRefreshing
    }
    protected var isRefreshing: Boolean by Delegates.observable(false) { _, _, new ->
        swipeRefreshLayout?.isRefreshing = new
    }
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.onOptionsItemSelected.observe(this, Observer {
            onOptionsItemSelected(it)
        })
        mainViewModel.onFabClicked.observe(this, Observer { onFabClicked() })

        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        performRefresh()
    }

    override fun onResume() {
        super.onResume()

        config = provideConfig()
        config.observe(this, Observer {
            activity?.invalidateOptionsMenu()
            it?.also { mainViewModel.config.value = it }
        })

        swipeRefreshLayout = view?.findViewById(R.id.swipeRefresh)

        view?.findViewById<Toolbar>(R.id.toolbar).also {
            mainActivity.setSupportActionBar(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val menuTopRes = config.value?.menuTopRes
        if (menuTopRes != null && menuTopRes != 0)
            inflater?.inflate(menuTopRes, menu)

        inflater?.inflate(R.menu.fragment_main_top, menu)
        if (config.value?.supportsRefresh == false)
            menu?.findItem(R.id.base_action_refresh)?.isVisible = false
        for (id in config.value?.menuTopHiddenIds.orEmpty())
            if (id != 0)
                menu?.findItem(id)?.isVisible = false

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.base_action_share -> {
                var link = url ?: return true
                if (link.startsWith('/'))
                    link = combinePath(HOST, link)
                context?.shareLink(link, mainViewModel.config.value?.title)
            }
            R.id.base_action_refresh -> performRefresh()
        // TODO: Remove when deep linking is readded
            R.id.base_action_openInBrowser -> context?.openUrl(url.asUri())
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }


    protected abstract suspend fun refresh()
    protected fun performRefresh() {
        isRefreshing = true
        launch {
            withContext(UI) { refresh() }
            withContext(UI) { isRefreshing = false }
        }
    }

    open fun onFabClicked() {}
}

data class MainFragmentConfig(
    val fragmentType: FragmentType = FragmentType.SECONDARY,

    val title: String?,
    val showTitle: Boolean = true,
    val subtitle: String? = null,
    @ColorInt
    val toolbarColor: Int? = null,

    @MenuRes
    val menuTopRes: Int = 0,
    val menuTopHiddenIds: List<Int> = emptyList(),
    @MenuRes
    val menuBottomRes: Int = 0,
    val menuBottomHiddenIds: List<Int> = emptyList(),
    val supportsRefresh: Boolean = true,

    val fabVisible: Boolean = true,
    @DrawableRes
    val fabIconRes: Int = 0
)

enum class FragmentType {
    PRIMARY,
    SECONDARY
}
