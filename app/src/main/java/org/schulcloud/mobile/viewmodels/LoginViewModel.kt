package org.schulcloud.mobile.viewmodels

import android.util.Patterns
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.mutableLiveDataOf
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class LoginViewModel : BaseViewModel() {
    val loginState = mutableLiveDataOf<LoginStatus>()

    fun login(email: String, password: String) {
        val invalidInputs = mutableListOf<LoginInput>()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            invalidInputs.add(LoginInput.EMAIL)
        if (password.isEmpty())
            invalidInputs.add(LoginInput.PASSWORD)

        loginState.value = if (invalidInputs.isNotEmpty())
            LoginStatus.InvalidInputs(invalidInputs)
        else {
            UserRepository.login(email, password, loginCallback())
            LoginStatus.Pending()
        }
    }

    private fun loginCallback(): RequestJobCallback {
        return object : RequestJobCallback() {
            override fun onSuccess() {
                loginState.value = LoginStatus.LoggedIn()
            }

            override fun onError(code: ErrorCode) {
                loginState.value = LoginStatus.Error(code.toString())
            }
        }
    }

    sealed class LoginStatus {
        class Pending : LoginStatus()
        class LoggedIn : LoginStatus()
        class InvalidInputs(val invalidInputs: MutableList<LoginInput>) : LoginStatus()
        class Error(val error: String) : LoginStatus()
    }

    enum class LoginInput { EMAIL, PASSWORD }
}
