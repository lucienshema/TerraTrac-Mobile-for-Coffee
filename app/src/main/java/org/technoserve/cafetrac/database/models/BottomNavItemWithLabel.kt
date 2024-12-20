package com.example.cafetrac.database.models

import androidx.compose.ui.graphics.vector.ImageVector


data class BottomNavItemWithLabel(
    val route: String,
    val label: String,
    val icon: ImageVector
)

