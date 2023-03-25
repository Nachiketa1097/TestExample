package com.example.testexample.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testexample.R
import com.example.testexample.adapter.UserAdapter
import com.example.testexample.databinding.FragmentUserListBinding
import com.example.testexample.model.UserDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserListFragment : Fragment(),OnClickListener, UserAdapter.Callback{
    private lateinit var binding: FragmentUserListBinding
    private lateinit var mContext: Context
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var userListData: ArrayList<UserDetail>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().getReference("UserListDetail")
        userListData = ArrayList<UserDetail>()
        val adapter = UserAdapter(this)
        adapter.dataSet = userListData
        binding.rvUser.layoutManager = LinearLayoutManager(mContext)
        binding.rvUser.adapter = adapter

        database.child("user").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userListData.clear()

                for (postSnapShot in snapshot.children){
                    val currentUser = postSnapShot.getValue(UserDetail::class.java)
                    if (auth.currentUser?.uid != currentUser?.uid){
                        userListData.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){

        }
    }

    override fun onUserItemClick(userDetail: UserDetail) {
        val b = Bundle()
        b.putString(ChatFragment.USER_NAME,userDetail.name)
        b.putString(ChatFragment.USER_UID,userDetail.uid)
        b.putString(ChatFragment.USER_IMAGE,userDetail.photoUrl)
        findNavController().navigate(R.id.action_chatHomeFragment_to_chatFragment, b)
    }
}