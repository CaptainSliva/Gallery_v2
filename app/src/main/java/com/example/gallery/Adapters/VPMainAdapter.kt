package com.example.gallery.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class VPMainAdapter(fragment: FragmentActivity, val list: List<Fragment>): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}