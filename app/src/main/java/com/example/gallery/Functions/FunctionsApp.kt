package com.example.gallery.Functions

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.gallery.R

class FunctionsApp {
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}