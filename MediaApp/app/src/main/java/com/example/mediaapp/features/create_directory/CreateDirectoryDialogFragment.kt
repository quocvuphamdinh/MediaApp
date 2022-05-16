package com.example.mediaapp.features.create_directory

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDialogCreateDirectoryBinding

class CreateDirectoryDialogFragment: DialogFragment() {
    private lateinit var binding: FragmentDialogCreateDirectoryBinding
    private var clickCreate: ((text:String) -> Unit)? = null
    fun setClickCreate(click: (text:String) -> Unit){
        clickCreate = click
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_create_directory, null, false)
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            binding.buttonCreateDirectory.setOnClickListener {
                clickCreate?.let { create ->
                    create(binding.editTextCreateDirectory.text.toString().trim())
                }
            }
            binding.buttonCancelDirectory.setOnClickListener {
                dialog?.cancel()
            }
        }
    }
    fun cancelDialog(){
        dialog?.cancel()
    }
}