package org.schulcloud.mobile.controllers.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.controllers.login.LoginActivity
import org.schulcloud.mobile.models.user.UserRepository

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation(savedInstanceState == null)
    }

    private fun setupNavigation(isFirstRun: Boolean) {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.main_drawer_open, R.string.main_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        if (isFirstRun) {
            nav_view.setCheckedItem(R.id.nav_dashboard)
            addFragment(DashboardFragment(), DashboardFragment.TAG)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_dashboard ->
                replaceFragment(DashboardFragment(), DashboardFragment.TAG)
            R.id.nav_courses ->
                replaceFragment(CourseListFragment(), CourseListFragment.TAG)
            R.id.nav_files ->
                replaceFragment(FileOverviewFragment(), FileOverviewFragment.TAG)
            R.id.nav_logout -> {
                UserRepository.logout()
                showLoginActivity()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment, tag)
                .commit()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(tag)
                .commit()
    }

    private fun showLoginActivity() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
