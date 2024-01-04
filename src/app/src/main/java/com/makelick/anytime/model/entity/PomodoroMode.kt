package com.makelick.anytime.model.entity

import com.makelick.anytime.model.TimerRepository.Companion.LONG_BREAK_TIME
import com.makelick.anytime.model.TimerRepository.Companion.POMODORO_TIME
import com.makelick.anytime.model.TimerRepository.Companion.SHORT_BREAK_TIME

enum class PomodoroMode(val title: String, val timeInMillis: Long) {
    POMODORO("Pomodoro", POMODORO_TIME),
    SHORT_BREAK("Short Break", SHORT_BREAK_TIME),
    LONG_BREAK("Long Break", LONG_BREAK_TIME)
}
