package com.example.mediaapp.features.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDialogViewReceiverBinding
import com.example.mediaapp.features.adapters.AccountAdapter
import com.example.mediaapp.models.User
import com.example.mediaapp.util.DataStore

class ViewReceiverDialogFragment : DialogFragment() {
    private lateinit var binding : FragmentDialogViewReceiverBinding
    private lateinit var accountAdapter: AccountAdapter

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_view_receiver, null, false)
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)

            setUpRecyclerView()
        }
    }

    private fun setUpRecyclerView(){
        accountAdapter = AccountAdapter(object : AccountAdapter.ClickAccountItem{
            override fun clickItem(user: User) {
                Toast.makeText(requireContext(), "Email: ${user.email}", Toast.LENGTH_SHORT).show()
            }
        })
        accountAdapter.submitList(DataStore.getListUser())
        binding.rcvViewReceiver.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvViewReceiver.adapter = accountAdapter
    }

    fun closeDialog() = dialog?.cancel()
}