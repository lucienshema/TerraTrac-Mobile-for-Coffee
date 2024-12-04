package org.technoserve.cafetrac.ui.screens.farms

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import com.example.cafetrac.database.models.Akrabi
import com.example.cafetrac.database.models.Farm
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetrac.viewmodels.FarmViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.BottomEnd
import com.example.cafetrac.ui.screens.collectionsites.BottomActionBar
import org.technoserve.cafetrac.utils.DeviceIdUtil
import com.example.cafetrac.database.models.Language
import com.example.cafetrac.database.models.ParcelableFarmData
import com.example.cafetrac.database.models.ParcelablePair
import org.technoserve.cafetrac.ui.components.ConfirmationDialog
import org.technoserve.cafetrac.ui.components.DeleteAllDialogPresenter
import org.technoserve.cafetrac.ui.components.FarmCard
import org.technoserve.cafetrac.ui.components.FormatSelectionDialog
import org.technoserve.cafetrac.ui.components.ImportFileDialog
import org.technoserve.cafetrac.viewmodels.LanguageViewModel


var siteID = 0L

enum class Action {
    Export,
    Share,
}





@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FarmList(
    navController: NavController,
    siteId: Long,
    languageViewModel: LanguageViewModel,
    darkMode: MutableState<Boolean>,
    languages: List<Language>
) {
    siteID = siteId
    val context = LocalContext.current
    val farmViewModel: FarmViewModel =
        viewModel(
            factory = FarmViewModelFactory(context.applicationContext as Application),
        )
    val selectedIds = remember { mutableStateListOf<Long>() }
    // Create a mutable state for the selected farm
    val selectedFarm = remember { mutableStateOf<Farm?>(null) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val listItems by farmViewModel.readAllData(siteId).observeAsState(listOf())
    val cwsListItems by farmViewModel.readAllSites.observeAsState(listOf())
    // var showExportDialog by remember { mutableStateOf(false) }
    var showFormatDialog by remember { mutableStateOf(false) }
    var action by remember { mutableStateOf<Action?>(null) }
    val activity = context as Activity
    var exportFormat by remember { mutableStateOf("") }

    var showImportDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    // var (searchQuery, setSearchQuery) = remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    var isSearchActive by remember { mutableStateOf(false) }
    var drawerVisible by remember { mutableStateOf(false) }

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)

    var isImportDisabled by remember { mutableStateOf(false) }


    var currentPage by remember { mutableStateOf(1) }
    //  val pageSize = 3 // Define page size
//    val totalPages = (listItems.size + pageSize - 1) / pageSize
//
//    val filteredListItems = listItems.filter { it.farmerName.contains(searchQuery, ignoreCase = true) }
//
//    // Calculate total pages for the filtered list
//    val pages = (filteredListItems.size + pageSize - 1) / pageSize


    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs =
        listOf(
            stringResource(id = R.string.all),
            stringResource(id = R.string.needs_update),
//            stringResource(id = R.string.no_update_needed),
        )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()


    // State to control the visibility of BuyThroughAkrabiForm
    var isFormVisible by remember { mutableStateOf(false) }

    val collectionSites by farmViewModel.readAllSites.observeAsState(emptyList())
    var akrabis by remember { mutableStateOf(listOf<Akrabi>()) }

    // State to manage the loading status
    val isLoading = remember { mutableStateOf(true) }
    var deviceId by remember { mutableStateOf("") }
    // State variable to observe restore status
    val restoreStatus by farmViewModel.restoreStatus.observeAsState()

    var showRestorePrompt by remember { mutableStateOf(false) }
    var finalMessage by remember { mutableStateOf("") }
    var showFinalMessage by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        deviceId = DeviceIdUtil.getDeviceId(context)
    }


    // Simulate a network request or data loading
    LaunchedEffect(Unit) {
        // Simulate a delay for loading
        delay(2000) // Adjust the delay as needed
        // After loading data, set isLoading to false
        isLoading.value = false
    }


    fun createFileForSharing(selectedFarms: List<Farm>? = null): File? {
        // Use selectedFarms if provided, otherwise default to listItems
        val farmsToExport = selectedFarms ?: listItems

        // Get the current date and time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val getSiteById = cwsListItems.find { it.siteId == siteID }
        val siteName = getSiteById?.name ?: "SiteName"
        val filename =
            if (exportFormat == "CSV") "farms_${siteName}_$timestamp.csv" else "farms_${siteName}_$timestamp.geojson"
        val mimeType = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
        // Get the Downloads directory
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, filename)

        try {
            file.bufferedWriter().use { writer ->
                if (exportFormat == "CSV") {
                    writer.write(
                        "remote_id,farmer_name,member_id,collection_site,agent_name,farm_village,farm_district,farm_size,latitude,longitude,polygon,created_at,updated_at\n"
                    )
                    farmsToExport.forEach { farm ->
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
                                        coordinates.joinToString(", ", prefix = "[", postfix = "]")
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
                } else {
                    val geoJson = buildString {
                        append("{\"type\": \"FeatureCollection\", \"features\": [")
                        farmsToExport.forEachIndexed { index, farm ->
                            val regex = "\\(([^,]+), ([^)]+)\\)".toRegex()
                            val matches = regex.findAll(farm.coordinates.toString())
                            val geoJsonCoordinates =
                                matches.map { match ->
                                    val (lat, lon) = match.destructured
                                    "[$lon, $lat]"
                                }.joinToString(", ", prefix = "[", postfix = "]")
                            val latitude =
                                farm.latitude.toDoubleOrNull()?.takeIf { it != 0.0 } ?: 0.0
                            val longitude =
                                farm.longitude.toDoubleOrNull()?.takeIf { it != 0.0 } ?: 0.0

                            val feature = """
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
                                    "coordinates": ${if ((farm.coordinates?.size ?: 0) > 1) "[$geoJsonCoordinates]" else "[$latitude, $longitude]"}
                                }
                            }
                        """.trimIndent()
                            append(feature)
                            if (index < farmsToExport.size - 1) append(",")
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
                        listItems.forEach { farm ->
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
                    } else {
                        val geoJson =
                            buildString {
                                append("{\"type\": \"FeatureCollection\", \"features\": [")
                                listItems.forEachIndexed { index, farm ->
                                    val regex = "\\(([^,]+), ([^)]+)\\)".toRegex()
                                    val matches = regex.findAll(farm.coordinates.toString())
                                    val geoJsonCoordinates =
                                        matches
                                            .map { match ->
                                                val (lat, lon) = match.destructured
                                                "[$lon, $lat]"
                                            }.joinToString(", ", prefix = "[", postfix = "]")
                                    // Ensure latitude and longitude are not null
                                    val latitude =
                                        farm.latitude.toDoubleOrNull()?.takeIf { it != 0.0 } ?: 0.0
                                    val longitude =
                                        farm.longitude.toDoubleOrNull()?.takeIf { it != 0.0 } ?: 0.0

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
        val intent =
            Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = mimeType
                val getSiteById = cwsListItems.find { it.siteId == siteID }
                val siteName = getSiteById?.name ?: "SiteName"
                val timestamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename =
                    if (exportFormat == "CSV") "farms_${siteName}_$timestamp.csv" else "farms_${siteName}_$timestamp.geojson"
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


    fun deleteSelectedFarms() {
        selectedIds.forEach { id ->
            farmViewModel.deleteById(id)
        }
        selectedIds.clear()
    }

    fun exportSelectedFarms() {
        val farmsToExport = listItems.filter { it.id in selectedIds }
        // Proceed with the export logic using farmsToExport
        createFileForSharing(farmsToExport)
        selectedIds.clear()
    }

    fun shareSelectedFarms() {
        val farmsToShare = listItems.filter { it.id in selectedIds }
        val fileToShare = createFileForSharing(farmsToShare)

        fileToShare?.let {
            shareFile(it)
        } ?: run {
            Toast.makeText(context, R.string.error_export_msg, Toast.LENGTH_SHORT).show()
        }

        selectedIds.clear()
    }

    if (showFormatDialog) {
        FormatSelectionDialog(
            onDismiss = { showFormatDialog = false },
            onFormatSelected = { format ->
                exportFormat = format
                showFormatDialog = false

                when (action) {
                    Action.Export -> {
                        if (selectedIds.isEmpty()) {
                            // Export all farms
                            exportFile(activity)
                        } else {
                            // Export only selected farms
                            exportSelectedFarms()
                        }
                    }

                    Action.Share -> {
                        if (selectedIds.isEmpty()) {
                            // Share all farms
                            shareFileAction()
                        } else {
                            // Share only selected farms
                            shareSelectedFarms()
                        }
                    }

                    else -> {}
                }
            }
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

    if (showImportDialog) {
        println("site ID am Using: $siteId")
        // ImportFileDialog( siteId,onDismiss = { showImportDialog = false ; refreshTrigger = !refreshTrigger},navController = navController)
        ImportFileDialog(
            siteId,
            onDismiss = { showImportDialog = false },
            navController = navController
        )
    }





    fun onDelete() {
        selectedFarm.value?.let { farm ->
            val toDelete =
                mutableListOf<Long>().apply {
                    addAll(selectedIds)
                    add(farm.id)
                }
            farmViewModel.deleteList(toDelete)
            selectedIds.removeAll(selectedIds)
            farmViewModel.deleteFarmById(farm)
            selectedFarm.value = null
            selectedIds.removeAll(selectedIds)
            showDeleteDialog.value = false
        }
    }

    // Function to show data or no data message
    @Composable
    fun showDataContent() {
        val hasData = listItems.isNotEmpty() // Check if there's data available

        if (hasData) {
            Column {
                // Only show the TabRow and HorizontalPager if there is data
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                .height(3.dp),
                            color = MaterialTheme.colorScheme.onPrimary // Color for the indicator
                        )
                    },
                    divider = { HorizontalDivider() }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(title) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) { page ->
                    val filteredListItems = when (page) {
                        1 -> listItems.filter { it.needsUpdate }
                        else -> listItems
                    }.filter {
                        it.farmerName.contains(searchQuery, ignoreCase = true)
                    }
                    if (filteredListItems.isNotEmpty() || searchQuery.isNotEmpty()) {
                        // Show the list only when loading is complete
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 90.dp)
                        ) {
                            val filteredList = filteredListItems.filter {
                                it.farmerName.contains(searchQuery, ignoreCase = true)
                            }

                            if (filteredList.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Top,
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_results_found),
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            } else {
                                items(filteredList) { farm ->
                                    FarmCard(
                                        farm = farm,
                                        navController = navController,
                                        isSelected = selectedIds.contains(farm.id),
                                        onCardClick = {
                                            navController.currentBackStackEntry?.arguments?.apply {
                                                putParcelableArrayList(
                                                    "coordinates",
                                                    farm.coordinates?.map {
                                                        it.first?.let { it1 ->
                                                            it.second?.let { it2 ->
                                                                ParcelablePair(it1, it2)
                                                            }
                                                        }
                                                    }?.let { ArrayList(it) }
                                                )
                                                putParcelable(
                                                    "farmData",
                                                    ParcelableFarmData(farm, "view")
                                                )
                                            }
                                            navController.navigate(route = "setPolygon")
                                        },
                                        onDeleteClick = {
                                            selectedIds.add(farm.id)
                                            selectedFarm.value = farm
                                            showDeleteDialog.value = true
                                        },
                                        onToggleSelect = { id ->
                                            if (selectedIds.contains(id)) {
                                                selectedIds.remove(id)
                                            } else {
                                                selectedIds.add(id)
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp, 8.dp),
                            painter = painterResource(id = R.drawable.no_data2),
                            contentDescription = null
                        )
                    }
                }
            }
        } else {
            // Display a message or image indicating no data available
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp, 8.dp),
                    painter = painterResource(id = R.drawable.no_data2),
                    contentDescription = null
                )
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.farm_list)) },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigate("siteList") },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Site List")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                farmViewModel.restoreData(
                                    deviceId = deviceId,
                                    phoneNumber = "",
                                    email = "",
                                    farmViewModel = farmViewModel
                                ) { success ->
                                    if (success) {
                                        finalMessage =
                                            context.getString(R.string.data_restored_successfully)
                                        showFinalMessage = true
                                    } else {
                                        showFinalMessage = true
                                        showRestorePrompt = true
                                    }
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restore",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(1.dp))
                        if (listItems.isNotEmpty()) {
                            IconButton(
                                onClick = { action = Action.Export;showFormatDialog = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.save),
                                    contentDescription = "Export",
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                            Spacer(modifier = Modifier.width(1.dp))
                        }
                        if (listItems.isNotEmpty()) {
                            IconButton(
                                onClick = { action = Action.Share;showFormatDialog = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(1.dp))
                        }
                        IconButton(
                            onClick = {
                                if (!isImportDisabled) {
                                    showImportDialog = true
                                    isImportDisabled =
                                        true // Disable the import icon after importing
                                }
                            },
                            modifier = Modifier.size(36.dp),
                            enabled = !isImportDisabled // Disable the button if import is completed
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.icons8_import_file_48),
                                contentDescription = "Import",
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(1.dp))
                        IconButton(
                            onClick = { isSearchActive = !isSearchActive },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (isSearchActive) "Close Search" else "Search",
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("addFarm/${siteId}")
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(end = 0.dp, bottom = 48.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .align(
                            BottomEnd
                        )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Farm in a Site")
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

                    showDataContent()

                }
            }
        )


        if (selectedIds.isNotEmpty()) {
            BottomActionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                onDeleteClick = {
                    showDeleteDialog.value = true
                    deleteSelectedFarms()
                },
                onExportClicked = {
                    action = Action.Export
                    showFormatDialog = true
                    exportSelectedFarms()
                },
                onShareClicked = {
                    action = Action.Share
                    showFormatDialog = true
                    shareSelectedFarms()
                }
            )
        }

        if (showDeleteDialog.value) {
            DeleteAllDialogPresenter(showDeleteDialog, onProceedFn = { onDelete() })
        }
    }
}













fun OutputStream.writeCsv(farms: List<Farm>) {
    val writer = bufferedWriter()
    writer.write(""""Farmer Name", "Village", "District"""")
    writer.newLine()
    farms.forEach {
        writer.write("${it.farmerName}, ${it.village}, \"${it.district}\"")
        writer.newLine()
    }
    writer.flush()
}

// on below line creating a method to write data to txt file.
private fun writeTextData(
    file: File,
    farms: List<Farm>,
    onDismiss: () -> Unit,
    format: String,
) {
    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(file)

        fileOutputStream
            .write(
                """"Farmer Name", "Village", "District", "Size in Ha", "Cherry harvested this year in Kgs", "latitude", "longitude" , "createdAt", "updatedAt" """
                    .toByteArray(),
            )
        fileOutputStream.write(10)
        farms.forEach {
            fileOutputStream.write(
                "${it.farmerName}, ${it.village},${it.district},${it.size},${it.purchases},${it.latitude},${it.longitude},${
                    Date(
                        it.createdAt,
                    )
                }, \"${Date(it.updatedAt)}\"".toByteArray(),
            )
            fileOutputStream.write(10)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}


fun updateFarm(
    farmViewModel: FarmViewModel,
    item: Farm,
) {
    farmViewModel.updateFarm(item)
}
