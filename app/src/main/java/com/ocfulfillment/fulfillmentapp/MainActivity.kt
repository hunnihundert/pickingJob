package com.ocfulfillment.fulfillmentapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
    }


    private fun initToolbar() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val topLevelDestinations = hashSetOf(R.id.loginFragment,R.id.pickJobsFragment)
        val appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations).build()
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

}