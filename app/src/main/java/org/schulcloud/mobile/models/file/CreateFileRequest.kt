package org.schulcloud.mobile.models.file

import io.realm.RealmObject


open class CreateFileRequest : RealmObject() {
    var key: String = ""
    var path: String? = null
    var name: String? = null

    var type: String? = null
    var size: Long? = null
    var thumbnail: String? = null
    var flatFileName: String? = null
}
