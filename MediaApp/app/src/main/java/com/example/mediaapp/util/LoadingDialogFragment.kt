package com.example.mediaapp.util

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDialogLoadingBinding


class LoadingDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogLoadingBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_loading, null, false)
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
        }
    }

    fun cancelDialog(){
        dialog?.dismiss()
    }
}