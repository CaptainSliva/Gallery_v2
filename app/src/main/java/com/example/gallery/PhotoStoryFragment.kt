package com.example.gallery

import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.gallery.Database.Comment
import com.example.gallery.Database.CommentsDao
import com.example.gallery.Functions.FunctionsImages
import com.example.gallery.Functions.FunctionsMedia
import com.example.gallery.databinding.FullscreenPictureFragmentBinding
import com.example.gallery.databinding.PhotoStoryFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoStoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoStoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: PhotoStoryFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var db: CommentsDao

    private var comment = ""
    private var mediaUri = "uri"
    private var newComment = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = PhotoStoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaUri = arguments?.getString("mediaUri").toString()
        val mediaPath = arguments?.getString("mediaPath")
        val mediaDur = arguments?.getString("mediaDur")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        db = connectToDB(binding.root.context)
        CoroutineScope(Dispatchers.IO).launch {
            comment = db.findImageByHash(FunctionsImages().md5(FunctionsMedia().getThumbnailSafe(requireContext(), mediaUri.toUri())!!))?.image_comment?:""
            if (comment.trim().length != 0) newComment = false
            activity?.runOnUiThread {
                binding.etHistory.setText(comment)
            }
        }
        Glide.with(this)
            .load(mediaUri)
            .into(binding.ivImage)
        binding.tvDate.text = mediaUri!!.toUri().encodedUserInfo

//        binding.ivImage.setOnClickListener {
//            val bundle = bundleOf(
//                "mediaUri" to mediaUri,
//                "mediaPath" to mediaPath,
//                "mediaDur" to mediaDur
//            )
//            findNavController().navigate(R.id.action_photoStoryFragment_to_fullscreenPicture, bundle)
//        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoStoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotoStoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        CoroutineScope(Dispatchers.IO).launch {
            println("stop")
            val etHistory = binding.etHistory.text.toString()
            val imageHash = FunctionsImages().md5(FunctionsMedia().getThumbnailSafe(binding.root.context, mediaUri.toUri())!!)
            println("$imageHash\n$etHistory\n")
            when {
                etHistory.trim().length == 0 -> db.deleteCommentByHash(imageHash)
                etHistory.trim().length != 0 && newComment -> db.addComment(Comment(0, mediaUri, imageHash, etHistory))
                comment != etHistory && !newComment -> db.replaceCommentByHash(imageHash, etHistory)
            }
        }
    }
}