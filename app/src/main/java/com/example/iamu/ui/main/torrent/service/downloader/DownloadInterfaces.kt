package com.example.iamu.ui.main.torrent.service.downloader

import com.example.iamu.ui.main.torrent.service.TorrentDownloadState

interface Downloader {
    fun download(torrentFile: String, destinationDirectory: String)
}

interface TorrentDownloadListener {
    companion object {
        @JvmField
        val EMPTY_LISTENER: TorrentDownloadListener = object : TorrentDownloadListener {
            override fun onDownloadStart(torrentFile: String) {}
            override fun onDownloadProgress(torrentFile: String, progress: Float) {}
            override fun onDownloadEnd(torrentFile: String, torrentDownloadState: TorrentDownloadState) {}
        }
    }

    fun onDownloadStart(torrentFile: String)
    fun onDownloadProgress(torrentFile: String, progress: Float)
    fun onDownloadEnd(torrentFile: String, torrentDownloadState: TorrentDownloadState)
}
