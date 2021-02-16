package com.example.iamu.ui.main.torrent.service.downloader

import android.net.Uri
import com.example.iamu.ui.main.torrent.service.TorrentDownloadState
import com.github.se_bastiaan.torrentstream.StreamStatus
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import java.io.File

class TorrentDownloader(private val listener: TorrentDownloadListener) : Downloader {

    override fun download(torrentFile: String, destinationDirectory: String) {
        try {
            TorrentStream.init(
                TorrentOptions.Builder()
                    .saveLocation(destinationDirectory)
                    .build()
            )!!.apply {
                addListener(object : TorrentListener {
                    override fun onStreamPrepared(torrent: Torrent) {}
                    override fun onStreamReady(torrent: Torrent) {}

                    override fun onStreamStarted(torrent: Torrent) {
                        listener.onDownloadStart(torrentFile)
                    }

                    override fun onStreamError(torrent: Torrent, e: Exception) {
                        listener.onDownloadEnd(torrentFile, TorrentDownloadState.ERROR)
                    }

                    override fun onStreamProgress(torrent: Torrent, status: StreamStatus) {
                        listener.onDownloadProgress(torrentFile, status.progress)
                        if (status.progress == 100f) {
                            listener.onDownloadEnd(torrentFile, TorrentDownloadState.COMPLETED)
                        }
                    }

                    override fun onStreamStopped() {}
                })

                startStream(Uri.fromFile(File(torrentFile)).toString())
            }

        } catch (e: Exception) {
            listener.onDownloadEnd(torrentFile, TorrentDownloadState.ERROR)
        }
    }
}