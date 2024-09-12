package com.example.fotografpaylasma

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // BottomNavigationView ile NavController'i bağla
        bottomNavigationView.setupWithNavController(navController)

        // Giriş sonrası BottomNavigationView'i gösterme
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.profilFragment -> bottomNavigationView.visibility = View.VISIBLE
                R.id.feedFragment, R.id.yuklemeFragment, R.id.profilFragment -> bottomNavigationView.visibility = View.VISIBLE
                else -> bottomNavigationView.visibility = View.GONE
            }
        }
    }
}
