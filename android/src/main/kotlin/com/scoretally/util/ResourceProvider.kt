package com.scoretally.util

import androidx.annotation.StringRes

/**
 * Abstraction for accessing application resources.
 * Allows ViewModels to access string resources without depending on Android Context.
 */
interface ResourceProvider {
    /**
     * Get a localized string resource
     * @param resId String resource ID
     * @return Localized string
     */
    fun getString(@StringRes resId: Int): String

    /**
     * Get a formatted localized string resource
     * @param resId String resource ID
     * @param formatArgs Format arguments
     * @return Formatted localized string
     */
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}
