package com.example.mediaapp.features.authenticate.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentLoginBinding
import com.example.mediaapp.models.User
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.LoadingDialogFragment
import com.example.mediaapp.util.MediaApplication

class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private lateinit var loadingDialogFragment: LoadingDialogFragment
    private val viewModel :  LoginViewModel by viewModels{
        LoginViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialogFragment = LoadingDialogFragment()

        binding.textViewRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonLogin.setOnClickListener {
            loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
            login()
        }

        subcribeToObservers()
    }

    private fun subcribeToObservers() {
        viewModel.loginNotify.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        viewModel.isSuccess.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
            if(it){
                startActivity(Intent(context, HomeActivity::class.java))
                activity?.finish()
                activity?.overridePendingTransition(0, 0)
            }
        })
    }

    private fun login() {
        val username = binding.editTextUsername.text.toString()
        val password = binding.editTextPassword.text.toString()
        val user = User("", username, "", "", password, "")
        viewModel.login(user)
    }
}