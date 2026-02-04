package com.example.passwordlessauth

import android.app.Application
import com.example.passwordlessauth.analytics.TimberLogger

class PasswordlessAuthApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TimberLogger.init()
    }
}
