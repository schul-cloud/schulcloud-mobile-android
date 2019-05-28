package org.schulcloud.mobile.models.material

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId
import java.util.*

open class Material : RealmObject(), HasId {
    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var providerName: String? = null
    var url: String? = null
    var title: String? = null
    var description: String? = null
    var thumbnail: String? = null
    var featuredUntil: Date? = null
    var clickCount: Int? = null
    var createdAt: String? = null

    var tags: RealmList<String>? = null
}
