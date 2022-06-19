package com.example.mediaapp.features.profile

import android.annotation.SuppressLint
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
import com.example.mediaapp.R
import com.example.mediaapp.features.base.main.MainActivity
import com.example.mediaapp.databinding.FragmentProfileBinding
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.models.User
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.MediaApplication
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val loadingDialogFragment by lazy { LoadingDialogFragment() }
    private var user: User? = null
    private var isHaveChangeAccountInfo = "NO"
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewBackProfile.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.CHANGE_ACCOUNT_INFO, isHaveChangeAccountInfo)
            findNavController().popBackStack()
        }
        binding.textViewSignOut.setOnClickListener {
            clickLogOut()
        }
        binding.imageViewSettingProfile.setOnClickListener {
            clickSetting()
        }
        binding.buttonChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }
        subcribeToObservers()
    }

    private fun clickSetting() {
        val bundle = Bundle()
        bundle.putString("firstname", user?.firstName)
        bundle.putString("lastname", user?.lastName)
        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment, bundle)
    }

    private fun subcribeToObservers() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            user = it
            bindUserDataToView(it)
            loadingDialogFragment.cancelDialog()
        })
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Constants.CHANGE_ACCOUNT_INFO)?.observe(viewLifecycleOwner) { data ->
            if(data == "OK"){
                isHaveChangeAccountInfo = "OK"
                viewModel.getAccountInfo()
            }else{
                isHaveChangeAccountInfo = "NO"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindUserDataToView(user: User?) {
        user?.let {
            binding.textViewFullNameProfile.text = "${user.firstName} ${user.lastName}"
            binding.textViewUsernameProfile.text = user.username
            binding.textViewEmailProfile.text = user.email
            binding.textViewInsideAvatarProfile.text = user.firstName.substring(0, 1)
                .uppercase(Locale.getDefault()) + user.lastName.substring(0, 1).uppercase(Locale.getDefault())
            binding.cardViewAvatarProfile.setCardBackgroundColor(user.color!!)
        }
    }

    private fun clickLogOut() {
        viewModel.removeUserDataFromSharedPref()
        val i = Intent(context, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        activity?.overridePendingTransition(0, 0)
    }
}