package com.example.wellfish.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
//import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wellfish.R
//import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.data.pref.UserPreference
import com.example.wellfish.data.pref.dataStore
import com.example.wellfish.databinding.ActivityMainBinding
import com.example.wellfish.ui.camera.CameraFragment
import com.example.wellfish.ui.camera.FishFragment
import com.example.wellfish.ui.history.HistoryFragment
import com.example.wellfish.ui.home.HomeFragment
import com.example.wellfish.ui.onboarding.OnboardingActivity
import com.example.wellfish.ui.setting.SettingFragment
import com.example.wellfish.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("onboarding_pref", MODE_PRIVATE) //onboarding flag start

        replaceFragment(HomeFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.tab_home -> replaceFragment(HomeFragment())
                R.id.tab_fish -> replaceFragment(FishFragment())
                R.id.tab_camera -> replaceFragment(CameraFragment())
                R.id.tab_history -> replaceFragment(HistoryFragment())
                R.id.tab_setting -> replaceFragment(SettingFragment())

                else -> { }
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            checkOnboardingAndLoginState()
        }
    }

    private suspend fun checkOnboardingAndLoginState() {
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        val isLoggedIn = isLoggedIn()

        Log.d("MainActivity", "isFirstTime: $isFirstTime, isLoggedIn: $isLoggedIn")

        if (isFirstTime) {
            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
            val intent = Intent(this@MainActivity, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        } else if (!isLoggedIn) {
            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private suspend fun isLoggedIn(): Boolean {
        val userPref = UserPreference.getInstance(dataStore)
        val session = userPref.getSession().first()
        return session.isLogin
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}