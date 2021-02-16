package com.example.iamu.ui.main.torrent

import android.content.Context
import java.io.File

class TorrentEntry(context: Context, val torrentFile: File) {
    val media: File by lazy { TorrentManagement.getTorrentMediaRoot(context, torrentFile) }
    fun calcTorrentName() = torrentFile.nameWithoutExtension
}