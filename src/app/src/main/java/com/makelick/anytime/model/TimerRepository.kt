package com.makelick.anytime.model

import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepository @Inject constructor() {

    private var timer: CountDownTimer? = null
    private var remainingTime = 0L
    val currentTime = MutableStateFlow<Long>(0)

    val isTimerRunning: Boolean
        get() = timer != null

    fun startTimer(timeInMillis: Long) {
        remainingTime = if (remainingTime == 0L) timeInMillis else remainingTime
        timer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                currentTime.value = millisUntilFinished
            }

            override fun onFinish() {
                remainingTime = 0
                currentTime.value = 0
            }
        }.start()
    }

    fun pauseTimer() {
        timer?.cancel()
        timer = null
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
        currentTime.value = 0 // TODO: change to type time
        remainingTime = 0
    }
}