package com.priomkhan.salahtime.ui.main

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.priomkhan.salahtime.R

class MainActivity : AppCompatActivity() {
    //** Member variables to hold the name of the shared preferences file
    private lateinit var mPreferences: SharedPreferences
    //** Name your shared preferences file
    private val sharedPrefFile : String = "com.priomkhan.salahtime"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //**  Initialize the shared preferences
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        //navView.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.transparent));

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_next_prayer, R.id.navigation_prayer_time, R.id.qibla_compass
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }




}