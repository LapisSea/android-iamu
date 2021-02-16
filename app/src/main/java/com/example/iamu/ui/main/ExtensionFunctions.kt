package com.example.iamu.ui.main

import android.content.Context
import android.content.res.Resources
import android.webkit.MimeTypeMap
import com.example.iamu.ui.main.db.FILE_PROVIDER_CONTENT_URI
import com.example.iamu.ui.main.db.model.DBFile
import java.io.File
import java.sql.Timestamp

fun Context.stringResByName(resIdName: String?) =
    getString(resIdByName(resIdName, "string"))

fun Context.resIdByName(resIdName: String?, resType: String): Int {
    resIdName?.let {
        return resources.getIdentifier(it, resType, packageName)
    }
    throw Resources.NotFoundException()
}

fun File.mimeType() = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)