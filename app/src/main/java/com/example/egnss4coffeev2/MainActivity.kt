package com.example.egnss4coffeev2



import BottomNavBar
import BuyThroughAkrabiScreen
import DirectBuyScreen
import ShoppingScreen
import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.navArgument
import com.example.egnss4coffeev2.database.Akrabi
import com.example.egnss4coffeev2.database.AkrabiViewModel
import com.example.egnss4coffeev2.database.AkrabiViewModelFactory
import com.example.egnss4coffeev2.database.CollectionSite
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.database.FarmViewModelFactory
import com.example.egnss4coffeev2.database.sync.SyncService
import com.example.egnss4coffeev2.map.MapViewModel
import com.example.egnss4coffeev2.ui.screens.AddFarm
import com.example.egnss4coffeev2.ui.screens.AddSite
import com.example.egnss4coffeev2.ui.screens.AkrabiListScreenScreen
import com.example.egnss4coffeev2.ui.screens.BoughtItemDetailScreen
import com.example.egnss4coffeev2.ui.screens.BoughtItemsList
import com.example.egnss4coffeev2.ui.screens.BuyThroughAkrabiForm
import com.example.egnss4coffeev2.ui.screens.CollectionSiteList
import com.example.egnss4coffeev2.ui.screens.CreateAkrabiFormScreen
import com.example.egnss4coffeev2.ui.screens.DirectBuyForm
import com.example.egnss4coffeev2.ui.screens.EditAkrabiScreen
import com.example.egnss4coffeev2.ui.screens.FarmList
import com.example.egnss4coffeev2.ui.screens.Home
import com.example.egnss4coffeev2.ui.screens.ScreenWithSidebar
import com.example.egnss4coffeev2.ui.screens.SetPolygon
import com.example.egnss4coffeev2.ui.screens.SettingsScreen
import com.example.egnss4coffeev2.ui.screens.UpdateFarmForm
import com.example.egnss4coffeev2.ui.theme.FarmCollectorThemeV2
import com.example.egnss4coffeev2.utils.LanguageViewModel
import com.example.egnss4coffeev2.utils.LanguageViewModelFactory
import com.example.egnss4coffeev2.utils.getLocalizedLanguages
import com.example.egnss4coffeev2.utils.updateLocale
import currentRoute
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: MapViewModel by viewModels()
    private val languageViewModel: LanguageViewModel by viewModels {
        LanguageViewModelFactory(application)
    }
    private val sharedPreferences by lazy {
        getSharedPreferences("theme_mode", MODE_PRIVATE)
    }
    private val sharedPref by lazy {
        getSharedPreferences("FarmCollector", MODE_PRIVATE)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val darkMode = mutableStateOf(sharedPreferences.getBoolean("dark_mode", false))

        // Apply the selected theme
        if (darkMode.value) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //        remove plot_size from shared preferences if it exists
        if (sharedPref.contains("plot_size")) {
            sharedPref.edit().remove("plot_size").apply()
        }

        // remove selected unit from shared preferences if it exists
        if (sharedPref.contains("selectedUnit")) {
            sharedPref.edit().remove("selectedUnit").apply()
        }



        // Start the service when the activity is created
        startSyncService()
        setContent {
            val navController = rememberNavController()
            val currentLanguage by languageViewModel.currentLanguage.collectAsState()

            LaunchedEffect(currentLanguage) {
                updateLocale(context = applicationContext, Locale(currentLanguage.code))
            }

            FarmCollectorThemeV2(darkTheme = darkMode.value) {
                // Determine appropriate permissions based on the SDK version
                val permissions = remember {
                    mutableListOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                    ).apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            // For API level 33 and above, use the new READ_MEDIA_IMAGES permission
                            add(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            // For API levels below 33, use READ_EXTERNAL_STORAGE
                            add(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        // WRITE_EXTERNAL_STORAGE is deprecated but still used for API levels where it applies
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                }

                val multiplePermissionsState = rememberMultiplePermissionsState(permissions)

                LaunchedEffect(Unit) {
                    // Launch the permission request
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val languages = getLocalizedLanguages(applicationContext)
                    val farmViewModel: FarmViewModel = viewModel(
                        factory = FarmViewModelFactory(applicationContext as Application)
                    )
                    val akrabiViewModel: AkrabiViewModel = viewModel(
                        factory = AkrabiViewModelFactory(applicationContext as Application)
                    )
                    val listItems by farmViewModel.readData.observeAsState(listOf())


                    val collectionSites by farmViewModel.readAllSites.observeAsState(emptyList())
                    var akrabis by remember { mutableStateOf(listOf<Akrabi>()) }

                    Scaffold(
                        bottomBar = {
                            val currentRoute = currentRoute(navController)
                            if (currentRoute == "shopping" ||
                                currentRoute == BottomNavItem.DirectBuy.route ||
                                currentRoute == BottomNavItem.BuyThroughAkrabi.route) {
                                BottomNavBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") {
                                Home(navController, languageViewModel, languages)
                            }
                            composable("siteList") {
                                ScreenWithSidebar(navController) {
                                    CollectionSiteList(navController)
                                }
                            }
                            composable("farmList/{siteId}") { backStackEntry ->
                                val siteId = backStackEntry.arguments?.getString("siteId")
                                if (siteId != null) {
                                    ScreenWithSidebar(navController) {
                                    FarmList(
                                        navController = navController, siteId = siteId.toLong()
                                    )
                                    }
                                }
                            }
                            composable("addFarm/{siteId}") { backStackEntry ->
                                val siteId = backStackEntry.arguments?.getString("siteId")
                                if (siteId != null) {
                                    AddFarm(navController = navController, siteId = siteId.toLong())
                                }
                            }
                            composable("addSite") {
                                AddSite(navController)
                            }
                            composable("shopping") {
                                ShoppingScreen(navController, farmViewModel = farmViewModel)
                            }

                            composable("create_akrabi_form") {
                                CreateAkrabiFormScreen(navController,akrabiViewModel)
                            }
                            composable("akrabi_list_screen") {
                                AkrabiListScreenScreen(navController)
                            }

                            // Add this composable for the edit Akrabi screen
                            composable("edit_akrabi_form/{akrabiId}") { backStackEntry ->
                                val akrabiId = backStackEntry.arguments?.getString("akrabiId")?.toLong()
                                if (akrabiId != null) {
                                    EditAkrabiScreen(
                                        akrabiId = akrabiId,
                                        viewModel = akrabiViewModel,
                                        navController = navController
                                    )
                                } else {
                                    Text("Error: Invalid Akrabi ID")
                                }
                            }

                            composable(BottomNavItem.DirectBuy.route) {
                                //DirectBuyScreen()
                                DirectBuyForm(
                                    collectionSites = collectionSites, // List of CollectionSite objects
                                    farmers = listItems, // List of Farm objects
                                    onSubmit = { directBuy ->
                                        // Handle the submission of the DirectBuy form, e.g., save to database
                                        farmViewModel.insertBoughtItemDirect(directBuy)
                                    },
                                    navController = navController // Pass the NavController
                                )
                            }
                            composable(BottomNavItem.BuyThroughAkrabi.route) {
                                //BuyThroughAkrabiScreen()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    BuyThroughAkrabiForm(
                                        collectionSites = collectionSites,
                                        akrabis = akrabis,
                                        onCreateAkrabi = { newAkrabi ->
                                            // Navigate to CreateAkrabiForm
                                            navController.navigate("create_akrabi_form")
                                        },
                                        onSubmit = { buyThroughAkrabi ->
                                            farmViewModel.insertBoughtItem(buyThroughAkrabi)
                                            // Handle form submission
                                            Log.d(
                                                "BuyThroughAkrabiForm",
                                                "Form submitted: $buyThroughAkrabi"
                                            )
                                            // Hide the form after submission
//                                                isFormVisible = false
                                        },
                                        navController
                                    )
                                }
                            }

                            composable("bought_items") {
                                BoughtItemsList(
                                    farmViewModel = farmViewModel,
                                    onItemClick = { selectedItem ->
                                        navController.navigate("bought_item_detail/${selectedItem.id}")
                                    }
                                )
                            }

                            composable(
                                route = "bought_item_detail/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getLong("id")
                                if (id != null) {
                                    BoughtItemDetailScreen(
                                        itemId = id,
                                        farmViewModel = farmViewModel,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                } else {
                                    // Handle error case, perhaps navigate back or show an error message
                                    Text("Error: Invalid item ID")
                                }
                            }

                            composable("updateFarm/{farmId}") { backStackEntry ->
                                val farmId = backStackEntry.arguments?.getString("farmId")
                                if (farmId != null) {
                                    UpdateFarmForm(
                                        navController = navController,
                                        farmId = farmId.toLong(),
                                        listItems = listItems
                                    )
                                }
                            }
                            // Screen for displaying and setting farm polygon coordinates
                            composable(
                                "setPolygon", arguments = listOf(navArgument("coordinates") {
                                    type = NavType.StringType
                                })
                            ) {
                                SetPolygon(navController, viewModel)
                            }
                            composable("settings") {
                                SettingsScreen(
                                    navController,
                                    darkMode,
                                    languageViewModel,
                                    languages
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    private fun startSyncService() {
        val serviceIntent = Intent(this, SyncService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }




}

