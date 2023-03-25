package com.example.testexample.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.testexample.R
import com.example.testexample.databinding.FragmentSignUpBinding
import com.example.testexample.model.UserDetail
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment : Fragment(), OnClickListener
{
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var mContext: Context
    private lateinit var database: DatabaseReference

    private lateinit var auth: FirebaseAuth
    private val Req_Code:Int=123
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("208525921531-jl78dqgd49d9gf6tp67mjs01sh5cko0g.apps.googleusercontent.com")
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(mContext,gso)

        database = FirebaseDatabase.getInstance().getReference("UserListDetail")
    }

    private fun initClickListener() {
        binding.tvSignIn.setOnClickListener(this)
        binding.tvSignUp.setOnClickListener(this)
        binding.tvUploadImage.setOnClickListener(this)
    }

    private fun signInWithEmailAndPassword() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val userDetail = UserDetail(name,email,auth.currentUser?.uid!!, "")
                    database.child("user").child(name).setValue(userDetail).addOnSuccessListener {
                      //   Toast.makeText(mContext,"Successfully added", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener{
                        Toast.makeText(mContext,it.message.toString(), Toast.LENGTH_LONG).show()
                    }

                    val b = Bundle()
                    b.putString(UploadImageFragment.USER_NAME, name)
                    findNavController().navigate(R.id.action_signUpFragment_to_uploadImageFragment, b)
                } else {
                    Toast.makeText(mContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_sign_in ->{
                findNavController().popBackStack()
            }
            R.id.tv_sign_up ->{
              //  findNavController().navigate(R.id.action_signUpFragment_to_uploadImageFragment)

               signInWithEmailAndPassword()
            }
            R.id.tv_upload_image ->{
            }
        }
    }

}