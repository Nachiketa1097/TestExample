package com.example.testexample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testexample.R
import com.example.testexample.model.Message

class SendMessageAdapter (private val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<SendMessageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sentMessage: TextView
        init {
            sentMessage = view.findViewById(R.id.tv_sent)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.sent, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        holder.sentMessage.text =  currentMessage.message
    }

    override fun getItemCount() = messageList.size

}