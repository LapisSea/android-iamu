package com.example.iamu.ui.main.tabs

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.iamu.ui.main.db.FilePortal
import com.example.iamu.ui.main.torrent.TorrentManagement.Companion.listDownloads
import java.io.File
import java.util.function.Function

class RecentFragment() : FileFlexFragment(FilePortal())
class AllFragment() : FileFlexFragment(object : FilePortal() {
    override fun listFiles(context: Context): Array<File> {
        return listDownloads(context)
    }
})

enum class Tabs(private val make: () -> Fragment) {
    RECENT({ RecentFragment() }),
    DOWNLOADED({ AllFragment() }),
    SOURCES({ TorrentFileListFragment() });

    fun makeScreen(): Fragment {
        return make()
    }
}