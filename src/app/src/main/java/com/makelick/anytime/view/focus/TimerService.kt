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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var timerRepository: TimerRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timeInMillis = intent?.getLongExtra("timeInMillis", 0) ?: 0

        timerRepository.startTimer(timeInMillis)
        createNotificationChannel()
        CoroutineScope(Dispatchers.Main).launch {
            timerRepository.currentTime.collect {
                if (it > 0) {
                    val notification = createNotification(
                        getString(R.string.timer_notification_content, it / 1000)
                    )
                    startForeground(1, notification)
                } else {
                    val notification = createNotification(
                        getString(R.string.notification_timer_finished),
                        true
                    )
                    startForeground(1, notification)
                    // TODO: Start next timer or stop service
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?) = null

    private fun createNotification(content: String, isFinal: Boolean = false): Notification {
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.graph)
            .setDestination(R.id.focusFragment)
            .createPendingIntent()


        return NotificationCompat.Builder(this, "TIMER_CHANNEL")
            .setContentTitle("Timer Service") // TODO: change to type title
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
            "TIMER_CHANNEL",
            "Timer Service",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for Timer Service"
        }

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}