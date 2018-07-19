package org.schulcloud.mobile.models.file

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Date: 7/5/2018
 */
open class Directory : RealmObject() {
    @PrimaryKey
    var key: String? = null
    var name: String? = null
    var path: String? = null
}
