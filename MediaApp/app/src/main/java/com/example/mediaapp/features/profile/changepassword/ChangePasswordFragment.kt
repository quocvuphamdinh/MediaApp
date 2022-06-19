package com.example.mediaapp.features.profile.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.databinding.FragmentChangePasswordBinding
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.util.Constants

class ChangePasswordFragment: Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private val loadingDialogFragment by lazy { LoadingDialogFragment() }
    private val viewModel: ChangePasswordViewModel by viewModels {
        ChangePasswordViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCheckPassword.setOnClickListener {
            clickCheckPassword()
        }
        binding.buttonChangePassword.setOnClickListener {
            clickChangePassword()
        }
        binding.imageViewBack.setOnClickListener{
            findNavController().popBackStack()
        }
        subcribeToObservers()
    }

    private fun clickChangePassword() {
        loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
        val newPassword = binding.edtNewPassword.text.toString().trim()
        val confirmPassword = binding.edtConfirmPassword.text.toString().trim()
        viewModel.changePassword(newPassword, confirmPassword)
    }

    private fun clickCheckPassword() {
        loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
        val password = binding.edtCheckPassword.text.toString().trim()
        viewModel.checkPassword(password)
    }

    private fun subcribeToObservers() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.successCheckPassword.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
            if(it){
                binding.linearCheckpassword.visibility = View.GONE
                binding.linearChangepassword.visibility = View.VISIBLE
            }else{
                binding.linearCheckpassword.visibility = View.VISIBLE
                binding.linearChangepassword.visibility = View.GONE
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
            if(it){
                findNavController().popBackStack()
            }
        })
    }
}