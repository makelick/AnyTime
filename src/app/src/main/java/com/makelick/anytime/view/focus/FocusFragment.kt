package com.makelick.anytime.view.focus

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.makelick.anytime.databinding.FragmentFocusBinding
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FocusFragment : BaseFragment<FragmentFocusBinding>(FragmentFocusBinding::inflate) {

    private val viewModel: FocusViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {

        binding.iconPlay.setOnClickListener {
            if (viewModel.isTimerRunning) {
                viewModel.pauseTimer()
                context?.stopService(Intent(context, TimerService::class.java))
            } else {
                context?.startService(
                    Intent(context, TimerService::class.java)
                        .putExtra("timeInMillis", 20_000L)
                )
            }
        }

        binding.iconRestart.setOnClickListener {
            viewModel.stopTimer()
            context?.stopService(Intent(context, TimerService::class.java))

        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.currentTime.collect {
                binding.time.text = (it / 1000).toString()
            }
        }
    }

}