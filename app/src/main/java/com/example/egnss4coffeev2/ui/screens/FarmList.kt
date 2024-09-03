
package com.example.egnss4coffeev2.ui.screens

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.egnss4coffeev2.R
import com.example.egnss4coffeev2.database.Akrabi
import com.example.egnss4coffeev2.database.CollectionSite
import com.example.egnss4coffeev2.database.Farm
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.database.FarmViewModelFactory
import com.example.egnss4coffeev2.hasLocationPermission
import com.example.egnss4coffeev2.utils.convertSize
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.joda.time.Instant
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.regex.Pattern
import android.R.attr.checked
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.collectAsState
import com.example.egnss4coffeev2.database.BuyThroughAkrabi
import com.example.egnss4coffeev2.database.DirectBuy
import com.example.egnss4coffeev2.map.MapViewModel
import com.example.egnss4coffeev2.utils.Language
import com.example.egnss4coffeev2.utils.LanguageViewModel
import com.example.egnss4coffeev2.utils.LanguageViewModelFactory
import com.example.egnss4coffeev2.utils.languages


var siteID = 0L

enum class Action {
    Export,
    Share,
}

data class ParcelablePair(val first: Double, val second: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(first)
        parcel.writeDouble(second)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelablePair> {
        override fun createFromParcel(parcel: Parcel): ParcelablePair {
            return ParcelablePair(parcel)
        }

        override fun newArray(size: Int): Array<ParcelablePair?> {
            return arrayOfNulls(size)
        }
    }
}

data class ParcelableFarmData(val farm: Farm, val view: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Farm::class.java.classLoader)!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(farm, flags)
        parcel.writeString(view)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ParcelableFarmData> {
        override fun createFromParcel(parcel: Parcel): ParcelableFarmData {
            return ParcelableFarmData(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableFarmData?> {
            return arrayOfNulls(size)
        }
    }
}


@Composable
fun isSystemInDarkTheme(): Boolean {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("dark_mode", false)
}

@Composable
fun FormatSelectionDialog(
    onDismiss: () -> Unit,
    onFormatSelected: (String) -> Unit,
) {
    var selectedFormat by remember { mutableStateOf("CSV") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.select_file_format)) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "CSV",
                        onClick = { selectedFormat = "CSV" },
                    )
                    Text(stringResource(R.string.csv))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "GeoJSON",
                        onClick = { selectedFormat = "GeoJSON" },
                    )
                    Text(stringResource(R.string.geojson))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onFormatSelected(selectedFormat)
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
fun ConfirmationDialog(
    listItems: List<Farm>,
    action: Action,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun validateFarms(farms: List<Farm>): Pair<Int, List<Farm>> {
        val incompleteFarms =
            farms.filter { farm ->
                farm.farmerName.isEmpty() ||
                        farm.district.isEmpty()||
                        farm.village.isEmpty() ||
                        farm.latitude == "0.0" ||
                        farm.longitude == "0.0" ||
                        farm.size == 0.0f ||
                        farm.remoteId.toString().isEmpty()
            }
        return Pair(farms.size, incompleteFarms)
    }
    val (totalFarms, incompleteFarms) = validateFarms(listItems)
    val message =
        when (action) {
            Action.Export -> stringResource(R.string.confirm_export, totalFarms, incompleteFarms.size)
            Action.Share -> stringResource(R.string.confirm_share, totalFarms, incompleteFarms.size)
        }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.confirm)) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(text = stringResource(R.string.yes))
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.no))
            }
        },
    )
}

@Composable
fun ConfirmationDialogDirectBuy(
    listItems: List<DirectBuy>,
    action: Action,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun validateDirectBuy(directBuyItems: List<DirectBuy>): Pair<Int, List<DirectBuy>> {
        val incompletedirectBuyItems =
            directBuyItems.filter { directBuy ->
                directBuy.farmerName.isEmpty() ||
                        directBuy.siteName.isEmpty()||
                        directBuy.location.isEmpty()
            }
        return Pair(directBuyItems.size, incompletedirectBuyItems)
    }
    val (totaldirectBuyItems, incompletedirectBuyItems) = validateDirectBuy(listItems)
    val message =
        when (action) {
            Action.Export -> stringResource(R.string.confirm_export_items, totaldirectBuyItems, incompletedirectBuyItems.size)
            Action.Share -> stringResource(R.string.confirm_share_items, totaldirectBuyItems, incompletedirectBuyItems.size)
        }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.confirm)) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(text = stringResource(R.string.yes))
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.no))
            }
        },
    )
}

@Composable
fun ConfirmationDialogBuyThroughAkrabi(
    listItems: List<BuyThroughAkrabi>,
    action: Action,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun validateBuyThroughAkrabi(buyThroughAkrabiItems: List<BuyThroughAkrabi>): Pair<Int, List<BuyThroughAkrabi>> {
        val incompletebuyThroughAkrabiItems =
            buyThroughAkrabiItems.filter { buyThroughAkrabiItem ->
                buyThroughAkrabiItem.akrabiName.isEmpty() ||
                        buyThroughAkrabiItem.siteName.isEmpty()||
                        buyThroughAkrabiItem.location.isEmpty()
            }
        return Pair(buyThroughAkrabiItems.size, incompletebuyThroughAkrabiItems)
    }
    val (totalbuyThroughAkrabiItems, incompletebuyThroughAkrabiItems) = validateBuyThroughAkrabi(listItems)
    val message =
        when (action) {
            Action.Export -> stringResource(R.string.confirm_export_items, totalbuyThroughAkrabiItems, incompletebuyThroughAkrabiItems.size)
            Action.Share -> stringResource(R.string.confirm_share_items, totalbuyThroughAkrabiItems, incompletebuyThroughAkrabiItems.size)
        }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.confirm)) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(text = stringResource(R.string.yes))
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.no))
            }
        },
    )
}

//@Composable
//fun ConfirmationDialog(
//    listItems: List<Farm>,
//    selectedIds: List<Long>,
//    action: Action,
//    onConfirm: () -> Unit,
//    onDismiss: () -> Unit,
//) {
//    fun validateFarms(farms: List<Farm>): Pair<Int, List<Farm>> {
//        val incompleteFarms = farms.filter { farm ->
//            farm.farmerName.isEmpty() ||
//                    farm.district.isEmpty() ||
//                    farm.village.isEmpty() ||
//                    farm.latitude == "0.0" ||
//                    farm.longitude == "0.0" ||
//                    farm.size == 0.0f ||
//                    farm.remoteId.toString().isEmpty()
//        }
//        return Pair(farms.size, incompleteFarms)
//    }
//
//    // Determine if we are working with all items or only selected items
//    val farmsToProcess = if (selectedIds.isEmpty()) {
//        listItems // Use all farms if nothing is selected
//    } else {
//        listItems.filter { it.id in selectedIds } // Only use selected farms
//    }
//
//    // Validate the farms based on the selected scope
//    val (totalFarms, incompleteFarms) = validateFarms(farmsToProcess)
//
//    // Generate the message based on the action type
//    val message = when (action) {
//        Action.Export -> stringResource(R.string.confirm_export, totalFarms, incompleteFarms.size)
//        Action.Share -> stringResource(R.string.confirm_share, totalFarms, incompleteFarms.size)
//    }
//
//    // Display the confirmation dialog
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text(text = stringResource(R.string.confirm)) },
//        text = { Text(text = message) },
//        confirmButton = {
//            Button(onClick = {
//                onConfirm()
//                onDismiss()
//            }) {
//                Text(text = stringResource(R.string.yes))
//            }
//        },
//        dismissButton = {
//            Button(onClick = { onDismiss() }) {
//                Text(text = stringResource(R.string.no))
//            }
//        },
//    )
//}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
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


    // State to manage the loading status
    val isLoading = remember { mutableStateOf(true) }

    // State to control the visibility of BuyThroughAkrabiForm
    var isFormVisible by remember { mutableStateOf(false) }

    val collectionSites by farmViewModel.readAllSites.observeAsState(emptyList())
    var akrabis by remember { mutableStateOf(listOf<Akrabi>()) }


    // Simulate a network request or data loading
    LaunchedEffect(Unit) {
        // Simulate a delay for loading
        delay(2000) // Adjust the delay as needed
        // After loading data, set isLoading to false
        isLoading.value = false
    }


    fun createFileForSharing(selectedFarms: List<Farm>): File? {
        // Modify the existing logic to use selectedFarms instead of listItems
        // (the rest of the code remains the same)

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
                        "remote_id,farmer_name,member_id,collection_site,agent_name,farm_village,farm_district,farm_size,latitude,longitude,polygon,created_at,updated_at\n",
                    )
                    selectedFarms.forEach { farm ->
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
                                        coordinates.joinToString(", ", prefix = "[", postfix = "]")
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
                } else {
                    val geoJson =
                        buildString {
                            append("{\"type\": \"FeatureCollection\", \"features\": [")
                            selectedFarms.forEachIndexed { index, farm ->
                                val regex = "\\(([^,]+), ([^)]+)\\)".toRegex()
                                val matches = regex.findAll(farm.coordinates.toString())
                                val geoJsonCoordinates =
                                    matches
                                        .map { match ->
                                            val (lat, lon) = match.destructured
                                            "[$lon, $lat]"
                                        }.joinToString(", ", prefix = "[", postfix = "]")
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
            return file
        } catch (e: IOException) {
            Toast.makeText(context, R.string.error_export_msg, Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun createFileForSharing(): File? {
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
                        "remote_id,farmer_name,member_id,collection_site,agent_name,farm_village,farm_district,farm_size,latitude,longitude,polygon,created_at,updated_at\n",
                    )
                    listItems.forEach { farm ->
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
                                        coordinates.joinToString(", ", prefix = "[", postfix = "]")
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

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Column {
//            FarmListHeaderPlots(
//                title = stringResource(id = R.string.farm_list),
//                onAddFarmClicked = { navController.navigate("addFarm/${siteId}") },
//                onBackClicked = { navController.navigate("siteList") },
//                onBackSearchClicked = { navController.navigate("farmList/${siteId}") },
//                onExportClicked = {
//                    action = Action.Export
//                    showFormatDialog = true
//                },
//                onShareClicked = {
//                    action = Action.Share
//                    showFormatDialog = true
//                },
//                searchQuery = searchQuery,
//                isSearchVisible = isSearchVisible,
//                onSearchQueryChanged = { query ->
//                    searchQuery = query
//                    currentPage = 1
//                },
//                onSearchVisibilityChanged = { isSearchVisible = it },
//                onBuyThroughAkrabiClicked = {
//                    navController.navigate("shopping")
//                },
//                onImportClicked = { showImportDialog = true },
//                showAdd = true,
//                showExport = listItems.isNotEmpty(),
//                showShare = listItems.isNotEmpty(),
//                showSearch = listItems.isNotEmpty(),
//                showBuyThroughAkrabi = listItems.isNotEmpty(),
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.farm_list)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("siteList")}, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Site List")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { /* Handle restore action */ },
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
                            IconButton(onClick = {action = Action.Export;showFormatDialog = true}, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.save),
                                    contentDescription = "Export",
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                            Spacer(modifier = Modifier.width(1.dp))
                        }
                        if (listItems.isNotEmpty()) {
                            IconButton(onClick = {action = Action.Share;showFormatDialog = true}, modifier = Modifier.size(36.dp)) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(1.dp))
                        }
                        IconButton(
                            onClick = {
                                if (!isImportDisabled) {
                                    showImportDialog = true
                                    isImportDisabled = true // Disable the import icon after importing
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
                        IconButton(onClick = { isSearchActive = !isSearchActive }, modifier = Modifier.size(36.dp)) {
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
                    modifier = Modifier.padding(16.dp)
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

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isLoading.value) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
                        LazyColumn {
                            items(3) {
                                FarmCardSkeleton()
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    } else {
                        val itemsPerPage = 3 // Adjust this value as needed
                        var currentPage by remember { mutableStateOf(1) }

                        // Filter items based on the selected tab
                        val filteredItems = if (selectedTabIndex == 0) {
                            listItems
                        } else {
                            listItems.filter { it.needsUpdate }
                        }.filter { farm ->
                            farm.farmerName.contains(searchQuery, ignoreCase = true) ||
                                    farm.id.toString().contains(searchQuery, ignoreCase = true)
                        }

                        val totalPages = (filteredItems.size + itemsPerPage - 1) / itemsPerPage
                        val paginatedList =
                            filteredItems.chunked(itemsPerPage).getOrNull(currentPage - 1)
                                ?: emptyList()


                        // Show number of selected items if any
                        if (selectedIds.isNotEmpty()) {
                            Text(
                                text = "${selectedIds.size} item(s) selected",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        if (filteredItems.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No results found",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {
                                items(paginatedList) { farm ->
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

                        if (listItems.isEmpty()) {
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
                        if (filteredItems.isNotEmpty()) {
                            CustomPaginationControls(
                                currentPage = currentPage,
                                totalPages = totalPages,
                                onPageChange = { newPage ->
                                    currentPage = newPage
                                }
                            )
                        }
                    }
                }
            }
        )


        if (selectedIds.isNotEmpty()) {
            BottomActionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                onDeleteClick = {
                    // showDeleteDialog.value = true
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
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ImportFileDialog(
    siteId: Long,
    onDismiss: () -> Unit,
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val farmViewModel: FarmViewModel = viewModel()
    var selectedFileType by remember { mutableStateOf("") }
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }
    // var importCompleted by remember { mutableStateOf(false) }

    // Create a launcher to handle the file picker result
    val importLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                coroutineScope.launch {
                    try {
                        val result = farmViewModel.importFile(context, it, siteId)
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        navController.navigate("farmList/$siteId") // Navigate to the refreshed farm list
                        onDismiss() // Dismiss the dialog after import is complete
                    } catch (e: Exception) {
                        Toast.makeText(context, R.string.import_failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    // Create a launcher to handle the file creation result
    val createDocumentLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument(),
        ) { uri: Uri? ->
            uri?.let {
                // Get the template content based on the selected file type
                val templateContent = farmViewModel.getTemplateContent(selectedFileType)
                // Save the template content to the created document
                coroutineScope.launch {
                    try {
                        farmViewModel.saveFileToUri(context, it, templateContent)
                    } catch (e: Exception) {
                        Toast.makeText(context, R.string.template_download_failed, Toast.LENGTH_SHORT).show()
                    }
                    onDismiss() // Dismiss the dialog
                }
            }
        }

    // Function to download the template file
    fun downloadTemplate() {
        coroutineScope.launch {
            try {
                // Prompt the user to select where to save the file
                createDocumentLauncher.launch(
                    when (selectedFileType) {
                        "csv" -> "farm_template.csv"
                        "geojson" -> "farm_template.geojson"
                        else -> throw IllegalArgumentException("Unsupported file type: $selectedFileType")
                    },
                )
            } catch (e: Exception) {
                Toast.makeText(context, R.string.template_download_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.import_file)) },
        text = {
            Column(
                modifier =
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.select_file_type),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .clickable { isDropdownMenuExpanded = true }
                        .padding(16.dp),
                ) {
                    Text(
                        text = if (selectedFileType.isNotEmpty()) selectedFileType else stringResource(R.string.select_file_type),
                        color = if (selectedFileType.isNotEmpty()) Color.Black else Color.Gray,
                    )
                    DropdownMenu(
                        expanded = isDropdownMenuExpanded,
                        onDismissRequest = { isDropdownMenuExpanded = false },
                    ) {
                        DropdownMenuItem(onClick = {
                            selectedFileType = "csv"
                            isDropdownMenuExpanded = false
                        }, text = { Text("CSV") })
                        DropdownMenuItem(onClick = {
                            selectedFileType = "geojson"
                            isDropdownMenuExpanded = false
                        }, text = { Text("GeoJSON") })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { downloadTemplate() },
                    enabled = selectedFileType.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Text(stringResource(R.string.download_template))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.select_file_to_import),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                importLauncher.launch("*/*")
            }) {
                Text(stringResource(R.string.select_file))
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

//@Composable
//fun DeleteAllDialogPresenter(
//    showDeleteDialog: MutableState<Boolean>,
//    onProceedFn: () -> Unit,
//) {
//    if (showDeleteDialog.value) {
//        AlertDialog(
//            modifier = Modifier.padding(horizontal = 32.dp),
//            onDismissRequest = { showDeleteDialog.value = false },
//            title = { Text(text = stringResource(id = R.string.delete_this_item)) },
//            text = {
//                Column {
//                    Text(stringResource(id = R.string.are_you_sure))
//                    Text(stringResource(id = R.string.item_will_be_deleted))
//                }
//            },
//            confirmButton = {
//                TextButton(onClick = { onProceedFn() }) {
//                    Text(text = stringResource(id = R.string.yes))
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDeleteDialog.value = false }) {
//                    Text(text = stringResource(id = R.string.no))
//                }
//            },
//        )
//    }
//}

@Composable
fun DeleteAllDialogPresenter(
    showDeleteDialog: MutableState<Boolean>,
    onProceedFn: (deleteAll: Boolean) -> Unit,
) {
    val (selectedOption, setSelectedOption) = remember { mutableStateOf(true) } // true for deleting all data, false for deleting selected data

    if (showDeleteDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 32.dp),
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = stringResource(id = R.string.delete_this_item)) },
            text = {
                Column {
                    Text(stringResource(id = R.string.are_you_sure))
                    Text(stringResource(id = R.string.item_will_be_deleted))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Options for deleting all data or only selected data
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption,
                            onClick = { setSelectedOption(true) }
                        )
                        Text(
                            text = stringResource(id = R.string.delete_all),
                            modifier = Modifier.clickable { setSelectedOption(true) }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = !selectedOption,
                            onClick = { setSelectedOption(false) }
                        )
                        Text(
                            text = stringResource(id = R.string.delete_selected),
                            modifier = Modifier.clickable { setSelectedOption(false) }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onProceedFn(selectedOption)
                    showDeleteDialog.value = false
                }) {
                    Text(text = stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text(text = stringResource(id = R.string.no))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FarmListHeader(
    title: String,
    onSearchQueryChanged: (String) -> Unit,
    onAddFarmClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onBackSearchClicked: () -> Unit,
    showAdd: Boolean,
    showSearch: Boolean,
    selectedItemsCount: Int,
    selectAllEnabled: Boolean,
    isAllSelected: Boolean,
    onSelectAllChanged: (Boolean) -> Unit,
    navController: NavController,
    darkMode: MutableState<Boolean>,
    languageViewModel: LanguageViewModel,
    languages: List<Language>
) {
    // State for holding the search query
    var searchQuery by remember { mutableStateOf("") }

    var isSearchVisible by remember { mutableStateOf(false)}

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)
    var drawerVisible by remember { mutableStateOf(false) }

    TopAppBar(
        modifier =
        Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = { drawerVisible = ! drawerVisible }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        title = {
            Text(
                text = title,
                style =
                MaterialTheme.typography.bodySmall.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                ),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center,
            )
        },
        actions = {
            if (showAdd) {
                IconButton(onClick = onAddFarmClicked) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
            if (showSearch) {
                IconButton(onClick = {
                    isSearchVisible = !isSearchVisible
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }

            if (selectedItemsCount > 0 && selectAllEnabled) {
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = { onSelectAllChanged(it) }
                )
            }

        },
    )
    // Conditional rendering of the search field
    if (isSearchVisible && showSearch) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChanged(it)
                },
                modifier =
                Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                label = { Text(stringResource(R.string.search)) },
                leadingIcon = {
                    IconButton(onClick = {
                        // onBackSearchClicked()
                        searchQuery = ""
                        onSearchQueryChanged("")
                        isSearchVisible = !isSearchVisible
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
//                trailingIcon = {
//                    IconButton(onClick = {
//                        searchQuery = ""
//                        onSearchQueryChanged("")
//                        isSearchVisible = !isSearchVisible
//                    }) {
//                        Icon(Icons.Default.Close, contentDescription = "Close")
//                    }
//                },
                singleLine = true,
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
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
                                        navController.navigate("shopping")
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
                                            sharedPreferences.edit().putBoolean("dark_mode", it)
                                                .apply()
                                        }
                                    )
                                }
                            }
                            item {
                                Divider()
                            }
//                            item {
//                                // Language Selector
//                                Text(
//                                    text = stringResource(id = R.string.select_language),
//                                    style = MaterialTheme.typography.titleMedium
//                                )
//                                Column(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    languages.forEach { language ->
//                                        LanguageCardSideBar(
//                                            language = language,
//                                            isSelected = language == currentLanguage,
//                                            onSelect = {
//                                                languageViewModel.selectLanguage(language, context)
//                                            }
//                                        )
//                                    }
//                                }
//                            }
                            // using checkbox

                            item {
                                Text(
                                    text = stringResource(id = R.string.select_language),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
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
                                        Text(text = currentLanguage.displayName)
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(230.dp) // Set the width of the DropdownMenu to match the Box
                                            .background(Color.White) // Set the background color to white for visibility
                                    ) {
                                        languages.forEach { language ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = language.displayName,
                                                        color = Color.Black // Ensure text is visible against the white background
                                                    )
                                                },
                                                onClick = {
                                                    languageViewModel.selectLanguage(language, context)
                                                    expanded = false
                                                },
                                                modifier = Modifier
                                                    .background(Color.White) // Ensure each menu item has a white background
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
                                        navController.popBackStack()
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmListHeaderPlots(
    title: String,
    onAddFarmClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onBackSearchClicked: () -> Unit,
    onExportClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onImportClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String, // Pass the search query as a parameter
    isSearchVisible: Boolean, // Control search visibility from outside
    onSearchVisibilityChanged: (Boolean) -> Unit ,// Add this
    onBuyThroughAkrabiClicked: () -> Unit, // Added this
    showAdd: Boolean,
    showExport: Boolean,
    showShare: Boolean,
    showSearch: Boolean,
    showBuyThroughAkrabi: Boolean // Added this
) {
    val context = LocalContext.current as Activity

//    // State for holding the search query
//    var searchQuery by remember { mutableStateOf("") }
//    var isSearchVisible by remember { mutableStateOf(false) }

    // State for tracking if import has been completed
    var isImportDisabled by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = title, fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (showExport) {
                IconButton(onClick = onExportClicked, modifier = Modifier.size(24.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.save),
                        contentDescription = "Export",
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (showShare) {
                IconButton(onClick = onShareClicked, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            IconButton(
                onClick = {
                    if (!isImportDisabled) {
                        onImportClicked()
                        isImportDisabled = true // Disable the import icon after importing
                    }
                },
                modifier = Modifier.size(24.dp),
                enabled = !isImportDisabled // Disable the button if import is completed
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icons8_import_file_48),
                    contentDescription = "Import",
                    modifier = Modifier.size(24.dp),
                )
            }
//            Spacer(modifier = Modifier.width(2.dp))
//            // New button for buying through Akrabi
//            if (showBuyThroughAkrabi) {
//                IconButton(onClick = onBuyThroughAkrabiClicked, modifier = Modifier.size(24.dp)) {
//                    Icon(Icons.Default.ShoppingCart, contentDescription = "Buy Through Akrabi", modifier = Modifier.size(24.dp))
//                }
//                Spacer(modifier = Modifier.width(2.dp))
//            }

            Spacer(modifier = Modifier.width(2.dp))

            if (showAdd) {
                IconButton(onClick = {
                    // Remove plot_size from shared preferences
                    val sharedPref = context.getSharedPreferences("FarmCollector", Context.MODE_PRIVATE)
                    if (sharedPref.contains("plot_size")) {
                        sharedPref.edit().remove("plot_size").apply()
                    }
                    if (sharedPref.contains("selectedUnit")) {
                        sharedPref.edit().remove("selectedUnit").apply()
                    }
                    // Call the onAddFarmClicked lambda
                    onAddFarmClicked()
                }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (showSearch) {
                IconButton(onClick = {
//                    isSearchVisible = !isSearchVisible
                    onSearchVisibilityChanged(!isSearchVisible)
                }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(24.dp))
                }
            }
        },
    )

//    // Conditional rendering of the search field
//    if (isSearchVisible && showSearch) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier =
//            Modifier
//                .padding(horizontal = 16.dp)
//                .fillMaxWidth(),
//        ) {
//            OutlinedTextField(
//                value = searchQuery,
//                onValueChange = {
//                    searchQuery = it
//                    onSearchQueryChanged(it)
//                },
//                modifier =
//                Modifier
//                    .padding(start = 8.dp)
//                    .weight(1f),
//                label = { Text(stringResource(R.string.search)) },
//                leadingIcon = {
//                    IconButton(onClick = {
//                        // onBackSearchClicked()
//                        searchQuery = ""
//                        onSearchQueryChanged("")
//                        isSearchVisible = !isSearchVisible
//                    }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                singleLine = true,
//                colors =
//                TextFieldDefaults.outlinedTextFieldColors(
//                    cursorColor = MaterialTheme.colorScheme.onSurface,
//                ),
//            )
//        }
//    }

    // Conditional rendering of the search field
    if (isSearchVisible && showSearch) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                label = { Text(stringResource(R.string.search)) },
                leadingIcon = {
                    IconButton(onClick = {
                        onBackSearchClicked()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                singleLine = true,
            )
        }
    }
}

@Composable
fun FarmCard(
    farm: Farm,
    navController: NavController,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleSelect: (Long) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    ElevatedCard(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable { onCardClick() },
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleSelect(farm.id) }
                    .background(
                        if (isSelected) Color.LightGray else Color.Transparent
                    )
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked= isSelected,
                    onCheckedChange = { onToggleSelect(farm.id)}
                )

                // Farm Name and Village (Left Side)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = farm.farmerName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = textColor
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${stringResource(id = R.string.size)}: ${formatInput(farm.size.toString())} ${stringResource(id = R.string.ha)}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                    )
                    Text(
                        text = "${stringResource(id = R.string.village)}: ${farm.village}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                    )
                    Text(
                        text = "${stringResource(id = R.string.district)}: ${farm.district}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                    )
                }

                // Action Icons (Right Side)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    IconButton(
                        onClick = { navController.navigate("updateFarm/${farm.id}") },
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .size(24.dp) // Reduced padding and set explicit size
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .size(24.dp) // Reduced padding and set explicit size
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete),
                            tint = Color.Red
                        )
                    }
                }
            }

            // Show the label if the farm needs an update
            if (farm.needsUpdate) {
                Text(
                    text = stringResource(id = R.string.needs_update),
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FarmCardSkeleton(
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White

    ElevatedCard(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skeleton checkbox
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                )

                // Skeleton farm info (Left Side)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    // Skeleton for farm name
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(0.5f)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                            .padding(bottom = 4.dp)
                    )

                    // Skeleton for size
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.4f)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Skeleton for village
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.6f)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Skeleton for district
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.4f)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    )
                }

                // Skeleton for action icons (Right Side)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    // Skeleton edit icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.LightGray, shape = CircleShape)
                            .padding(end = 2.dp)
                    )

                    // Skeleton delete icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.LightGray, shape = CircleShape)
                            .padding(end = 2.dp)
                    )
                }
            }

            // Skeleton for needs update label
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.3f)
                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    .padding(top = 4.dp)
            )
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

@SuppressLint("MissingPermission", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UpdateFarmForm(
    navController: NavController,
    farmId: Long?,
    listItems: List<Farm>,
    languageViewModel: LanguageViewModel,
    darkMode: MutableState<Boolean>,
    languages: List<Language>
) {
    val floatValue = 123.45f
    val item = listItems.find { it.id == farmId } ?: Farm(
        siteId = 0L,
        farmerPhoto = "Default photo",
        farmerName = "Default Farmer",
        memberId = "",
        village = "Default Village",
        district = "Default District",
        purchases = floatValue,
        size = floatValue,
        latitude = "0.0", // Default latitude
        longitude = "0.0", // Default longitude
        coordinates = null,
        age = 0,  // Default age
        gender = "",  // Default gender
        govtIdNumber = "",  // Default government ID number
        numberOfTrees = 0,  // Default number of trees
        phone = "",  // Default phone
        photo = "",  // Default photo
        synced = false,
        scheduledForSync = false,
        createdAt = 1L,
        updatedAt = 1L
    )

    val context = LocalContext.current as Activity
    var farmerName by remember { mutableStateOf(item.farmerName) }
    var memberId by remember { mutableStateOf(item.memberId) }
    var farmerPhoto by remember { mutableStateOf(item.farmerPhoto) }
    var village by remember { mutableStateOf(item.village) }
    var district by remember { mutableStateOf(item.district) }
    var age by remember { mutableStateOf(item.age.toString()) }
    var gender by remember { mutableStateOf(item.gender) }
    var govtIdNumber by remember { mutableStateOf(item.govtIdNumber.toString()) }
    var phone by remember { mutableStateOf(item.phone) }
    var numberOfTrees by remember { mutableStateOf(item.numberOfTrees.toString()) }

    //var photo by remember { mutableStateOf(item.photo)}
    // var photoUri by remember { mutableStateOf(Uri.parse(item.photo))}

    var size by remember { mutableStateOf(item.size.toString()) }
    var latitude by remember { mutableStateOf(item.latitude) }
    var longitude by remember { mutableStateOf(item.longitude) }
    var coordinates by remember { mutableStateOf(item.coordinates) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val farmViewModel: FarmViewModel =
        viewModel(
            factory = FarmViewModelFactory(context.applicationContext as Application),
        )
    val showDialog = remember { mutableStateOf(false) }
    val showLocationDialog = remember { mutableStateOf(false) }
    val showLocationDialogNew = remember { mutableStateOf(false) }
    val showPermissionRequest = remember { mutableStateOf(false) }
    val file = context.createImageFile()
    val uri =
        FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            context.packageName + ".provider",
            file,
        )
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Ha", "Acres", "Sqm", "Timad", "Fichesa", "Manzana", "Tarea")
    var selectedUnit by remember { mutableStateOf(items[0]) }

    val scientificNotationPattern = Pattern.compile("([+-]?\\d*\\.?\\d+)[eE][+-]?\\d+")

    LaunchedEffect(Unit) {
        if (!isLocationEnabled(context)) {
            showLocationDialog.value = true
        }
    }

    // Define string constants
    val titleText = stringResource(id = R.string.enable_location_services)
    val messageText = stringResource(id = R.string.location_services_required_message)
    val enableButtonText = stringResource(id = R.string.enable)

    // Dialog to prompt user to enable location services
    if (showLocationDialog.value) {
        AlertDialog(
            onDismissRequest = { showLocationDialog.value = false },
            title = { Text(titleText) },
            text = { Text(messageText) },
            confirmButton = {
                Button(onClick = {
                    showLocationDialog.value = false
                    promptEnableLocation(context)
                }) {
                    Text(enableButtonText)
                }
            },
            dismissButton = {
                Button(onClick = {
                    showLocationDialog.value = false
                    Toast.makeText(context, R.string.location_permission_denied_message, Toast.LENGTH_SHORT).show()
                }) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
        )
    }

//    if (navController.currentBackStackEntry!!.savedStateHandle.contains("coordinates")) {
//        coordinates =
//            navController.currentBackStackEntry!!.savedStateHandle.get<List<Pair<Double, Double>>>(
//                "coordinates",
//            )
//    }

    if (navController.currentBackStackEntry!!.savedStateHandle.contains("coordinates")) {
        val parcelableCoordinates = navController.currentBackStackEntry!!
            .savedStateHandle
            .get<List<ParcelablePair>>("coordinates")

        coordinates = parcelableCoordinates?.map { Pair(it.first, it.second) }
    }


    val fillForm = stringResource(id = R.string.fill_form)

    fun validateForm(): Boolean {
        var isValid = true

        if (farmerName.isBlank()) {
            isValid = false
        }

        if (village.isBlank()) {
            isValid = false
        }

        if (district.isBlank()) {
            isValid = false
        }

        if (size.toFloatOrNull()?.let { it > 0 } != true) {
            isValid = false
        }

        if (latitude.isBlank() || longitude.isBlank()) {
            isValid = false
        }

        return isValid
    }

    /**
     * Updating Farm details
     * Before sending to the database
     */

    fun updateFarmInstance() {
        val isValid = validateForm()
        if (isValid) {
            item.farmerPhoto = ""
            item.farmerName = farmerName
            item.memberId = memberId
            item.latitude = latitude
            item.village = village
            item.district = district
            item.age = age?.toInt()
            item.gender = gender
            item.govtIdNumber = govtIdNumber
            item.phone = phone
            item.numberOfTrees = numberOfTrees.toIntOrNull()?: 0
            item.photo = "" // Default photo
            item.longitude = longitude
            if ((size.toDoubleOrNull()?.let { convertSize(it, selectedUnit).toFloat() } ?: 0f) >= 4) {
                if ((coordinates?.size ?: 0) < 3) {
                    Toast
                        .makeText(
                            context,
                            R.string.error_polygon_points,
                            Toast.LENGTH_SHORT,
                        ).show()
                    return
                }
                item.coordinates = coordinates?.plus(coordinates?.first()) as List<Pair<Double, Double>>
            } else {
                item.coordinates = listOf(Pair(item.longitude.toDoubleOrNull() ?: 0.0, item.latitude.toDoubleOrNull() ?: 0.0)) // Example default value
            }
            item.size = convertSize(size.toDouble(), selectedUnit).toFloat()
            item.purchases = 0.toFloat()
            item.updatedAt = Instant.now().millis
            updateFarm(farmViewModel, item)
            item.needsUpdate = false
            val returnIntent = Intent()
            context.setResult(Activity.RESULT_OK, returnIntent)
            navController.navigate("farmList/$siteID")
        } else {
            Toast.makeText(context, fillForm, Toast.LENGTH_SHORT).show()
        }
    }
    // Confirm farm update and ask if they wish to capture new polygon
    if (showDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 32.dp),
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(id = R.string.update_farm)) },
            text = {
                Column {
                    Text(text = stringResource(id = R.string.confirm_update_farm))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    updateFarmInstance()
                }) {
                    Text(text = stringResource(id = R.string.update_farm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick =
                    {
                        showDialog.value = false
                        navController.navigate("setPolygon")
                    },
                ) {
                    Text(text = stringResource(id = R.string.set_polygon))
                }
            },
        )
    }

    val scrollState = rememberScrollState()
    val (focusRequester1) = FocusRequester.createRefs()
    val (focusRequester2) = FocusRequester.createRefs()
    val (focusRequester3) = FocusRequester.createRefs()

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val inputLabelColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    val inputTextColor = if (isDarkTheme) Color.White else Color.Black
    val buttonColor = if (isDarkTheme) Color.Black else Color.White
    val inputBorder = if (isDarkTheme) Color.LightGray else Color.DarkGray

    var drawerVisible by remember { mutableStateOf(false) }
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)



    if (showPermissionRequest.value) {
        LocationPermissionRequest(
            onLocationEnabled = {
                showLocationDialog.value = true
            },
            onPermissionsGranted = {
                showPermissionRequest.value = false
            },
            onPermissionsDenied = {
                // Handle permissions denied
                // Show a message or take appropriate action
            },
            showLocationDialogNew = showLocationDialogNew,
            hasToShowDialog = showLocationDialogNew.value,
        )
    }

//    Column(
//        modifier =
//        Modifier
//            .fillMaxWidth()
//            .background(backgroundColor)
//            .padding(16.dp)
////            .verticalScroll(state = scrollState),
//    ) {
//        FarmListHeader(
//            title = stringResource(id = R.string.update_farm),
//            onSearchQueryChanged = {},
//            onAddFarmClicked = { /* Handle adding a farm here */ },
//            onBackClicked = { navController.popBackStack() },
//            onBackSearchClicked = {},
//            showAdd = false,
//            showSearch = false,
//            selectedItemsCount = 0,
//            selectAllEnabled = false,
//            isAllSelected =false,
//            onSelectAllChanged = { null},
//            darkMode = darkMode,
//            languages = languages,
//            languageViewModel = languageViewModel,
//            navController = navController
//        )
//
//        Column(
//            modifier =
//            Modifier
//                .fillMaxWidth()
//                .background(backgroundColor)
//                .padding(8.dp)
//                .verticalScroll(state = scrollState),
//        ) {

    // Composable content
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.update_farm)) },
//                    navigationIcon = {
//                        IconButton(onClick = { drawerVisible = !drawerVisible }) {
//                            Icon(Icons.Default.Menu, contentDescription = "Menu")
//                        }
//                    }
//                    actions = {
//                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
//                            Icon(
//                                if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
//                                contentDescription = if (isSearchActive) "Close Search" else "Search"
//                            )
//                        }
//                    }
                )
            },
//            floatingActionButton = {
//                FloatingActionButton(
//                    onClick = {
//                        navController.navigate("addSite")
//                    },
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Icon(Icons.Default.Add, contentDescription = "Add Site")
//                }
//            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(8.dp)
                        .verticalScroll(state= rememberScrollState())
                ) {

                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester1.requestFocus() },
                        ),
                        value = farmerName,
                        onValueChange = { farmerName = it },
                        label = {
                            Text(
                                stringResource(id = R.string.farm_name),
                                color = inputLabelColor
                            )
                        },
                        isError = farmerName.isBlank(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                            errorLeadingIconColor = Color.Red,
                            cursorColor = inputTextColor,
                            errorCursorColor = Color.Red,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder,
                            errorBorderColor = Color.Red
                        ),
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .onKeyEvent {
                                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                                    focusRequester1.requestFocus()
                                    true
                                }
                                false
                            },
                    )
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester1.requestFocus() },
                        ),
                        value = memberId,
                        onValueChange = { memberId = it },
                        label = {
                            Text(
                                stringResource(id = R.string.member_id),
                                color = inputLabelColor
                            )
                        },
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .onKeyEvent {
                                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                                    focusRequester1.requestFocus()
                                }
                                false
                            },
                    )
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester2.requestFocus() },
                        ),
                        value = village,
                        onValueChange = { village = it },
                        label = {
                            Text(
                                stringResource(id = R.string.village),
                                color = inputLabelColor
                            )
                        },
                        isError = village.isBlank(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                            errorLeadingIconColor = Color.Red,
                            cursorColor = inputTextColor,
                            errorCursorColor = Color.Red,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder,
                            errorBorderColor = Color.Red
                        ),
                        modifier =
                        Modifier
                            .focusRequester(focusRequester1)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester3.requestFocus() },
                        ),
                        value = district,
                        onValueChange = { district = it },
                        label = {
                            Text(
                                stringResource(id = R.string.district),
                                color = inputLabelColor
                            )
                        },
                        isError = district.isBlank(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                            errorLeadingIconColor = Color.Red,
                            cursorColor = inputTextColor,
                            errorCursorColor = Color.Red,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder,
                            errorBorderColor = Color.Red
                        ),
                        modifier =
                        Modifier
                            .focusRequester(focusRequester2)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )

                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester3.requestFocus() },
                        ),
                        value = age,
                        onValueChange = { age = it },
                        label = {
                            Text(
                                stringResource(id = R.string.age),
                                color = inputLabelColor
                            )
                        },
                        isError = district.isBlank(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                            errorLeadingIconColor = Color.Red,
                            cursorColor = inputTextColor,
                            errorCursorColor = Color.Red,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder,
                            errorBorderColor = Color.Red
                        ),
                        modifier =
                        Modifier
                            .focusRequester(focusRequester2)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )


                    // Gender (Dropdown)
                    gender?.let {
                        GenderDropdown(
                            gender = it,
                            onGenderSelected =
                            { selectedGender ->
                                gender = selectedGender
                            }
                        )
                    }

                    // Govt ID Number
                    govtIdNumber?.let {
                        OutlinedTextField(
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            value = it,
                            onValueChange = { govtIdNumber = it },
                            label = { Text("Govt ID Number", color = inputLabelColor) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = backgroundColor,
                                cursorColor = inputTextColor,
                                focusedBorderColor = inputBorder,
                                unfocusedBorderColor = inputBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                    }

                    // Number of Trees
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        value = numberOfTrees,
                        onValueChange = { numberOfTrees = it },
                        label = { Text("Number of Trees", color = inputLabelColor) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = backgroundColor,
                            cursorColor = inputTextColor,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )

                    // Phone
                    phone?.let {
                        OutlinedTextField(
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            value = it,
                            onValueChange = { phone = it },
                            label = { Text("Phone", color = inputLabelColor) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = backgroundColor,
                                cursorColor = inputTextColor,
                                focusedBorderColor = inputBorder,
                                unfocusedBorderColor = inputBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                    }

//            // Pick Image Button
//            ImagePicker { uri ->
//                photoUri = uri
//                photo = uri?.toString() ?: ""
//            }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        OutlinedTextField(
                            singleLine = true,
                            value = truncateToDecimalPlaces(size, 9),
                            onValueChange = {
                                size = it
                            },
//                value =  truncateToDecimalPlaces(formatInput(size),9),
//                onValueChange = { inputValue ->
//                    val formattedValue = when {
//                        validateSize(inputValue) -> inputValue
//                        // Check if the input is in scientific notation
//                        scientificNotationPattern.matcher(inputValue).matches() -> {
//                            truncateToDecimalPlaces(formatInput(inputValue),9)
//                        }
//                        else -> inputValue
//                    }
//
//                    // Update the size state with the formatted value
//                    size = formattedValue
//                },
                            keyboardOptions =
                            KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                            ),
                            label = {
                                Text(
                                    stringResource(id = R.string.size_in_hectares) + " (*)",
                                    color = inputLabelColor
                                )
                            },
                            isError = size.toFloatOrNull() == null || size.toFloat() <= 0, // Validate size
                            colors =
                            TextFieldDefaults.textFieldColors(
                                containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                                errorLeadingIconColor = Color.Red,
                                cursorColor = inputTextColor,
                                errorCursorColor = Color.Red,
                                focusedIndicatorColor = inputBorder,
                                unfocusedIndicatorColor = inputBorder,
                                errorIndicatorColor = Color.Red,
                            ),
                            modifier =
                            Modifier
                                .focusRequester(focusRequester3)
                                .weight(1f)
                                .padding(bottom = 4.dp),
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        // Size measure
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = selectedUnit,
                                onValueChange = { },
                                label = { Text(stringResource(R.string.unit)) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded,
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                                ),
                                modifier = Modifier.menuAnchor(),
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                },
                            ) {
                                items.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        { Text(text = selectionOption) },
                                        onClick = {
                                            selectedUnit = selectionOption
                                            expanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

//        Spacer(modifier = Modifier.height(16.dp)) // Add space between the latitude and longitude input fields
                    if ((size.toDoubleOrNull()?.let { convertSize(it, selectedUnit).toFloat() }
                            ?: 0f) < 4f) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = latitude,
                                onValueChange = { latitude = it },
                                label = {
                                    Text(
                                        stringResource(id = R.string.latitude),
                                        color = inputLabelColor
                                    )
                                },
                                modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(bottom = 4.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Add space between the latitude and longitude input fields
                            OutlinedTextField(
                                readOnly = true,
                                value = longitude,
                                onValueChange = { longitude = it },
                                label = {
                                    Text(
                                        stringResource(id = R.string.longitude),
                                        color = inputLabelColor
                                    )
                                },
                                modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(bottom = 16.dp),
                            )
                        }
                    }
                    Button(
                        onClick = {
                            showPermissionRequest.value = true
                            if (!isLocationEnabled(context)) {
                                showLocationDialog.value = true
                            } else {
                                if (isLocationEnabled(context) && context.hasLocationPermission()) {
//                        if (size.toFloatOrNull() != null && size.toFloat() < 4) {
                                    if (size.toDoubleOrNull()
                                            ?.let { convertSize(it, selectedUnit).toFloat() }
                                            ?.let { it < 4f } == true
                                    ) {
                                        // Simulate collecting latitude and longitude
                                        if (context.hasLocationPermission()) {
                                            val locationRequest =
                                                LocationRequest.create().apply {
                                                    priority =
                                                        LocationRequest.PRIORITY_HIGH_ACCURACY
                                                    interval =
                                                        10000 // Update interval in milliseconds
                                                    fastestInterval =
                                                        5000 // Fastest update interval in milliseconds
                                                }

                                            fusedLocationClient.requestLocationUpdates(
                                                locationRequest,
                                                object : LocationCallback() {
                                                    override fun onLocationResult(locationResult: LocationResult) {
                                                        locationResult.lastLocation?.let { lastLocation ->
                                                            // Handle the new location
                                                            latitude = "${lastLocation.latitude}"
                                                            longitude = "${lastLocation.longitude}"
                                                            // Log.d("FARM_LOCATION", "loaded success,,,,,,,")
                                                        }
                                                    }
                                                },
                                                Looper.getMainLooper(),
                                            )
                                        }
                                    } else {
                                        if (isLocationEnabled(context)) {
                                            navController.navigate("setPolygon")
                                        }
                                    }
                                } else {
                                    showPermissionRequest.value = true
                                    showLocationDialog.value = true
                                }
                            }
                        },
                        modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(0.7f)
                            .padding(bottom = 5.dp),
                        enabled = size.toFloatOrNull() != null,
                    ) {
                        Text(
                            // text = if (size.toFloatOrNull() != null && size.toFloat() < 4) stringResource(id = R.string.get_coordinates) else stringResource(
                            text =
                            if (size.toDoubleOrNull()
                                    ?.let { convertSize(it, selectedUnit).toFloat() }
                                    ?.let { it < 4f } ==
                                true
                            ) {
                                stringResource(id = R.string.get_coordinates)
                            } else {
                                stringResource(
                                    id = R.string.set_new_polygon,
                                )
                            },
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.Center // Ensures buttons are spaced evenly
                    ) {

                        Button(onClick = { navController.popBackStack()},
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text(text = stringResource(id = R.string.cancel), color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp)) // Adds some space between the buttons
                        Button(
                            onClick = {
                                if (validateForm()) {
                                    showDialog.value = true
                                } else {
                                    Toast.makeText(context, fillForm, Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.update_farm))
                        }
                    }



                }
            }
        )
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
                                        navController.navigate("shopping")
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
                                            sharedPreferences.edit().putBoolean("dark_mode", it)
                                                .apply()
                                        }
                                    )
                                }
                            }
                            item {
                                Divider()
                            }
                            item {
                                Text(
                                    text = stringResource(id = R.string.select_language),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
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
                                        Text(text = currentLanguage.displayName)
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(230.dp) // Set the width of the DropdownMenu to match the Box
                                            .background(Color.White) // Set the background color to white for visibility
                                    ) {
                                        languages.forEach { language ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = language.displayName,
                                                        color = Color.Black // Ensure text is visible against the white background
                                                    )
                                                },
                                                onClick = {
                                                    languageViewModel.selectLanguage(language, context)
                                                    expanded = false
                                                },
                                                modifier = Modifier
                                                    .background(Color.White) // Ensure each menu item has a white background
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
                                        navController.popBackStack()
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

fun updateFarm(
    farmViewModel: FarmViewModel,
    item: Farm,
) {
    farmViewModel.updateFarm(item)
}
