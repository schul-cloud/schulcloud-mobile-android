package org.schulcloud.mobile.storages

import org.schulcloud.mobile.storages.base.BaseStorage

object UserStorage : BaseStorage("pref_user_v2") {
    private const val USER_ID = "id"
    private const val ACCESS_TOKEN = "token"

    var userId by BaseStorage.StringPreference(USER_ID)
    var accessToken by BaseStorage.StringPreference(ACCESS_TOKEN)
}
