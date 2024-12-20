package com.example.cafetrac.database.helpers

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val AGREED_KEY = "has_agreed_to_terms"

    var hasAgreedToTerms: Boolean
        get() = sharedPreferences.getBoolean(AGREED_KEY, false)
        set(value) = sharedPreferences.edit().putBoolean(AGREED_KEY, value).apply()
}
