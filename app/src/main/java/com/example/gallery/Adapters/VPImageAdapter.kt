package com.example.gallery.Adapters

import android.app.RecoverableSecurityException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gallery.CustomClasses.Picture
import com.example.gallery.Functions.FunctionsImages
import com.example.gallery.Functions.FunctionsMedia
import com.example.gallery.Other.ClickReceiver
import com.example.gallery.R
import com.example.gallery.connectToDB
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VPImageAdapter(private val listPicture: List<Picture>, private val clickListener: ClickReceiver): RecyclerView.Adapter<VPImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoView: PlayerView = itemView.findViewById(R.id.video_view)
        val photoView: PhotoView = itemView.findViewById(R.id.image_view)
//        val ibRotate: ImageButton = itemView.findViewById(R.id.ib_rotate)
//        val ibComment: ImageButton = itemView.findViewById(R.id.ib_comment)
//        val clTouchView: View = itemView.findViewById(R.id.fullscreen_content_controls)
//        val ibDelete: ImageButton = itemView.findViewById(R.id.ib_delete)
//        val ibShare: ImageButton = itemView.findViewById(R.id.ib_share)
//        val textStory: TextView = itemView.findViewById(R.id.tv_story)
//        val menu: ConstraintLayout = itemView.findViewById(R.id.second_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fullscreen_content_view_fragment, parent, false)
        return ViewHolder((view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPicture[position]

        if (FunctionsImages().isVideo(item.path)) {
            holder.videoView.visibility = View.VISIBLE
            holder.photoView.visibility = View.GONE
            val player = ExoPlayer.Builder(holder.videoView.context).build()
            holder.videoView.player = player

            val mediaItem = MediaItem.fromUri(item.uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
            holder.videoView.setOnClickListener {
                clickListener.onItemClick() }
        } else {
            holder.videoView.visibility = View.GONE
            holder.photoView.visibility = View.VISIBLE
            Glide.with(holder.photoView.context)
                .load(item.uri)
                .into(holder.photoView)
            holder.photoView.setOnClickListener {
                clickListener.onItemClick() }
        }

//        holder.clTouchView.setOnClickListener {
//            if (visible) {
//                holder.menu.visibility = View.GONE
//                visible = false
//            } else {
//                holder.menu.visibility = View.VISIBLE
//                visible = true
//            }
//
//        }
    }

    override fun getItemCount(): Int = listPicture.size

}