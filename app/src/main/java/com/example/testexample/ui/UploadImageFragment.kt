package com.example.testexample.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testexample.R
import com.example.testexample.databinding.FragmentUploadImageBinding
import com.example.testexample.model.UserDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class UploadImageFragment : Fragment(), OnClickListener {
    private lateinit var mContext: Context
    private lateinit var binding: FragmentUploadImageBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var imageUri:Uri? = null
    private var name: String=""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        arguments?.let {
            name = it.getString(USER_NAME,"")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_upload_image, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()

        auth = FirebaseAuth.getInstance()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
        val date = Date()
        val filename = formatter.format(date)

        database = FirebaseDatabase.getInstance().getReference("UserListDetail")

    }

    private fun initClickListener() {
        binding.cvUploadImage.setOnClickListener(this)
        binding.tvSetupProfile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.cv_upload_image->{
                setImage()
            }
            R.id.tv_setup_profile->{
                findNavController().navigate(R.id.action_uploadImageFragment_to_chatHomeFragment)
            }
        }
    }

    private fun setImage() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 100)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null && requestCode == 100) {
            imageUri = data.data
            binding.ivUploadImage.setImageURI(data.data)
            uploadImage(imageUri!!)
        }
    }

    private fun uploadImage(uri: Uri) {
       val storage = FirebaseStorage.getInstance().getReference("images/${name.trim()}.${getFileExtensionType(uri)}")
       binding.progressBar.visibility = View.VISIBLE
       storage.putFile(imageUri!!).addOnSuccessListener {
           if (binding.progressBar.isShown){
               binding.progressBar.visibility = View.GONE
               downloadImage(uri)
           }
           Toast.makeText(mContext, "successfully uploaded", Toast.LENGTH_LONG).show()
       }.addOnFailureListener {
           binding.progressBar.visibility = View.GONE
           Toast.makeText(mContext, "something went wrong", Toast.LENGTH_LONG).show()
       }
    }

    private fun downloadImage(uri: Uri){
        val storage = FirebaseStorage.getInstance().getReference("images/${name.trim()}.${getFileExtensionType(uri)}")
        storage.downloadUrl.addOnSuccessListener { it ->
          //  Glide.with(binding.ivUploadImage).load(it).into(binding.ivUploadImage)
            val userDetail = UserDetail(name, auth.currentUser!!.email,auth.currentUser?.uid!!, it.toString())
            database.child("user").child(name).setValue(userDetail).addOnSuccessListener {
            }.addOnFailureListener{
                Toast.makeText(mContext,it.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getFileExtensionType(uri: Uri) : String{
        val cr = mContext.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri)).toString()
    }

    companion object{
        const val USER_NAME = "user_name"
    }
}