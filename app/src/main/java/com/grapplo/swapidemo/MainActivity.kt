package com.grapplo.swapidemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val navController by lazy {
        findNavController(R.id.nav_host_fragment)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
        toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }
}
