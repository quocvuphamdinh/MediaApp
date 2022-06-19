package com.example.mediaapp.features.base.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.R
import com.example.mediaapp.databinding.ActivityHomeBinding
import com.example.mediaapp.features.search.SearchDialogFragment
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.util.setupWithNavController
import com.example.mediaapp.models.User
import com.example.mediaapp.features.MediaApplication
import com.google.android.material.navigation.NavigationView
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityHomeBinding
    private lateinit var headerLayout: View
    private var currentNavController: LiveData<NavController>? = null
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as MediaApplication).repository)
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MediaApp)
        //setContentView(R.layout.activity_home)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        setupBottomNavigationBar()
        binding.navigationView.setNavigationItemSelectedListener(this)
        if(savedInstanceState!=null){
            val dialogFragmentRun = supportFragmentManager.findFragmentByTag(Constants.SEARCH_DIALOG_TAG) as SearchDialogFragment?
        }
        viewModel.user.observe(this, Observer {
            bindUserDataToView(it)
        })
        currentNavController?.value?.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Constants.CHANGE_ACCOUNT_INFO)?.observe(this) { data ->
            if(data == "OK"){
                viewModel.getAccountInfo()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindUserDataToView(user: User?) {
        user?.let {
            val txtFullName: TextView = headerLayout.findViewById(R.id.textViewFullnameHeader)
            val txtEmail: TextView = headerLayout.findViewById(R.id.textViewEmailHeader)
            val cardView: CardView = headerLayout.findViewById(R.id.cardViewAvatarHeader)
            val txtInside: TextView = headerLayout.findViewById(R.id.textViewInsideAvatarHeader)
            txtFullName.text = it.firstName +" "+it.lastName
            txtEmail.text = it.email
            cardView.setCardBackgroundColor(it.color!!)
            txtInside.text = it.firstName.substring(0, 1).uppercase(Locale.getDefault())+it.lastName.substring(0, 1).uppercase(Locale.getDefault())
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.nav_my_space,
            R.navigation.nav_share_with_me,
            R.navigation.nav_favorite,
            R.navigation.nav_my_share
        )

        headerLayout = binding.navigationView.inflateHeaderView(R.layout.header_nav_view)
        val controller = binding.bottomNav.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.navHostFragmentHome,
            intent
        )

        controller.observe(this, Observer { navController ->
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id){
                    R.id.mySpaceFragment, R.id.shareWithMeFragment, R.id.favoriteFragment, R.id.myShareFragment
                    -> {
                        binding.bottomNav.visibility = View.VISIBLE
                        binding.appbarMain.visibility = View.VISIBLE
                        binding.toolbarMain.visibility = View.VISIBLE
                        binding.navigationView.visibility = View.VISIBLE
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        setUpNavigationDrawer()
                    }
                    R.id.directoryDetailFragment, R.id.directoryDetailFragment2 ->{
                        binding.bottomNav.visibility = View.VISIBLE
                        binding.appbarMain.visibility = View.GONE
                        binding.toolbarMain.visibility = View.GONE
                        binding.navigationView.visibility = View.GONE
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
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
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.profileFrament){
            currentNavController?.value?.navigate(R.id.action_global_profileFragment)
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
}