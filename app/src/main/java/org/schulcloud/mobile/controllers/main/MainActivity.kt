package org.schulcloud.mobile.controllers.main

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.iterator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.android.synthetic.main.activity_main.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.storages.Onboarding
import org.schulcloud.mobile.utils.getTextColorForBackground
import org.schulcloud.mobile.utils.getTextColorSecondaryForBackground
import org.schulcloud.mobile.utils.setTintCompat
import org.schulcloud.mobile.utils.visibilityBool
import org.schulcloud.mobile.viewmodels.MainViewModel
import org.schulcloud.mobile.viewmodels.ToolbarColors

class MainActivity : BaseActivity() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName

        private const val DARKEN_FACTOR = 0.2f
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }
    private val navController: NavController by lazy { findNavController(navHost) }
    private var toolbar: Toolbar? = null
    private var toolbarWrapper: ViewGroup? = null
    private var optionsMenu: Menu? = null

    private var lastConfig: MainFragmentConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.config.observe(this, Observer { config ->
            if (config == null || config == lastConfig) return@Observer

            if (lastConfig?.title != config.title)
                title = config.title.takeIf { config.showTitle }
            if (lastConfig?.subtitle != config.subtitle)
                supportActionBar?.subtitle = config.subtitle
            recalculateToolbarColors()

            bottomAppBar.apply {
                val menuChanged = lastConfig?.menuBottomRes != config.menuBottomRes
                if (menuChanged) {
                    menu.clear()
                    for (menuRes in config.menuBottomRes.filterNotNull())
                        inflateMenu(menuRes)
                }

                if (menuChanged
                        || lastConfig?.menuBottomHiddenIds != config.menuBottomHiddenIds) {
                    for (item in menu)
                        item.isVisible = !config.menuBottomHiddenIds.contains(item.itemId)
                }
            }

            fab.visibilityBool = config.fabVisible && config.fabIconRes != 0
            if (lastConfig?.fragmentType != config.fragmentType)
                bottomAppBar.fabAlignmentMode = when (config.fragmentType) {
                    FragmentType.PRIMARY -> BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                    FragmentType.SECONDARY -> BottomAppBar.FAB_ALIGNMENT_MODE_END
                }
            if (lastConfig?.fabIconRes != config.fabIconRes)
                fab.setImageResource(config.fabIconRes)
            lastConfig = config
        })
        viewModel.toolbarColors.observe(this, Observer {
            updateToolbarColors()
        })

        bottomAppBar.setNavigationOnClickListener { showDrawer() }
        bottomAppBar.setOnMenuItemClickListener {
            viewModel.onOptionsItemSelected.value = it
            return@setOnMenuItemClickListener true
        }

        fab.setOnClickListener { viewModel.onFabClicked.call() }
    }

    override fun onResume() {
        super.onResume()

        Onboarding.navigation.getUpdates()?.forEach { version ->
            when (version) {
                Onboarding.NAVIGATION_1 -> TapTargetView.showFor(this,
                        TapTarget.forToolbarNavigationIcon(bottomAppBar,
                                getString(R.string.main_navigation_onboarding_1))
                                .drawShadow(true),
                        object : TapTargetView.Listener() {
                            override fun onTargetClick(view: TapTargetView?) {
                                showDrawer()
                                super.onTargetClick(view)
                            }

                            override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                                if (userInitiated)
                                    Onboarding.navigation.update(Onboarding.NAVIGATION_1)
                            }
                        })
            }
        }
    }

    private fun showDrawer() {
        val drawer = NavigationDrawerFragment()
        drawer.show(supportFragmentManager, drawer.tag)
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)

        this.toolbar = toolbar
        if (toolbar != null)
            NavigationUI.setupWithNavController(toolbar, navController)
        updateToolbarColors()
    }

    fun setToolbarWrapper(toolbarWrapper: ViewGroup?) {
        this.toolbarWrapper = toolbarWrapper
        updateToolbarColors()
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun onBackPressed() {
        if (!navController.popBackStack())
            super.onBackPressed()
    }

    override fun openOptionsMenu() {
        super.openOptionsMenu()
        updateToolbarColors()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        optionsMenu = menu
        updateToolbarColors()
        return super.onCreateOptionsMenu(menu)
    }

    override fun invalidateOptionsMenu() {
        super.invalidateOptionsMenu()
        updateToolbarColors()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        viewModel.onOptionsItemSelected.value = item
        return true
    }


    private fun recalculateToolbarColors() {
        val color = viewModel.config.value?.toolbarColor
                ?: ContextCompat.getColor(this, R.color.toolbar_background_default)

        viewModel.toolbarColors.value = ToolbarColors(color,
                getTextColorForBackground(color), getTextColorSecondaryForBackground(color),
                ColorUtils.blendARGB(color, Color.BLACK, DARKEN_FACTOR))
    }

    private fun updateToolbarColors() {
        val colors = viewModel.toolbarColors.value ?: return
        val toolbar = toolbar

        // Background
        toolbarWrapper?.setBackgroundColor(colors.color)
        toolbar?.setBackgroundColor(colors.color)

        // Back button
        val textColorFilter =
                PorterDuffColorFilter(colors.textColor, PorterDuff.Mode.SRC_ATOP)
        if (toolbar != null)
            for (view in toolbar.children)
                if (view is ImageButton) {
                    view.drawable?.colorFilter = textColorFilter
                    break
                }

        // Option items
        optionsMenu?.forEach { item ->
            item.icon?.setTintCompat(colors.textColor)
        }

        // Title + subtitle
        toolbar?.setTitleTextColor(colors.textColor)
        toolbar?.setSubtitleTextColor(colors.textColor)

        // Overflow icon
        toolbar?.overflowIcon?.setTintCompat(colors.textColor)

        // Status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window?.statusBarColor = colors.statusBarColor
    }
}
