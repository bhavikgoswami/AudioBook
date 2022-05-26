package com.book.audiobook.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.book.audiobook.R
import com.book.audiobook.databinding.MainActivityBinding
import com.book.audiobook.ui.fragment.HomeFragment
import com.book.audiobook.ui.fragment.LibraryFragment
import com.book.audiobook.ui.fragment.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


       val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupWithNavController(binding.bottomNavigationView, navController)
        //NavigationUI.setupWithNavController( navController)
        /*navController.addOnDestinationChangedListener { _, destination, bundle ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.libraryFragment,
                -> {
                    R.id.homeFragment
                }
            }
        }

        binding.bottomNavigationView.setupWithNavController(navController)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)*/
    }


}