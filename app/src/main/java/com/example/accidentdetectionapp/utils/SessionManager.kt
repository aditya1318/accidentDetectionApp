package com.example.accidentdetectionapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.example.accidentdetectionapp.domain.entity.UserEntity
import javax.inject.Inject

class SessionManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AccidentDetectionAppPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val USER_DETAILS = "user_details"
        private const val AUTH_TOKEN = "auth_token"
    }

    // Save auth token
    fun saveAuthToken(token: String) {
        prefs.edit().apply {
            putString(AUTH_TOKEN, token)
            apply()
        }
    }

    // Fetch auth token
    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    // Save user details
    fun saveUserDetails(user: UserEntity) {
        val json = gson.toJson(user)
        prefs.edit().apply {
            putString(USER_DETAILS, json)
            apply()
        }
    }

    // Fetch user details
    fun fetchUserDetails(): UserEntity? {
        val json = prefs.getString(USER_DETAILS, null)
        return if (json != null) gson.fromJson(json, UserEntity::class.java) else null
    }

    // Clear all saved data
    fun clear() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }
}
