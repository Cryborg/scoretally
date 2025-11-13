package com.scoretally.domain.model

data class AuthState(
    val isSignedIn: Boolean = false,
    val userId: String? = null,
    val userEmail: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null
)
