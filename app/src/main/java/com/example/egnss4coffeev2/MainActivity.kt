package com.example.egnss4coffeev2


import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.database.FarmViewModelFactory
import com.example.egnss4coffeev2.database.sync.SyncService
import com.example.egnss4coffeev2.map.MapViewModel
import com.example.egnss4coffeev2.ui.screens.AddFarm
import com.example.egnss4coffeev2.ui.screens.AddSite
import com.example.egnss4coffeev2.ui.screens.CollectionSiteList
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
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue with the action that requires permission.
            startSyncService()
        } else {
            // Permission is denied. Handle the case where the user denies the permission.
        }
    }

    private val viewModel: MapViewModel by viewModels()
    private val languageViewModel: LanguageViewModel by viewModels {
        LanguageViewModelFactory(application)
    }
    private val sharedPreferences by lazy {
        getSharedPreferences("theme_mode", MODE_PRIVATE)
    }

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

        // Start the service when the activity is created
        startSyncService()

        // Optionally, request permission if needed
        requestSyncPermission()



        setContent {
            val navController = rememberNavController()
            val currentLanguage by languageViewModel.currentLanguage.collectAsState()

            LaunchedEffect(currentLanguage) {
                updateLocale(context = applicationContext, Locale(currentLanguage.code))
            }

            FarmCollectorThemeV2(darkTheme = darkMode.value) {
                val multiplePermissionsState = rememberMultiplePermissionsState(
                    listOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                LaunchedEffect(true) {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val languages = getLocalizedLanguages(applicationContext)
                    val farmViewModel: FarmViewModel = viewModel(
                        factory = FarmViewModelFactory(applicationContext as Application)
                    )
                    val listItems by farmViewModel.readData.observeAsState(listOf())
                    NavHost(
                        navController = navController, startDestination = "home"
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

    private fun requestSyncPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted, continue with the action
                startSyncService()
            }
            else -> {
                // Permission has not been granted, request it
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private fun startSyncService() {
        val serviceIntent = Intent(this, SyncService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }




}

