package com.example.mediaapp.features.util

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.mediaapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WarningDialogFragment(private val title: String, private val message: String): DialogFragment() {
    private var clickYes: (() -> Unit)? = null
    fun setClickYes(click: () -> Unit){
        clickYes = click
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).apply {
            setTitle(title)
            setMessage(message)
            setIcon(R.drawable.ic_baseline_error_24)
            setPositiveButton("Yes") { _, _ ->
                clickYes?.let { click ->
                    click()
                }
            }
            setNegativeButton("No") { _, _ ->
                dialog?.cancel()
            }
        }.create()
    }
}