package com.example.iamu.ui.main.db

import android.content.*
import android.database.Cursor
import android.net.Uri
import com.example.iamu.ui.main.db.model.DBFile
import java.lang.IllegalArgumentException

private const val AUTHORITY = "com.example.iamu.ui.main.db.api.provider"
private const val PATH = "items"
val FILE_PROVIDER_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")

private const val ITEMS = 10
private const val ITEM_ID = 20

private val URI_MATCHER: UriMatcher = with(UriMatcher(UriMatcher.NO_MATCH)) {
    addURI(AUTHORITY, PATH, ITEMS)
    addURI(AUTHORITY, "$PATH/#", ITEM_ID)
    this
}

private const val CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH
private const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH


class FileProvider : ContentProvider() {

    private lateinit var repo: FileRepo

    override fun onCreate(): Boolean {
        repo = SQLHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? =
        repo.query(projection, selection, selectionArgs, sortOrder)

    override fun getType(uri: Uri): String {
        when (URI_MATCHER.match(uri)) {
            ITEMS -> return CONTENT_DIR_TYPE
            ITEM_ID -> return CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Bad Uri: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = repo.insert(values)
        return ContentUris.withAppendedId(FILE_PROVIDER_CONTENT_URI, id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (URI_MATCHER.match(uri)) {
            ITEMS -> return repo.delete(selection, selectionArgs)
            ITEM_ID -> {
                val id = uri.lastPathSegment
                if (id != null) return repo.delete("${DBFile::id.name} = ?", arrayOf(id))
            }
        }
        throw IllegalArgumentException("Bad Uri: $uri")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        when (URI_MATCHER.match(uri)) {
            ITEMS -> return repo.update(values, selection, selectionArgs)
            ITEM_ID -> {
                val id = uri.lastPathSegment
                if (id != null) return repo.update(values, "${DBFile::id.name} = ?", arrayOf(id))
            }
        }
        throw IllegalArgumentException("Bad Uri: $uri")
    }
}