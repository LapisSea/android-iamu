package com.example.iamu.ui.main.torrent

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.example.iamu.R
import com.example.iamu.ui.main.SETTINGS_SHARED_PREFERENCES_FILE_NAME
import java.io.File

class TorrentManagement {
    companion object {
        private const val root = "iamu"
        private const val torrentRoot = "torrents/"
        private const val mediaRoot = "media/"

        fun listTorrentFiles(context: Context): Array<File> {
            val root = getTorrentRoot(context)
            if (!root.exists()) return arrayOf()
            return root.listFiles()!!
        }

        fun listTorrentMediaFiles(context: Context,torrent: File): Array<File> {
            val root = getMediaRoot(context)
            if (!root.exists()) return arrayOf()
            return root.listFiles()!!
        }


        private fun getRoot(context: Context): File {
            val preferences: SharedPreferences = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
            val customRoot = preferences.getString(context.getString(R.string.root_directory_key), null) ?: root
            return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), customRoot)
        };
        fun getTorrentRoot(context: Context): File {
            return File(getRoot(context), torrentRoot)
        }

        fun getMediaRoot(context: Context): File {
            return File(getRoot(context), mediaRoot)
        }

        fun getTorrentMediaRoot(context: Context,torrent: File) = File(getMediaRoot(context), torrent.nameWithoutExtension)

        @JvmStatic
        fun listDownloads(context: Context): Array<File> {
            fun list(file: File, dest: MutableList<File>) {
                val folders = mutableListOf<File>()

                for (f in file.listFiles() ?: return) {
                    if (f.isDirectory) folders.add(f)
                    else if(!f.path.endsWith(".parts")) dest.add(f)
                }
                for (folder in folders) {
                    list(folder, dest)
                }
            }

            val files = mutableListOf<File>()
            list(getMediaRoot(context), files)
            return files.toTypedArray()

//            return IntRange(0, 100).mapIndexed {i,t->
//                "$i.torrent"
//            }.map { File(it) }.toTypedArray()
//            return IntRange(0, 100).map {
//                IntRange(0, 30).map { CharRange('0','z').random() }.joinToString { it.toString() } + ".torrent"
//            }.map { File(it) }.toTypedArray()
        }
    }

}