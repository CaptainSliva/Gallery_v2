package com.example.gallery.Functions

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.BUCKET_ID
import android.util.Log
import com.example.gallery.CustomClasses.Album
import kotlin.math.log

class FunctionsAlbums {
    fun getListAlbums(context: Context): Set<Album> {
        val albums = mutableSetOf<Album>()
        val images = getListAlbumsImages(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val videos = getListAlbumsImages(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        val bidIm = images.map { it.bID }
        val bidVid = videos.map { it.bID }
        images.forEach { im ->
            videos.forEach { vid ->
                if (im.bID == vid.bID) {
                    im.itemsCount+=vid.itemsCount
                    albums.add(im)
                }
                when {
                    im.bID !in bidVid -> albums.add(im)
                    vid.bID !in bidIm -> albums.add(vid)
                }
            }
        }

        Log.i("album","$albums")

        return albums
    }

    inline private fun getListAlbumsImages(context: Context, contentUri: Uri): List<Album> {
        val albums = mutableListOf<Album>()
        var itemsCount = hashMapOf<String, Int>()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
        )
        val sortOrder = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} ASC"
        val uniqueAlbums = mutableListOf<String>()


        context.contentResolver.query(
            contentUri,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn)

                var count = 1
                if (itemsCount[bucketId] != null) {
                    count = itemsCount[bucketId]!!+1
                    albums.forEach {
                        if (it.bID == bucketId) it.itemsCount = count
                    }
                }
                itemsCount[bucketId] = count

                if (!uniqueAlbums.contains(bucketId)) {
                    uniqueAlbums.add(bucketId)
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val id = cursor.getLong(idColumn)
                    val uri = ContentUris.withAppendedId(
                        contentUri,
                        id
                    )
                    val name = cursor.getString(bucketNameColumn)


                    val thumbnail = FunctionsMedia().getThumbnailSafe(context, uri)

                    println("URI $uri")
                    println("id - $id\n")
                    println("bucketId = $bucketId")

                    println("name = $name")
                    println("thmb - $thumbnail")
                    albums.add(Album(bucketId, name, 1, thumbnail))

                }
            }
        }
        return albums
    }

}