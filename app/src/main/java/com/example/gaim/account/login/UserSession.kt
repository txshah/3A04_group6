package com.example.gaim.account.login

/**
 * Singleton class to track the current logged-in user.
 */
object UserSession {
    private var username: String? = null
    
    /**
     * Sets the current logged-in user.
     */
    fun setUser(username: String) {
        this.username = username
    }
    
    /**
     * Gets the current logged-in user.
     * 
     * @return The username or null if no user is logged in
     */
    fun getUser(): String? {
        return username
    }
    
    /**
     * Clears the current session (logout).
     */
    fun clearSession() {
        username = null
    }
    
    /**
     * Checks if a user is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return username != null
    }
} 