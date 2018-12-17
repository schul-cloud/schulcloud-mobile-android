package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class NavigationDrawerViewModel : BaseViewModel() {
    val user: LiveData<User?> = UserRepository.currentUser(realm)
}
