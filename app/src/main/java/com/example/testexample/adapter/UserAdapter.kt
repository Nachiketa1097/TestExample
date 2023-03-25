package com.example.testexample.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testexample.R
import com.example.testexample.model.UserDetail

class UserAdapter (private val callback: Callback ) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private lateinit var mContext: Context
    var dataSet: ArrayList<UserDetail> = ArrayList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val userImage: ImageView
        init {
            userName = view.findViewById(R.id.tv_user_name)
            userImage = view.findViewById(R.id.iv_user_image)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        mContext = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_user_detail, viewGroup, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val userDetail = dataSet[position]
        viewHolder.userName.text = userDetail.name
        Glide.with(mContext).load(userDetail.photoUrl).centerCrop().into(viewHolder.userImage)

        viewHolder.itemView.setOnClickListener{
            callback.onUserItemClick(userDetail)
        }
    }

    override fun getItemCount() = dataSet.size

    interface Callback{
        fun onUserItemClick(userDetail: UserDetail)
    }

}
