package org.technoserve.cafetrac.ui.screens.akrabis

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

import org.technoserve.cafetrac.ui.components.PhotoPicker
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditBuyThroughAkrabiForm(
    itemId: Long,
    farmViewModel: FarmViewModel,
    navController: NavController
) {
    // Fetch the item data based on the itemId
    val item by farmViewModel.getBoughtItemById(itemId).collectAsStateWithLifecycle(null)

    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var location by remember { mutableStateOf("") }
    var selectedSiteName by remember { mutableStateOf("") }
    var akrabiSearch by remember { mutableStateOf("") }
    var akrabiNumber by remember { mutableStateOf("") }
    var akrabiName by remember { mutableStateOf("") }
    var cherrySold by remember { mutableStateOf("") }
    var pricePerKg by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    LaunchedEffect(item) {
        item?.let {


            date = it.date // Assuming date is stored as a String in ISO format
            time = LocalTime.parse(it.time) // Assuming time is stored as a String in ISO format
            location = it.location
            selectedSiteName = it.siteName
            akrabiSearch = it.akrabiSearch
            akrabiNumber = it.akrabiNumber
            akrabiName = it.akrabiName
            cherrySold = it.cherrySold.toString()
            pricePerKg = it.pricePerKg.toString()
            paid = it.paid.toString()
            photoUri = it.photo?.let { Uri.parse(it) } // Assuming photo is stored as a String URI
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it
        }
    }

    // Call this when the user clicks the "Pick Photo" button
    fun pickPhoto() {
        pickImageLauncher.launch("image/*")
    }

    // Call this when the user clicks the "Remove Photo" button
    fun removePhoto() {
        photoUri = null
    }

    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Store validation errors
    val focusRequesterLocation = FocusRequester()
    val focusRequesterSite = FocusRequester()
    val focusRequesterAkrabi = FocusRequester()
    val focusRequesterCherrySold = FocusRequester()
    val focusRequesterPricePerKg = FocusRequester()
    val focusRequesterPaid = FocusRequester()

    // Focus manager
    val focusManager = LocalFocusManager.current

    // Handle focus change when "Next" is pressed
    fun onNextFocus(currentField: FocusRequester, nextField: FocusRequester) {
        currentField.requestFocus()
        focusManager.moveFocus(FocusDirection.Down)
        nextField.requestFocus()
    }

    // Get the strings from resources within a @Composable context
    val locationError = stringResource(id = R.string.location)
    val siteError = stringResource(id = R.string.site_name)
    val akrabiError = stringResource(id = R.string.akrabi_name)
    val cherryError = stringResource(id = R.string.cherry_sold)
    val priceError = stringResource(id = R.string.price_per_kg)
    val paidError = stringResource(id = R.string.paid)

    // Form validation function
    fun validateFormBuyThroughAkrabi(
        location: String,
        selectedSiteName: String,
        akrabiName: String,
        cherrySold: String,
        pricePerKg: String
    ): List<String> {
        val errors = mutableListOf<String>()
        if (location.isBlank()) errors.add(locationError)
        if (selectedSiteName.isBlank()) errors.add(siteError)
        if (akrabiName.isBlank()) errors.add(akrabiError)
        if (cherrySold.isBlank() || cherrySold.toDoubleOrNull() == null) errors.add(cherryError)
        if (pricePerKg.isBlank() || pricePerKg.toDoubleOrNull() == null) errors.add(priceError)
        if (paid.isBlank() || paid.toDoubleOrNull() == null) errors.add(paidError)
        return errors
    }

    // Define string constants
    val title = stringResource(id = R.string.buy_through_akrabi)
    val dateLabel = stringResource(id = R.string.date)
    val timeLabel = stringResource(id = R.string.time)
    val locationLabel = stringResource(id = R.string.location)
    val akrabiLabel = stringResource(id = R.string.akrabi_name)
    val akrabiNumberLabel = stringResource(id = R.string.akrabi_number)
    val SiteLabel = stringResource(id = R.string.site_name)
    val selectAkrabiLabel = stringResource(id = R.string.akrabi_name)
    val cherrySoldLabel = stringResource(id = R.string.cherry_sold)
    val pricePerKgLabel = stringResource(id = R.string.price_per_kg)
    val paidLabel = stringResource(id = R.string.paid)
    val submitLabel = stringResource(id = R.string.submit)

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = stringResource(id = R.string.confirm_update)) },
            text = { Text(text = stringResource(id = R.string.are_you_sure_save_the_changes)) },
            confirmButton = {
                Button(
                    onClick = {
                        item?.let {
                            val updatedItem = it.copy(
                                date = date,
                                time = time.toString(),
                                location = location,
                                siteName = selectedSiteName,
                                akrabiSearch = akrabiSearch,
                                akrabiNumber = akrabiNumber,
                                akrabiName = akrabiName,
                                cherrySold = cherrySold.toDouble(),
                                pricePerKg = pricePerKg.toDouble(),
                                paid = paid.toDouble(),
                                photoUri = photoUri?.toString()
                            )
                            farmViewModel.updateBuyThroughAkrabi(updatedItem)
                            navController.popBackStack()
                        }
                        showConfirmationDialog = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
            tonalElevation = 6.dp // Adds a subtle shadow for better UX
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.update_bought_item)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

//        // Date and Time pickers
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            TextField(
//                value = date.toString(),
//                onValueChange = { date = LocalDate.parse(it) },
//                label = { Text("Date") },
//                modifier = Modifier.weight(1f),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                readOnly = true,
//                trailingIcon = {
//                    IconButton(onClick = { /* Show date picker */ }) {
//                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Date")
//                    }
//                }
//            )
//            TextField(
//                value = time.toString(),
//                onValueChange = { time = LocalTime.parse(it) },
//                label = { Text("Time") },
//                modifier = Modifier.weight(1f),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                readOnly = true,
//                trailingIcon = {
//                    IconButton(onClick = { /* Show time picker */ }) {
//                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Time")
//                    }
//                }
//            )
//        }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(id = R.string.location)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterLocation),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterLocation, focusRequesterSite)
                    }
                ),
                isError = validationErrors.contains(stringResource(id = R.string.location))
            )
            if (validationErrors.contains(stringResource(id = R.string.location))) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                singleLine = true,
                value = selectedSiteName,
                onValueChange = { selectedSiteName = it },
                label = { Text(text = stringResource(id = R.string.site_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterSite, focusRequesterAkrabi)
                    }
                ),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterSite),
                isError = validationErrors.contains(SiteLabel)
            )

            if (validationErrors.contains(SiteLabel)) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

//        TextField(
//            value = akrabiSearch,
//            onValueChange = { akrabiSearch = it },
//            label = { Text("Akrabi Search") }
//        )

            OutlinedTextField(
                value = akrabiName,
                onValueChange = { akrabiName = it },
                label = { Text(text = stringResource(id = R.string.akrabi_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterAkrabi, focusRequesterCherrySold)
                    }
                ),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterAkrabi),
                isError = validationErrors.contains(akrabiLabel)
            )

            if (validationErrors.contains(akrabiLabel)) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }


            OutlinedTextField(
                value = akrabiNumber,
                onValueChange = { akrabiNumber = it },
                label = { Text(text = stringResource(id = R.string.akrabi_number)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                )
            )

            if (validationErrors.contains(akrabiNumber)) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = cherrySold,
                onValueChange = { cherrySold = it },
                label = { Text(text = stringResource(id = R.string.cherry_sold)) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterCherrySold),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterCherrySold, focusRequesterPricePerKg)
                    }
                ),
                isError = validationErrors.contains(cherrySoldLabel)
            )
            if (validationErrors.contains(cherrySoldLabel)) {
                Text(
                    text = stringResource(id = R.string.invalid_value),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = pricePerKg,
                onValueChange = { pricePerKg = it },
                label = { Text(text = stringResource(id = R.string.price_per_kg)) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterPricePerKg),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                isError = validationErrors.contains(pricePerKgLabel)
            )
            if (validationErrors.contains(pricePerKgLabel)) {
                Text(
                    text = stringResource(id = R.string.invalid_value),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = paid,
                onValueChange = { paid = it },
                label = { Text(text = stringResource(id = R.string.paid)) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterPaid),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                isError = validationErrors.contains(paidLabel)
            )
            if (validationErrors.contains(paidLabel)) {
                Text(
                    text = stringResource(id = R.string.invalid_value),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            PhotoPicker(
                photoUri = photoUri,
//            onPhotoSelected = { uri -> photoUri = uri },
                onPickPhotoClick = { pickPhoto() },
                onRemovePhotoClick = { removePhoto() },
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = stringResource(id = R.string.cancel), color = Color.White)
                }

                Button(
                    onClick = {
                        validationErrors = validateFormBuyThroughAkrabi(
                            location = location,
                            selectedSiteName = selectedSiteName,
                            akrabiName = akrabiName,
                            cherrySold = cherrySold,
                            pricePerKg = pricePerKg
                        )
                        if (validationErrors.isEmpty()) {
                            showConfirmationDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.save_changes))
                }
            }
        }
    }
}
