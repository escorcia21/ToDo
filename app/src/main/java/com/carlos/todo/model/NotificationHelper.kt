package com.carlos.todo.model

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.carlos.todo.R


const val notiId = 1
const val channelID = "chanel 1"
const val titleExtra = "titleExtra"
const val smsExtra = "mensaje"
class NotificationHelper: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification  = NotificationCompat.Builder(context,channelID)
            .setSmallIcon(R.drawable.sharp_notifications_active_24)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(smsExtra))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notiId,notification)
    }
}