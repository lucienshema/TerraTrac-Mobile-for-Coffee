import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.egnss4coffeev2.R
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.ui.screens.BoughtItemsList

sealed class BottomNavItem(val route: String, val label: String, val icon: Int) {
    object DirectBuy : BottomNavItem("direct_buy", "Direct Buy", R.drawable.save)
    object BuyThroughAkrabi : BottomNavItem("buy_through_akrabi", "Buying Through Akrabi", R.drawable.share)
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
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
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
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShoppingScreen(navController: NavHostController,farmViewModel: FarmViewModel) {
    val nestedNavController = rememberNavController()
    NavHost(
        navController = nestedNavController,
        startDestination = BottomNavItem.DirectBuy.route
    ) {
        composable(BottomNavItem.DirectBuy.route) {
            // DirectBuyScreen()
            BoughtItemsList( farmViewModel = farmViewModel,
                onItemClick = { selectedItem ->
                    navController.navigate("bought_item_detail/${selectedItem.id}")
                })
        }
        composable(BottomNavItem.BuyThroughAkrabi.route) {
            BuyThroughAkrabiScreen()
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}


@Composable
fun DirectBuyScreen() {
    // Implement your Direct Buy UI here
    Text(text = "Direct Buy Screen")
}

@Composable
fun BuyThroughAkrabiScreen() {
    // Implement your Buy Through Akrabi UI here
    Text(text = "Buying Through Akrabi Screen")
}
