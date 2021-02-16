package com.example.iamu.ui.main.torrent.service.intent

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.iamu.ui.main.torrent.service.TorrentDownloadState
import com.example.iamu.ui.main.torrent.service.TorrentService


class DownloadEndBroadcast {
    private var torrentFile: String? = null
    private var torrentDownloadState: TorrentDownloadState? = null
    fun setTorrentFile(torrentFile: String?): DownloadEndBroadcast {
        this.torrentFile = torrentFile
        return this
    }

    fun setDownloadState(downloadState: TorrentDownloadState?): DownloadEndBroadcast {
        this.torrentDownloadState = downloadState
        return this
    }

    fun createIntent(): Intent {
        val intent = Intent(TorrentActionIntent.BROADCAST_END.id)
        intent.putExtra(TorrentExtraDataIntent.TORRENT_FILE.id, torrentFile)
        intent.putExtra(TorrentExtraDataIntent.DOWNLOAD_STATE.id, torrentDownloadState!!.ordinal)
        return intent
    }

    companion object {
        @JvmStatic
        fun createIntent(torrentFile: String?, torrentDownloadState: TorrentDownloadState?): Intent {
            return DownloadEndBroadcast()
                .setTorrentFile(torrentFile)
                .setDownloadState(torrentDownloadState)
                .createIntent()
        }
    }
}

class DownloadProgressBroadcast {
    private lateinit var torrentFile: String
    private var progress = 0F
    fun setTorrentFile(torrentFile: String): DownloadProgressBroadcast {
        this.torrentFile = torrentFile
        return this
    }

    fun setProgress(progress: Float): DownloadProgressBroadcast {
        this.progress = progress
        return this
    }

    fun createIntent(): Intent {
        val intent = Intent(TorrentActionIntent.BROADCAST_PROGRESS.id)
        intent.putExtra(TorrentExtraDataIntent.TORRENT_FILE.id, torrentFile)
        intent.putExtra(TorrentExtraDataIntent.DOWNLOAD_PROGRESS.id, progress)
        return intent
    }

    companion object {
        @JvmStatic
        fun createProgressIntent(torrentFile: String, progress: Float): Intent {
            return DownloadProgressBroadcast()
                .setTorrentFile(torrentFile)
                .setProgress(progress)
                .createIntent()
        }
    }
}

class TorrentDownloadRequest {
    private var torrentFile: Uri? = null
    private var destinationDirectory: Uri? = null
    fun setTorrentFile(torrentFile: Uri?): TorrentDownloadRequest {
        this.torrentFile = torrentFile
        return this
    }

    fun setDestinationDirectory(destinationDirectory: Uri?): TorrentDownloadRequest {
        this.destinationDirectory = destinationDirectory
        return this
    }

    fun createIntent(context: Context?): Intent {
        checkNotNull(torrentFile) { "torrentFile must not be null" }
        checkNotNull(destinationDirectory) { "destinationDirectory must not be null" }
        val intent = Intent(context, TorrentService::class.java)
        intent.action = TorrentActionIntent.START_DOWNLOAD.id
        intent.putExtra(TorrentExtraDataIntent.TORRENT_FILE.id, torrentFile!!.path)
        intent.putExtra(TorrentExtraDataIntent.DESTINATION_DIRECTORY.id, destinationDirectory!!.path)
        return intent
    }
}
