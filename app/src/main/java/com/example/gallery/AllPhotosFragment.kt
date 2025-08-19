package com.example.gallery

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.gallery.Adapters.PhotosRVadapter
import com.example.gallery.databinding.AllPhotosFragmentListBinding
import com.example.gallery.CustomClasses.Picture
import com.example.gallery.Functions.FunctionsApp
import com.example.gallery.Functions.FunctionsImages
import com.example.gallery.Functions.FunctionsMedia
import com.example.gallery.Other.OnLoadMoreListener
import com.example.gallery.Other.SpacingItemDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AllPhotosFragment : Fragment() {

    private var _binding: AllPhotosFragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var add = 20 // Количество элементов для подгрузки за раз
    private var isLoading = false
    private var hasMore = true

    var recyclerDataArrayList = mutableListOf<Picture>()
    lateinit var adapter: PhotosRVadapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = AllPhotosFragmentListBinding.inflate(inflater, container, false)
        return binding.root

        //Во фрагментах UI рисуется в onViewCreated !!!111

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bucketID = arguments?.getString("bucketID")!!
        val albumName = arguments?.getString("albumName")!!
        val amountOfItems = arguments?.getInt("amountOfItems")!!

        val rvPhotos = binding.rvPhotos


        adapter = PhotosRVadapter(recyclerDataArrayList, requireContext())
        rvPhotos.adapter = adapter
        rvPhotos.addItemDecoration(SpacingItemDecoration(FunctionsApp().dpToPx(binding.root.context, 1)))

        // Загружаем первую порцию данных
        addItems(bucketID, amountOfItems)

        adapter.setOnLoadMoreListener(object: OnLoadMoreListener {
            override fun onLoadMore() {
                if (!isLoading && hasMore) {
                    addItems(bucketID, amountOfItems)
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addItems(bucketID: String, amountOfItems: Int) {
        val rvSize = recyclerDataArrayList.size

        println("amountOfItems $rvSize / $amountOfItems | $add")
        if (isLoading) return
        if (amountOfItems-rvSize < add) add = amountOfItems-rvSize

        isLoading = true
        adapter.setLoading(true) // Добавьте этот метод в адаптер

        CoroutineScope(Dispatchers.IO).launch {
            val newItems = FunctionsMedia().getAllPictures(requireContext(), bucketID, add)
            withContext(Dispatchers.Main) {
                if (amountOfItems == rvSize) {
                    hasMore = false
                    adapter.setNoMore(true)
                } else {
                    val startPosition = recyclerDataArrayList.size
                    recyclerDataArrayList.addAll(newItems)
                    listpicture.addAll(newItems)
                    adapter.notifyItemRangeInserted(startPosition, startPosition+newItems.size)
                }
                isLoading = false
                adapter.setLoading(false)
            }
        }
    }
}