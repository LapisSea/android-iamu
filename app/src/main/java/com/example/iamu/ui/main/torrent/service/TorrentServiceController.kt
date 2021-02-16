package com.example.iamu.ui.main.torrent.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.iamu.ui.main.torrent.TorrentEntry
import com.example.iamu.ui.main.torrent.service.downloader.TorrentDownloadListener
import com.example.iamu.ui.main.torrent.service.intent.TorrentActionIntent
import com.example.iamu.ui.main.torrent.service.intent.TorrentDownloadRequest
import com.example.iamu.ui.main.torrent.service.intent.TorrentExtraDataIntent
import java.io.File

class TorrentServiceController(val context: Context) {

    @Volatile
    private var torrentDownloadListener: TorrentDownloadListener? = null

    private val progressBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val listener = torrentDownloadListener ?: return

            val torrentFile = intent.getStringExtra(TorrentExtraDataIntent.TORRENT_FILE.id)!!
            val progress = intent.getFloatExtra(TorrentExtraDataIntent.DOWNLOAD_PROGRESS.id, -1F)

            if (progress<-99) listener.onDownloadStart(torrentFile)
            else listener.onDownloadProgress(torrentFile, progress)
        }
    }
    private val endBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val listener = torrentDownloadListener ?: return

            val torrentFile = intent.getStringExtra(TorrentExtraDataIntent.TORRENT_FILE.id)!!
            val downloadState = TorrentDownloadState.values()[intent.getIntExtra(TorrentExtraDataIntent.DOWNLOAD_STATE.id, 0)]
            listener.onDownloadEnd(torrentFile, downloadState)
        }
    }

    fun registerDownloadListener(listener: TorrentDownloadListener) {
        torrentDownloadListener = listener

        LocalBroadcastManager.getInstance(context).registerReceiver(
            progressBroadcastReceiver,
            IntentFilter(TorrentActionIntent.BROADCAST_PROGRESS.id)
        )
        LocalBroadcastManager.getInstance(context).registerReceiver(
            endBroadcastReceiver,
            IntentFilter(TorrentActionIntent.BROADCAST_END.id)
        )
    }

    fun unregisterDownloadListener(listener: TorrentDownloadListener) {
        torrentDownloadListener = null
        LocalBroadcastManager.getInstance(context).unregisterReceiver(progressBroadcastReceiver)
        LocalBroadcastManager.getInstance(context).unregisterReceiver(endBroadcastReceiver)
    }

    fun enqueue(torrent: TorrentEntry) = enqueue(torrent.torrentFile, torrent.media)
    fun enqueue(torrentFile: File, destinationDir: File) = enqueue(Uri.fromFile(torrentFile), Uri.fromFile(destinationDir))
    fun enqueue(torrentFile: Uri, destinationDir: Uri) = enqueue(TorrentDownloadRequest().setTorrentFile(torrentFile).setDestinationDirectory(destinationDir))
    fun enqueue(torrentDownloadRequest: TorrentDownloadRequest) = context.startService(torrentDownloadRequest.createIntent(context))

}