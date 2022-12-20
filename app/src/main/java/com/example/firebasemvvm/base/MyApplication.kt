package com.example.firebasemvvm.base

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.firebasemvvm.utils.SessionManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class
MyApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        SessionManager.initializeContext(applicationContext)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}