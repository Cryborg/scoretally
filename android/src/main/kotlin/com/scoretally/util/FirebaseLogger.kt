package com.scoretally.util

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of Logger interface
 */
@Singleton
class FirebaseLogger @Inject constructor() : Logger {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun recordException(exception: Throwable) {
        crashlytics.recordException(exception)
    }
}
