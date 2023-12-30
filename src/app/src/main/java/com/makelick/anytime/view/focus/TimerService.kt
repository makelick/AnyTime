package com.makelick.anytime.view.focus

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.makelick.anytime.R
import com.makelick.anytime.model.TimerRepository
import com.makelick.anytime.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var timerRepository: TimerRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        timerRepository.startTimer(timerRepository.timerMode.value.timeInMillis)
        createNotificationChannel()
        CoroutineScope(Dispatchers.Main).launch {
            timerRepository.currentTime.collect {
                if (it > 0) {
                    val notification = createNotification(
                        getString(R.string.timer_notification_content, getStringTime(it))
                    )
                    startForeground(1, notification)
                } else {
                    val notification = createNotification(
                        getString(R.string.notification_timer_finished),
                        true
                    )
                    getSystemService(NotificationManager::class.java).notify(2, notification)
                    timerRepository.nextMode()
                    timerRepository.stopTimer()
                    delay(1000)
                    timerRepository.startTimer(timerRepository.timerMode.value.timeInMillis)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?) = null

    private fun getStringTime(time: Long): String {
        val minutes = (time / 1000) / 60
        val seconds = (time / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun createNotification(
        content: String,
        isFinal: Boolean = false
    ): Notification {
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.graph)
            .setDestination(R.id.focusFragment)
            .createPendingIntent()


        return NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_NAME)
            .setContentTitle(timerRepository.timerMode.value.title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_focus)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(pendingIntent)
            .setSilent(!isFinal)
            .setOngoing(!isFinal)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATIONS_CHANNEL_NAME,
            TimerRepository.KEY_POMODORO,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.timer_notification_channel_description)
        }

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val NOTIFICATIONS_CHANNEL_NAME = "TIMER_CHANNEL"
    }
}