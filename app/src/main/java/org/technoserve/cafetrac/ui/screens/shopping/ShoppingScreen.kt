package com.example.cafetrac.ui.screens.shopping

import BottomNavBar
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cafetrac.database.models.Language
import com.example.cafetrac.ui.screens.directbuy.BoughtItemsList
import org.technoserve.cafetrac.ui.screens.directbuy.BoughtItemsListDirectBuy
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetrac.viewmodels.LanguageViewModel

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