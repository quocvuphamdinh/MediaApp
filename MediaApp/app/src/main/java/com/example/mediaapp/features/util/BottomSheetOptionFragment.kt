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

class BottomSheetOptionFragment(
    private val isDirectory: Boolean,
    private val rootType: Int,
    private val isDetail: Boolean=false): BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetOptionBinding
    private var clickCreateNewFolder: (() -> Unit)? = null
    private var clickCreateNewFile: (() -> Unit)? = null
    private var clickShare: (() -> Unit)? = null
    private var clickAddToFavorite: (() -> Unit)? = null
    private var clickEdit: (() -> Unit)? = null
    private var clickDelete: (() -> Unit)? = null
    private var clickDownload: (() -> Unit)? = null
    private var clickViewReceiver: (() -> Unit)? = null
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
    fun setClickDownload(click: () -> Unit){
        clickDownload = click
    }
    fun setClickViewReceiver(click: () -> Unit){
        clickViewReceiver = click
    }
    fun closeBottomSheet(){
        dialog?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_bottom_sheet_option, null, false)
        return BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog).apply {
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
                if(rootType!= Constants.MY_SHARE){
                    binding.linearShareOption.visibility = View.VISIBLE
                    binding.linearShareOption.setOnClickListener {
                        clickShare?.let { share ->
                            share()
                        }
                    }
                }else{
                    binding.linearShareOption.visibility = View.GONE
                }
            }else{
                binding.linearShareOption.visibility = View.GONE
            }
            if(rootType!= Constants.FAVORITE){
                if(rootType!= Constants.MY_SHARE){
                    binding.linearFavoriteOption.visibility = View.VISIBLE
                    binding.linearFavoriteOption.setOnClickListener {
                        clickAddToFavorite?.let { addToFavorite ->
                            addToFavorite()
                        }
                    }
                }else{
                    binding.linearFavoriteOption.visibility = View.GONE
                }
            }else{
                binding.linearFavoriteOption.visibility = View.GONE
            }
            if (rootType==Constants.MY_SPACE && isDirectory){
                binding.linearEditOption.visibility = View.VISIBLE
                binding.linearEditOption.setOnClickListener {
                    clickEdit?.let { edit ->
                        edit()
                    }
                }
            }else{
                binding.linearEditOption.visibility = View.GONE
            }
            if((isDetail && rootType==Constants.SHARE_WITH_ME) || (isDetail && rootType==Constants.MY_SHARE) || (isDetail && rootType==Constants.FAVORITE)){
                binding.linearDeleteOption.visibility = View.GONE
            }else{
                binding.linearDeleteOption.visibility = View.VISIBLE
                binding.linearDeleteOption.setOnClickListener {
                    clickDelete?.let { delete ->
                        delete()
                    }
                }
            }
            if(!isDirectory && rootType != Constants.MY_SHARE){
                binding.linearDownloadOption.visibility = View.VISIBLE
                binding.linearDownloadOption.setOnClickListener {
                    clickDownload?.let { download ->
                        download()
                    }
                }
            }else{
                binding.linearDownloadOption.visibility = View.GONE
            }
            if(rootType == Constants.MY_SHARE){
                binding.linearViewReceiverOption.visibility = View.VISIBLE
                binding.linearViewReceiverOption.setOnClickListener {
                    clickViewReceiver?.let { viewReceiver ->
                        viewReceiver()
                    }
                }
            }else{
                binding.linearViewReceiverOption.visibility = View.GONE
            }
        }
    }
}