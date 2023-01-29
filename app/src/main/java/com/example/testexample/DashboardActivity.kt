package com.example.testexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.testexample.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity(),OnClickListener {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController
    private var bottomLinearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        bottomLinearLayout = binding.llBottomNavigation


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        initClickListener()
        setTextColor(binding.tvHome, binding.tvFolder)
        setImageIconColor(binding.ivHome,binding.ivFolder)

        hideBottomLayout(View.VISIBLE)
    }

    private fun initClickListener() {
        binding.llHome.setOnClickListener(this)
        binding.llFolder.setOnClickListener(this)
    }

    override fun onClick(v: View) {
    when(v.id){
        R.id.ll_home ->{
            setTextColor(binding.tvHome, binding.tvFolder)
            setImageIconColor(binding.ivHome,binding.ivFolder)
            navController.popBackStack()
            changeNavGraph(R.navigation.home_navigation)
        }
        R.id.ll_folder ->{
            setTextColor(binding.tvFolder, binding.tvHome)
            setImageIconColor(binding.ivFolder,binding.ivHome)
            changeNavGraph(R.navigation.folder_navigation)
        }
      }
    }

    private fun setTextColor(textView1: TextView, textView2: TextView){
        textView1.setTextColor(resources.getColor(R.color.GREEN_016866))
        textView2.setTextColor(resources.getColor(R.color.black_373737))
    }

    private fun setImageIconColor(imageView1: ImageView, imageView2: ImageView){
       imageView1.setColorFilter(resources.getColor(R.color.GREEN_016866))
       imageView2.setColorFilter(resources.getColor(R.color.black_373737))
    }

    fun hideBottomLayout(visibility: Int){
        bottomLinearLayout?.visibility = visibility
    }

    private fun changeNavGraph(navGraphId: Int){
        val graphInflater = binding.navHostFragment.findNavController().navInflater
        val navGraph: NavGraph = graphInflater.inflate(navGraphId)
        navController.graph = navGraph
    }

    override fun onResume() {
        super.onResume()
        hideBottomLayout(View.VISIBLE)
        setTextColor(binding.tvHome, binding.tvFolder)
        setImageIconColor(binding.ivHome,binding.ivFolder)
    }
}