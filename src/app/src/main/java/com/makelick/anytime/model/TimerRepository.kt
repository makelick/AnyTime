package com.makelick.anytime.model

import android.os.CountDownTimer
import com.makelick.anytime.model.entity.PomodoroMode
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

    private var timer: CountDownTimer? = null

    val timerMode = MutableStateFlow(PomodoroMode.POMODORO)
    val timerBreaksCount = MutableStateFlow(0)
    val isTimerRunning = MutableStateFlow(timer != null)
    val currentTime = MutableStateFlow<Long>(0)


    init {
        CoroutineScope(Dispatchers.IO).launch {
            timerMode.emit(
                getModeByTitle(
                dataStoreRepository.getFromDataStore(DataStoreRepository.KEY_TIMER_MODE)
                    .first() ?: PomodoroMode.POMODORO.title)
            )
            currentTime.value = timerMode.value.timeInMillis
        }
        CoroutineScope(Dispatchers.IO).launch {
            timerBreaksCount.emit(
                dataStoreRepository.getFromDataStore(DataStoreRepository.KEY_TIMER_BREAKS_COUNT)
                    .first() ?: 0
            )
        }
    }

    fun startTimer(timeInMillis: Long) {
        isTimerRunning.value = true
        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime.value = millisUntilFinished
            }

            override fun onFinish() {
                currentTime.value = 0
            }
        }.start()
    }

    fun pauseTimer() {
        isTimerRunning.value = false
        timer?.cancel()
        timer = null
    }

    fun stopTimer() {
        isTimerRunning.value = false
        timer?.cancel()
        timer = null
        currentTime.value = timerMode.value.timeInMillis
    }

    fun nextMode() {
        CoroutineScope(Dispatchers.IO).launch {
            if (timerMode.value == PomodoroMode.POMODORO) {
                if (timerBreaksCount.value == 4) {
                    dataStoreRepository.saveToDataStore(
                        DataStoreRepository.KEY_TIMER_MODE,
                        PomodoroMode.LONG_BREAK.title
                    )
                    timerMode.value = PomodoroMode.LONG_BREAK

                    dataStoreRepository.saveToDataStore(
                        DataStoreRepository.KEY_TIMER_BREAKS_COUNT,
                        0
                    )
                    timerBreaksCount.value = 0
                } else {
                    dataStoreRepository.saveToDataStore(
                        DataStoreRepository.KEY_TIMER_MODE,
                        PomodoroMode.SHORT_BREAK.title
                    )
                    timerMode.value = PomodoroMode.SHORT_BREAK

                    dataStoreRepository.saveToDataStore(
                        DataStoreRepository.KEY_TIMER_BREAKS_COUNT,
                        timerBreaksCount.value + 1
                    )
                    timerBreaksCount.value = timerBreaksCount.value + 1
                }
            } else {
                dataStoreRepository.saveToDataStore(
                    DataStoreRepository.KEY_TIMER_MODE,
                    PomodoroMode.POMODORO.title
                )
                timerMode.value = PomodoroMode.POMODORO
            }
            currentTime.value = timerMode.value.timeInMillis
        }
    }

    private fun getModeByTitle(title: String): PomodoroMode {
        return when (title) {
            PomodoroMode.POMODORO.title -> PomodoroMode.POMODORO
            PomodoroMode.SHORT_BREAK.title -> PomodoroMode.SHORT_BREAK
            PomodoroMode.LONG_BREAK.title -> PomodoroMode.LONG_BREAK
            else -> PomodoroMode.POMODORO
        }
    }
}