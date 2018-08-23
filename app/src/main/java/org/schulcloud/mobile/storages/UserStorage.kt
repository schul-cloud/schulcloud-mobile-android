package org.schulcloud.mobile.storages

import android.content.Context
import org.schulcloud.mobile.storages.base.BaseStorage

object UserStorage : BaseStorage("pref_user_v2", Context.MODE_PRIVATE) {
    private const val USER_ID = "id"
    private const val ACCESS_TOKEN = "token"

    var userId by BaseStorage.StringPreference(this, USER_ID)
    var accessToken by BaseStorage.StringPreference(this, ACCESS_TOKEN)
}
