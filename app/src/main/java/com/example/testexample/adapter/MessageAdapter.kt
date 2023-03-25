package com.example.testexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testexample.R
import com.example.testexample.model.Message
import com.google.firebase.auth.FirebaseAuth

@Suppress("NAME_SHADOWING")
class MessageAdapter(val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    class SentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sentMessage: TextView
        init {
            sentMessage = view.findViewById(R.id.tv_sent)
        }
    }

    class ReceiveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val receiveMessage: TextView
        init {
            receiveMessage = view.findViewById(R.id.tv_receive)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.receive, viewGroup, false)
            return ReceiveViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.sent, viewGroup, false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (viewHolder.javaClass == SentViewHolder::class.java) {
             val viewHolder = viewHolder as SentViewHolder
             if (currentMessage.message?.isNotEmpty() == true){
                 viewHolder.sentMessage.text = currentMessage.message
             }
         }
         else if (viewHolder.javaClass == ReceiveViewHolder::class.java) {
             val viewHolder = viewHolder as ReceiveViewHolder
             if (currentMessage.message?.isNotEmpty() == true){
                 viewHolder.receiveMessage.text = currentMessage.message
             }
         }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else {
            return ITEM_RECEIVE
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount() = messageList.size
}
