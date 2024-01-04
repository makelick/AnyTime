package com.makelick.anytime.view.focus

import androidx.lifecycle.ViewModel
import com.makelick.anytime.model.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : ViewModel() {

    val timerMode = timerRepository.timerMode
    val timerBreaksCount = timerRepository.timerBreaksCount
    val isTimerRunning  = timerRepository.isTimerRunning
    val currentTime = timerRepository.currentTime

    fun pauseTimer() {
        timerRepository.pauseTimer()
    }

    fun stopTimer() {
        timerRepository.stopTimer()
    }

    fun nextMode() {
        timerRepository.nextMode()
    }

}
