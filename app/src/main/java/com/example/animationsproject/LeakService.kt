package com.example.animationsproject

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.concurrent.thread

class LeakService : Service() {
    companion object{
        const val NOTIFICATION_ID = 1
        const val PROGRESS_MAX = 10
        const val CANCEL_ACTION = "Cancel"
    }
    private lateinit var thread: Thread
    private var isRunning = false
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == CANCEL_ACTION) {
            Log.d("cancel","Cancel")
            stopSelf()
        }
        if(!isRunning){
            Log.d("service","RUNNING")
            isRunning = true
            val cancelIntent = Intent(this,LeakService::class.java).apply {
                action = CANCEL_ACTION
            }

            val cancelPendingIntent = PendingIntent.getService(this,0,cancelIntent,PendingIntent.FLAG_CANCEL_CURRENT)

            val builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setContentTitle("Взлом")
                .setContentText("Джокер пытается взломать ваш телефон, отмените это немедленно!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.ic_launcher_foreground,"Отменить",cancelPendingIntent)

            NotificationManagerCompat.from(this).apply {
                builder.setProgress(PROGRESS_MAX,0,false)
                startForeground(NOTIFICATION_ID,builder.build())
                thread = thread {
                    var counter = 0
                    while(counter <= PROGRESS_MAX && isRunning) {
                        try {
                            ++counter
                            if(counter == PROGRESS_MAX){
                                builder.apply {
                                    setContentText("Вы взломаны!")
                                    clearActions()
                                    setProgress(0,0,false)
                                }
                                notify(NOTIFICATION_ID,builder.build())
                                Thread.sleep(5000L)
                            }
                            else {
                                Thread.sleep(2000L)
                                builder.setProgress(PROGRESS_MAX, counter, false)
                                notify(NOTIFICATION_ID, builder.build())
                            }
                        }
                        catch (e: InterruptedException){
                            isRunning = false
                            Thread.currentThread().interrupt()
                        }
                    }
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("cancel","onDestroy")
        isRunning = false
        thread.interrupt()
    }
}