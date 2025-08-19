package com.example.gallery.Permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gallery.PERMISSION_REQUEST_CODE


class RequestPermissions(private val activity: Activity) {
    fun checkPermissions() {
        if (!hasPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_MEDIA_VIDEO,
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
            else {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        else {
            ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

    }
}