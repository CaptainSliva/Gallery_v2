package com.example.gallery.Functions

import android.app.AlertDialog
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.Size
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.example.gallery.CustomClasses.Picture
import com.example.gallery.PERMISSION_REQUEST_CODE
import com.example.gallery.startId
import java.io.File


class FunctionsMedia {
//    val start = 0
//    val stop = 10000

    fun getAllPictures(context: Context, bucketId: String, add: Int = 1000000000): List<Picture> {
        val mediaAlbm = getAllMedia(context, MediaStore.Files.getContentUri("external"), bucketId, add)
        println(mediaAlbm)
        return mediaAlbm
    }

    //    Для Android 10 (API 29) и выше рекомендуется использовать scoped storage.
//
//    Для больших коллекций медиафайлов выполняйте запрос в фоновом потоке.
//
//    В Android 11+ могут быть дополнительные ограничения на доступ к файлам.
//
//    Для получения миниатюр можно использовать MediaStore.Images.Thumbnails и MediaStore.Video.Thumbnails.

    private fun getAllMedia(context: Context, contentUri: Uri, bucketIdArg: String = "", stop: Int): MutableList<Picture> {
        val mediaFiles = mutableListOf<Picture>()
        var n = -1
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATA
        )
        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketIdArg)
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC" // DATE_MODIFIED // DATE_TAKEN

        context.applicationContext.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
            val dateAdded = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (cursor.moveToNext()) {
                if (n == stop) break
                val bucketId = cursor.getString(bucketIdColumn)
//                if (bucketIdArg != "") {
//                    if (bucketId == bucketIdArg) {
                val id = cursor.getLong(idColumn)

                when {
                    startId == 0L -> {
                        startId = id
                        n++
                    }
                    id == startId -> {
                        println("startid - $startId end - $n/$stop")
                        n++
                        continue
                    }
                }

                if (n in 0..stop) {
                    val path = cursor.getString(pathColumn)
                    val uri = ContentUris.withAppendedId(
                        contentUri,
                        id
                    )
                    val duration = cursor.getInt(durationColumn)
                    n++
                    if (duration > 0) {
                        val thumbnail = getThumbnailSafe(context, ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id))
                        println("video $n $id $uri, $path $bucketIdArg $dateAdded")
                        mediaFiles.add(Picture(bucketId, uri, path, thumbnail!!, duration.toString()))
                    }
                    else {
                        val thumbnail = getThumbnailSafe(context, ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id))
                        println("image $n $id $uri, $path $bucketId $dateAdded")
                        mediaFiles.add(Picture(bucketId, uri, path, thumbnail!!, ""))
                    }
                }
                if (n == stop) {
                    startId = id
                }
            }
        }

        return mediaFiles
    }

    inline fun getThumbnailSafe(context: Context, uri: Uri): Bitmap? {
        return try {
            // Попробуем сначала стандартный способ
            context.contentResolver.loadThumbnail(uri, Size(640, 480), null)
        } catch (e: Exception) {
            Log.w("ImageLoading", "Standard thumbnail loading failed, trying alternative")
            try {
                // Альтернативный способ через MediaStore.Images.Thumbnails
                val thumbUri = ContentUris.withAppendedId(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    ContentUris.parseId(uri)
                )
                MediaStore.Images.Thumbnails.getThumbnail(
                    context.contentResolver,
                    ContentUris.parseId(uri),
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                )
            } catch (e: Exception) {
                Log.w("ImageLoading", "Thumbnail loading failed completely")
                null
            }
        }
    }

    fun addPicturesFromUris(context: Context, uriList: List<Uri>): MutableList<Picture>{
        val mediaFiles = mutableListOf<Picture>()
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATA
        )
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        uriList.forEach { uri ->
            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
                    val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)

                    val id = cursor.getLong(idColumn)
                    val bucketId = cursor.getString(bucketIdColumn)
                    val path = cursor.getString(pathColumn)
                    val duration = cursor.getInt(durationColumn)

                    val thumbnail = if (duration > 0) {
                        getThumbnailSafe(context, ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id))
                    } else {
                        getThumbnailSafe(context, ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id))
                    }
                    mediaFiles.add(Picture(bucketId, uri, path, thumbnail!!, if (duration > 0) duration.toString() else ""))
                }
            }
        }
        return mediaFiles
    }


}