package org.technoserve.cafetrac.ui.screens.bottomnavbar


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.technoserve.cafetrac.ui.components.getBottomNavItems
import org.technoserve.cafetrac.ui.components.isSystemInDarkTheme


sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object DirectBuy : BottomNavItem("direct_buy", Icons.Default.ShoppingCart)
    object BuyThroughAkrabi : BottomNavItem("buy_through_akrabi", Icons.Default.ShoppingCart)
}



@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = getBottomNavItems()
    val isDarkMode = isSystemInDarkTheme()
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp),
                        tint = if (currentRoute == item.route) {
                            Color.White
                        } else {
                            if (isDarkMode) Color.Gray else Color.DarkGray
                        }
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // popUpTo the immediate parent of the current route
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isDarkMode) Color.White else Color.Black,
                    unselectedIconColor = if (isDarkMode) Color.Gray else Color.DarkGray,
                    selectedTextColor = if (isDarkMode) Color.White else Color.Black,
                    unselectedTextColor = if (isDarkMode) Color.Gray else Color.DarkGray,
                    indicatorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}



