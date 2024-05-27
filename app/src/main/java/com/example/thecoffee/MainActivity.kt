package com.example.thecoffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.thecoffee.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(this, R.id.nav_host_fragment_container)
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavView.visibility =
                when (destination.id) {
                    R.id.splashFragment,
                    R.id.loginFragment,
                    R.id.itemDrinkDetailFragment,
                    R.id.homeAdminFragment,
                    R.id.manageOrderAdminFragment,
                    R.id.historyOrderFragment,
                    R.id.managerDrinkAdminFragment,
                    R.id.manageAddDrinkFragment,
                    R.id.manageVoucherAdminFragment,
                    R.id.manageDetailVoucherAdminFragment,
                    -> View.GONE

                    else -> View.VISIBLE
                }
        }

    }
}

