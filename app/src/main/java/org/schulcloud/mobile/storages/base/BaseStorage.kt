package org.schulcloud.mobile.storages.base

import android.content.SharedPreferences
import androidx.core.content.edit
import org.schulcloud.mobile.SchulCloudApp
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

abstract class BaseStorage(name: String, mode: Int) {
    private val preferences: SharedPreferences = SchulCloudApp.instance.getSharedPreferences(name, mode)

    fun getString(key: String, defValue: String? = null): String? = preferences.getString(key, defValue)
    fun putString(key: String, value: String?) = preferences.edit { putString(key, value) }

    fun getInt(key: String, defValue: Int = 0): Int = preferences.getInt(key, defValue)
    fun putInt(key: String, value: Int) = preferences.edit { putInt(key, value) }

    fun clear() = preferences.edit { clear() }

    class StringPreference(private val storage: BaseStorage, val key: String) :
            ObservableProperty<String?>(storage.getString(key)) {
        override fun afterChange(property: KProperty<*>, oldValue: String?, newValue: String?) {
            storage.putString(key, newValue)
        }
    }

    class IntPreference(private val storage: BaseStorage, val key: String) :
            ObservableProperty<Int?>(storage.getInt(key, NULL)) {
        companion object {
            const val NULL = Int.MIN_VALUE + 1
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): Int? {
            val value = super.getValue(thisRef, property)
            return if (value == NULL) null else value
        }

        override fun afterChange(property: KProperty<*>, oldValue: Int?, newValue: Int?) {
            storage.putInt(key, newValue ?: NULL)
        }
    }
}
