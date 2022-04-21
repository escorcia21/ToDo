package com.carlos.todo.model

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.carlos.todo.R


const val notiId = 1
const val channelID = "chanel 1"
const val titleExtra = "Recordatorio"
const val smsExtra = "mensaje"
class NotificationHelper: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification  = NotificationCompat.Builder(context,channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setColorized(true)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(smsExtra))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notiId,notification)
    }
}