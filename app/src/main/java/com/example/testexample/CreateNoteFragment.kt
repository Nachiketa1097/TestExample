package com.example.testexample

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.testexample.databinding.FragmentCreateNoteBinding
import com.example.testexample.model.AppDatabase
import com.example.testexample.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("SetTextI18n")
class CreateNoteFragment : Fragment(), OnClickListener, TextWatcher{
    private lateinit var binding: FragmentCreateNoteBinding
    private lateinit var mContext: Context
    private var mNoteTitle: String = ""
    private var mNote: String = ""
    private var mCharacters: String = ""
    private var mDate: String = ""
    private var folderName: String = ""
    private lateinit var database: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_note, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentDate()
        initClickListener()
        initNumberOfCharacter()
        showSoftKeyboard(binding.etYourNote)
        hideBottomLayout()
        getArgs()
        folderName = arguments?.let {
            it.getString(FolderNoteListFragment.FOLDER_NAME)
        }.toString()

        database = FirebaseDatabase.getInstance().getReference("Note")

    }

    private fun getArgs() {
        if (arguments != null) {
            mNoteTitle = CreateNoteFragmentArgs.fromBundle(requireArguments()).noteTitle
            mNote = CreateNoteFragmentArgs.fromBundle(requireArguments()).note
            mCharacters = CreateNoteFragmentArgs.fromBundle(requireArguments()).characters
            mDate = CreateNoteFragmentArgs.fromBundle(requireArguments()).date

            binding.etTitle.setText(mNoteTitle)
            binding.etYourNote.setText(mNote)
            binding.tvDate.text = mDate
            binding.tvCharacters.text = "$mCharacters characters"
            getCurrentDate()
        }
    }

    private fun initNumberOfCharacter(){
        binding.tvCharacters.text = "0 characters"
    }

    private fun initClickListener() {
        binding.etYourNote.addTextChangedListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.ivDone.setOnClickListener(this)
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()){
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onClick(v: View) {
       when(v.id){
           R.id.iv_back ->{
             findNavController().popBackStack()
           }
           R.id.iv_done ->{
             createNote()
               findNavController().popBackStack()
           }
       }
    }

    private fun createNote(){
        mNoteTitle = binding.etTitle.text.toString()
        mNote = binding.etYourNote.text.toString()

        val db = AppDatabase.buildDatabase(mContext)

        val user = User(0, mNoteTitle, mNote, mCharacters , mDate, folderName)

        database.child(mNoteTitle).setValue(user).addOnSuccessListener {
            Toast.makeText(mContext,"Successfully added", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(mContext,it.message.toString(), Toast.LENGTH_LONG).show()
        }

        lifecycleScope.launch {
            val userDao = db.userDao()
            userDao.insertUser(user)

        }
    }

    private fun getCurrentDate(){
        val calendar = Calendar.getInstance()

        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val monthName = when(month){
            0->"January"
            1->"February"
            2->"March"
            3->"April"
            4->"May"
            5->"June"
            6->"July"
            7->"August"
            8->"September"
            9->"October"
            10->"November"
            11->"December"
            else -> {
                ""
            }
        }

        val weekName = when(dayOfWeek){
            1->"Sun"
            2->"Mon"
            3->"Tue"
            4->"Wed"
            5->"Thu"
            6->"Fri"
            7->"Sat"
            else -> {
                ""
            }
        }

        mDate = "$monthName $dayOfMonth $hour:$minute $weekName"
        binding.tvDate.text = "$mDate  |  "
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      mCharacters = binding.etYourNote.text.toString().trim().length.toString()
      binding.tvCharacters.text = "$mCharacters characters"
    }

    override fun afterTextChanged(s: Editable?) {
       binding.tvCharacters.text = "$mCharacters characters"
    }

    private fun hideBottomLayout(){
        (activity as DashboardActivity).hideBottomLayout(GONE)
    }

    companion object{
        const val FOLDER_NAME = "folder_name"
    }
}