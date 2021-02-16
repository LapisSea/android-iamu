package com.example.iamu.ui.main

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.example.iamu.R
import com.example.iamu.ui.main.torrent.TorrentEntry
import com.example.iamu.ui.main.torrent.TorrentManagement
import com.example.iamu.ui.main.torrent.service.TorrentServiceController
import java.io.File


class AddTorrentActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000


    private lateinit var lblStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_file_render)

        lblStatus = findViewById(R.id.lblStatus)

        handleIntent()
    }

    private fun handleIntent() {

        val uri: Uri? = intent.data
        if (uri == null) {
            couldNotOpenFilePopup()
            return
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE)
        } else get()

    }

    private fun get() {
        val uri = intent.data!!

        fun fileFromName(name: String): File {
            val torrentsLoc = TorrentManagement.getTorrentRoot(this)
            torrentsLoc.mkdirs()
            var torrentLoc: File;
            var num = 0;
            while (true) {
                torrentLoc = File(torrentsLoc, (if (num == 0) "" else "$num - ") + name)
                if (!torrentLoc.exists()) return torrentLoc
                num += 1
            }
        }

        fun getFileName(uri: Uri): String {
            var result: String? = null
            if (uri.scheme == "content") {
                contentResolver.query(uri, null, null, null, null).use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result!!.substring(cut + 1)
                }
            }
            return result!!
        }

        val name = uri.pathSegments.last();


        when (uri.scheme) {
            "http", "https" -> download(uri, name, fileFromName(name));
            "content" -> {
                val file = fileFromName(getFileName(uri))
                contentResolver.openInputStream(uri).use { inp ->
                    if (inp != null) {
                        file.outputStream().use { inp.copyTo(it) }
                    }
                }
                runOnUiThread { lblStatus.setText(R.string.copied) }
                success(file)
            }
            "file" -> {
                val file = fileFromName(name)
                uri.toFile().inputStream().use { inp ->
                    file.outputStream().use { inp.copyTo(it) }
                }
                runOnUiThread { lblStatus.setText(R.string.copied) }
                success(file)
            }
            else -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setMessage(uri.scheme)
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ -> finish() }
                val alert: AlertDialog = builder.create()
                alert.show()
            }
        }
    }

    private fun download(uri: Uri, name: String, torrentLoc: File) {

        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setDestinationUri(Uri.fromFile(torrentLoc))
        request.setTitle("Downloading torrent: $name")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            var downloading = true;
            while (downloading) {
                Thread.sleep(30)
                val c: Cursor = manager.query(DownloadManager.Query().setFilterById(id))
                if (c.moveToFirst()) {
                    val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        runOnUiThread { lblStatus.setText(R.string.DOWNLOADED) }
                        Thread.sleep(1000)
                        handler.post { success(torrentLoc) }
                        downloading = false
                    }
                    if (status == DownloadManager.STATUS_FAILED) {
                        runOnUiThread {
                            lblStatus.text = getString(R.string.failed_to_download)

                            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                            builder.setMessage(R.string.could_not_get_file_data)
                                .setCancelable(false)
                                .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                                    finish()
                                })
                            val alert: AlertDialog = builder.create()
                            alert.show()
                        }
                        downloading = false
                    }
                }
            }
        }
    }

    private fun success(torrentFile: File) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        TorrentServiceController(this).enqueue(TorrentEntry(this, torrentFile))
    }

    private fun couldNotOpenFilePopup() {
        Toast.makeText(this, getString(R.string.could_not_open_file), Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                    get()
                } else {
                    couldNotOpenFilePopup();
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}