package com.example.mediaapp.features.base.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.mediaapp.R
import com.example.mediaapp.databinding.ActivityMainBinding
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.features.MediaApplication

class MainActivity : AppCompatActivity(){
    private lateinit var binding : ActivityMainBinding
    private lateinit var navHostFragment:NavHostFragment
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as MediaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MediaApp)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val firstTimeLogin = viewModel.getFirstTimeLogin()
        if(!firstTimeLogin){
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            this.finish()
            overridePendingTransition(0, 0)
        }

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragmentMain) as NavHostFragment
        val navController = navHostFragment.navController
    }
}