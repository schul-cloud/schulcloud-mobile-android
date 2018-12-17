package org.schulcloud.mobile.models.homework.submission

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class Submission : RealmObject(), HasId {

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var homeworkId: String? = null
    var studentId: String? = null
    var comment: String? = null
    var createdAt: String? = null

    var fileIds: RealmList<String>? = null

    var grade: Int? = null
    var gradeComment: String? = null

    var comments: RealmList<Comment>? = null

}
