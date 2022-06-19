package com.example.mediaapp.features.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentOnboardingBinding
import com.example.mediaapp.features.MediaApplication

class OnboardingFragment : Fragment() {
    private lateinit var binding:FragmentOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels {
        OnboardingViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstTimeUseApp = viewModel.getFirstTimeUseApp()
        if(!firstTimeUseApp){
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }

        binding.buttonOnboarding.setOnClickListener {
            viewModel.saveFirstTimeUseAppToSharedPref()
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }
    }
}