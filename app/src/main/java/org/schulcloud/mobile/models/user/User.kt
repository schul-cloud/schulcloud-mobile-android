package org.schulcloud.mobile.models.user

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId
import org.schulcloud.mobile.models.base.RealmString

open class User : RealmObject(), HasId {

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var schoolId: String? = null
    var displayName: String? = null

    var permissions: RealmList<RealmString>? = null

    val name get() = "$firstName $lastName"
    val shortName get() = "${firstName?.get(0)}. $lastName"
}


enum class Permission(val string: String) {
    FILE_CREATE("FILE_CREATE"),
    SUBMISSIONS_EDIT("SUBMISSIONS_EDIT")
}

fun User?.hasPermission(permission: Permission): Boolean {
    return this?.permissions
            ?.any { it.value.equals(permission.string, true) } == true
}
