package com.example.mediaapp.features.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDialogSearchBinding
import com.example.mediaapp.features.adapters.AccountAdapter
import com.example.mediaapp.models.User

class SearchAccountDialogFragment : DialogFragment() {
    private lateinit var binding : FragmentDialogSearchBinding
    private lateinit var accountAdapter: AccountAdapter
    private var clickAccountItem: ((user: User) -> Unit)? = null
    private var textChangeListener: ((keyword: String) -> Unit)? = null
    var accounts: MutableLiveData<List<User>> = MutableLiveData()

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_search, null, false)
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            setUpEditText()
            setUpRecyclerViewSearch()
            textChangeListener?.let { textChange ->
                textChange(binding.editTextSearch.text.toString().trim())
            }
            accounts.observe(this@SearchAccountDialogFragment, Observer {
                accountAdapter.submitList(it)
            })
        }
    }

    fun setClickAccountItem(click: (user: User) -> Unit){
        clickAccountItem = click
    }
    fun setTextChangeListener(textChange: (keyword: String) -> Unit){
        textChangeListener = textChange
    }

    private fun setUpEditText(){
        binding.editTextSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textChangeListener?.let { textChange ->
                    textChange(s.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
    private fun setUpRecyclerViewSearch(){
        accountAdapter = AccountAdapter(object : AccountAdapter.ClickAccountItem{
            override fun clickItem(user: User) {
                clickAccountItem?.let { click ->
                    click(user)
                }
            }
        })
        binding.rcvSearch.layoutManager= LinearLayoutManager(requireContext())
        binding.rcvSearch.adapter = accountAdapter
    }
}