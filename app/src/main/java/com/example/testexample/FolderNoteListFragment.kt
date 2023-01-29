package com.example.testexample

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.testexample.adapter.CreateNoteAdapter
import com.example.testexample.databinding.FragmentFolderNoteListBinding
import com.example.testexample.model.AppDatabase
import com.example.testexample.model.User
import kotlinx.coroutines.launch

class FolderNoteListFragment : Fragment(), OnClickListener, CreateNoteAdapter.Callback{
    private lateinit var binding: FragmentFolderNoteListBinding
    private lateinit var mContext: Context
    private var folderName: String = ""
    private var layoutClick: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folderName = arguments?.let {
            it.getString(FOLDER_NAME)
        }.toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_folder_note_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        getGridNote()
        (activity as DashboardActivity).hideBottomLayout(View.GONE)
    }

    private fun initClickListener() {
        binding.fabCreateNote.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.ivNoteLayout.setOnClickListener(this)
    }

    private fun getGridNote(){
        val db = AppDatabase.buildDatabase(mContext)
        lifecycleScope.launch {
            val arrayList = db.userDao().getFolderList(folderName)
            val adapter = CreateNoteAdapter(this@FolderNoteListFragment)
            adapter.dataSet = arrayList as ArrayList<User>
            binding.rvFolderNote.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
            binding.rvFolderNote.adapter = adapter
        }
    }

    private fun getLinearNote(){
        val db = AppDatabase.buildDatabase(mContext)
        lifecycleScope.launch {
            val arrayList = db.userDao().getFolderList(folderName)
            val adapter = CreateNoteAdapter(this@FolderNoteListFragment)
            adapter.dataSet = arrayList as ArrayList<User>
            binding.rvFolderNote.layoutManager = LinearLayoutManager(mContext)
            binding.rvFolderNote.adapter = adapter
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab_create_note_ ->{
                val b = Bundle()
                b.putString(CreateNoteFragment.FOLDER_NAME, folderName)
                findNavController().navigate(R.id.action_folderNoteListFragment2_to_createNoteFragment2, b)
            }
            R.id.iv_back ->{
                findNavController().popBackStack()
            }
            R.id.iv_note_layout ->{
                layoutClick++

                if (layoutClick%2==0){
                    getGridNote()
                    binding.ivNoteLayout.setImageResource(R.drawable.grid_layout_icon)
                }
                else{
                    getLinearNote()
                    binding.ivNoteLayout.setImageResource(R.drawable.linear_layout_icon)
                }
            }
        }
    }

    private fun deleteNoteItem(user: User){
        val dialogDeleteNote = Dialog(mContext, android.R.style.Theme_Material_Light_NoActionBar)
        dialogDeleteNote.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialogDeleteNote.setContentView(R.layout.dialog_delete_note)
        val tvDelete = dialogDeleteNote.findViewById<TextView>(R.id.tv_delete)

        tvDelete.setOnClickListener{
            deleteNote(user)
            getGridNote()
            dialogDeleteNote.dismiss()
        }
        dialogDeleteNote.show()
    }

    private fun deleteNote(user: User){
        val db = AppDatabase.buildDatabase(mContext)
        lifecycleScope.launch {
            db.userDao().delete(user)
        }
    }

    override fun onItemDelete(user: User) {
        deleteNoteItem(user)
    }

    override fun onNoteItemClick(user: User) {
        val action = FolderNoteListFragmentDirections.actionFolderNoteListFragment2ToCreateNoteFragment2(
            user.id, user.noteTitle!!, user.note!!, user.characters!!, user.date!!)
        findNavController().navigate(action)
    }

    companion object{
        const val FOLDER_NAME = "folder_name"
    }
}