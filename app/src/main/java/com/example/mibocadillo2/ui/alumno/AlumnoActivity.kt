package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mibocadillo2.R
import com.example.mibocadillo2.databinding.ActivityAlumnoBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlumnoBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el Toolbar como ActionBar
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Configurar NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        // Configurar BottomNavigationView
        val bottomNavView: BottomNavigationView = binding.bottomNavigationView
        NavigationUI.setupWithNavController(bottomNavView, navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                // Navega al ProfileFragment utilizando el NavController
                navController.navigate(R.id.nav_profile)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
