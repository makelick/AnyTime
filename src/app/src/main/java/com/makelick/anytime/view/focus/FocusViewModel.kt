package com.makelick.anytime.view.focus

import androidx.lifecycle.ViewModel
import com.makelick.anytime.model.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : ViewModel() {

    val currentTime = timerRepository.currentTime
    val isTimerRunning: Boolean
        get() = timerRepository.isTimerRunning

    fun stopTimer() {
        timerRepository.stopTimer()
    }

    fun pauseTimer() {
        timerRepository.pauseTimer()
    }


}