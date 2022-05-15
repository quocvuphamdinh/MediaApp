package com.example.mediaapp.features.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.features.base.main.MainActivity
import com.example.mediaapp.databinding.FragmentProfileBinding
import com.example.mediaapp.util.MediaApplication

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory((activity?.application as MediaApplication).repository)
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
            findNavController().popBackStack()
        }
        binding.textViewSignOut.setOnClickListener {
            clickLogOut()
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