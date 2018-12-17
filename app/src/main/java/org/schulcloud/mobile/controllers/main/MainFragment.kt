package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.util.set
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.MainViewModel


abstract class MainFragment<F : MainFragment<F, VM>, VM : ViewModel>(
    protected val refreshableImpl: RefreshableImpl = RefreshableImpl()
) :
        BaseFragment(),
        Refreshable by refreshableImpl {

    protected val mainActivity: MainActivity get() = activity as MainActivity
    protected val mainViewModel: MainViewModel
        get() = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

    protected open val isInnerFragment: Boolean = false
    protected open val currentInnerFragment: LiveData<Int?> = liveDataOf()
    protected open val innerFragments: LiveData<List<InnerMainFragment<*, F, VM>?>> = liveDataOf(emptyList())
    private val innerFragmentsRefreshed = SparseBooleanArray()
    private var isFirstInit: Boolean = true

    val isInitialized: Boolean get() = !isFirstInit

    protected val navController: NavController
        get() = findNavController(this)
    lateinit var config: LiveData<MainFragmentConfig>
        private set

    lateinit var viewModel: VM
        protected set


    open var url: String? = null

    init {
        refreshableImpl.refresh = { performRefresh() }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.onOptionsItemSelected.observe(this, Observer {
            onOptionsItemSelected(it)
        })
        mainViewModel.onFabClicked.observe(this, Observer { onFabClicked() })

        config = provideConfig()
        setHasOptionsMenu(true)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()

        // Config already provided in onCreate
        if (!isFirstInit)
            config = provideConfig()
        swipeRefreshLayout = view?.findViewById(R.id.swipeRefresh)

        if (!isInnerFragment) {
            config.observe(this, Observer { config ->
                activity?.invalidateOptionsMenu()
                config?.also { mainViewModel.config.value = config }
            })

            mainActivity.setSupportActionBar(view?.findViewById(R.id.toolbar))
            mainActivity.setToolbarWrapper(view?.findViewById(R.id.toolbarWrapper))

            if (isFirstInit)
                performRefresh()
        }

        isFirstInit = false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (!isInnerFragment) {
            config.value?.menuTopRes?.also {
                for (menuRes in it.filterNotNull())
                    inflater?.inflate(menuRes, menu)
            }

            inflater?.inflate(R.menu.fragment_main_top, menu)
            if (config.value?.supportsRefresh == false)
                menu?.findItem(R.id.base_action_refresh)?.isVisible = false

            config.value?.menuTopHiddenIds?.also {
                for (id in it.filterNotNull())
                    menu?.findItem(id)?.isVisible = false
            }
        }

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
            else -> {
                val fragments = innerFragments.value.orEmpty().toMutableList()
                // Currently visible parent takes precedence
                currentInnerFragment.value?.also { fragments.move(it, 0) }
                for (innerFragment in fragments)
                    if (innerFragment?.onOptionsItemSelected(item) == true)
                        return true
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }


    private fun provideConfig(): LiveData<MainFragmentConfig> {
        var lastFragment: InnerMainFragment<*, F, VM>? = null
        var pos = 0

        val (result, addFunc) = switch<MainFragmentConfig?>()
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (currentInnerFragment.value ?: 0 != pos)
                    return

                @Suppress("UNCHECKED_CAST")
                addFunc(lastFragment?.config as? LiveData<MainFragmentConfig?> ?: liveDataOf())
            }
        }

        return provideConfig(currentInnerFragment
                .map { it ?: 0 }
                .combineLatest(innerFragments)
                .switchMapNullable { (position, fragments) ->
                    pos = position

                    val innerFragment = fragments.getOrNull(pos)
                    if (innerFragment != null) {
                        if (!innerFragmentsRefreshed[pos]) {
                            innerFragment.performRefreshWithParent(true)
                            innerFragmentsRefreshed[pos] = true
                        }

                        // Remove observer from old fragment
                        lastFragment?.let { lifecycle.removeObserver(observer) }

                        // Add to new fragment
                        lastFragment = innerFragment
                        // workaround as fragments are not fully initialized when tab is switched
                        innerFragment.getLifecycle().removeObserver(observer)
                        innerFragment.getLifecycle().addObserver(observer)
                        result
                    } else
                        null
                })
    }

    protected open fun provideConfig(selectedTabConfig: LiveData<MainFragmentConfig?>)
            : LiveData<MainFragmentConfig> {
        return provideSelfConfig()
                .combineLatestNullable(selectedTabConfig)
                .map { (self, tab) ->
                    MainFragmentConfig(
                            self.fragmentType,
                            currentInnerFragment.value,
                            tab?.title ?: self.title,
                            tab?.showTitle ?: self.showTitle,
                            tab?.subtitle ?: self.subtitle,
                            tab?.toolbarColor ?: self.toolbarColor,
                            (tab?.menuTopRes ?: emptyList()).union(self.menuTopRes),
                            (tab?.menuTopHiddenIds ?: emptyList()).union(self.menuTopHiddenIds),
                            (tab?.menuBottomRes ?: emptyList()).union(self.menuBottomRes),
                            (tab?.menuBottomHiddenIds ?: emptyList()).union(self.menuBottomHiddenIds),
                            tab?.supportsRefresh ?: self.supportsRefresh,
                            tab?.fabVisible ?: self.fabVisible,
                            tab?.fabIconRes ?: self.fabIconRes
                    )
                }
    }

    protected abstract fun provideSelfConfig(): LiveData<MainFragmentConfig>


    override fun performRefresh() = performRefreshWithChild(false)
    fun performRefreshWithChild(fromInner: Boolean) {
        refreshableImpl.isRefreshing = true
        launch {
            withContext(UI) {
                val innerFragment = currentInnerFragment.value?.let {
                    innerFragments.value?.getOrNull(it)
                }
                if (fromInner || innerFragment == null)
                    refresh()
                else
                    innerFragment.performRefreshWithParent(true)
            }
            withContext(UI) { refreshableImpl.isRefreshing = false }
        }
    }

    abstract suspend fun refresh()

    open fun onFabClicked() {}
}

data class MainFragmentConfig(
    val fragmentType: FragmentType = FragmentType.SECONDARY,
    val innerFragmentIndex: Int? = null,

    val title: String? = null,
    val showTitle: Boolean = true,
    val subtitle: String? = null,
    @ColorInt
    val toolbarColor: Int? = null,

    val menuTopRes: Iterable<Int?> = emptyList(),
    val menuTopHiddenIds: Iterable<Int?> = emptyList(),
    val menuBottomRes: Iterable<Int?> = emptyList(),
    val menuBottomHiddenIds: Iterable<Int?> = emptyList(),
    val supportsRefresh: Boolean = true,

    val fabVisible: Boolean = true,
    @DrawableRes
    val fabIconRes: Int = 0
)

enum class FragmentType {
    PRIMARY,
    SECONDARY
}
