package org.schulcloud.mobile.utils

import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


inline fun <T> Delegates.changeObservable(
    initialValue: T,
    crossinline onChange: (property: KProperty<*>, oldValue: T, newValue: T) -> Unit
): ReadWriteProperty<Any?, T> {
    return object : ObservableProperty<T>(initialValue) {
        override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
            return oldValue != newValue
        }

        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            onChange(property, oldValue, newValue)
        }
    }
}
