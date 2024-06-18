package com.example.wellfish.ui.main

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wellfish.R
//import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.data.pref.UserPreference
import com.example.wellfish.data.pref.dataStore
import com.example.wellfish.databinding.ActivityMainBinding
import com.example.wellfish.ui.camera.CameraFragment
import com.example.wellfish.ui.camera.FishFragment
import com.example.wellfish.ui.camera.ObjectDetectorHelper
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
    private var isGuest: Boolean = false
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("onboarding_pref", MODE_PRIVATE) //onboarding flag start

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        isGuest = intent.getBooleanExtra("IS_GUEST", false)

        replaceFragment(HomeFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.tab_home -> replaceFragment(HomeFragment())
                R.id.tab_fish -> replaceFragment(FishFragment())
                R.id.tab_camera -> startCameraX() //replaceFragment(CameraFragment())
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
        } else if (!isLoggedIn && !isGuest) {
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

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun startCameraX() {
        replaceFragment(CameraFragment())
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}