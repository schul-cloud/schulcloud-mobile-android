package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.MainViewModel


abstract class MainFragment<VM : ViewModel>(refreshableImpl: RefreshableImpl = RefreshableImpl()) : BaseFragment(),
        Refreshable by refreshableImpl {

    protected val mainActivity: MainActivity get() = activity as MainActivity
    protected val mainViewModel: MainViewModel
        get() = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

    private var isFirstInit: Boolean = true

    protected val navController: NavController
        get() = findNavController(this)
    protected lateinit var config: LiveData<MainFragmentConfig>
        private set

    lateinit var viewModel: VM
        protected set

    protected abstract fun provideConfig(): LiveData<MainFragmentConfig>

    open var url: String? = null

    init {
        refreshableImpl.refresh = { refresh() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstInit = savedInstanceState == null

        mainViewModel.onOptionsItemSelected.observe(this, Observer {
            onOptionsItemSelected(it)
        })
        mainViewModel.onFabClicked.observe(this, Observer { onFabClicked() })

        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()

        config = provideConfig()
        config.observe(this, Observer {
            activity?.invalidateOptionsMenu()
            it?.also { mainViewModel.config.value = it }
        })

        swipeRefreshLayout = view?.findViewById(R.id.swipeRefresh)

        mainActivity.setSupportActionBar(view?.findViewById(R.id.toolbar))
        mainActivity.setToolbarWrapper(view?.findViewById(R.id.toolbarWrapper))

        if (isFirstInit)
            performRefresh()

        isFirstInit = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuTopRes = config.value?.menuTopRes
        if (menuTopRes != null && menuTopRes != 0)
            inflater.inflate(menuTopRes, menu)

        inflater.inflate(R.menu.fragment_main_top, menu)
        if (config.value?.supportsRefresh == false)
            menu.findItem(R.id.base_action_refresh)?.isVisible = false
        for (id in config.value?.menuTopHiddenIds.orEmpty())
            if (id != 0)
                menu.findItem(id)?.isVisible = false

        mainActivity.updateToolbarColors()

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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


    abstract suspend fun refresh()

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


interface ParentFragment {
    suspend fun refreshWithChild(fromChild: Boolean)
}
