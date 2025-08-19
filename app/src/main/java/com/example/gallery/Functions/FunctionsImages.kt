package com.example.gallery.Functions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.MessageDigest

class FunctionsImages {

    fun md5(bitmap: Bitmap): String { // На вход идёт Bitmap изображения
        val byteBuffer = ByteBuffer.allocate(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(byteBuffer)
        val pixelData = byteBuffer.array()
        val md = MessageDigest.getInstance("MD5").apply {
            update(pixelData)
        }
        return BigInteger(1, md.digest()).toString(16).padStart(32, '0')
    }

    fun compressBitmap(bmp: Bitmap, size: Int): Bitmap {
        val stream = ByteArrayOutputStream()
        val dimension = bmp.width.coerceAtMost(bmp.height)
        if (android.os.Build.VERSION.SDK_INT < 30) {
            Bitmap.createScaledBitmap(ThumbnailUtils.extractThumbnail(bmp, dimension, dimension, ThumbnailUtils.OPTIONS_RECYCLE_INPUT), size, size, true).compress(
                Bitmap.CompressFormat.WEBP, 100, stream)
        } else {
            Bitmap.createScaledBitmap(ThumbnailUtils.extractThumbnail(bmp, dimension, dimension, ThumbnailUtils.OPTIONS_RECYCLE_INPUT), size, size, true).compress(
                Bitmap.CompressFormat.WEBP_LOSSLESS, 100, stream)
        }
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }

    fun isVideo(path: String): Boolean {
        val extension = path.substringAfterLast('.', "")
        return arrayOf("mp4", "avi", "mkv", "mov").contains(extension.lowercase())
    }

    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }


}

////Масштабирование изображений
////
////Используйте PhotoView для зума
////Добавьте в build.gradle:
//
////gradle
//
//implementation 'com.github.chrisbanes:PhotoView:2.3.0'
//
////Замените ImageView в разметке:
////xml
//
//<com.github.chrisbanes.photoview.PhotoView
//android:id="@+id/fullscreen_content"
//android:layout_width="match_parent"
//android:layout_height="match_parent"
//android:scaleType="fitCenter"/>
//
////Настройка масштабирования программно
////
////kotlin
//
//val photoView = findViewById<PhotoView>(R.id.fullscreen_content)
//photoView.setImageBitmap(yourBitmap)
//
//// Дополнительные настройки
//photoView.maximumScale = 5f
//photoView.mediumScale = 3f
//photoView.minimumScale = 1f
//photoView.scaleType = ImageView.ScaleType.CENTER_CROP
//
////Воспроизведение видео
////
////Добавьте VideoView в разметку
////
////xml
//

////Код для воспроизведения видео
////
////kotlin
//
//val videoView = findViewById<VideoView>(R.id.video_view)
//val mediaController = MediaController(this)
//mediaController.setAnchorView(videoView)
//
//videoView.apply {
//    setMediaController(mediaController)
//    setVideoURI(Uri.parse(videoPath))
//    requestFocus()
//    setOnPreparedListener { mp ->
//        mp.isLooping = true
//        videoView.start()
//    }
//    setOnErrorListener { _, _, _ ->
//        Toast.makeText(this@FullimageActivity, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show()
//        false
//    }
//}
//
////Определение типа контента
////
////kotlin
//

//

//
////Оптимизация работы с медиа
////
////Для видео используйте ExoPlayer (более современная альтернатива)
////
////gradle
////
////implementation 'com.google.android.exoplayer:exoplayer:2.19.1'
////
////Код для ExoPlayer
////
////kotlin
//
//val player = ExoPlayer.Builder(this).build()
//binding.playerView.player = player
//
//val mediaItem = MediaItem.fromUri(Uri.parse(videoPath))
//player.setMediaItem(mediaItem)
//player.prepare()
//player.playWhenReady = true
//
////Добавьте в разметку для ExoPlayer
//
////xml

////Переключение между изображениями и видео
////kotlin
//
//fun setupMedia(uri: String, duration: Int) {
//    if (duration > 0) { // Это видео
//        showVideo(uri)
//    } else { // Это изображение
//        showImage(uri)
//    }
//}
//
//private fun showVideo(uri: String) {
//    binding.playerView.visibility = View.VISIBLE
//    binding.fullscreenContent.visibility = View.GONE
//
//    player.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
//    player.prepare()
//    player.playWhenReady = true
//}
//
//private fun showImage(uri: String) {
//    binding.playerView.visibility = View.GONE
//    binding.fullscreenContent.visibility = View.VISIBLE
//
//    Glide.with(this)
//        .load(uri)
//        .transition(DrawableTransitionOptions.withCrossFade())
//        .into(binding.fullscreenContent)
//}
//
////Обработка поворота экрана
////kotlin
//
//override fun onConfigurationChanged(newConfig: Configuration) {
//    super.onConfigurationChanged(newConfig)
//
//    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//        // Настройки для альбомного режима
//        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
//    } else {
//        // Настройки для портретного режима
//        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
//    }
//}