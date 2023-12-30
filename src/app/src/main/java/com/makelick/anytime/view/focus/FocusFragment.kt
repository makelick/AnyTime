package com.makelick.anytime.view.focus

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.PermissionChecker
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentFocusBinding
import com.makelick.anytime.model.entity.PomodoroMode
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
        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (PermissionChecker.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                @Suppress("DEPRECATION")
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun setupUI() {

        binding.playButton.setOnClickListener {
            if (viewModel.isTimerRunning.value) {
                context?.stopService(Intent(context, TimerService::class.java))
                viewModel.pauseTimer()
                binding.iconPlay.setImageResource(R.drawable.ic_play)
            } else {
                context?.startService(Intent(context, TimerService::class.java))
                binding.iconPlay.setImageResource(R.drawable.ic_pause)
            }
        }

        binding.restartButton.setOnClickListener {
            stopTimer()
            binding.iconPlay.setImageResource(R.drawable.ic_play)
        }

        binding.nextButton.setOnClickListener {
            viewModel.nextMode()
            stopTimer()
            context?.startService(Intent(context, TimerService::class.java))
            binding.iconPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    private fun stopTimer() {
        context?.stopService(Intent(context, TimerService::class.java))
        viewModel.stopTimer()
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.isTimerRunning.collect {
                if (it) {
                    binding.iconPlay.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.iconPlay.setImageResource(R.drawable.ic_play)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currentTime.collect {
                binding.time.text = getStringTime(it)
            }
        }

        lifecycleScope.launch {
            viewModel.timerMode.collect {
                with(binding) {
                    title.text = it.title
                    hint.text = getHint(it)

                    timeCard.setCardBackgroundColor(getTimerColor(it))
                    playButton.setCardBackgroundColor(getTimerColor(it))

                    if (it == PomodoroMode.POMODORO) {
                        countOfBreaks.text = getString(
                            R.string.focus_count_of_breaks,
                            viewModel.timerBreaksCount.value
                        )
                        countOfBreaks.visibility = View.VISIBLE
                    } else {
                        countOfBreaks.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun getTimerColor(mode: PomodoroMode): Int {
        return when (mode) {
            PomodoroMode.POMODORO -> resources.getColor(R.color.primary, null)
            PomodoroMode.SHORT_BREAK -> resources.getColor(R.color.secondary, null)
            PomodoroMode.LONG_BREAK -> resources.getColor(R.color.accent, null)
        }
    }

    private fun getHint(mode: PomodoroMode): String {
        return when (mode) {
            PomodoroMode.POMODORO -> getString(R.string.focus_hint_pomodoro)
            PomodoroMode.SHORT_BREAK -> getString(R.string.focus_hint_short_break)
            PomodoroMode.LONG_BREAK -> getString(R.string.focus_hint_long_break)
        }
    }

    private fun getStringTime(time: Long): String {
        val minutes = (time / 1000) / 60
        val seconds = (time / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}