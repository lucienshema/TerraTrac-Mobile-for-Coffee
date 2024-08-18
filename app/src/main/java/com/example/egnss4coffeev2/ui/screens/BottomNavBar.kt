import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.egnss4coffeev2.R
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.ui.screens.BoughtItemsList
import com.example.egnss4coffeev2.ui.screens.BoughtItemsListDirectBuy

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object DirectBuy : BottomNavItem("direct_buy", "Direct Buy", Icons.Default.ShoppingCart)
    object BuyThroughAkrabi : BottomNavItem("buy_through_akrabi", "Buying Through Akrabi", Icons.Default.ShoppingCart)
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.DirectBuy,
        BottomNavItem.BuyThroughAkrabi
    )

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
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = { Text(item.label,  fontSize = 10.sp, fontWeight = FontWeight.Bold ) },
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
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
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
fun ShoppingScreen(navController: NavHostController, farmViewModel: FarmViewModel) {
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
                    navController
                )
            }
            composable(BottomNavItem.BuyThroughAkrabi.route) {
                // BuyThroughAkrabiScreen()
                BoughtItemsList(
                    farmViewModel = farmViewModel,
                    onItemClick = { selectedItem ->
                        navController.navigate("bought_item_detail/${selectedItem.id}")
                    },
                    navController
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