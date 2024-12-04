package org.technoserve.cafetrac.ui.screens.akrabis

import android.content.Context
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.cafetrac.database.models.Akrabi
import com.example.cafetrac.database.models.Language
import org.technoserve.cafetrac.ui.components.CustomDrawer
import org.technoserve.cafetrac.viewmodels.AkrabiViewModel
import org.technoserve.cafetrac.viewmodels.LanguageViewModel
import kotlinx.coroutines.delay
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkrabiListScreenScreen(navController: NavController, darkMode: MutableState<Boolean>,
                           languageViewModel: LanguageViewModel,
                           languages: List<Language>) {
    val viewModel: AkrabiViewModel = viewModel()
    val akrabis by viewModel.akrabis.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var akrabiToDelete by remember { mutableStateOf<Akrabi?>(null) }

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var filteredItems = akrabis.filter {
        it.akrabiName.contains(searchQuery, ignoreCase = true)
    }

    // Drawer state
    var drawerOffset by remember { mutableStateOf(0f) }
    val drawerWidth = 250.dp
    val drawerWidthPx = with(LocalDensity.current) { drawerWidth.toPx() }

    var isDrawerOpen by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }

    var drawerVisible by remember { mutableStateOf(false) }

    // Simulate data loading delay
    LaunchedEffect(Unit) {
        delay(2000) // Simulate loading time
        isLoading = false
    }

    // Handle drawer gesture
    val gestureModifier = Modifier
        .offset(x = drawerOffset.dp)
        .pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                drawerOffset = (drawerOffset + dragAmount.x).coerceIn(0f, drawerWidthPx)
                isDrawerOpen = drawerOffset > 0
            }
        }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text= stringResource(id= R.string.confirm))
            },
            text = {
                Text(text= stringResource(id= R.string.item_will_be_deleted))
            },
            confirmButton = {
                Button(
                    onClick = {
                        akrabiToDelete?.let { viewModel.deleteAkrabi(it) }
                        showDialog = false
                        akrabiToDelete = null
                    }
                ) {
                    Text(text= stringResource(id= R.string.delete))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        akrabiToDelete = null
                    }
                ) {
                    Text(text= stringResource(id= R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
            tonalElevation = 6.dp // Adds a subtle shadow for better UX
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text= stringResource(id= R.string.akrabi_list)) },
                    navigationIcon = {
                        IconButton(onClick = { drawerVisible = ! drawerVisible }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("create_akrabi_form")
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Akrabi")
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp),
                            singleLine = true,
                            leadingIcon = {
                                IconButton(onClick = { isSearchActive = false; searchQuery = "" }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Close Search")
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                                    }
                                }
                            }
                        )
                    }


                    if (filteredItems.isEmpty()) {
                        // Show "List not found" message
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_results_found),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Image(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp, 8.dp),
//                                painter = painterResource(id = R.drawable.no_data2),
//                                contentDescription = null
//                            )
//                        }
                    } else {


                        AkrabiListScreen(
                            akrabis = filteredItems,
                            isLoading= isLoading,
                            onViewDetails = { akrabi ->
                                // Navigate to the View Akrabi details screen
                                navController.navigate("akrabiDetails/${akrabi.id}")
                            },
                            onEdit = { akrabi ->
                                // Navigate to the Edit Akrabi form with pre-filled data
                                navController.navigate("edit_akrabi_form/${akrabi.id}")
                            },
                            onDelete = { akrabi ->
                                akrabiToDelete = akrabi
                                showDialog = true
                            }
                        )
                    }
                }
            }
        )
    }

    CustomDrawer(
        drawerVisible = drawerVisible,
        onClose = { drawerVisible = false },
        navController = navController,
        darkMode = darkMode,
        currentLanguage = currentLanguage,
        languages = languages,
        onLanguageSelected = { language -> languageViewModel.selectLanguage(language, context) },
        onLogout = {
            navController.navigate("home")
            drawerVisible = false
        }
    )

}