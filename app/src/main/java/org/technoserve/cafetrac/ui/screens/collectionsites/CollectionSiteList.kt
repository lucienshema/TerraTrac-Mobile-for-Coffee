package com.example.cafetrac.ui.screens.collectionsites

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetrac.viewmodels.FarmViewModelFactory
import org.technoserve.cafetrac.ui.screens.farms.Action
import org.technoserve.cafetrac.ui.screens.farms.siteID
import com.example.cafetrac.database.models.Language
import org.technoserve.cafetrac.ui.components.ConfirmationDialog
import org.technoserve.cafetrac.ui.components.CustomDrawer
import org.technoserve.cafetrac.ui.components.CustomPaginationControls
import org.technoserve.cafetrac.ui.components.DeleteAllDialogPresenter
import org.technoserve.cafetrac.ui.components.FormatSelectionDialog
import org.technoserve.cafetrac.ui.components.SkeletonSiteCard
import org.technoserve.cafetrac.ui.components.siteCard
import org.technoserve.cafetrac.viewmodels.LanguageViewModel
import kotlinx.coroutines.delay
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



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
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end= 0.dp, bottom = 48.dp).background(MaterialTheme.colorScheme.background).align(
                        BottomEnd
                    )
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






