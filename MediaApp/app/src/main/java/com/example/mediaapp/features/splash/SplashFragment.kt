package com.example.mediaapp.features.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentSplashBinding
import com.example.mediaapp.util.MediaApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private lateinit var binding : FragmentSplashBinding
    private var i = 0
    private val viewModel : SplashViewModel by viewModels {
        SplashViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val isFirstTime = viewModel.getFirstTimeLogin()
//        if(!isFirstTime){
//            findNavController().navigate(R.id.action_splashFragment_to_myPlaceFragment)
//        }

        lifecycleScope.launch {
            while (i<100){
                i+=1
                delay(50)
            }
            findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
        }
    }
}