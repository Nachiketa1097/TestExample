package com.example.testexample

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testexample.adapter.NewFolderAdapter
import com.example.testexample.databinding.FragmentFolderBinding
import com.example.testexample.model.AppNewFolderDatabase
import com.example.testexample.model.UserFolder
import kotlinx.coroutines.launch

class FolderFragment : Fragment() , OnClickListener, NewFolderAdapter.Callback{
    private lateinit var binding: FragmentFolderBinding
    private lateinit var dialogCreateNewFolder: Dialog
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_folder, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAllFolderList()
        initClickListener()
    }

    private fun initClickListener() {
        binding.fabCreateFolder.setOnClickListener(this)
    }

    private fun getAllFolderList(){
        val db =AppNewFolderDatabase.buildDatabase(mContext)

        lifecycleScope.launch {
            val arrayList = db.userFolderDao().getNewFolder()
            val adapter = NewFolderAdapter(this@FolderFragment)
            adapter.dataSet = arrayList as ArrayList<UserFolder>
            binding.rvNewFolder.layoutManager = LinearLayoutManager(mContext)
            binding.rvNewFolder.adapter = adapter
        }
    }

    private fun deleteFolder(userFolder: UserFolder){
        val db =AppNewFolderDatabase.buildDatabase(mContext)
        lifecycleScope.launch{
            db.userFolderDao().deleteNewFolder(userFolder)
        }

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab_create_folder ->{
                createNewFolder()
            }
        }
    }

    private fun createNewFolder(){
        dialogCreateNewFolder = Dialog(mContext, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialogCreateNewFolder.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialogCreateNewFolder.setContentView(R.layout.dialog_new_folder)
        val etNewFolder = dialogCreateNewFolder.findViewById<EditText>(R.id.et_new_folder)
        val tvCancel = dialogCreateNewFolder.findViewById<TextView>(R.id.tv_cancel)
        val tvOK = dialogCreateNewFolder.findViewById<TextView>(R.id.tv_ok)
        tvCancel.setOnClickListener{
            dialogCreateNewFolder.dismiss()
        }

        tvOK.setOnClickListener{
            val folderName = etNewFolder.text.toString()
            getNewFolderName(folderName)
            getAllFolderList()
            dialogCreateNewFolder.dismiss()
        }
        dialogCreateNewFolder.show()
    }

    private fun deleteFolderItem(userFolder: UserFolder){
        val dialogDeleteNote = Dialog(mContext, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialogDeleteNote.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialogDeleteNote.setContentView(R.layout.dialog_delete_note)
        val tvDelete = dialogDeleteNote.findViewById<TextView>(R.id.tv_delete)

        tvDelete.setOnClickListener{
            deleteFolder(userFolder)
            getAllFolderList()
            dialogDeleteNote.dismiss()
        }
        dialogDeleteNote.show()
    }

    private fun getNewFolderName(newFolder: String){
        val db = AppNewFolderDatabase.buildDatabase(mContext)
        val userFolder = UserFolder(0, newFolder)

        lifecycleScope.launch {
            val userFolderDao = db.userFolderDao()
            userFolderDao.insertNewFolder(userFolder)
        }
    }

    private fun replaceToCreateNoteFragment(newFolder: String){
     //   val action = FolderFragmentDirections.actionFolderFragmentToFolderNoteListFragment()
      //  findNavController().navigate(action)
    }
    override fun onFolderClick(userFolder: UserFolder) {
        deleteFolderItem(userFolder)
    }

    override fun onFolderItemClick(userFolder: UserFolder) {
        val b = Bundle()
        b.putString(FolderNoteListFragment.FOLDER_NAME , userFolder.newFolder)
         findNavController().navigate(R.id.action_folderFragment2_to_folderNoteListFragment2, b)
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).hideBottomLayout(View.VISIBLE)

    }
}