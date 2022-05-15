package com.example.mediaapp.features.authenticate.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentRegisterBinding
import com.example.mediaapp.models.User
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.LoadingDialogFragment
import com.example.mediaapp.util.MediaApplication

class RegisterFragment : Fragment() {
    private lateinit var binding:FragmentRegisterBinding
    private lateinit var loadingDialogFragment: LoadingDialogFragment
    private val viewModel : RegisterViewModel by viewModels{
        RegisterViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialogFragment = LoadingDialogFragment()
        binding.textViewLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.buttonSignUp.setOnClickListener {
            loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
            registerAccount()
        }
        subcribeObservers()
    }

    private fun subcribeObservers() {
        viewModel.successRegister.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
        viewModel.errorToast.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
            if(it){
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        })
    }

    private fun registerAccount() {
        val firstName = binding.edtFisrtName.text.toString().trim()
        val lastName = binding.edtLastName.text.toString().trim()
        val userName = binding.edtUsername.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.editTextTextPassword.text.toString().trim()
        val confirmPassword = binding.editTextTextPassword2.text.toString().trim()
        val user = User(firstName, userName, lastName, email, password, confirmPassword)
        if(viewModel.validateRegister(user)){
            viewModel.registerAccount(user)
        }
    }
}