package com.example.wellfish.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.wellfish.R
import com.example.wellfish.databinding.ActivityOnboardingBinding
import com.example.wellfish.ui.utils.Animation
import com.example.wellfish.ui.welcome.WelcomeActivity
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager2
    private lateinit var textSkip: TextView
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mViewPager = binding.viewPager
        mViewPager.adapter = OnboardingViewPagerAdapter(this, this)
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()

        textSkip = findViewById(R.id.text_skip)
        textSkip.setOnClickListener {
            navigateToWelcome()
        }

        val btnNextStep: Button = findViewById(R.id.btn_next_step)
        btnNextStep.setOnClickListener {
            if (getItem() > mViewPager.childCount) {
                navigateToWelcome()
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }
    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

    //directing to welcome page
    private fun navigateToWelcome() {
        finish()
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        Animation.animateSlideLeft(this)
    }
}
