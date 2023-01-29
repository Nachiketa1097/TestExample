package com.example.testexample.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.testexample.R
import com.example.testexample.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mContext: Context

    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val Req_Code:Int=123

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initClickListener()
    }

    private fun init() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken("208525921531-jl78dqgd49d9gf6tp67mjs01sh5cko0g.apps.googleusercontent.com")
                 .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(mContext,gso)

        auth = FirebaseAuth.getInstance()
    }

    private fun initClickListener() {
        binding.tvGoogleLogin.setOnClickListener(this)
        binding.tvLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_google_login-> {
                signInUsingGoogle()
            }
            R.id.tv_logout-> {
                signOutFromGoogle()
            }
        }
    }

    private fun signInUsingGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,Req_Code)
      //  launcher.launch(signInIntent)
    }

//    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//        result->
//            if (result.resultCode == Activity.RESULT_OK){
//                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//                handleResult(task)
//            }
//    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Req_Code){
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("task", task.result.displayName.toString())
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account!=null){
                updateUI(account)
            }
        }else{
            Toast.makeText(mContext, task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(mContext, "Logged In successfully", Toast.LENGTH_LONG).show()
                binding.tvUserDetail.text = "${account.email} \n ${account.displayName}"

                Glide.with(mContext).load(account.photoUrl).centerCrop().into(binding.ivUserImage);
            }else{
                Toast.makeText(mContext, it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signOutFromGoogle() {
        auth.signOut()
    }
}