package com.example.testexample.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.testexample.R
import com.example.testexample.adapter.VpHomeAdapter
import com.example.testexample.databinding.FragmentChatHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class ChatHomeFragment : Fragment(), OnClickListener {
    private lateinit var mContext: Context
    private lateinit var binding:FragmentChatHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_home, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        auth = FirebaseAuth.getInstance()
        binding.tvUserWelcome.text = "Hello! ${auth.currentUser!!.displayName}"

        val vpAdapter = VpHomeAdapter(this)
        binding.vpHome.adapter = vpAdapter
        binding.vpHome.isUserInputEnabled = false
        setupViewPager()
    }


    private fun setupViewPager() {
        TabLayoutMediator(binding.tabLayout, binding.vpHome) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Chats"
                1 -> tab.text = "Status"
                2 -> tab.text = "Calls"
            }
        }.attach()
    }

    private fun initClickListener() {
       binding.ivMenu.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
           R.id.iv_menu->{
               showPopUp()
           }
        }
    }

    private fun showPopUp() {
        val popupMenu = PopupMenu(mContext, binding.ivMenu)

        popupMenu.menuInflater.inflate(R.menu.home_manu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.logout ->{
                    auth.signOut()
                    findNavController().popBackStack()
                }
                else ->{
                    Toast.makeText(mContext, "You Clicked " + item!!.title, Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popupMenu.show()
    }

}