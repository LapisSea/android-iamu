package com.example.iamu.ui.main.db

import android.content.Context
import java.io.File

interface IFileFetcher {
    fun listFiles(context: Context): Array<File>
    fun logFile(context: Context, file: File)
}