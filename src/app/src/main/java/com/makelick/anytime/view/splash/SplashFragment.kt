package com.makelick.anytime.view.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentSplashBinding
import com.makelick.anytime.model.TimerRepository.Companion.SECOND
import com.makelick.anytime.view.BaseFragment
import com.makelick.anytime.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            delay(SECOND)
            if (viewModel.isUserLoggedIn()) {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToTasksFragment())
                (activity as MainActivity).changeBottomNavSelectedId(R.id.tasks)
            } else {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }
    }

}
