package com.example.testexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.testexample.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomLayout()
        initClickListener()
    }

    private fun initClickListener() {
        binding.ivBackSearch.setOnClickListener(this)
    }

    private fun hideBottomLayout(){
        (activity as DashboardActivity).hideBottomLayout(View.GONE)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_back_search ->{
                findNavController().popBackStack()
            }
        }
    }
}