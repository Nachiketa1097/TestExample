package com.example.testexample.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.testexample.R
import com.example.testexample.adapter.MessageAdapter
import com.example.testexample.databinding.FragmentChatBinding
import com.example.testexample.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentChatBinding
    private lateinit var mContext: Context
    private lateinit var mDbRef: DatabaseReference

    private lateinit var auth: FirebaseAuth
    private lateinit var messageList : ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    var userName:String = ""
    var userImage:String = ""
    var receiverUid:String = ""

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        arguments?.let {
            userName = it.getString(USER_NAME).toString()
            receiverUid = it.getString(USER_UID).toString()
            userImage = it.getString(USER_IMAGE).toString()
        }

        initMessage()
        if (binding.etMessage.text.toString().isNotEmpty()){
          //  sendMessage()
        }
    }

    private fun initMessage() {

        auth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()


        binding.tvNameUser.text = userName
        Glide.with(mContext).load(userImage).centerCrop().into(binding.ivUserImage);

        val senderUid = auth.currentUser?.uid
        senderRoom = senderUid + receiverUid
        Log.d("senderRoom", senderRoom!!)
        receiverRoom = receiverUid + senderUid
        Log.d("senderRoom", receiverRoom!!)

        messageList = ArrayList()

        messageAdapter = MessageAdapter(messageList)
        binding.rvChat.layoutManager = LinearLayoutManager(mContext)
        binding.rvChat.adapter = messageAdapter

//        mDbRef.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//              //  messageList.clear()
//                for (posSnapshot in snapshot.children){
//                    val message = posSnapshot.getValue(Message::class.java)
//                    messageList.add(message!!)
//                    messageAdapter.notifyDataSetChanged()
//                    Log.d("messageList", messageList.toString())
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object :ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (posSnapshot in snapshot.children){
                        val message = posSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    private fun initClickListener() {
        binding.ivSend.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_send -> {
               sendMessage()
            }
            R.id.iv_back -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString()
        val messageObject = Message(message,auth.currentUser?.uid)

        mDbRef.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(messageObject)
            }
        binding.etMessage.setText("")

        initMessage()
    }

    companion object{
        const val USER_NAME = "user_name"
        const val USER_IMAGE = "user_image"
        const val USER_UID = "user_uid"
    }
}