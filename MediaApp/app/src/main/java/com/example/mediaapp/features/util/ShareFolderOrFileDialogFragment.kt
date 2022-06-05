package com.example.mediaapp.features.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDialogSearchBinding
import com.example.mediaapp.databinding.FragmentDialogShareBinding

class ShareFolderOrFileDialogFragment : DialogFragment() {
    private lateinit var binding : FragmentDialogShareBinding
    private var clickToShare: ((email: String) -> Unit)? = null

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_share, null, false)
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            binding.btnDialogShare.setOnClickListener {
                clickToShare?.let { click ->
                    click(binding.editTextSearch.text.toString().trim())
                }
            }
        }
    }

    fun setClickToShare(click:(email: String) -> Unit){
        clickToShare = click
    }
    fun closeDialog() = dialog?.cancel()
}