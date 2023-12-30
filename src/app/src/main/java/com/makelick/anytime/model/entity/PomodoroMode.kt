package com.makelick.anytime.model.entity

enum class PomodoroMode(val title: String, val timeInMillis: Long) {
    POMODORO("Pomodoro", 25 * 60 * 1000L),
    SHORT_BREAK("Short Break", 5 * 60 * 1000L),
    LONG_BREAK("Long Break", 15 * 60 * 1000L)
}
