package com.example.iamu.ui.main.torrent.service

import com.example.iamu.ui.main.torrent.service.intent.DownloadProgressBroadcast.Companion.createProgressIntent
import com.example.iamu.ui.main.torrent.service.intent.DownloadEndBroadcast.Companion.createIntent
import android.app.IntentService
import android.content.Context
import com.example.iamu.ui.main.torrent.service.downloader.Downloader
import android.content.Intent
import com.example.iamu.ui.main.torrent.service.downloader.TorrentDownloader
import com.example.iamu.ui.main.torrent.service.downloader.TorrentDownloadListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.iamu.ui.main.DownloadState
import com.example.iamu.ui.main.Iamu
import com.example.iamu.ui.main.torrent.service.intent.TorrentActionIntent
import com.example.iamu.ui.main.torrent.service.intent.TorrentExtraDataIntent
import java.io.File

class TorrentService : IntentService(TorrentService::class.java.simpleName) {

    private val downloader: Downloader = TorrentDownloader(object : TorrentDownloadListener {
        override fun onDownloadStart(torrentFile: String) {
            Iamu.notifyDownloadStateUpdate(this@TorrentService, File(torrentFile), DownloadState.STARTED)
            broadcast(createProgressIntent(torrentFile, -100F))
        }

        override fun onDownloadProgress(torrentFile: String, progress: Float) {
            broadcast(createProgressIntent(torrentFile, progress))
        }

        override fun onDownloadEnd(torrentFile: String, torrentDownloadState: TorrentDownloadState) {
            Iamu.notifyDownloadStateUpdate(
                this@TorrentService, File(torrentFile),
                if (torrentDownloadState == TorrentDownloadState.COMPLETED) DownloadState.FINISHED else DownloadState.FAILED
            )
            broadcast(createIntent(torrentFile, torrentDownloadState))
        }

        private fun broadcast(intent: Intent) {
            LocalBroadcastManager.getInstance(this@TorrentService).sendBroadcast(intent)
        }
    })

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            TorrentActionIntent.START_DOWNLOAD.id -> {
                val torrentFile = intent.getStringExtra(TorrentExtraDataIntent.TORRENT_FILE.id)
                val destinationDirectory = intent.getStringExtra(TorrentExtraDataIntent.DESTINATION_DIRECTORY.id)
                downloader.download(torrentFile!!, destinationDirectory!!)
            }
        }
    }
}