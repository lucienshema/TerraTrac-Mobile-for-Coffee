
package org.technoserve.cafetrac.ui.screens.farms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import org.technoserve.cafetrac.database.models.Farm
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import com.example.cafetrac.database.models.Language
import org.technoserve.cafetrac.database.models.ParcelablePair
import org.technoserve.cafetrac.ui.components.DrawerItem

import org.technoserve.cafetrac.viewmodels.LanguageViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import org.joda.time.Instant
import org.technoserve.cafetrac.ui.components.FarmForm
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import javax.inject.Inject


private const val REQUEST_CHECK_SETTINGS = 1000

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFarm(navController: NavController, siteId: Long, languageViewModel: LanguageViewModel,
            darkMode: MutableState<Boolean>,
            languages: List<Language>) {
    var coordinatesData: List<Pair<Double, Double>>? = null

    var accuracyArrayData: List<Float?>? = null
    if (navController.currentBackStackEntry!!.savedStateHandle.contains("coordinates")) {
        val parcelableCoordinates = navController.currentBackStackEntry!!
            .savedStateHandle
            .get<List<ParcelablePair>>("coordinates")
        coordinatesData = parcelableCoordinates?.map { Pair(it.first, it.second) }
        accuracyArrayData = navController.currentBackStackEntry!!.savedStateHandle.get<List<Float?>>("accuracyArray")
    }


    // State variables
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var drawerVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)

    // Composable content
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.add_farm)) },
                    navigationIcon = {
                        IconButton(onClick = {navController.popBackStack()}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Farm List")
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    // Call the FarmForm composable with the necessary parameters
                    FarmForm(navController, siteId,coordinatesData,accuracyArrayData)
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
// Helper function to truncate a string representation of a number to a specific number of decimal places
fun truncateToDecimalPlaces(value: String, decimalPlaces: Int): String {
    val dotIndex = value.indexOf('.')
    return if (dotIndex == -1 || dotIndex + decimalPlaces + 1 > value.length) {
        // If there's no decimal point or the length is already less than required, return the original value
        value
    } else {
        // Truncate the value after the specified number of decimal places
        value.substring(0, dotIndex + decimalPlaces + 1)
    }
}

// Function to read and format stored value
fun readStoredValue(sharedPref: SharedPreferences): String {
    val storedValue = sharedPref.getString("plot_size", "") ?: ""

    // Truncate the value to 4 decimal places without rounding
    val formattedValue = truncateToDecimalPlaces(storedValue, 9)

    return formattedValue
}

// Function to format input value to 6 decimal places without scientific notation
fun formatInput(input: String): String {
    return try {
        val number = BigDecimal(input)
        val scale = number.scale()
        val decimalPlaces = scale - number.precision()

        when {
            decimalPlaces > 3 -> {
                // Format to 6 decimal places without trailing zeros if more than 3 decimal places
                BigDecimal(input).setScale(9, RoundingMode.DOWN).stripTrailingZeros().toPlainString()

                //truncateToDecimalPlaces(input,9)

            }
            decimalPlaces == 0 -> {
                // No decimal part, return the number as is
                input
            }
            else -> {
                // Set the precision to 6 decimal places without rounding
                val formattedNumber = number.setScale(9, RoundingMode.DOWN)
                // If 3 or fewer decimal places, return as is without trailing zeros
                formattedNumber.stripTrailingZeros().toPlainString()
//                truncateToDecimalPlaces(input,9)
            }
        }
    } catch (e: NumberFormatException) {
        input // Return an empty string if the input is invalid
    }
}
fun validateSize(size: String): Boolean {
    // Check if the input matches the allowed pattern: digits and at most one dot
    val regex = Regex("^[0-9]*\\.?[0-9]*$")
    return size.matches(regex) && size.toFloatOrNull() != null && size.toFloat() > 0 && size.isNotBlank()
}




fun addFarm(
    farmViewModel: FarmViewModel,
    siteId: Long,
    remote_id: UUID,
    farmerPhoto: String,
    farmerName: String,
    memberId: String,
    village: String,
    district: String,
    purchases: Float,
    size: Float,
    latitude: String,
    longitude: String,
    coordinates: List<Pair<Double, Double>>?,
    accuracyArray : List<Float?>?,
    age: Int,  // New field
    gender: String,  // New field
    govtIdNumber: String,  // New field
    numberOfTrees: Int,  // New field
    phone: String,  // New field
    photo: String  // New field
): Farm {
    val farm = Farm(
        siteId = siteId,
        remoteId = remote_id,
        farmerPhoto = farmerPhoto,
        farmerName = farmerName,
        memberId = memberId,
        village = village,
        district = district,
        purchases = purchases,
        size = size,
        latitude = latitude,
        longitude = longitude,
        coordinates = coordinates,
        accuracyArray = accuracyArray,
        age = age?:0,  // Default value if null
        gender = gender ?: "",  // Default value if null
        govtIdNumber = govtIdNumber ?: "",  // Default value if null
        numberOfTrees = numberOfTrees,  // Default value if null
        phone = phone ?: "",  // Default value if null
        photo = photo ?: "",  // Default value if null
        synced = false,
        scheduledForSync = false,
        createdAt = Instant.now().millis,
        updatedAt = Instant.now().millis
    )
    farmViewModel.addFarm(farm, siteId)
    return farm
}


fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

fun promptEnableLocation(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionRequest(
    onLocationEnabled: () -> Unit,
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit,
    showLocationDialogNew: MutableState<Boolean>,
    hasToShowDialog: Boolean
) {
    val context = LocalContext.current
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        if (isLocationEnabled(context)) {
            if (multiplePermissionsState.allPermissionsGranted) {
                onPermissionsGranted()
            } else {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        } else {
            onLocationEnabled()
        }
    }

    // Optionally, show some text to inform the user about the importance of permissions
    if ((!multiplePermissionsState.allPermissionsGranted) && hasToShowDialog) {
        Column {
            AlertDialog(
                onDismissRequest = { showLocationDialogNew.value = false },
                title = { Text(stringResource(id = R.string.enable_location)) },
                text = { Text(stringResource(id = R.string.enable_location_msg)) },
                confirmButton = {
                    Button(onClick = {
                        // Perform action to enable location permissions
                        promptEnableLocation(context)
                        showLocationDialogNew.value = false  // Dismiss the dialog after action
                    }) {
                        Text(stringResource(id = R.string.yes))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        // Show a toast message indicating that the permission was denied
                        Toast.makeText(
                            context,
                            R.string.location_permission_denied_message,
                            Toast.LENGTH_SHORT
                        ).show()
                        showLocationDialogNew.value = false  // Dismiss the dialog after action
                    }) {
                        Text(stringResource(id = R.string.no))
                    }
                },
                containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
                tonalElevation = 6.dp // Adds a subtle shadow for better UX
            )
        }
    }
}


@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )

    return image
}

fun createDefaultBitmap(width: Int, height: Int): Bitmap {
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
}

@HiltViewModel
class FarmFormViewModel @Inject constructor() : ViewModel() {
    private val farmerName = mutableStateOf("")
    val memberId = mutableStateOf("")
    val village = mutableStateOf("")
    val district = mutableStateOf("")
    val size = mutableStateOf("")
    val latitude = mutableStateOf("")
    val longitude = mutableStateOf("")

    fun setFarmerName(name: String) {
        farmerName.value = name
    }

    fun setVillage(villageName: String) {
        village.value = villageName
    }

    fun setDistrict(districtName: String) {
        district.value = districtName
    }

    fun setSize(sizeValue: String) {
        size.value = sizeValue
    }

    fun setLatitude(latitudeValue: String) {
        latitude.value = latitudeValue
    }

    fun setLongitude(longitudeValue: String) {
        longitude.value = longitudeValue
    }
}

fun List<Pair<Double, Double>>.toLatLngList(): List<LatLng> {
    return map { LatLng(it.first, it.second) }
}