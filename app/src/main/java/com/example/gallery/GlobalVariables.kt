package com.example.gallery

import android.content.Context
import androidx.room.Room
import com.example.gallery.CustomClasses.Album
import com.example.gallery.CustomClasses.Picture
import com.example.gallery.Database.AppDatabase
import com.example.gallery.Database.CommentsDao

val PERMISSION_REQUEST_CODE = 100
var mainContext: MainActivity? = null
var allAlbums = listOf<Album>()
var listpicture = mutableListOf<Picture>()
var startId = 0L
const val versionDB = 1

fun connectToDB(context: Context): CommentsDao {
    val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "CommentsDB"
    ).build()
    return db.commentsDao()
}
