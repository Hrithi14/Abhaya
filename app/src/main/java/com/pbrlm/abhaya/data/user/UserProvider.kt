package com.pbrlm.abhaya.data.user

/**
 * Provides the current user ID for emergency requests.
 *
 * Currently returns a mock user ID for development.
 * Replace with FirebaseAuth.currentUser?.uid when authentication is integrated.
 *
 * Every emergency request MUST have a userId — this is the join point
 * for the authentication module.
 */
interface UserProvider {
    fun getCurrentUserId(): String
    fun getCurrentUserName(): String
    fun getCurrentUserPhone(): String
    fun isLoggedIn(): Boolean
}

/**
 * Mock implementation for development — returns a stable demo user.
 * Replace with FirebaseUserProvider when auth is integrated.
 */
class MockUserProvider : UserProvider {
    override fun getCurrentUserId(): String = "user-demo-001"
    override fun getCurrentUserName(): String = "Demo User"
    override fun getCurrentUserPhone(): String = ""
    override fun isLoggedIn(): Boolean = true
}
