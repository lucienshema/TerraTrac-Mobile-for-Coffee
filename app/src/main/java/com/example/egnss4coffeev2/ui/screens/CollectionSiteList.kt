package com.example.egnss4coffeev2.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.egnss4coffeev2.R
import com.example.egnss4coffeev2.database.CollectionSite
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.database.FarmViewModelFactory
import com.example.egnss4coffeev2.ui.composes.UpdateCollectionDialog
import com.example.egnss4coffeev2.utils.Language
import com.example.egnss4coffeev2.utils.LanguageViewModel
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//@Composable
//fun CollectionSiteList(navController: NavController) {
//    val context = LocalContext.current
//    val farmViewModel: FarmViewModel =
//        viewModel(
//            factory = FarmViewModelFactory(context.applicationContext as Application),
//        )
//    val selectedIds = remember { mutableStateListOf<Long>() }
//    val showDeleteDialog = remember { mutableStateOf(false) }
//
//    val listItems by farmViewModel.readAllSites.observeAsState(listOf())
//
//    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }
//
//    fun onDelete() {
//        val toDelete = mutableListOf<Long>()
//        toDelete.addAll(selectedIds)
//        farmViewModel.deleteListSite(toDelete)
//        selectedIds.removeAll(selectedIds)
//        showDeleteDialog.value = false
//    }
//
//    fun toggleSelection(siteId: Long, isChecked: Boolean) {
//        if (isChecked) {
//            selectedIds.add(siteId)
//        } else {
//            selectedIds.remove(siteId)
//        }
//    }
//
//    fun refreshListItems() {
//        // TODO: update saved predictions list when db gets updated
//        //  currently using a terrible makeshift solution
//        navController.navigate("home")
//        navController.navigate("farmList") {
//            navController.graph.startDestinationRoute?.let { route ->
//                popUpTo(route) {
//                    saveState = true
//                }
//            }
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//
//    // State to manage the loading status
//    val isLoading = remember { mutableStateOf(true) }
//
//    // Simulate a network request or data loading
//    LaunchedEffect(Unit) {
//        // Simulate a delay for loading
//        delay(500) // Adjust the delay as needed
//        // After loading data, set isLoading to false
//        isLoading.value = false
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//    ) {
//        FarmListHeader(
//            title = stringResource(id = R.string.collection_site_list),
//            onSearchQueryChanged = setSearchQuery,
//            onAddFarmClicked = { navController.navigate("addSite") },
//            onBackSearchClicked = { navController.navigate("siteList") },
//            onBackClicked = { navController.navigate("shopping") },
//            showAdd = true,
//            showSearch = true,
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Show loader while data is loading
//        if (isLoading.value) {
//            // Show loader while data is loading
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }  else {
//            if (listItems.isNotEmpty()) {
//                // Show list of items after loading is complete
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                ) {
//                    // Filter the list based on the search query
//                    val filteredList = listItems.filter {
//                        it.name.contains(searchQuery, ignoreCase = true)
//                    }
//
//                    // Display a message if no results are found
//                    if (searchQuery.isNotEmpty() && filteredList.isEmpty()) {
//                        item {
//                            Text(
//                                text = stringResource(R.string.no_results_found),
//                                modifier = Modifier
//                                    .padding(16.dp)
//                                    .fillMaxWidth(),
//                                textAlign = TextAlign.Center,
//                                style = MaterialTheme.typography.bodyMedium,
//                            )
//                        }
//                    } else {
//                        // Display the list of filtered items
//                        items(filteredList) { site ->
//                            val isSelected = selectedIds.contains(site.siteId)
//                            siteCard(
//                                site = site,
//                                isSelected = isSelected,
//                                onCheckedChange = { isChecked ->
//                                    toggleSelection(site.siteId, isChecked)
//                                },
//                                onCardClick = {
//                                    navController.navigate("farmList/${site.siteId}")
//                                },
//                                onDeleteClick = {
//                                    toggleSelection(site.siteId, true)
//                                   // selectedIds.add(site.siteId)
//                                    showDeleteDialog.value = true
//                                },
//                                farmViewModel = farmViewModel,
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                        }
//                    }
//                }
//            }
//
//            else {
//                Spacer(modifier = Modifier.height(8.dp))
//                Image(
//                    modifier =
//                    Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.CenterHorizontally)
//                        .padding(16.dp, 8.dp),
//                    painter = painterResource(id = R.drawable.no_data2),
//                    contentDescription = null,
//                )
//            }
//        }
//
//        // Display delete dialog if showDeleteDialog is true
//        if (showDeleteDialog.value) {
//            DeleteAllDialogPresenter(showDeleteDialog, onProceedFn = { onDelete() })
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun siteCard(
//    site: CollectionSite,
//    isSelected: Boolean,
//    onCheckedChange: (Boolean) -> Unit,
//    onCardClick: () -> Unit,
//    onDeleteClick: () -> Unit,
//    farmViewModel: FarmViewModel,
//) {
//    val showDialog = remember { mutableStateOf(false) }
//    if (showDialog.value) {
//        UpdateCollectionDialog(
//            site = site,
//            showDialog = showDialog,
//            farmViewModel = farmViewModel,
//        )
//    }
//    val isDarkTheme = isSystemInDarkTheme()
//    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
//    val textColor = if (isDarkTheme) Color.White else Color.Black
//    val iconColor = if (isDarkTheme) Color.White else Color.Black
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(top = 8.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        ElevatedCard(
//            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
//            modifier = Modifier
//                .background(backgroundColor)
//                .fillMaxWidth() // Adjusted to fill max width
//                .padding(8.dp),
//            onClick = { onCardClick() },
//        ) {
//            Column(
//                modifier = Modifier
//                    .background(backgroundColor)
//                    .padding(16.dp),
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.fillMaxWidth(),
//                ) {
//                    Checkbox(
//                        checked = isSelected,
//                        onCheckedChange = onCheckedChange
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Column(
//                        modifier = Modifier
//                            .weight(1.1f)
//                            .padding(bottom = 4.dp),
//                    ) {
//                        Text(
//                            text = site.name,
//                            style = MaterialTheme.typography.bodyLarge.copy(
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 20.sp,
//                                color = textColor
//                            ),
//                            modifier = Modifier.padding(bottom = 4.dp),
//                        )
//                        Text(
//                            text = "${stringResource(id = R.string.agent_name)}: ${site.agentName}",
//                            style = MaterialTheme.typography.bodySmall.copy(color = textColor),
//                            modifier = Modifier.padding(bottom = 1.dp),
//                        )
//                        Text(
//                            text = "${stringResource(id = R.string.village)}: ${site.village}",
//                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
//                        )
//                        Text(
//                            text = "${stringResource(id = R.string.district)}: ${site.district}",
//                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
//                        )
//                        if (site.phoneNumber.isNotEmpty()) {
//                            Text(
//                                text = "${stringResource(id = R.string.phone_number)}: ${site.phoneNumber}",
//                                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
//                            )
//                        }
//                        if (site.email.isNotEmpty()) {
//                            Text(
//                                text = "${stringResource(id = R.string.email)}: ${site.email}",
//                                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
//                            )
//                        }
//                    }
//                    // Adjusted IconButton modifiers for tighter spacing
//                    IconButton(
//                        onClick = { showDialog.value = true },
//                        modifier = Modifier
//                            .size(24.dp)
//                            .padding(end = 4.dp) // Reduced end padding
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Edit,
//                            contentDescription = "Update",
//                            tint = iconColor,
//                        )
//                    }
//                    Spacer(modifier = Modifier.padding(4.dp)) // Reduced spacer padding
//                    // Adjusted IconButton modifiers for tighter spacing
//                    IconButton(
//                        onClick = { onDeleteClick() },
//                        modifier = Modifier
//                            .size(24.dp)
//                            .padding(start = 4.dp) // Reduced start padding
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Delete,
//                            contentDescription = "Delete",
//                            tint = Color.Red,
//                        )
//                    }
//                }
//            }
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("AutoboxingStateCreation")
@Composable
fun CollectionSiteList(navController: NavController, languageViewModel: LanguageViewModel,
                       darkMode: MutableState<Boolean>,
                       languages: List<Language>) {
    val context = LocalContext.current
    val farmViewModel: FarmViewModel =
        viewModel(
            factory = FarmViewModelFactory(context.applicationContext as Application),
        )
    val selectedIds = remember { mutableStateListOf<Long>() }
    val showDeleteDialog = remember { mutableStateOf(false) }

    // var (searchQuery, setSearchQuery) = remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val isLoading = remember { mutableStateOf(true) }

    var showFormatDialog by remember { mutableStateOf(false) }
    var action by remember { mutableStateOf<Action?>(null) }
    val activity = context as Activity
    var exportFormat by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    farmViewModel.updateSelectedSiteIds(selectedIds)

    val lazyPagingItems = farmViewModel.pager.collectAsLazyPagingItems()

    val pageSize = 3
    val pagedData = farmViewModel.pager.collectAsLazyPagingItems()
    var currentPage by remember { mutableIntStateOf(1) }
    val totalPages = (pagedData.itemCount + pageSize - 1) / pageSize

    val isLoadingPage = lazyPagingItems.loadState.append is LoadState.Loading
    val isError = lazyPagingItems.loadState.refresh is LoadState.Error
    val items = lazyPagingItems.itemSnapshotList.items

    // print items size
    println("items size ${lazyPagingItems.itemCount}")


    val cwsListItems by farmViewModel.readAllSites.observeAsState(listOf())

    val filteredList = cwsListItems.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }
    //val listItems by farmViewModel.readData.observeAsState(emptyList())

    val listItems by farmViewModel.getFilteredFarms(selectedIds).observeAsState(emptyList())

    // Track the number of selected items
    val selectedItemsCount = selectedIds.size

    // State for holding the search query
   // var searchQuery by remember { mutableStateOf("") }

    var isSearchVisible by remember { mutableStateOf(false)}

    var isSearchActive by remember { mutableStateOf(false) }
    var drawerVisible by remember { mutableStateOf(false) }

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)

    // Simulate loading for demonstration purposes
    LaunchedEffect(Unit) {
        Log.d("CollectionSiteList", "Items count: ${lazyPagingItems.itemCount}\"")
        delay(500)
        isLoading.value = false
    }



    fun onDelete() {
        val toDelete = selectedIds.toList()
        farmViewModel.deleteListSite(toDelete)
        selectedIds.clear()
        showDeleteDialog.value = false
    }

    fun toggleSelection(siteId: Long, isChecked: Boolean) {
        if (isChecked) {
            selectedIds.add(siteId)
        } else {
            selectedIds.remove(siteId)
        }
    }

    fun createFileForSharing(): File? {
        // Get the current date and time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val getSiteById = cwsListItems.find { it.siteId == siteID }
        val siteName = getSiteById?.name ?: "SiteName"


        // Get names of all selected sites
        val selectedSiteNames = cwsListItems
            .filter { selectedIds.contains(it.siteId) }
            .map { it.name }
            .take(3)  // Limit to first 3 sites to avoid extremely long filenames
            .joinToString("_") { it.replace(" ", "_") }

        val additionalSitesCount = (selectedIds.size - 3).coerceAtLeast(0)
        val siteNamesPart = if (additionalSitesCount > 0) {
            "${selectedSiteNames}_and_${additionalSitesCount}_more"
        } else {
            selectedSiteNames
        }
        val fileExtension = if (exportFormat == "CSV") "csv" else "geojson"
        val filename = "farms_${siteNamesPart}_$timestamp.$fileExtension"

        val mimeType = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
        // Get the Downloads directory
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, filename)

        try {
            file.bufferedWriter().use { writer ->
                if (exportFormat == "CSV") {
                    writer.write(
                        "remote_id,farmer_name,member_id,collection_site,agent_name,farm_village,farm_district,farm_size,latitude,longitude,polygon,created_at,updated_at\n",
                    )
//                    listItems.forEach { farm ->
                    selectedIds.forEach { siteId ->
                        val site = cwsListItems.find { it.siteId == siteId }
                        val siteName = site?.name ?: "Unknown"
                        listItems.filter { it.siteId == siteId }.forEach { farm ->
                            val regex = "\\(([^,]+), ([^)]+)\\)".toRegex()
                            val matches = regex.findAll(farm.coordinates.toString())
//                        val reversedCoordinates =
//                            matches
//                                .map { match ->
//                                    val (lat, lon) = match.destructured
//                                    "[$lon, $lat]"
//                                }.toList() // Convert Sequence to List for easy handling
//                                .let { coordinates ->
//                                    if (coordinates.isNotEmpty()) {
//                                        if (coordinates.size == 1) {
//                                            // Single point, return without additional brackets
//                                            coordinates.first()
//                                        } else {
//                                            // Multiple points, add enclosing brackets
//                                            coordinates.joinToString(", ", prefix = "[", postfix = "]")
//                                        }
//                                    } else {
//                                        "" // Return an empty string if there are no coordinates
//                                    }
//                                }


                            val reversedCoordinates =
                                matches
                                    .map { match ->
                                        val (lat, lon) = match.destructured
                                        "[$lon, $lat]"
                                    }.toList()
                                    .let { coordinates ->
                                        if (coordinates.isNotEmpty()) {
                                            // Always include brackets, even for a single point
                                            coordinates.joinToString(
                                                ", ",
                                                prefix = "[",
                                                postfix = "]"
                                            )
                                        } else {
                                            val lon = farm.longitude ?: "0.0"
                                            val lat = farm.latitude ?: "0.0"
                                            "[$lon, $lat]"
                                        }
                                    }

                            val line =
                                "${farm.remoteId},${farm.farmerName},${farm.memberId},${getSiteById?.name},${getSiteById?.agentName},${farm.village},${farm.district},${farm.size},${farm.latitude},${farm.longitude},\"${reversedCoordinates}\",${
                                    Date(
                                        farm.createdAt,
                                    )
                                },${Date(farm.updatedAt)}\n"
                            writer.write(line)
                        }
                    }
                } else {
                    val geoJson =
                        buildString {
                            append("{\"type\": \"FeatureCollection\", \"features\": [")
//                            listItems.forEachIndexed { index, farm ->
                            selectedIds.forEachIndexed { index, siteId ->
                                val site = cwsListItems.find { it.siteId == siteId }
                                val siteName = site?.name ?: "Unknown"
                                listItems.filter { it.siteId == siteId }
                                    .forEachIndexed { farmIndex, farm ->

                                        val regex = "\\(([^,]+), ([^)]+)\\)".toRegex()
                                        val matches = regex.findAll(farm.coordinates.toString())
                                        val geoJsonCoordinates =
                                            matches
                                                .map { match ->
                                                    val (lat, lon) = match.destructured
                                                    "[$lon, $lat]"
                                                }.joinToString(", ", prefix = "[", postfix = "]")
                                        val latitude =
                                            farm.latitude.toDoubleOrNull()?.takeIf { it != 0.0 }
                                                ?: 0.0
                                        val longitude =
                                            farm.longitude.toDoubleOrNull()?.takeIf { it != 0.0 }
                                                ?: 0.0

                                        val feature =
                                            """
                                    {
                                        "type": "Feature",
                                        "properties": {
                                            "remote_id": "${farm.remoteId ?: ""}",
                                            "farmer_name": "${farm.farmerName ?: ""}",
                                            "member_id": "${farm.memberId ?: ""}",
                                            "collection_site": "${getSiteById?.name ?: ""}",
                                            "agent_name": "${getSiteById?.agentName ?: ""}",
                                            "farm_village": "${farm.village ?: ""}",
                                            "farm_district": "${farm.district ?: ""}",
                                             "farm_size": ${farm.size ?: 0.0},
                                            "latitude": $latitude,
                                            "longitude": $longitude,
                                            "created_at": "${farm.createdAt?.let { Date(it) } ?: "null"}",
                                            "updated_at": "${farm.updatedAt?.let { Date(it) } ?: "null"}"

                                        },
                                        "geometry": {
                                            "type": "${if ((farm.coordinates?.size ?: 0) > 1) "Polygon" else "Point"}",
                                            "coordinates": ${if ((farm.coordinates?.size ?: 0) > 1) "[$geoJsonCoordinates]" else "[$latitude,$longitude]"}
                                        }
                                    }
                                    """.trimIndent()
                                        append(feature)
                                        if (farmIndex < listItems.filter { it.siteId == siteId }.size - 1 || (index < selectedIds.size - 1)) append(
                                            ","
                                        )
                                    }
                            }
                            append("]}")
                        }
                    writer.write(geoJson)
                }
            }
            return file
        } catch (e: IOException) {
            Toast.makeText(context, R.string.error_export_msg, Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun createFile(
        context: Context,
        uri: Uri,
    ): Boolean {
        // Get the current date and time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val getSiteById = cwsListItems.find { it.siteId == siteID }
        val siteName = getSiteById?.name ?: "SiteName"
        val filename =
            if (exportFormat == "CSV") "farms_${siteName}_$timestamp.csv" else "farms_${siteName}_$timestamp.geojson"

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    if (exportFormat == "CSV") {
                        writer.write(
                            "remote_id,farmer_name,member_id,collection_site,agent_name,farm_village,farm_district,farm_size,latitude,longitude,polygon,created_at,updated_at\n",
                        )
//                        listItems.forEach { farm ->
                        selectedIds.forEach { siteId ->
                            val site = cwsListItems.find { it.siteId == siteId }
                            val siteName = site?.name ?: "Unknown"
                            listItems.filter { it.siteId == siteId }.forEach { farm ->
                                val regex = "\\(([^,]+), ([^)]+)\\)".toRegex()
                                val matches = regex.findAll(farm.coordinates.toString())
//                            val reversedCoordinates =
//                                matches
//                                    .map { match ->
//                                        val (lat, lon) = match.destructured
//                                        "[$lon, $lat]"
//                                    }.toList() // Convert Sequence to List for easy handling
//                                    .let { coordinates ->
//                                        if (coordinates.isNotEmpty()) {
//                                            if (coordinates.size == 1) {
//                                                // Single point, return without additional brackets
//                                                coordinates.first()
//                                            } else {
//                                                // Multiple points, add enclosing brackets
//                                                coordinates.joinToString(", ", prefix = "[", postfix = "]")
//                                            }
//                                        } else {
//                                            "" // Return an empty string if here are no coordinates
//                                        }
//                                    }

                                val reversedCoordinates =
                                    matches
                                        .map { match ->
                                            val (lat, lon) = match.destructured
                                            "[$lon, $lat]"
                                        }.toList()
                                        .let { coordinates ->
                                            if (coordinates.isNotEmpty()) {
                                                // Always include brackets, even for a single point
                                                coordinates.joinToString(
                                                    ", ",
                                                    prefix = "[",
                                                    postfix = "]"
                                                )
                                            } else {
                                                val lon = farm.longitude ?: "0.0"
                                                val lat = farm.latitude ?: "0.0"
                                                "[$lon, $lat]"
                                            }
                                        }

                                val line =
                                    "${farm.remoteId},${farm.farmerName},${farm.memberId},${getSiteById?.name},${getSiteById?.agentName},${farm.village},${farm.district},${farm.size},${farm.latitude},${farm.longitude},\"${reversedCoordinates}\",${
                                        Date(farm.createdAt)
                                    },${Date(farm.updatedAt)}\n"
                                writer.write(line)
                            }
                        }
                    } else {
                        val geoJson =
                            buildString {
                                append("{\"type\": \"FeatureCollection\", \"features\": [")
//                                listItems.forEachIndexed { index, farm ->
                                selectedIds.forEachIndexed { index, siteId ->
                                    val site = cwsListItems.find { it.siteId == siteId }
                                    val siteName = site?.name ?: "Unknown"
                                    listItems.filter { it.siteId == siteId }
                                        .forEachIndexed { farmIndex, farm ->
                                            val regex = "\\(([^,]+), ([^)]+)\\)".toRegex()
                                            val matches = regex.findAll(farm.coordinates.toString())
                                            val geoJsonCoordinates =
                                                matches
                                                    .map { match ->
                                                        val (lat, lon) = match.destructured
                                                        "[$lon, $lat]"
                                                    }
                                                    .joinToString(", ", prefix = "[", postfix = "]")
                                            // Ensure latitude and longitude are not null
                                            val latitude =
                                                farm.latitude.toDoubleOrNull()?.takeIf { it != 0.0 }
                                                    ?: 0.0
                                            val longitude =
                                                farm.longitude.toDoubleOrNull()
                                                    ?.takeIf { it != 0.0 }
                                                    ?: 0.0

                                            val feature =
                                                """
                                        {
                                            "type": "Feature",
                                            "properties": {
                                                "remote_id": "${farm.remoteId ?: ""}",
                                                "farmer_name": "${farm.farmerName ?: ""}",
                                                "member_id": "${farm.memberId ?: ""}",
                                                "collection_site": "${getSiteById?.name ?: ""}",
                                                "agent_name": "${getSiteById?.agentName ?: ""}",
                                                "farm_village": "${farm.village ?: ""}",
                                                "farm_district": "${farm.district ?: ""}",
                                                 "farm_size": ${farm.size ?: 0.0},
                                                "latitude": $latitude,
                                                "longitude": $longitude,
                                                "created_at": "${farm.createdAt?.let { Date(it) } ?: "null"}",
                                                "updated_at": "${farm.updatedAt?.let { Date(it) } ?: "null"}"

                                            },
                                            "geometry": {
                                                "type": "${if ((farm.coordinates?.size ?: 0) > 1) "Polygon" else "Point"}",
                                                "coordinates": ${if ((farm.coordinates?.size ?: 0) > 1) "[$geoJsonCoordinates]" else "[$latitude,$longitude]"}
                                            }
                                        }
                                        """.trimIndent()
                                            append(feature)
                                            if (index < listItems.size - 1) append(",")
                                        }
                                }
                                append("]}")
                            }
                        writer.write(geoJson)
                    }
                }
            }
            return true
        } catch (e: IOException) {
            Toast.makeText(context, R.string.error_export_msg, Toast.LENGTH_SHORT).show()
            return false
        }
    }

    val createDocumentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val context = activity?.applicationContext
                    if (context != null && createFile(context, uri)) {
                        Toast.makeText(context, R.string.success_export_msg, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    fun initiateFileCreation(activity: Activity) {
        val mimeType = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType

            // Get names of all selected sites
            val selectedSiteNames = cwsListItems
                .filter { selectedIds.contains(it.siteId) }
                .map { it.name }
                .take(3)  // Limit to first 3 sites to avoid extremely long filenames
                .joinToString("_") { it.replace(" ", "_") }

            val additionalSitesCount = (selectedIds.size - 3).coerceAtLeast(0)
            val siteNamesPart = if (additionalSitesCount > 0) {
                "${selectedSiteNames}_and_${additionalSitesCount}_more"
            } else {
                selectedSiteNames
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileExtension = if (exportFormat == "CSV") "csv" else "geojson"
            val filename = "farms_${siteNamesPart}_$timestamp.$fileExtension"


                putExtra(Intent.EXTRA_TITLE, filename)
            }
        createDocumentLauncher.launch(intent)
    }

    // Function to share the file
    fun shareFile(file: File) {
        val fileURI: Uri =
            context.let {
                FileProvider.getUriForFile(
                    it,
                    context.applicationContext.packageName.toString() + ".provider",
                    file,
                )
            }

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
                putExtra(Intent.EXTRA_SUBJECT, "Farm Data")
                putExtra(Intent.EXTRA_TEXT, "Sharing the farm data file.")
                putExtra(Intent.EXTRA_STREAM, fileURI)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        val chooserIntent = Intent.createChooser(shareIntent, "Share file")
        activity.startActivity(chooserIntent)
    }


    fun exportFile(activity: Activity) {
        showConfirmationDialog = true
    }

    // Function to handle the share action
    fun shareFileAction() {
        showConfirmationDialog = true
    }

    if (showFormatDialog) {
        FormatSelectionDialog(
            onDismiss = { showFormatDialog = false },
            onFormatSelected = { format ->
                exportFormat = format
                showFormatDialog = false
                when (action) {
                    Action.Export -> exportFile(activity)
                    Action.Share -> shareFileAction()
                    else -> {}
                }
            },
        )
    }

    if (showConfirmationDialog) {
        ConfirmationDialog(
            listItems,
            action = action!!, // Ensure action is not null
           // selectedIds = selectedIds,
            onConfirm = {
                when (action) {
                    Action.Export -> initiateFileCreation(activity)
                    Action.Share -> {
                        val file = createFileForSharing()
                        if (file != null) {
                            shareFile(file)
                        }
                    }

                    else -> {}
                }
            },
            onDismiss = { showConfirmationDialog = false },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.collection_site_list)) },
                    navigationIcon = {
                        IconButton(onClick = { drawerVisible = !drawerVisible }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(
                                if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (isSearchActive) "Close Search" else "Search"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("addSite")
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Site")
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    // Search field below the header
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            label = { Text(stringResource(R.string.search)) },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                cursorColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }

                    when {
                        pagedData.loadState.refresh is LoadState.Loading -> {
                            LazyColumn {
                                items(3) {
                                    SkeletonSiteCard()
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                        pagedData.loadState.refresh is LoadState.Error -> {
                            Text(
                                text = stringResource(id = R.string.error_loading_more_sites),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )
                        }
                        cwsListItems.isNotEmpty() -> {
                            Column(modifier = Modifier.weight(1f)) {
                                if (selectedItemsCount >= 1) {
                                    Text(
                                        text = "$selectedItemsCount selected",
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                if (searchQuery.isNotEmpty() && filteredList.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.no_results_found),
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                } else {
                                    LazyColumn {
                                        val pageSize = 3
                                        val startIndex = (currentPage - 1) * pageSize
                                        val endIndex = minOf(startIndex + pageSize, filteredList.size)

                                        items(endIndex - startIndex) { index ->
                                            val siteIndex = startIndex + index
                                            val site = filteredList[siteIndex]
                                            siteCard(
                                                site = site,
                                                isSelected = selectedIds.contains(site.siteId),
                                                onCheckedChange = { isChecked ->
                                                    toggleSelection(site.siteId, isChecked)
                                                },
                                                onCardClick = {
                                                    navController.navigate("farmList/${site.siteId}")
                                                },
                                                totalFarms = farmViewModel.getTotalFarms(site.siteId)
                                                    .observeAsState(0).value,
                                                farmsWithIncompleteData = farmViewModel.getFarmsWithIncompleteData(site.siteId)
                                                    .observeAsState(0).value,
                                                farmViewModel = farmViewModel
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        item {
                                            CustomPaginationControls(
                                                currentPage = currentPage,
                                                totalPages = (filteredList.size + pageSize - 1) / pageSize,
                                                onPageChange = { newPage ->
                                                    currentPage = newPage
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
//                            Text(
//                                text = stringResource(R.string.no_results_found),
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(16.dp),
//                                color = MaterialTheme.colorScheme.onSurface,
//                                textAlign = TextAlign.Center,
//                                style = MaterialTheme.typography.bodyMedium,
//                            )
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp, 8.dp),
                                    painter = painterResource(id = R.drawable.no_data2),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        )

        if (selectedIds.isNotEmpty()) {
            BottomActionBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                onDeleteClick = { showDeleteDialog.value = true },
                onExportClicked = {
                    action = Action.Export
                    showFormatDialog = true
                },
                onShareClicked = {
                    action = Action.Share
                    showFormatDialog = true
                }
            )
        }

        if (showDeleteDialog.value) {
            DeleteAllDialogPresenter(showDeleteDialog, onProceedFn = { onDelete() })
        }
    }


    if (drawerVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable { drawerVisible = false },
            contentAlignment = Alignment.TopStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    // Header
                    Text(
                        text = stringResource(id = R.string.menu),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider()

                    // Scrollable Content
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 64.dp)
                        ) {
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.home),
                                    painter = painterResource(R.drawable.home),
                                    onClick = {
                                        // navController.navigate("shopping")
                                        //navController.previousBackStackEntry
                                        drawerVisible = false
                                    }
                                )
                            }
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.collection_site_registration),
                                    painter = painterResource(R.drawable.add_collection_site),
                                    onClick = {
                                        navController.navigate("siteList")
                                        drawerVisible = false
                                    }
                                )
                            }
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.farmer_registration),
                                    painter = painterResource(R.drawable.person_add),
                                    onClick = {
                                        navController.navigate("siteList")
                                        drawerVisible = false
                                    }
                                )
                            }

                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.akrabi_registration),
                                    painter = painterResource(R.drawable.person_add),
                                    onClick = {
                                        navController.navigate("akrabi_list_screen")
                                        drawerVisible = false
                                    }
                                )
                            }

                            item {
                                Divider()
                            }
                            item {
                                // Dark Mode Toggle
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.light_dark_theme),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Switch(
                                        checked = darkMode.value,
                                        onCheckedChange = {
                                            darkMode.value = it
                                            sharedPreferences.edit().putBoolean("dark_mode", it).apply()
                                        }
                                    )
                                }
                            }
                            item {
                                Divider()
                            }
                            // using checkbox

                            item {
                                Text(
                                    text = stringResource(id = R.string.select_language),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Box(
                                    modifier = Modifier
                                        .width(230.dp)
                                        .padding(8.dp)
                                ) {
                                    var expanded by remember { mutableStateOf(false) } // Ensure expanded is inside the Box
                                    OutlinedButton(
                                        onClick = { expanded = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = currentLanguage.displayName, color = MaterialTheme.colorScheme.onBackground)

                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(230.dp) // Set the width of the DropdownMenu to match the Box
                                            .background(MaterialTheme.colorScheme.background) // Set the background color to white for visibility
                                    ) {
                                        languages.forEach { language ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = language.displayName,
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    )
                                                },
                                                onClick = {
                                                    languageViewModel.selectLanguage(language, context)
                                                    expanded = false
                                                },
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.background) // Ensure each menu item has a white background
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                // Logout Item
                                DrawerItem(
                                    text = stringResource(id = R.string.logout),
                                    painter = painterResource(R.drawable.logout),
                                    onClick = {
                                        // Call your logout function here
                                        // navigate to login screen or refresh UI
                                        navController.navigate("home")
                                        drawerVisible = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun BottomActionBar(
    modifier: Modifier = Modifier, // Accepting modifier as a parameter
    onDeleteClick: () -> Unit,
    onExportClicked: () -> Unit,
    onShareClicked: () -> Unit,
) {
    BottomAppBar(
        modifier = modifier, // Applying the modifier to the BottomAppBar
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        content = {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Red,
                )
            }
            IconButton(onClick = onExportClicked) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Download",
                )
            }
            IconButton(onClick = onShareClicked) {
                Icon(
                    imageVector = Icons.Default.Share,
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Share",
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun siteCard(
    site: CollectionSite,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onCardClick: () -> Unit,
    totalFarms: Int,
    farmsWithIncompleteData: Int,
    farmViewModel: FarmViewModel,
) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        UpdateCollectionDialog(
            site = site,
            showDialog = showDialog,
            farmViewModel = farmViewModel,
        )
    }
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth()
                .padding(2.dp),
            onClick = { onCardClick() },
        ) {
            Column(
                modifier = Modifier
                    .background(backgroundColor)
                    .padding(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(backgroundColor)
                        .padding(2.dp)
                        .fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onCheckedChange,
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = if (isDarkTheme) Color.Green else Color.Blue,
                            uncheckedColor = if (isDarkTheme) Color.Gray else Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f) // Adjusted weight
                            .padding(start = 2.dp) // Reduced padding between checkbox and text
                    ) {
                        Text(
                            text = site.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = textColor
                            ),
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                        Text(
                            text = "${stringResource(id = R.string.agent_name)}: ${site.agentName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = textColor),
                            modifier = Modifier.padding(bottom = 1.dp),
                        )
//                        Text(
//                            text = "${stringResource(id = R.string.village)}: ${site.village}",
//                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
//                        )
//                        Text(
//                            text = "${stringResource(id = R.string.district)}: ${site.district}",
//                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
//                        )
                        if (site.phoneNumber.isNotEmpty()) {
                            Text(
                                text = "${stringResource(id = R.string.phone_number)}: ${site.phoneNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
                            )
                        }
//                        if (site.email.isNotEmpty()) {
//                            Text(
//                                text = "${stringResource(id = R.string.email)}: ${site.email}",
//                                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
//                            )
//                        }

                        Text(
                            text = stringResource(
                                id = R.string.total_farms,
                                totalFarms
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        )

                        Text(
                            text = stringResource(
                                id = R.string.total_farms_with_incomplete_data,
                                farmsWithIncompleteData
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Blue
                            ),
                        )
                    }
                    IconButton(
                        onClick = {
                            showDialog.value = true
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Site Details",
                            tint = iconColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SkeletonSiteCard() {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val placeholderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth()
                .padding(2.dp)
                .shimmer()
        ) {
            Column(
                modifier = Modifier
                    .background(backgroundColor)
                    .padding(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(backgroundColor)
                        .padding(2.dp)
                        .fillMaxWidth()
                ) {
                    // Checkbox placeholder with shimmer
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(placeholderColor, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Placeholder for site info
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 2.dp)
                    ) {
                        repeat(5) { // Repeat placeholders for each text line
                            Spacer(
                                modifier = Modifier
                                    .height(16.dp)
                                    .fillMaxWidth(0.8f)
                                    .background(placeholderColor, shape = RoundedCornerShape(4.dp))
                                    .padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Placeholder for farm info
                        Box(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth(0.5f)
                                .background(placeholderColor, shape = RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Placeholder for farms with incomplete data
                        Box(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth(0.6f)
                                .background(placeholderColor, shape = RoundedCornerShape(4.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Icon placeholder with shimmer
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(placeholderColor, shape = CircleShape)
                    )
                }
            }
        }
    }
}


    @Composable
    fun CustomPaginationControls(
        currentPage: Int,
        totalPages: Int,
        onPageChange: (Int) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
                enabled = currentPage > 1
            ) {
                Icon(painter = painterResource(R.drawable.previous), contentDescription = "Previous Page")
            }

            Text("Page $currentPage of $totalPages", modifier = Modifier.padding(horizontal = 16.dp))

            IconButton(
                onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages
            ) {
                Icon(painter = painterResource(R.drawable.next), contentDescription = "Next Page")
            }
        }
    }