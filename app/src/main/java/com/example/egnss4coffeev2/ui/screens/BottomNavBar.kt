import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.ui.screens.BoughtItemsList
import com.example.egnss4coffeev2.ui.screens.BoughtItemsListDirectBuy
import com.example.egnss4coffeev2.utils.Language
import com.example.egnss4coffeev2.utils.LanguageViewModel
import com.example.egnss4coffeev2.R
import com.example.egnss4coffeev2.ui.screens.isSystemInDarkTheme


sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object DirectBuy : BottomNavItem("direct_buy", Icons.Default.ShoppingCart)
    object BuyThroughAkrabi : BottomNavItem("buy_through_akrabi", Icons.Default.ShoppingCart)
}

@Composable
fun getBottomNavItems(): List<BottomNavItemWithLabel> {
    return listOf(
        BottomNavItemWithLabel(
            route = BottomNavItem.DirectBuy.route,
            label = stringResource(id = R.string.direct_buy),
            icon = BottomNavItem.DirectBuy.icon
        ),
        BottomNavItemWithLabel(
            route = BottomNavItem.BuyThroughAkrabi.route,
            label = stringResource(id = R.string.buy_through_akrabi),
            icon = BottomNavItem.BuyThroughAkrabi.icon
        )
    )
}

data class BottomNavItemWithLabel(
    val route: String,
    val label: String,
    val icon: ImageVector
)

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

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShoppingScreen(navController: NavHostController, farmViewModel: FarmViewModel, darkMode: MutableState<Boolean>, languageViewModel: LanguageViewModel, languages: List<Language>) {
    val nestedNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = nestedNavController)
        }
    ) {
        NavHost(
            navController = nestedNavController,
            startDestination = BottomNavItem.DirectBuy.route
        ) {
            composable(BottomNavItem.DirectBuy.route) {
                // DirectBuyScreen()
                BoughtItemsListDirectBuy(
                    farmViewModel = farmViewModel,
                    onItemClick = { selectedItem ->
                        navController.navigate("bought_item_detail/${selectedItem.id}")
                    },
                    navController,
                    darkMode,
                    languageViewModel=languageViewModel,
                    languages
                )
            }
            composable(BottomNavItem.BuyThroughAkrabi.route) {
                // BuyThroughAkrabiScreen()
                BoughtItemsList(
                    farmViewModel = farmViewModel,
                    onItemClick = { selectedItem ->
                        navController.navigate("bought_item_detail/${selectedItem.id}")
                    },
                    navController,
                    darkMode,
                    languageViewModel=languageViewModel,
                    languages
                )
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}