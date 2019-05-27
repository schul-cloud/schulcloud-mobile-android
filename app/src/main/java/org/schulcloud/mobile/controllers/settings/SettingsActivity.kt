package org.schulcloud.mobile.controllers.settings

import android.os.Bundle
import kotlinx.android.synthetic.main.toolbar.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        setupActionBar()

        if (supportFragmentManager.findFragmentByTag(SettingsFragment.TAG) == null)
            supportFragmentManager.beginTransaction()
                    .add(R.id.content, SettingsFragment(), SettingsFragment.TAG)
                    .commit()
    }
}
