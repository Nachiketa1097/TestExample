package com.example.testexample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.testexample.R
import com.example.testexample.model.Message
import com.example.testexample.model.MessageModel
import com.google.firebase.auth.FirebaseAuth

class MessageModelAdapter(val messageList: ArrayList<MessageModel>) :
    RecyclerView.Adapter<MessageModelAdapter.ViewHolder>() {
    private lateinit var mContext: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sentMessage: TextView
        init {
            sentMessage = view.findViewById(R.id.tv_sent)
        }
    }

    fun add(messageModel: MessageModel){
        messageList.add(messageModel)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        mContext = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.sent, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        holder.sentMessage.text = currentMessage.message

        if (currentMessage.senderId.equals(FirebaseAuth.getInstance().currentUser?.uid)){
            holder.sentMessage.setTextColor(ContextCompat.getColor(mContext, R.color.violet_771f98))
        }else{
            holder.sentMessage.setTextColor(ContextCompat.getColor(mContext, R.color.white))
        }
    }

    override fun getItemCount() = messageList.size

}
