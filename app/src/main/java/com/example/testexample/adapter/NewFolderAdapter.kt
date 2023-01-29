package com.example.testexample.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testexample.R
import com.example.testexample.model.UserFolder

class NewFolderAdapter (private val callback: Callback ) :
    RecyclerView.Adapter<NewFolderAdapter.ViewHolder>() {
    var dataSet: ArrayList<UserFolder> = ArrayList()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newFolder: TextView

        init {
            newFolder = view.findViewById(R.id.tv_new_folder)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_new_folder, viewGroup, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.newFolder.text = dataSet[position].newFolder
        val userFolder = dataSet[position]
        viewHolder.itemView.setOnLongClickListener {
            callback.onFolderClick(userFolder)
            true
        }
        viewHolder.itemView.setOnClickListener{
            callback.onFolderItemClick(userFolder)
        }
    }

    override fun getItemCount() = dataSet.size

    interface Callback{
        fun onFolderClick(userFolder: UserFolder)
        fun onFolderItemClick(userFolder: UserFolder)
    }

}
