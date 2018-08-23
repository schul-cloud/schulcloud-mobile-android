package org.schulcloud.mobile.storages

import android.content.Context
import org.schulcloud.mobile.storages.base.BaseStorage

@Suppress("MagicNumber")
object Onboarding : BaseStorage("pref_onboarding", Context.MODE_PRIVATE) {
    class VersionPreference(val key: String, val currentVersion: Int) {
        private val previousVersion: Int get() = getInt(key)

        fun getUpdates(updateToCurrent: Boolean = false): IntRange? {
            val updates = (previousVersion + 1)..currentVersion
            return if (updates.isEmpty())
                null
            else {
                if (updateToCurrent)
                    update(currentVersion)
                updates
            }
        }

        fun update(newVersion: Int) = putInt(key, newVersion.coerceIn(previousVersion, currentVersion))
    }

    /**
     * Add bottom app bar, bottom navigation drawer
     */
    const val NAVIGATION_1 = 1
    val navigation = VersionPreference("navigation", NAVIGATION_1)
}
