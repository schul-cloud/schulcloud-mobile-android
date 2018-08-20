package org.schulcloud.mobile.controllers.main

import android.graphics.Color
import android.graphics.Color.*
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.android.synthetic.main.activity_main.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.config.observe(this, Observer { config ->
            title = config.title
            supportActionBar?.subtitle = config.subtitle
            recalculateToolbarColors()

            bottomAppBar.apply {
                menu.clear()
                if (config.menuBottomRes != 0) {
                    inflateMenu(config.menuBottomRes)
                    for (id in config.menuBottomHiddenIds)
                        if (id != 0)
                            menu?.findItem(id)?.isVisible = false
                }
            }

            fab.visibilityBool = config.fabVisible && config.fabIconRes != 0
            bottomAppBar.fabAlignmentMode = when (config.fragmentType) {
                FragmentType.PRIMARY -> BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                FragmentType.SECONDARY -> BottomAppBar.FAB_ALIGNMENT_MODE_END
            }
            fab.setImageResource(config.fabIconRes)
        })

        viewModel.toolbarColors.observe(this, Observer {
            updateToolbarColors()
        })

        bottomAppBar.setNavigationOnClickListener {
            val navDrawer = org.schulcloud.mobile.controllers.main.NavigationDrawerFragment()
            navDrawer.show(supportFragmentManager, navDrawer.tag)
        }
        bottomAppBar.setOnMenuItemClickListener {
            viewModel.onOptionsItemSelected.value = it
            return@setOnMenuItemClickListener true
        }

        fab.setOnClickListener { viewModel.onFabClicked.call() }
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)

        this.toolbar = toolbar
        if (toolbar != null)
            NavigationUI.setupWithNavController(toolbar, navController)
        updateToolbarColors()
    }

    fun setToolbarWrapper(toolbarWrapper: ViewGroup) {
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

        // Formula from [Color#luminance()]
        val isLight = 0.2126 * red(color) + 0.7152 * green(color) + 0.0722 * blue(color) > 0.5 * 255

        val textColor = ContextCompat.getColor(this,
                if (isLight) R.color.material_text_primary_dark
                else R.color.material_text_primary_light)

        val statusBarColor = ColorUtils.blendARGB(color, Color.BLACK, DARKEN_FACTOR)

        viewModel.toolbarColors.value = ToolbarColors(color, textColor, isLight, statusBarColor)
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
