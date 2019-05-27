package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.filterNotNull

class NavigationDrawerViewModel : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val user: LiveData<User> = UserRepository.currentUser(realm)
            // When logging out without the filter there would a brief moment when no user is displayed but the drawer
            // is still shown
            .filterNotNull()
}
