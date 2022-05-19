package com.example.mediaapp.features.util

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDialogCreateDirectoryBinding

class CreateDirectoryDialogFragment(private val isVisibleRadioGroup:Boolean, private val nameYesButton: String): DialogFragment() {
    private lateinit var binding: FragmentDialogCreateDirectoryBinding
    private var oldNameDirectory : String = ""
    private var clickCreate: ((text:String, radioValue: String) -> Unit)? = null
    private var clickCreateWithoutRadioValue: ((text:String) -> Unit)? =null
    fun setOldNameToEditText(oldName: String){
        Log.d("edit", oldName)
        oldNameDirectory = oldName
    }
    fun setClickCreate(click: (text:String, radioValue: String) -> Unit){
        clickCreate = click
    }
    fun setClickCreateWithoutRadioValue(click: (text: String) -> Unit){
        clickCreateWithoutRadioValue = click
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_create_directory, null, false)
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            binding.buttonCreateDirectory.text = nameYesButton
            binding.editTextCreateDirectory.setText(oldNameDirectory)
            if (!isVisibleRadioGroup){
                binding.radioGroupCreateDirectory.visibility = View.GONE
                binding.buttonCreateDirectory.setOnClickListener {
                    clickCreateWithoutRadioValue?.let { create ->
                        create(binding.editTextCreateDirectory.text.toString().trim())
                    }
                }
            }else{
                binding.radioBtnDocument.isChecked = true
                binding.buttonCreateDirectory.setOnClickListener {
                    clickCreate?.let { create ->
                        val radioId = binding.radioGroupCreateDirectory.checkedRadioButtonId
                        val radioBtn = findViewById<RadioButton>(radioId)
                        create(binding.editTextCreateDirectory.text.toString().trim(), radioBtn.text.toString().trim())
                    }
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