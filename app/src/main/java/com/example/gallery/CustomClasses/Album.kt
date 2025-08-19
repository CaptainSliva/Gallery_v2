package com.example.gallery.CustomClasses

import android.graphics.Bitmap
import android.net.Uri

data class Album(
    val bID: String,
    val name: String,
    var itemsCount: Int, // TODO надо будет учесть
    val thumbnail: Bitmap?
)
