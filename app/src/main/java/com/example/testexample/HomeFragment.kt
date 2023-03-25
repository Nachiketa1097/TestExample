package com.example.testexample

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.testexample.adapter.CreateNoteAdapter
import com.example.testexample.databinding.FragmentHomeBinding
import com.example.testexample.model.AppDatabase
import com.example.testexample.model.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class HomeFragment : Fragment() , OnClickListener, CreateNoteAdapter.Callback {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mContext: Context
    private var layoutClick: Int = 0
    private lateinit var database: DatabaseReference
    var arrayList = ArrayList<User>()
    private lateinit var auth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        getGridNote()
        hideBottomLayout()

        auth = Firebase.auth
        val photoUrl = auth.currentUser?.photoUrl

        Glide.with(mContext).load(photoUrl).centerCrop().into(binding.ivUserProfile)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
           // val msg = getString(R.string.msg_token_fmt, token)
            //Log.d(TAG, msg)
            Toast.makeText(mContext, "msg", Toast.LENGTH_SHORT).show()
        })
    }

    private fun getGridNote() {
        val db = AppDatabase.buildDatabase(mContext)
        lifecycleScope.launch {
            val arrayList = db.userDao().getAll()
            val adapter = CreateNoteAdapter(this@HomeFragment)
            adapter.dataSet = arrayList as ArrayList<User>
            binding.rvNote.layoutManager =
                StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            binding.rvNote.adapter = adapter
        }
    }

    private fun getLinearNote() {
        val db = AppDatabase.buildDatabase(mContext)
        lifecycleScope.launch {
            val arrayList = db.userDao().getAll()
            val adapter = CreateNoteAdapter(this@HomeFragment)
            adapter.dataSet = arrayList as ArrayList<User>
            binding.rvNote.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            binding.rvNote.adapter = adapter
        }
    }

    private fun deleteNote(user: User) {
        val db = AppDatabase.buildDatabase(mContext)
        lifecycleScope.launch {
            db.userDao().delete(user)
        }
    }

    private fun deleteNoteItem(user: User) {
        val dialogDeleteNote = Dialog(mContext, android.R.style.Theme_Material_Light_NoActionBar)
        dialogDeleteNote.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialogDeleteNote.setContentView(R.layout.dialog_delete_note)
        val tvDelete = dialogDeleteNote.findViewById<TextView>(R.id.tv_delete)

        tvDelete.setOnClickListener {
            deleteNote(user)

            if (layoutClick % 2 == 0) {
                getGridNote()
            } else {
                getLinearNote()
            }

            dialogDeleteNote.dismiss()
        }
        dialogDeleteNote.show()
    }

    private fun initClickListener() {
        binding.fabCreateNote.setOnClickListener(this)
        binding.ivSearch.setOnClickListener(this)
        binding.ivNoteLayout.setOnClickListener(this)
        binding.ivUserProfile.setOnClickListener(this)
        binding.ivHamburgerMenu.setOnClickListener(this)
        binding.ivSearch.setOnClickListener(this)
    }

    private fun replaceToCreateNoteFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment()
        findNavController().navigate(action)
    }

    private fun replaceToSearchFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
        findNavController().navigate(action)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_create_note -> {
                replaceToCreateNoteFragment()
            }
            R.id.iv_search -> {
                getNoteListFromFirebase()

                // replaceToSearchFragment()
            }
            R.id.iv_hamburger_menu -> {
                getNoteListFromFirebase()

                //  replaceToSearchFragment()
            }
            R.id.iv_note_layout -> {
                layoutClick++

                if (layoutClick % 2 == 0) {
                    getGridNote()
                    binding.ivNoteLayout.setImageResource(R.drawable.grid_layout_icon)
                } else {
                    getLinearNote()
                    binding.ivNoteLayout.setImageResource(R.drawable.linear_layout_icon)
                }
            }
            R.id.iv_user_profile -> {
            }
            R.id.tv_search -> {
                //replaceToSearchFragment()
                 getNoteListFromFirebase()
            }
        }
    }

    private fun getNoteListFromFirebase() {
        database = FirebaseDatabase.getInstance().getReference("Note")

        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                     val user = userSnapshot.getValue(User::class.java)
                      arrayList.add(user!!)
                    }
                        val adapter = CreateNoteAdapter(this@HomeFragment)
                        adapter.dataSet = arrayList
                        binding.rvNote.layoutManager =
                            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                        binding.rvNote.adapter = adapter

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun hideBottomLayout() {
        (activity as DashboardActivity).hideBottomLayout(VISIBLE)
    }

    override fun onItemDelete(user: User) {
        deleteNoteItem(user)
    }

    override fun onNoteItemClick(user: User) {
        val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(
            user.noteTitle!!,
            user.note!!,
            user.characters!!,
            user.date!!
        )
        findNavController().navigate(action)
    }

}
