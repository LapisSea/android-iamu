package com.example.iamu.ui.main.db

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import androidx.annotation.RequiresApi
import com.example.iamu.ui.main.db.model.DBFile
import java.io.File
import java.sql.Timestamp
import java.time.Instant

open class FilePortal() : IFileFetcher {
    private val handler = Handler(HandlerThread("PushDB", THREAD_PRIORITY_FOREGROUND).apply { start() }.looper)

    override fun listFiles(context: Context): Array<File> {
        val items = mutableListOf<File>()
        val deleted = mutableListOf<File>()
        val cursor = context.contentResolver?.query(
            FILE_PROVIDER_CONTENT_URI,
            null, null, null,
            DBFile::addTime.name + " DESC"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val f = File(cursor.getString(cursor.getColumnIndex(DBFile::name.name)))
                (if (f.exists()) items else deleted).add(f)
            }
            cursor.close()
        }
        handler.post {
            for (file in deleted) {
                deleteEntry(context, file)
            }
        }
        return items.toTypedArray()
    }

    override fun logFile(context: Context,file: File) {
        deleteEntry(context, file)
        handler.post {
            context.contentResolver.insert(
                FILE_PROVIDER_CONTENT_URI,
                ContentValues().apply {
                    put(DBFile::name.name, file.absolutePath)
                },
            )
        }
    }

    private fun deleteEntry(context: Context, file: File) {
        context.contentResolver.delete(
            FILE_PROVIDER_CONTENT_URI,
            DBFile::name.name + "=?",
            arrayOf(file.absolutePath)
        )
    }
}