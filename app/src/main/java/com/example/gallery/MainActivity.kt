package com.example.gallery

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gallery.Adapters.PhotosRVadapter
import com.example.gallery.Adapters.VPMainAdapter
import com.example.gallery.Permissions.RequestPermissions
import com.example.gallery.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.security.Permissions

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        mainContext = this

        RequestPermissions(this).checkPermissions()
//        val vp = findViewById<ViewPager2>(R.id.vp_main)
//        val tab = findViewById<TabLayout>(R.id.tableLayout)
//
//        //Тут для VP данные
//        val mImageArray = listOf(
//            R.drawable.baseline_photo_24,
//            R.drawable.baseline_photo_album_24
//        )
//
//        val fragments : List<Fragment> = listOf(
//            AllAlbumsFragment::class.java.newInstance(),
//            AllPhotosFragment::class.java.newInstance()
//        )
//
//        val tabNames = listOf(getString(R.string.all_photos), getString(R.string.albums))
//
//        val VPadapter = VPMainAdapter(this, fragments)
//        vp.adapter = VPadapter
//
//        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                val bundle = Bundle()
//                supportFragmentManager.setFragmentResult("reskey", bundle)
//
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//
//        })
//
//        TabLayoutMediator(tab, vp) {tab, pos ->
//            tab.text = tabNames[pos]
//            tab.setIcon(mImageArray[pos])
//        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_search_photo -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.searchPhotoOnCommentFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}