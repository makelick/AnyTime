package com.makelick.anytime.model

import android.os.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepository @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {

    val timerMode = MutableStateFlow("")
    val timerBreaksCount = MutableStateFlow(0)

    private var timer: CountDownTimer? = null
    private var remainingTime = 0L
    val currentTime = MutableStateFlow<Long>(0)

    val isTimerRunning: Boolean
        get() = timer != null


    init {
        CoroutineScope(Dispatchers.IO).launch {
            timerMode.emit(
                dataStoreRepository.getFromDataStore(DataStoreRepository.KEY_TIMER_MODE)
                    .first() ?: KEY_POMODORO
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            timerBreaksCount.emit(
                dataStoreRepository.getFromDataStore(DataStoreRepository.KEY_TIMER_BREAKS_COUNT)
                    .first() ?: 0
            )
        }
    }

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
        currentTime.value = getStartTimeInMillis()
        remainingTime = 0
    }

    fun nextMode() {
        CoroutineScope(Dispatchers.IO).launch {
            if (timerMode.value == KEY_POMODORO) {
                if (timerBreaksCount.value == 4) {
                    dataStoreRepository.saveToDataStore(
                        DataStoreRepository.KEY_TIMER_MODE,
                        KEY_LONG_BREAK
                    )

                    dataStoreRepository.saveToDataStore(
                        DataStoreRepository.KEY_TIMER_BREAKS_COUNT,
                        0
                    )
                    timerMode.value = KEY_LONG_BREAK
                    timerBreaksCount.value = 0
                } else {
                    dataStoreRepository.saveToDataStore(
                        DataStoreRepository.KEY_TIMER_MODE,
                        KEY_SHORT_BREAK
                    )

                    dataStoreRepository.saveToDataStore(
                        DataStoreRepository.KEY_TIMER_BREAKS_COUNT,
                        timerBreaksCount.value + 1
                    )
                    timerMode.value = KEY_SHORT_BREAK
                    timerBreaksCount.value = timerBreaksCount.value + 1
                }
            } else {
                dataStoreRepository.saveToDataStore(
                    DataStoreRepository.KEY_TIMER_MODE,
                    KEY_POMODORO
                )
                timerMode.value = KEY_POMODORO
            }
        }
    }

    fun getStartTimeInMillis(): Long {
        return when (timerMode.value) {
            KEY_POMODORO -> 25 * 60 * 1000L
            KEY_SHORT_BREAK -> 5 * 60 * 1000L
            KEY_LONG_BREAK -> 15 * 60 * 1000L
            else -> 0
        }
    }

    companion object {
        const val KEY_POMODORO = "Pomodoro"
        const val KEY_SHORT_BREAK = "Short Break"
        const val KEY_LONG_BREAK = "Long Break"
    }
}