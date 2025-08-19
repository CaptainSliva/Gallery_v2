package com.example.gallery

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.Adapters.PhotosRVadapter
import com.example.gallery.CustomClasses.Picture
import com.example.gallery.Database.CommentsDao
import com.example.gallery.Functions.FunctionsApp
import com.example.gallery.Functions.FunctionsMedia
import com.example.gallery.Other.SpacingItemDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class SearchPhotoOnCommentFragment : Fragment() {

    lateinit var db: CommentsDao
    private var add = 20 // Количество элементов для подгрузки за раз
    private var isLoading = false
    private var hasMore = true
    var strictSearch = false

    var recyclerDataArrayList = mutableListOf<Picture>()
    lateinit var adapter: PhotosRVadapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_photo_on_comment_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvPhotos = view.findViewById<RecyclerView>(R.id.rv_find_photos)
        val etSearch: EditText = view.findViewById(R.id.idSearch)
        val btnExit: Button = view.findViewById(R.id.idExitBtn)
        val tvCountResults: TextView = view.findViewById(R.id.counter)
        val cbRegister: CheckBox = view.findViewById(R.id.idCbRegx)

        adapter = PhotosRVadapter(recyclerDataArrayList, requireContext())
        rvPhotos.adapter = adapter
        rvPhotos.addItemDecoration(SpacingItemDecoration(FunctionsApp().dpToPx(requireContext(), 1)))

        db = connectToDB(requireContext())

        etSearch.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length == 0) tvCountResults.text = "Найдено: 0"
            }
            override fun afterTextChanged(s: Editable) {
                searchPhotoOnStory(etSearch.text.toString(), tvCountResults)
            }
        })

        btnExit.setOnClickListener {
            clearAll(recyclerDataArrayList, adapter)
            etSearch.text.clear()
            tvCountResults.text = "Найдено: 0"
        }

        cbRegister.setOnClickListener {
            if (strictSearch) strictSearch = false
            else strictSearch = true
            searchPhotoOnStory(etSearch.text.toString(), tvCountResults)
        }

        // Загружаем первую порцию данных
//        addItems(bucketID, amountOfItems)
//
//        adapter.setOnLoadMoreListener(object: OnLoadMoreListener {
//            override fun onLoadMore() {
//                if (!isLoading && hasMore) {
//                    addItems(bucketID, amountOfItems)
//                }
//            }
//        })

    }

//    private fun addItems(bucketID: String, amountOfItems: Int) {
//        val rvSize = recyclerDataArrayList.size
//
//        println("amountOfItems $rvSize / $amountOfItems | $add")
//        if (isLoading) return
//        if (amountOfItems-rvSize < add) add = amountOfItems-rvSize
//
//        isLoading = true
//        adapter.setLoading(true) // Добавьте этот метод в адаптер
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val newItems = ionsMedia().getAllPictures(requireContext(), bucketID, add)
//            withContext(Dispatchers.Main) {
//                if (amountOfItems == rvSize) {
//                    hasMore = false
//                    adapter.setNoMore(true)
//                } else {
//                    val startPosition = recyclerDataArrayList.size
//                    recyclerDataArrayList.addAll(newItems)
//                    listpicture.addAll(newItems)
//                    adapter.notifyItemRangeInserted(startPosition, startPosition+newItems.size)
//                }
//                isLoading = false
//                adapter.setLoading(false)
//            }
//        }
//    }

    fun searchPhotoOnStory(s: String, tvCountResults: TextView) {
        clearAll(recyclerDataArrayList, adapter)
        if (s.isNotEmpty()) {
            var stop = 0
            CoroutineScope(Dispatchers.IO).launch {
                val listUri = when {
                    strictSearch -> db.findImageByRegisterComment(s)
                        .map { it.image_uri.toUri() }

                    else -> db.findImageByNoRegisterComment(s)
                        .map { it.image_uri.toUri() }
                }
                stop = listUri.size
                recyclerDataArrayList.addAll(
                    FunctionsMedia().addPicturesFromUris(
                        requireContext(),
                        listUri
                    )
                )
                listpicture = recyclerDataArrayList
                CoroutineScope(Dispatchers.Main).launch {
                    tvCountResults.text = "Найдено: ${stop}"
                    adapter.notifyItemRangeChanged(0, stop)
                }

//                    addItems(adapter, recyclerDataArrayList)
//                    addItems(adapter, recyclerDataArrayList)
                //addItems(adapter,recyclerDataArrayList, 0, 0)
                println("$stop $listpicture")
            }
        }
    }

    fun clearAll(recyclerDataArrayList: MutableList<Picture>, adapter: PhotosRVadapter)
    {
        recyclerDataArrayList.clear()
        adapter.notifyDataSetChanged()
    }
}