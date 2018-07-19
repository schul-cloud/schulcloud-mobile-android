package org.schulcloud.mobile.models.file

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class File : RealmObject() {
    @PrimaryKey
    var key: String = ""
    var name: String? = null
    var path: String? = null

    var type: String? = null
    var size: Long? = null
    var thumbnail: String? = null
    var flatFileName: String? = null
}
