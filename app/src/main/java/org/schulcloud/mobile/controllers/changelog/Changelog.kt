package org.schulcloud.mobile.controllers.changelog

import com.michaelflisar.changelog.ChangelogBuilder
import com.michaelflisar.changelog.ChangelogSetup
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.utils.DarkModeUtils


object Changelog {
    fun configure() {
        ChangelogSetup.get().registerTag(IntroTag())
        ChangelogSetup.get().registerTag(FeatureTag())
        ChangelogSetup.get().registerTag(FixTag())
        ChangelogSetup.get().registerTag(NoteTag())
    }

    fun showDialog(activity: BaseActivity, showOnlyNew: Boolean = true) {
        ChangelogBuilder().apply {
            withRenderer(Renderer())
            withTitle(activity.getString(R.string.changelog_title, BuildConfig.VERSION_NAME))
            if (showOnlyNew)
                withManagedShowOnStart(true)
        }.buildAndShowDialog(activity, DarkModeUtils.getInstance(activity).isActive)
    }
}
