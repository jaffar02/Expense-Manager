package com.alphadev.expensemanager.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alphadev.expensemanager.R

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        run {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                Thread.sleep(3000)
            }
            startActivity(Intent(this@Splash, Dashboard::class.java))
        }
    }
}