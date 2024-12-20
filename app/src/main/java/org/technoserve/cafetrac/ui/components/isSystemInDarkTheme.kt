package org.technoserve.cafetrac.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun isSystemInDarkTheme(): Boolean {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("dark_mode", false)
}
