
package com.example.cafetrac.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.technoserve.cafetrac.ui.screens.settings.BottomSidebar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenWithSidebar(
    navController: NavController, content: @Composable () -> Unit
) {
    Scaffold(bottomBar = { BottomSidebar(navController) }) {
        content()
    }
}
