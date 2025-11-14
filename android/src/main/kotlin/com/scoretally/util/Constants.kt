package com.scoretally.util

/**
 * Application-wide constants
 */
object Constants {
    /**
     * StateFlow timeout in milliseconds before stopping collection
     * when there are no active subscribers
     */
    const val STATE_FLOW_TIMEOUT_MILLIS = 5000L

    /**
     * Delay in milliseconds to wait for sync worker to execute
     * TODO: Replace with proper WorkManager status observation
     */
    const val SYNC_WORKER_DELAY_MILLIS = 2000L

    /**
     * BoardGameGeek API Bearer Token
     * Register at https://boardgamegeek.com/applications to obtain a token
     * TODO: Add your BGG API token here when received
     */
    const val BGG_API_TOKEN = ""
}
