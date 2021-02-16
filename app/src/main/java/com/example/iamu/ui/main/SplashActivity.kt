package com.example.iamu.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.iamu.R

private const val PERMISSION_CODE = 1000

class SplashActivity : AppCompatActivity() {
    private lateinit var startIcon: ImageView
    private var started = false
    private var activityVisible = true
    private var finishRequested = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startIcon = findViewById<ImageView>(R.id.start_icon)
        Utils.scaleZoomView(startIcon, 0F, 1F, 500, 1000)

        Utils.scaleZoomView(startIcon, 0F, 1F, 500, 1000)
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            runOnUiThread {
                if (activityVisible) startApp()
                else finishRequested=true
            }
        }, 2000)
    }

    private fun startApp() {
        if (started) return

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE)
            return
        }

        started = true

        startActivity(Intent(this, MainActivity::class.java))
        finish()
        overridePendingTransition( 0, R.anim.fade_out )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                startApp()
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activityVisible = true;
        if(finishRequested)startApp()
    }

    override fun onPause() {
        super.onPause()
        activityVisible = false;
    }
}