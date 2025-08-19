package com.example.gallery.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gallery.versionDB

@Database(entities = [
    Comment::class
                     ],
    version = versionDB
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun commentsDao(): CommentsDao
}