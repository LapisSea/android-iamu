package com.example.iamu.ui.main

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.example.iamu.R
import java.io.File

enum class DownloadState(val strId: Int) {
    FINISHED(R.string.finished),
    FAILED(R.string.failed_to_download),
    STARTED(R.string.started);
}

val ntfChannelID = "channelMain"

class Iamu : Application() {

    override fun onCreate() {

        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val men = getSystemService(NotificationManager::class.java) as NotificationManager
            men.createNotificationChannel(NotificationChannel(ntfChannelID, "Main channel", NotificationManager.IMPORTANCE_DEFAULT))
        }
    }

    companion object {

        fun notifyDownloadStateUpdate(context: Context, file: File, updateType: DownloadState) {
            with(context) {
                val men = NotificationManagerCompat.from(this)

                val b = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, ntfChannelID) else Notification.Builder(this)

                men.notify(file.path.hashCode(), b
                    .setTicker("downloadStateUpdate$file$updateType")
                    .setContentTitle("Torrent " + getString(updateType.strId))
                    .setContentText("Torrent ${file.name} download has " + getString(updateType.strId))
                    .setSmallIcon(R.drawable.start_icon_small)
                    .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0))
                    .notification
                    .apply {
                        flags = Notification.FLAG_AUTO_CANCEL
                    })
            }
        }
    }
}