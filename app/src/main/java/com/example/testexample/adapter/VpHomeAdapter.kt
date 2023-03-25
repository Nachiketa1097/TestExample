package com.example.testexample.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.testexample.ui.UserListFragment

class VpHomeAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> UserListFragment()
            1-> UserListFragment()
            2-> UserListFragment()
            else-> Fragment()
        }
    }
}