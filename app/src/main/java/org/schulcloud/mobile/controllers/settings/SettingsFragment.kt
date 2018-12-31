package org.schulcloud.mobile.controllers.settings

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.storages.Preferences
import org.schulcloud.mobile.utils.DarkModeUtils
import org.schulcloud.mobile.utils.hasPermission

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        val TAG = SettingsFragment::class.simpleName!!
    }

    // region Lifecycle
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        initTheme()
    }

    override fun onResume() {
        super.onResume()

        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(context)
                .unregisterOnSharedPreferenceChangeListener(this)

        super.onPause()
    }
    // endregion


    @Suppress("LocalVariableName")
    fun initTheme() {
        val context = context ?: return

        // Dark mode

        // Request location permission for accurate sunrise/sunset values
        val darkMode_night = findPreference<CheckBoxPreference>(Preferences.Theme.DARK_MODE_NIGHT)
        darkMode_night.summaryProvider = Preference.SummaryProvider<CheckBoxPreference> {
            if (Preferences.Theme.darkMode_night && !context.hasPermission(ACCESS_COARSE_LOCATION))
                getString(R.string.settings_theme_darkMode_night_summary_noLocationPermission)
            else null
        }
        darkMode_night.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            if (newValue == true && !context.hasPermission(ACCESS_COARSE_LOCATION))
                launch {
                    if ((activity as BaseActivity).requestPermission(ACCESS_COARSE_LOCATION))
                        DarkModeUtils.getInstance(context).onLocationPermissionGranted()
                }

            true
        }

        updateTheme_darkMode()
    }

    @Suppress("FunctionName", "LocalVariableName")
    private fun updateTheme_darkMode() {
        val darkMode = findPreference<SwitchPreferenceCompat>(Preferences.Theme.DARK_MODE)
        val active = Preferences.Theme.darkMode
        val ambientLightActive = Preferences.Theme.darkMode_ambientLight
        val nightActive = Preferences.Theme.darkMode_night
        val powerSaverActive = Preferences.Theme.darkMode_powerSaver
        darkMode.summaryOn =
                if (!ambientLightActive && !nightActive && !powerSaverActive)
                    getString(R.string.settings_theme_darkMode_summary_always)
                else {
                    val criteria = arrayOf(
                            if (ambientLightActive) getString(
                                    R.string.settings_theme_darkMode_summary_ambientLight) else null,
                            if (nightActive) getString(R.string.settings_theme_darkMode_summary_night) else null,
                            if (powerSaverActive) getString(
                                    R.string.settings_theme_darkMode_summary_powerSaver) else null
                    ).filterNotNull()
                    val criteriaText = criteria.joinToString(
                            getString(R.string.settings_theme_darkMode_summary_separator))
                    getString(R.string.settings_theme_darkMode_summary_container, criteriaText)
                }

        val context = context ?: return

        // Check HW support for ambient light sensor
        findPreference<CheckBoxPreference>(Preferences.Theme.DARK_MODE_AMBIENT_LIGHT)
                .isVisible = active && DarkModeUtils.getInstance(context).supportsAmbientLight

        findPreference<CheckBoxPreference>(Preferences.Theme.DARK_MODE_NIGHT)
                .isVisible = active

        // Check support for power saver
        findPreference<CheckBoxPreference>(Preferences.Theme.DARK_MODE_POWER_SAVER)
                .isVisible = active && DarkModeUtils.getInstance(context).supportsPowerSaver
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        key ?: return

        if (key.startsWith(Preferences.Theme.DARK_MODE))
            updateTheme_darkMode()
    }
}
