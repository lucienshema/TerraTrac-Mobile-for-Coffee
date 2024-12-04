package org.technoserve.cafetrac



import BottomNavBar
import android.Manifest
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.navArgument
import org.technoserve.cafetrac.viewmodels.AkrabiViewModel
import org.technoserve.cafetrac.viewmodels.AkrabiViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetrac.viewmodels.FarmViewModelFactory
import com.example.cafetrac.database.helpers.PreferencesManager
import org.technoserve.cafetrac.ui.components.currentRoute
import org.technoserve.cafetrac.viewmodels.MapViewModel
import com.example.cafetrac.ui.screens.farms.AddFarm
import com.example.cafetrac.ui.screens.collectionsites.AddSite
import org.technoserve.cafetrac.ui.screens.akrabis.AkrabiDetailScreen
import org.technoserve.cafetrac.ui.screens.akrabis.AkrabiListScreenScreen
import com.example.cafetrac.ui.screens.directbuy.BoughtItemDetailScreen
import com.example.cafetrac.ui.screens.directbuy.BoughtItemsList
import org.technoserve.cafetrac.ui.screens.directbuy.BoughtItemsListDirectBuy
import com.example.cafetrac.ui.screens.akrabis.BuyThroughAkrabiForm
import com.example.cafetrac.ui.screens.collectionsites.CollectionSiteList
import com.example.cafetrac.ui.screens.akrabis.CreateAkrabiFormScreen
import com.example.cafetrac.ui.screens.directbuy.DirectBuyDetailScreen
import com.example.cafetrac.ui.screens.akrabis.EditAkrabiScreen
import com.example.cafetrac.ui.screens.akrabis.EditBuyThroughAkrabiForm
import com.example.cafetrac.ui.screens.directbuy.DirectBuyForm
import com.example.cafetrac.ui.screens.directbuy.EditDirectBuyForm
import com.example.cafetrac.ui.screens.farms.FarmList
import com.example.cafetrac.ui.screens.home.Home
import com.example.cafetrac.ui.screens.privacy.PrivacyPolicyScreen
import org.technoserve.cafetrac.ui.screens.map.SetPolygon
import com.example.cafetrac.ui.screens.settings.SettingsScreen
import com.example.cafetrac.ui.screens.farms.UpdateFarmForm
import com.example.cafetrac.ui.screens.shopping.ShoppingScreen
import kotlinx.coroutines.delay
import org.technoserve.cafetrac.ui.theme.FarmCollectorThemeV2
import org.technoserve.cafetrac.viewmodels.LanguageViewModel
import org.technoserve.cafetrac.viewmodels.LanguageViewModelFactory
import org.technoserve.cafetraorg.technoserve.cafetrac.BuildConfig
import java.util.Locale


object Routes {
    const val HOME = "home"
    const val SITE_LIST = "siteList"
    const val FARM_LIST = "farmList/{siteId}"
    const val ADD_FARM = "addFarm/{siteId}"
    const val ADD_SITE = "addSite"
    const val UPDATE_FARM = "updateFarm/{farmId}"
    const val SET_POLYGON = "setPolygon"
    const val SETTINGS = "settings"
    const val SHOPPING = "shopping"
    const val AKRABI_LIST = "akrabiList"
    const val AKRABI_DETAIL = "akrabiDetail/{akrabiId}"
    const val CREATE_AKRABI = "createAkrabi"
    const val UPDATE_AKRABI = "updateAkrabi/{akrabiId}"
    const val PRIVACY_POLICY = "privacy_policy"
    const val CREATE_AKRABI_FORM = "createAkrabiForm"
    const val AKRABI_LIST_SCREEN = "akrabiListScreen"
    const val AKRABI_DETAILS = "akrabiDetails/{akrabiId}"
    const val EDIT_AKRABI_FORM = "editAkrabiForm/{akrabiId}"
    const val BOUGHT_ITEM_DETAIL = "boughtItemDetail/{selectedItemId}"
    const val BOUGHT_ITEM_DETAIL_DIRECT_BUY = "boughtItemDetailDirectBuy/{selectedItemId}"
    const val DIRECT_BUY_EDIT = "directBuyEdit/{id}"
    const val BUY_THROUGH_AKRABI_EDIT = "buyThroughAkrabiEdit/{id}"
    const val BOUGHT_ITEMS_DIRECT_BUY = "boughtItemsDirectBuy"
    const val BOUGHT_ITEMS_BUY_THROUGH_AKRABI = "boughtItemsBuyThroughAkrabi"
    val BOTTOM_NAV_ITEM_DIRECT_BUY = "${BottomNavItem.DirectBuy.route}/add"
    val BOTTOM_NAV_ITEM_BUY_THROUGH_AKRABI = "${BottomNavItem.BuyThroughAkrabi.route}/add"

}



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
//        startSyncService()
        setContent {
            val navController = rememberNavController()
            val currentLanguage by languageViewModel.currentLanguage.collectAsState()
            val context = LocalContext.current
            var canExitApp by remember { mutableStateOf(false) }

            LaunchedEffect(currentLanguage) {
                // updateLocale(context = applicationContext, Locale(currentLanguage.code))
                languageViewModel.updateLocale(context = applicationContext, Locale(currentLanguage.code))
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

                val preferencesManager = PreferencesManager(this)

                val multiplePermissionsState = rememberMultiplePermissionsState(permissions)

                LaunchedEffect(Unit) {
                    // Launch the permission request
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    // val languages = getLocalizedLanguages(applicationContext)
                    val languages = languageViewModel.languages
                    val farmViewModel: FarmViewModel = viewModel(
                        factory = FarmViewModelFactory(applicationContext as Application)
                    )
                    val akrabiViewModel: AkrabiViewModel = viewModel(
                        factory = AkrabiViewModelFactory(applicationContext as Application)
                    )
                    val listItems by farmViewModel.readData.observeAsState(listOf())


                    val collectionSites by farmViewModel.readAllSites.observeAsState(emptyList())
                    val akrabis by akrabiViewModel.akrabis.observeAsState(emptyList())

                    Scaffold(
                        bottomBar = {
                            val currentRoute = currentRoute(navController)
                            if (currentRoute in listOf(
                                    "bought_items_direct_buy",
                                    "bought_items_buy_through_Akrabi",
                                    BottomNavItem.DirectBuy.route,
                                    BottomNavItem.BuyThroughAkrabi.route
                                )
                            ) {
                                BottomNavBar(navController = navController)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Routes.HOME) {
                                var showExitToast by remember { mutableStateOf(false) }

                                // Handle back press with confirmation
                                BackHandler {
                                    if (canExitApp) {
                                        // Exit the app
                                        (context as? Activity)?.finish()
                                    } else {
                                        showExitToast = true
                                        canExitApp = true
                                    }
                                }

                                // Show exit toast and reset the state after a delay
                                if (showExitToast) {
                                    // Show the toast
                                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()

                                    // Reset `showExitToast` and `canExitApp` after 2 seconds
                                    LaunchedEffect(Unit) {
                                        delay(2000) // 2 seconds delay
                                        showExitToast = false
                                        canExitApp = false
                                    }
                                }

                                // Display Home Screen
                                Home(navController, languageViewModel, languages)
                            }

                            composable(Routes.SITE_LIST) {
                                    CollectionSiteList(navController,languageViewModel = languageViewModel,
                                        darkMode = darkMode,
                                        languages = languages)
                            }
                            composable(Routes.FARM_LIST) { backStackEntry ->
                                val siteId = backStackEntry.arguments?.getString("siteId")
                                if (siteId != null) {
                                    FarmList(
                                        navController = navController, siteId = siteId.toLong(),languageViewModel = languageViewModel,
                                        darkMode = darkMode,
                                        languages = languages
                                    )
                                }
                            }
                            composable(Routes.ADD_FARM) { backStackEntry ->
                                val siteId = backStackEntry.arguments?.getString("siteId")
                                if (siteId != null) {
                                    AddFarm(navController = navController, siteId = siteId.toLong(),   languageViewModel = languageViewModel,
                                        darkMode = darkMode,
                                        languages = languages)
                                }
                            }
                            composable(Routes.ADD_SITE) {
                                AddSite(navController,languageViewModel = languageViewModel,
                                    darkMode = darkMode,
                                    languages = languages)
                            }
                            composable(Routes.SHOPPING) {
                                // Check if user has agreed to terms
                                if (preferencesManager.hasAgreedToTerms) {
                                    ShoppingScreen(navController, farmViewModel = farmViewModel, darkMode,
                                        languageViewModel=languageViewModel,
                                        languages)
                                } else {
                                    // Redirect to privacy policy screen if not agreed
                                    navController.navigate("privacy_policy")
                                }
                            }

                            composable("create_akrabi_form") {
                                CreateAkrabiFormScreen(navController,akrabiViewModel,collectionSites)
                            }
                            composable("akrabi_list_screen") {
                                AkrabiListScreenScreen(navController, darkMode,
                                    languageViewModel=languageViewModel,
                                    languages)
                            }

                            composable("akrabiDetails/{akrabiId}") { backStackEntry ->
                                val akrabiId = backStackEntry.arguments?.getString("akrabiId")?.toLong()
                                val akrabi = akrabis.find { it.id == akrabiId }
                                if (akrabi != null) {
                                    AkrabiDetailScreen(
                                        akrabi = akrabi,
                                        navController,
                                        akrabiViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                else {
                                    Text("Error: Invalid Akrabi ID")
                                }
                            }

                            // Add this composable for the edit Akrabi screen
                            composable("edit_akrabi_form/{akrabiId}") { backStackEntry ->
                                val akrabiId = backStackEntry.arguments?.getString("akrabiId")?.toLong()
                                if (akrabiId != null) {
                                    EditAkrabiScreen(
                                        akrabiId = akrabiId,
                                        collectionSites = collectionSites,
                                        viewModel = akrabiViewModel,
                                        navController = navController
                                    )
                                } else {
                                    Text("Error: Invalid Akrabi ID")
                                }
                            }

                            composable(BottomNavItem.DirectBuy.route) {
                                BoughtItemsListDirectBuy( farmViewModel = farmViewModel,
                                    onItemClick = { selectedItem ->
                                        navController.navigate("bought_item_detail/${selectedItem.id}")
                                    },
                                    navController,
                                    darkMode,
                                    languageViewModel=languageViewModel,
                                    languages
                                )
                            }

                            composable("${BottomNavItem.DirectBuy.route}/add") {
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
                                BoughtItemsList( farmViewModel = farmViewModel,
                                    onItemClick = { selectedItem ->
                                        navController.navigate("bought_item_detail/${selectedItem.id}")
                                    },
                                    navController,
                                    darkMode,
                                    languageViewModel=languageViewModel,
                                    languages
                                )
                            }

                            composable("${BottomNavItem.BuyThroughAkrabi.route}/add") {
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
                                        },
                                        navController
                                    )
                                }
                            }

                            composable("bought_items_direct_buy") {
                                BoughtItemsListDirectBuy(
                                    farmViewModel = farmViewModel,
                                    onItemClick = { selectedItem ->
                                        navController.navigate("bought_item_detail_direct_buy/${selectedItem.id}")
                                    },
                                    navController,
                                    darkMode,
                                    languageViewModel=languageViewModel,
                                    languages
                                )
                            }

                            composable(
                                "direct_buy/edit/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val itemId = backStackEntry.arguments?.getLong("id")
                                if (itemId != null) {
                                    EditDirectBuyForm(
                                        itemId = itemId,
                                        farmViewModel = farmViewModel,
                                        navController = navController
                                    )
                                }
                            }

                            composable(
                                "buy_through_akrabi/edit/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val itemId = backStackEntry.arguments?.getLong("id")
                                if (itemId != null) {
                                    EditBuyThroughAkrabiForm(
                                        itemId = itemId,
                                        farmViewModel = farmViewModel,
                                        navController = navController
                                    )
                                }
                            }

                            composable("bought_items_buy_through_Akrabi") {
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


                            composable(
                                route = "bought_item_detail/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val itemId = backStackEntry.arguments?.getLong("id")
                                val selectedItem =
                                    itemId?.let { farmViewModel.getBoughtItemThroughAkrabiById(it) } // Ensure this function exists
                                selectedItem?.let { item ->
                                    BoughtItemDetailScreen(buyThroughAkrabi = item, onBack = { navController.popBackStack()}, navController,
                                        farmViewModel = farmViewModel)
                                }
                            }

                            composable(
                                route = "bought_item_detail_direct_buy/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val itemId = backStackEntry.arguments?.getLong("id")
                                val selectedItem =
                                    itemId?.let { farmViewModel.getBoughtItemDirectById(it) } // Ensure this function exists
                                selectedItem?.let { item ->
                                    DirectBuyDetailScreen(directBuy = item, onBack = { navController.popBackStack() }, navController,
                                        farmViewModel = farmViewModel)
                                }
                            }

                            composable(Routes.UPDATE_FARM) { backStackEntry ->
                                val farmId = backStackEntry.arguments?.getString("farmId")
                                if (farmId != null) {
                                    UpdateFarmForm(
                                        navController = navController,
                                        farmId = farmId.toLong(),
                                        listItems = listItems,
                                        languageViewModel = languageViewModel,
                                        darkMode = darkMode,
                                        languages = languages
                                    )
                                }
                            }
                            composable(
                                Routes.SET_POLYGON, arguments = listOf(navArgument("coordinates") {
                                    type = NavType.StringType
                                })
                            ) {
                                SetPolygon(navController, viewModel)
                            }
                            composable(Routes.SETTINGS) {
                                SettingsScreen(
                                    navController,
                                    darkMode,
                                    languageViewModel,
                                    languages
                                )
                            }
                            composable(Routes.PRIVACY_POLICY) {
                                PrivacyPolicyScreen(BuildConfig.DATA_PRIVACY_URL, onAgree = {
                                    navController.navigate("shopping") {
                                        popUpTo("home") { inclusive = true } // Optional: clear back stack
                                    }

                                })
                            }
                        }
                    }
                }
            }
        }
    }

}
