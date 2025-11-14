package com.scoretally.util

/**
 * Abstraction for logging and crash reporting
 * Allows the domain layer to log without depending on specific implementations (Firebase, etc.)
 */
interface Logger {
    /**
     * Log a message
     * @param message Message to log
     */
    fun log(message: String)

    /**
     * Record an exception for crash reporting
     * @param exception Exception to record
     */
    fun recordException(exception: Throwable)
}
