package com.example.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.Adapters.AlbumsRVadapter
import com.example.gallery.Adapters.PhotosRVadapter
import com.example.gallery.CustomClasses.Album
import com.example.gallery.CustomClasses.Picture
import com.example.gallery.Functions.FunctionsAlbums
import com.example.gallery.Functions.FunctionsApp
import com.example.gallery.Other.RecyclerTouchListener
import com.example.gallery.Other.SpacingItemDecoration
import com.example.gallery.databinding.AllAlbumsFragmentListBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AllAlbumsFragment : Fragment() {

    private var _binding: AllAlbumsFragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = AllAlbumsFragmentListBinding.inflate(inflater, container, false)
        return binding.root

        //Во фрагментах UI рисуется в onViewCreated !!!111

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listpicture.clear()
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
//
//        }
        startId = 0
        val rvAlbums = binding.rvAlbums

        //TEST
//        val listAlbums = mutableListOf<Album>()
//        val a = binding.root.context.getDrawable(R.drawable.kricun)!!.toBitmap()
//        for (i in 0..100) {
//            listAlbums.add(Album("name", Random.nextInt(100), a))
//        }
//        rvAlbums.adapter = AlbumsRVadapter(listAlbums)
//        rvAlbums.addItemDecoration(SpacingItemDecoration(FunctionsApp().dpToPx(binding.root.context, 1)))
//        rvAlbums.adapter?.notifyDataSetChanged()
        allAlbums = allAlbums.ifEmpty { FunctionsAlbums().getListAlbums(binding.root.context).toList() }
        rvAlbums.adapter = AlbumsRVadapter(allAlbums)
        rvAlbums.addItemDecoration(SpacingItemDecoration(FunctionsApp().dpToPx(binding.root.context, 1)))
        rvAlbums.adapter?.notifyDataSetChanged()

//        rvAlbums.addOnItemTouchListener(
//            RecyclerTouchListener(binding.root.context, rvAlbums, object: RecyclerTouchListener.ClickListener {
//                override fun onClick(view: View?, position: Int) {
//                        findNavController().navigate(R.id.action_allAlbumsFragment_to_allPhotosFragment)
//                }
//
//                override fun onLongClick(view: View?, position: Int) {
//                    TODO("Not yet implemented")
//                }
//            })
//        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}