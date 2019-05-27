package org.schulcloud.mobile.storages.base

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import org.schulcloud.mobile.SchulCloudApp
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class BaseStorage(name: String) {
    private val preferences: SharedPreferences = SchulCloudApp.instance.getSharedPreferences(name, MODE_PRIVATE)

    fun clear() = preferences.edit { clear() }


    fun getBoolean(key: String, defValue: Boolean = false) = preferences.getBoolean(key, defValue)
    fun putBoolean(key: String, value: Boolean) = preferences.edit { putBoolean(key, value) }

    class BooleanPreference(val key: String, val default: Boolean = false) : ReadWriteProperty<BaseStorage, Boolean> {
        override fun getValue(thisRef: BaseStorage, property: KProperty<*>) = thisRef.getBoolean(key, default)

        override fun setValue(thisRef: BaseStorage, property: KProperty<*>, value: Boolean) {
            thisRef.putBoolean(key, value)
        }
    }


    fun getInt(key: String, defValue: Int = 0) = preferences.getInt(key, defValue)
    fun putInt(key: String, value: Int) = preferences.edit { putInt(key, value) }

    class IntPreference(val key: String) : ReadWriteProperty<BaseStorage, Int?> {
        companion object {
            const val NULL = Int.MIN_VALUE + 1
        }

        override fun getValue(thisRef: BaseStorage, property: KProperty<*>): Int? {
            val value = thisRef.getInt(key)
            return if (value == NULL) null else value
        }

        override fun setValue(thisRef: BaseStorage, property: KProperty<*>, value: Int?) {
            thisRef.putInt(key, value ?: NULL)
        }
    }


    fun getString(key: String, defValue: String? = null): String? = preferences.getString(key, defValue)
    fun putString(key: String, value: String?) = preferences.edit { putString(key, value) }

    class StringPreference(val key: String) : ReadWriteProperty<BaseStorage, String?> {
        override fun getValue(thisRef: BaseStorage, property: KProperty<*>) = thisRef.getString(key)

        override fun setValue(thisRef: BaseStorage, property: KProperty<*>, value: String?) {
            thisRef.putString(key, value)
        }
    }
}
