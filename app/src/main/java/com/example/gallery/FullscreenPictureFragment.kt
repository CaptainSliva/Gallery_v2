package com.example.gallery

import android.app.RecoverableSecurityException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.gallery.Adapters.VPImageAdapter
import com.example.gallery.CustomClasses.Picture
import com.example.gallery.Database.CommentsDao
import com.example.gallery.Functions.FunctionsImages
import com.example.gallery.Functions.FunctionsMedia
import com.example.gallery.Other.ClickReceiver
import com.example.gallery.databinding.FullscreenPictureFragmentBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenPictureFragment : Fragment(), ClickReceiver {
    private val hideHandler = Handler(Looper.myLooper()!!)
//    lateinit var mediaUri: String
//    lateinit var mediaPath: String
//    lateinit var mediaDur: String
//    lateinit var bucketID: String

    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        val flags =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        fullscreenContentControls?.visibility = View.VISIBLE
    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var fullscreenContent: View? = null
    private var fullscreenContentControls: View? = null

    private var _binding: FullscreenPictureFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var isPortrait = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FullscreenPictureFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visible = true
        var mediaUri = arguments?.getString("mediaUri")!!
        var mediaPath = arguments?.getString("mediaPath")!!
        var mediaDur = arguments?.getString("mediaDur")!!
        val positionPicture = arguments?.getInt("positionPicture")!!
        val db = connectToDB(binding.root.context)

        binding.viewpager.adapter = VPImageAdapter(listpicture, this)
        println("текущая позиция - $positionPicture текущий id ${listpicture[positionPicture].uri}")
        binding.viewpager.setCurrentItem(positionPicture, false)
        println("фактовая позиция - ${binding.viewpager.currentItem} фактовый id ${listpicture[binding.viewpager.currentItem].uri} длина lispicture ${listpicture.size}")
        fullscreenContentControls = binding.fullscreenContentControls

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                println("selected")
                mediaUri = listpicture[position].uri.toString()
                mediaPath = listpicture[position].path
                mediaDur = listpicture[position].duration

                loadComment(db, mediaUri)
            }//TODO пропал комментарий с картинки
        })

        loadComment(db, mediaUri)


        // Set up the user interaction to manually show or hide the system UI.
        binding.mainFragment.setOnClickListener { toggle() }
//        fullscreenContent?.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.


        fullscreenContent = binding.viewpager


        binding.ibRotate.setOnClickListener {
            activity?.requestedOrientation = if (isPortrait) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            isPortrait = !isPortrait
        }

        binding.ibComment.setOnClickListener {
            Log.d("PrintF", mediaUri)
            val bundle = bundleOf(
                "mediaUri" to mediaUri,
                "mediaPath" to mediaPath,
                "mediaDur" to mediaDur
            )
            findNavController().navigate(R.id.action_fullscreenPicture_to_photoStoryFragment, bundle)
        }

        binding.ibDelete.setOnClickListener {
            try {
                binding.root.context.contentResolver.delete(mediaUri.toUri(), null, null)
            } catch (e: SecurityException) {
                try {
                    val recoverableSecurityException = e as? RecoverableSecurityException
                    recoverableSecurityException?.let {
                        requireActivity().startIntentSenderForResult(
                            it.userAction.actionIntent.intentSender,
                            1010,
                            null,
                            0,
                            0,
                            0,
                            null
                        )
                    }
                    Snackbar.make(binding.ibDelete,"Нажмите удалить ещё раз", 4000).show()
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }
            }
        }

        binding.ibShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_STREAM, mediaUri.toUri())
            sendIntent.setType("image/jpeg")
            startActivity(sendIntent)
        }
    }

    private fun loadComment(db: CommentsDao, mediaUri: String) {
        CoroutineScope(Dispatchers.IO).launch {
    //            db.clearComments()
            db.getAllComments().forEach { println(it.toString()) }

            val comment = db.findImageByHash(
                FunctionsImages().md5(
                    FunctionsMedia().getThumbnailSafe(
                        requireContext(),
                        mediaUri.toUri()
                    )!!
                )
            )?.image_comment ?: ""

//                comment = getString(R.string.lorem_ipsum)
            activity?.runOnUiThread {
                binding.tvStory.text = comment
                if (comment.trim().length == 0) binding.comment.visibility =
                    View.INVISIBLE else binding.comment.visibility = View.VISIBLE
                println("Comment - ${comment} uri - $mediaUri")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Clear the systemUiVisibility flag
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
        fullscreenContent = null
        fullscreenContentControls = null
//        binding.videoView.player?.pause() // крашит
    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
//        fullscreenContentControls?.visibility = View.GONE
        visible = false
        binding.comment.animate().translationY(if (visible) 50f else 500f)
        binding.secondContainer.animate().translationY(if (visible) 0f else 100f)
        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
        binding.secondContainer.visibility = View.INVISIBLE
        binding.idContainer.visibility = View.INVISIBLE
    }

    @Suppress("InlinedApi")
    private fun show() {
        // Show the system bar
//        fullscreenContent?.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        visible = true
        binding.comment.animate().translationY(if (visible) 0f else 500f)
        binding.secondContainer.animate().translationY(if (visible) 0f else 100f)
        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
//        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        binding.secondContainer.visibility = View.VISIBLE
        binding.idContainer.visibility = View.VISIBLE
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 2000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 100
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick() {
        toggle()
    }
}