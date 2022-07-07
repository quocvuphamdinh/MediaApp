package com.example.mediaapp.features.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDialogViewReceiverBinding
import com.example.mediaapp.features.adapters.AccountAdapter
import com.example.mediaapp.models.User

class ViewReceiverDialogFragment(private val list:List<User>, private val isDelete: Boolean) : DialogFragment() {
    private lateinit var binding : FragmentDialogViewReceiverBinding
    private lateinit var accountAdapter: AccountAdapter
    private var clickItem: ((user: User) -> Unit)? =null
    fun setCLickItem(click: (user: User) -> Unit){
        clickItem = click
    }

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
                clickItem?.let { click ->
                    click(user)
                }
            }
        }, isDelete)
        accountAdapter.submitList(list)
        binding.rcvViewReceiver.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvViewReceiver.adapter = accountAdapter
    }

    fun closeDialog() = dialog?.cancel()
}