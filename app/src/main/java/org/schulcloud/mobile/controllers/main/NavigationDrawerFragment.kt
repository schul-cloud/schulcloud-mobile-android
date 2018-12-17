package org.schulcloud.mobile.controllers.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.drawer_navigation.*
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.controllers.base.BaseSheet
import org.schulcloud.mobile.controllers.login.LoginActivity
import org.schulcloud.mobile.databinding.DrawerNavigationBinding
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.viewmodels.NavigationDrawerViewModel

class NavigationDrawerFragment : BaseSheet() {
    private lateinit var viewModel: NavigationDrawerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NavigationDrawerViewModel::class.java)
        launch {
            UserRepository.syncCurrentUser()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DrawerNavigationBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
            it.onLogout = {
                UserRepository.logout()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val navController = findNavController(this)
        NavigationUI.setupWithNavController(navigationView, navController)
        var firstNavigation = true
        navController.addOnNavigatedListener { _, _ ->
            if (!firstNavigation)
                dismiss()
            firstNavigation = !firstNavigation
        }
    }
}
