package com.example.mediaapp.features.profile.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.databinding.FragmentEditProfileBinding
import com.example.mediaapp.models.User
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.util.Constants

class EditProfileFragment: Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private val loadingDialogFragment by lazy { LoadingDialogFragment() }
    private var user: User? = null
    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewBack.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.CHANGE_ACCOUNT_INFO, "NO")
            findNavController().popBackStack()
        }
        binding.buttonSaveAccountInfo.setOnClickListener {
            clickChangeAccountInfo()
        }
        getUserData()
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
            if (it){
                findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.CHANGE_ACCOUNT_INFO, "OK")
                findNavController().popBackStack()
            }
        })
    }

    private fun clickChangeAccountInfo() {
        loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
        val firstName = binding.edtFisrtName.text.toString().trim()
        val lastName = binding.edtLastName.text.toString().trim()
        viewModel.changeAccountInfo(firstName, lastName)
    }

    private fun getUserData() {
        val bundle = arguments
        bundle?.let {
            user = User(
                it.getString("firstname")!!,
                "",
                it.getString("lastname")!!,
                "",
                "",
                ""
            )
            binding.edtFisrtName.setText(user?.firstName)
            binding.edtLastName.setText(user?.lastName)
        }
    }
}