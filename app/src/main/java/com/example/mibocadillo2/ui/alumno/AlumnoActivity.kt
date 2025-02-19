package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
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

        // Inicializamos el binding
        binding = ActivityAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtén el NavHostFragment y el NavController desde el FragmentContainerView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Configuramos el BottomNavigationView con el NavController
        val bottomNavView: BottomNavigationView = binding.bottomNavigationView
        NavigationUI.setupWithNavController(bottomNavView, navController)
    }
}
