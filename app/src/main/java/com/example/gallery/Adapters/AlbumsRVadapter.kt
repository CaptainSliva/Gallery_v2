package com.example.gallery.Adapters

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.CustomClasses.Album
import com.example.gallery.R
import com.example.gallery.databinding.AllAlbumsFragmentListBinding
import com.example.gallery.databinding.ItemAlbumBinding

class AlbumsRVadapter(private val listAlbums: List<Album>): RecyclerView.Adapter<AlbumsRVadapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root){
        val albumPreview = binding.ivAlbumPreview
        val albumName = binding.tvAlbumName
        val albumItems = binding.tvAlbumItemsCount

        override fun toString(): String {
            return """ALBUM
                image - ${albumPreview.background}
                name - ${albumName.text}
                count - ${albumItems.text}
                
            """
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listAlbums[position]
        holder.albumPreview.setImageBitmap(item.thumbnail)
        holder.albumName.text = item.name
        holder.albumItems.text = item.itemsCount.toString()

        val bundle = bundleOf(
            "bucketID" to item.bID,
            "albumName" to item.name,
            "amountOfItems" to item.itemsCount
        )
        holder.itemView.setOnClickListener {view ->
            view.findNavController().navigate(R.id.action_allAlbumsFragment_to_allPhotosFragment, bundle)
        }
    }

    override fun getItemCount(): Int = listAlbums.size

}