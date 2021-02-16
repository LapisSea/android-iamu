package com.example.iamu.ui.main.torrent.service.intent

enum class TorrentActionIntent {
    START_DOWNLOAD,
    BROADCAST_END,
    BROADCAST_PROGRESS;

    val id: String = "${this.javaClass.name}.${this.name}"
}

enum class TorrentExtraDataIntent {
    TORRENT_FILE,
    DESTINATION_DIRECTORY,
    DOWNLOAD_STATE,
    DOWNLOAD_PROGRESS;

    val id: String = "${this.javaClass.name}.${this.name}"
}