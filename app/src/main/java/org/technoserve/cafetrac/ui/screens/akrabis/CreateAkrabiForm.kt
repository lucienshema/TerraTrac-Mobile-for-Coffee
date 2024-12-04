package org.technoserve.cafetrac.ui.screens.akrabis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.cafetrac.database.models.Akrabi
import com.example.cafetrac.database.models.CollectionSite
import org.technoserve.cafetrac.ui.components.GenderDropdown
import org.technoserve.cafetrac.ui.components.ImagePicker
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAkrabiForm(
    navController: NavController, // Pass NavController for navigation
    akrabi: Akrabi? = null,
    title: String,
    collectionSites: List<CollectionSite>,
    onSubmit: (Akrabi) -> Unit,
    onCancel: () -> Unit
) {
    var akrabiNumber by remember { mutableStateOf(akrabi?.akrabiNumber ?: "") }
    var akrabiName by remember { mutableStateOf(akrabi?.akrabiName ?: "") }
    var selectedSiteName by remember { mutableStateOf(akrabi?.siteName ?: "") }

    var age by remember { mutableStateOf(akrabi?.age?.toString() ?: "") }
    //var gender by remember { mutableStateOf(akrabi?.gender ?: "") }

    var gender by remember { mutableStateOf("") }

    var woreda by remember { mutableStateOf(akrabi?.woreda ?: "") }
    var kebele by remember { mutableStateOf(akrabi?.kebele ?: "") }
    var govtIdNumber by remember { mutableStateOf(akrabi?.govtIdNumber ?: "") }
    var phone by remember { mutableStateOf(akrabi?.phone ?: "") }
    var photo by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf(akrabi?.photoUri ?: "") }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Store validation errors

    // Focus management
    val focusRequesterSite = remember { FocusRequester() }
    val focusRequesterAkrabi = remember { FocusRequester() }
    val focusRequesterAkrabiNumber = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Get string resources for validation
    val akrabiNumberError = stringResource(id = R.string.invalid_value)
    val siteError = stringResource(id = R.string.required_field)
    val akrabiNameError = stringResource(id = R.string.required_field)

    // Form validation function
    fun validateForm(): List<String> {
        val errors = mutableListOf<String>()
        if (akrabiNumber.isBlank()) errors.add(akrabiNumberError)
        if (selectedSiteName.isBlank()) errors.add(siteError)
        if (akrabiName.isBlank()) errors.add(akrabiNameError)
        return errors
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = akrabiNumber,
                onValueChange = { akrabiNumber = it },
                label = { Text(text = stringResource(id = R.string.akrabi_number)) },
                isError = validationErrors.contains(akrabiNumberError),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterAkrabiNumber),
                keyboardOptions = KeyboardOptions(
                    imeAction =  ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterAkrabi.requestFocus() }
                )
            )
            if (validationErrors.contains(akrabiNumberError)) {
                Text(
                    text = akrabiNumberError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = akrabiName,
                onValueChange = { akrabiName = it },
                label = { Text(text = stringResource(id = R.string.akrabi_name)) },
                isError = validationErrors.contains(akrabiNameError),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterAkrabi),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterSite.requestFocus() }
                )
            )
            if (validationErrors.contains(akrabiNameError)) {
                Text(
                    text = akrabiNameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for selecting site name
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedSiteName,
                    onValueChange = { selectedSiteName = it },
                    label = { Text(text = stringResource(id = R.string.select_or_create_site)) },
                    readOnly = true,
                    isError = validationErrors.contains(siteError),
                    trailingIcon = {
                        IconButton(onClick = { dropdownExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Site Name"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterSite)
                )

                DropdownMenu(
                    expanded = dropdownExpanded,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    collectionSites.forEach { site ->
                        DropdownMenuItem(
                            text = { Text(site.name) },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            onClick = {
                                selectedSiteName = site.name
                                dropdownExpanded = false
                            }
                        )
                    }

                    // Option to create a new site name
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.create_new_site)) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        onClick = {
                            dropdownExpanded = false
                            selectedSiteName = "" // Clear the selected site name
                            navController.navigate("addSite") // Navigate to the add site screen
                        }
                    )
                }
            }

            if (validationErrors.contains(siteError)) {
                Text(
                    text = siteError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text(text = "Age") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))


            GenderDropdown(gender = gender, onGenderSelected = { selectedGender ->
                gender = selectedGender
            })


            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = woreda,
                onValueChange = { woreda = it },
                label = { Text(text = "Woreda") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = kebele,
                onValueChange = { kebele = it },
                label = { Text(text = "Kebele") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = govtIdNumber,
                onValueChange = { govtIdNumber = it },
                label = { Text(text = "Govt ID Number") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(text = "Phone") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pick Image Button
            ImagePicker { uri ->
                photoUri = uri.toString()
                photo = uri.toString()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text(text = stringResource(id = R.string.cancel), color = Color.White)
                }

                Button(onClick = {
                    validationErrors = validateForm() // Validate the form
                    if (validationErrors.isEmpty()) {
                        val updatedAkrabi = Akrabi(
                            id = akrabi?.id ?: 0, // Use existing ID if editing
                            akrabiNumber = akrabiNumber,
                            akrabiName = akrabiName,
                            siteName = selectedSiteName,
                            age = age.toIntOrNull() ?: 0,
                            gender = gender,
                            woreda = woreda,
                            kebele = kebele,
                            govtIdNumber = govtIdNumber,
                            phone = phone,
                            photoUri = photoUri
                        )
                        onSubmit(updatedAkrabi)
                    }
                }) {
                    Text(text = stringResource(id = R.string.submit))
                }
            }
        }
    }
}
