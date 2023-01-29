package com.example.testexample.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testexample.R
import com.example.testexample.model.User

class CreateNoteAdapter(private val callback: Callback) :
    RecyclerView.Adapter<CreateNoteAdapter.ViewHolder>(){

    var dataSet: ArrayList<User> = ArrayList()
    private lateinit var mContext: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val note: TextView
        val date: TextView

        init {
            title = view.findViewById(R.id.tv_title)
            note = view.findViewById(R.id.tv_your_note)
            date = view.findViewById(R.id.tv_date_save)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_note, viewGroup, false)
        mContext = viewGroup.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.title.text = dataSet[position].noteTitle
        viewHolder.note.text = dataSet[position].note
        viewHolder.date.text = dataSet[position].date
        val user = dataSet[position]
        viewHolder.itemView.setOnLongClickListener {
            callback.onItemDelete(user)
            true
        }

        viewHolder.itemView.setOnClickListener{
            callback.onNoteItemClick(user)
        }
    }

    override fun getItemCount() = dataSet.size

    interface Callback{
        fun onItemDelete(user: User)

        fun onNoteItemClick(user: User)
    }

}

