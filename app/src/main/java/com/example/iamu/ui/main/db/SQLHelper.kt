package com.example.iamu.ui.main.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.iamu.ui.main.db.model.DBFile

private const val NAME = "iamu.files.db"
private const val VERSION_MAJOR = 1

private const val TB_NAME = "file"

private val CONSTRUCT_TABLE =
    "create table $TB_NAME(" +
            "${DBFile::id.name} integer primary key autoincrement, " +
            "${DBFile::name.name} text not null unique," +
            "${DBFile::addTime.name} Timestamp default CURRENT_TIMESTAMP not null" +
            ")"
private const val DESTRUCT_TABLE = "drop table $TB_NAME"

interface FileRepo {
    fun delete(selection: String?, selectionArgs: Array<String>?): Int
    fun update(values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int
    fun insert(values: ContentValues?): Long
    fun query(projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?) : Cursor?
}

class SQLHelper(context: Context?) : SQLiteOpenHelper(context, NAME, null, VERSION_MAJOR), FileRepo {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CONSTRUCT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DESTRUCT_TABLE)
        db?.execSQL(CONSTRUCT_TABLE)
    }

    override fun delete(selection: String?, selectionArgs: Array<String>?) = writableDatabase.delete(TB_NAME, selection, selectionArgs)
    override fun update(values: ContentValues?, selection: String?, selectionArgs: Array<String>?) = writableDatabase.update(TB_NAME, values, selection, selectionArgs)
    override fun insert(values: ContentValues?) = writableDatabase.insert(TB_NAME, null, values)
    override fun query(projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?) :Cursor?=
        writableDatabase.query(TB_NAME, projection, selection , selectionArgs,null,null , sortOrder)

}