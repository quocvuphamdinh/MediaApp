package com.example.mediaapp.features.util

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentBottomSheetOptionBinding
import com.example.mediaapp.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetOptionFragment(private val isDirectory: Boolean, private val rootType: Int): BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetOptionBinding
    private var clickCreateNewFolder: (() -> Unit)? = null
    private var clickCreateNewFile: (() -> Unit)? = null
    private var clickShare: (() -> Unit)? = null
    private var clickAddToFavorite: (() -> Unit)? = null
    private var clickEdit: (() -> Unit)? = null
    private var clickDelete: (() -> Unit)? = null
    private var titleName: String? = null

    fun setTitleName(name: String){
        titleName = name
    }
    fun setClickCreateNewFolder(click: () -> Unit){
        clickCreateNewFolder = click
    }
    fun setClickCreateNewFile(click: () -> Unit){
        clickCreateNewFile = click
    }
    fun setClickShare(click: () -> Unit){
        clickShare = click
    }
    fun setClickAddToFavorite(click: () -> Unit){
        clickAddToFavorite = click
    }
    fun setClickEdit(click: () -> Unit){
        clickEdit = click
    }
    fun setClickDelete(click: () -> Unit){
        clickDelete = click
    }
    fun closeBottomSheet(){
        dialog?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_bottom_sheet_option, null, false)
        return BottomSheetDialog(requireContext()).apply {
            setCanceledOnTouchOutside(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            binding.textViewTitleBottomSheet.text = titleName
            if(isDirectory && rootType==Constants.MY_SPACE){
                binding.linearCreateFolderOption.visibility = View.VISIBLE
                binding.linearCreateFolderOption.setOnClickListener {
                    clickCreateNewFolder?.let { createNewFolder ->
                        createNewFolder()
                    }
                }
                binding.linearCreateFileOption.visibility = View.VISIBLE
                binding.linearCreateFileOption.setOnClickListener {
                    clickCreateNewFile?.let { createNewFile ->
                        createNewFile()
                    }
                }
            }else{
                binding.linearCreateFolderOption.visibility = View.GONE
                binding.linearCreateFileOption.visibility = View.GONE
            }
            if(rootType != Constants.SHARE_WITH_ME){
                binding.linearShareOption.visibility = View.VISIBLE
                binding.linearShareOption.setOnClickListener {
                    clickShare?.let { share ->
                        share()
                    }
                }
            }else{
                binding.linearShareOption.visibility = View.GONE
            }
            if(rootType!= Constants.FAVORITE){
                binding.linearFavoriteOption.visibility = View.VISIBLE
                binding.linearFavoriteOption.setOnClickListener {
                    clickAddToFavorite?.let { addToFavorite ->
                        addToFavorite()
                    }
                }
            }else{
                binding.linearFavoriteOption.visibility = View.GONE
            }
            if (rootType==Constants.MY_SPACE){
                binding.linearEditOption.visibility = View.VISIBLE
                binding.linearEditOption.setOnClickListener {
                    clickEdit?.let { edit ->
                        edit()
                    }
                }
            }else{
                binding.linearEditOption.visibility = View.GONE
            }
            binding.linearDeleteOption.setOnClickListener {
                clickDelete?.let { delete ->
                    delete()
                }
            }
        }
    }
}