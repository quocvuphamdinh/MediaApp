package com.example.mediaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mediaapp.databinding.ActivityMainBinding
import com.example.mediaapp.features.search.SearchDialogFragment
import com.example.mediaapp.util.Constants
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding : ActivityMainBinding
    private lateinit var navHostFragment:NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if(savedInstanceState!=null){
            val dialogFragmentRun = supportFragmentManager.findFragmentByTag(Constants.SEARCH_DIALOG_TAG) as SearchDialogFragment?
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.mySpaceFragment, R.id.shareWithMeFragment, R.id.favoriteFragment, R.id.deleteFragment
                -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.appbarMain.visibility = View.VISIBLE
                    binding.toolbarMain.visibility = View.VISIBLE
                    binding.navigationView.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    setUpNavigationDrawer()
                }
                else -> {
                    binding.bottomNav.visibility = View.GONE
                    binding.appbarMain.visibility = View.GONE
                    binding.toolbarMain.visibility = View.GONE
                    binding.navigationView.visibility = View.GONE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_action_Drawer -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.END)
                }
            }
            R.id.menu_action_search -> {
                showDialogSearch()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showDialogSearch(){
        SearchDialogFragment().show(supportFragmentManager, Constants.SEARCH_DIALOG_TAG)
    }

    private fun setUpNavigationDrawer() {
        setSupportActionBar(binding.toolbarMain)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.profileFrament){
            navHostFragment.findNavController().navigate(R.id.action_global_profileFragment)
            return true
        }
        return false
    }
}