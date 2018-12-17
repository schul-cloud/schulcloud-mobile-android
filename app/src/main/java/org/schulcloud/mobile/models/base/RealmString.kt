package org.schulcloud.mobile.models.base

import io.realm.RealmModel
import io.realm.annotations.RealmClass


@RealmClass
open class RealmString @JvmOverloads constructor(var value: String = "") : RealmModel {

    override fun toString() = value
    override fun equals(other: Any?): Boolean {
        return when (other) {
            this === other -> true
            is String -> value == other
            is RealmString -> value == other.value
            else -> false
        }
    }

    override fun hashCode() = value.hashCode()
}
