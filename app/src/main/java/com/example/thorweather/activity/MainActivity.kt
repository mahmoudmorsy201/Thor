package com.example.thorweather.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.thorweather.R
import com.example.thorweather.databinding.ActivityMainBinding
import com.example.thorweather.util.ContextUtils
import com.example.thorweather.util.PermissionUtil
import com.example.thorweather.util.SharedPreferencesFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("RestrictedApi")
    override fun attachBaseContext(newBase: Context) {
        val shared = SharedPreferencesFactory(newBase)
        if (shared.getIsLangSet()) {
            val localeToSwitchTo = shared.getLanguage()
            val localeUpdatedContext: ContextWrapper =
                ContextUtils.updateLocale(newBase, Locale(localeToSwitchTo))
            super.attachBaseContext(localeUpdatedContext)
        } else {
            shared.setLanguage(Locale.getDefault().language)
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_favourite,
                R.id.navigation_alert,
                R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        PermissionUtil.checkOverlayPermission(this)

    }
}