package org.schulcloud.mobile.models.file

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId
import org.schulcloud.mobile.models.base.RealmString


open class File : RealmObject(), HasId {

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var key: String = ""
    var path: String? = null
    var name: String? = null

    var type: String? = null
    var size: Long? = null
    var thumbnail: String? = null
    var flatFileName: String? = null
    var permissions: RealmList<FilePermissions>? = null


    fun addPermissions(userIds: List<String>, additionalPermissions: List<String>) {
        for (userId in userIds) {
            val allPermissions = permissions
                    ?: RealmList<FilePermissions>().also { permissions = it }
            val userPermissions = allPermissions.firstOrNull { it.userId == userId }
                    ?: FilePermissions().also {
                        it.userId = userId
                        allPermissions.add(it)
                    }
            val permissions = userPermissions.permissions
                    ?: RealmList<RealmString>().also { userPermissions.permissions = it }

            for (newPermission in additionalPermissions)
                if (!permissions.any { it.value == newPermission })
                    permissions.add(RealmString(newPermission))
        }
    }
}

open class FilePermissions : RealmObject() {
    companion object {
        const val PERMISSION_READ = "can-read"
        const val PERMISSION_WRITE = "can-write"
    }

    var userId: String = ""
    var permissions: RealmList<RealmString>? = null
}
