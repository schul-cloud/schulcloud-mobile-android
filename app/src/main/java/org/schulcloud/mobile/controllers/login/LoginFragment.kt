package org.schulcloud.mobile.controllers.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_login.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.main.MainActivity
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.viewmodels.LoginViewModel

class LoginFragment : BaseFragment() {
    companion object {
        val TAG: String = LoginFragment::class.java.simpleName
    }

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                login()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        loginBtn.setOnClickListener { login() }
        demo_student.setOnClickListener { demoLoginStudent() }
        demo_teacher.setOnClickListener { demoLoginTeacher() }

        handleLoginStatus()
    }

    private fun login() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        loginViewModel.login(email, password)
    }

    private fun demoLoginStudent() {
        loginViewModel.login(getString(R.string.login_demo_student_username),
                getString(R.string.login_demo_student_password))
    }

    private fun demoLoginTeacher() {
        loginViewModel.login(getString(R.string.login_demo_teacher_username),
                getString(R.string.login_demo_teacher_password))
    }

    private fun handleLoginStatus() {
        loginViewModel.loginState.observe(this, Observer { loginState ->
            when (loginState) {
                is LoginViewModel.LoginStatus.Pending -> {
                    Log.d(TAG, "PENDING LOGIN")
                    errorText.visibility = View.GONE
                    progress.visibility = View.VISIBLE
                    loginBtn.isEnabled = false
                }
                is LoginViewModel.LoginStatus.LoggedIn -> {
                    Log.d(TAG, "LOGGED IN")
                    startMainActivity()
                    errorText.visibility = View.GONE
                }
                is LoginViewModel.LoginStatus.InvalidInputs -> {
                    Log.d(TAG, "INVALID FIELDS")
                    errorText.visibility = View.GONE
                    progress.visibility = View.GONE
                    loginBtn.isEnabled = true
                    handleInvalidFields(loginState.invalidInputs)
                }
                is LoginViewModel.LoginStatus.Error -> {
                    Log.d(TAG, "ERROR: " + loginState.error)
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(when (loginState.error) {
                        RequestJobCallback.ErrorCode.TIMEOUT -> R.string.login_error_server
                        RequestJobCallback.ErrorCode.ERROR -> R.string.login_error_passwordWrong
                        RequestJobCallback.ErrorCode.NO_NETWORK -> R.string.login_error_noConnection
                        else -> R.string.login_error
                    })
                    progress.visibility = View.GONE
                    loginBtn.isEnabled = true
                }
            }
        })
    }

    private fun handleInvalidFields(invalidInputs: List<LoginViewModel.LoginInput>) {
        invalidInputs.forEach { input ->
            when (input) {
                LoginViewModel.LoginInput.EMAIL ->
                    emailInput.error = getString(R.string.login_error_emailInvalid)
                LoginViewModel.LoginInput.PASSWORD ->
                    passwordInput.error = getString(R.string.login_error_passwordEmpty)
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
