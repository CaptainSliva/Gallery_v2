package com.example.gallery.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.databinding.ItemThumbnailBinding
import com.example.gallery.CustomClasses.Picture
import com.example.gallery.Other.OnLoadMoreListener
import com.example.gallery.R

class PhotosRVadapter(private val listThumbnails: List<Picture>, context: Context): RecyclerView.Adapter<PhotosRVadapter.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading = false
    private var noMore = false

    inner class ViewHolder(binding: ItemThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {
        val thumbnail = binding.ivThumbnail
        val timline = binding.tvTiming

        override fun toString(): String {
            return """IMAGE
                ${thumbnail.background}
                ${timline.text}
            """
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == listThumbnails.size - 1 && isLoading && !noMore) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemThumbnailBinding.inflate(
                LayoutInflater.from(parent.context),
                    parent,
                    false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listThumbnails[position]
        holder.thumbnail.setImageBitmap(item.thumbnail)
        holder.timline.text = item.duration
        if (item.duration.isNotEmpty()) {
            holder.timline.text = "${item.duration.toInt()/1000}"
        }

        holder.itemView.setOnClickListener { view ->
            val bundle = bundleOf(
                        "mediaUri" to item.uri.toString(),
                        "mediaPath" to item.path,
                        "mediaDur" to item.duration,
                        "positionPicture" to position
                    )
            println(item)
            try {
                view.findNavController().navigate(R.id.action_allPhotosFragment_to_fullscreenPicture, bundle)
            }catch (e:Exception){
                view.findNavController().navigate(R.id.action_searchPhotoOnCommentFragment_to_fullscreenPicture, bundle)
            }

        }
        if (position == listThumbnails.size - 1 && !isLoading && !noMore) {
            isLoading = true
            onLoadMoreListener?.onLoadMore()
            println("more")
        }
    }

    override fun getItemCount(): Int = listThumbnails.size

    fun setOnLoadMoreListener(listener: OnLoadMoreListener?) {
        this.onLoadMoreListener = listener
    }
    fun endLoading() {
        this.isLoading = false
    }
    fun setNoMore(noMore: Boolean) {
        this.noMore = noMore
    }

    fun setLoading(loading: Boolean) {
        if (isLoading != loading) {
            isLoading = loading
            if (loading) {
                notifyItemInserted(listThumbnails.size)
            } else {
                notifyItemRemoved(listThumbnails.size)
            }
        }
    }
}