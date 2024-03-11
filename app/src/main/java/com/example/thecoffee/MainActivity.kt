package com.example.thecoffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.thecoffee.databinding.ActivityMainBinding
import com.example.thecoffee.viewmodel.MyViewModelFactory
import com.example.thecoffee.viewmodel.SharedViewModel
import com.example.thecoffee.views.HomeFragment
import com.example.thecoffee.views.OrderFragment
import com.example.thecoffee.views.OtherFragment
import com.example.thecoffee.views.StoreFragment
import com.example.thecoffee.views.VoucherFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(this, R.id.nav_host_fragment_container)
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavView.visibility = if (
                destination.id == R.id.splashFragment
                || destination.id == R.id.loginFragment)
                View.GONE else View.VISIBLE
        }

    }
}

