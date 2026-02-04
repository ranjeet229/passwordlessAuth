package com.example.passwordlessauth.analytics

import timber.log.Timber

object TimberLogger {
    fun init() {
        Timber.plant(Timber.DebugTree())
    }
}
