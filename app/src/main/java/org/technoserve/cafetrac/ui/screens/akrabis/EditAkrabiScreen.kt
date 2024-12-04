package com.example.cafetrac.ui.screens.akrabis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.cafetrac.database.models.Akrabi
import com.example.cafetrac.database.models.CollectionSite
import org.technoserve.cafetrac.viewmodels.AkrabiViewModel
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@Composable
fun EditAkrabiScreen(
    akrabiId:   Long, // Ensure this matches the type in your ViewModel
    collectionSites: List<CollectionSite>,
    viewModel: AkrabiViewModel,
    navController: NavController
) {
    // Observe LiveData from ViewModel
    val akrabi by viewModel.getAkrabiById(akrabiId).observeAsState()

    // State to control the visibility of the confirmation dialog
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Store the updated Akrabi object temporarily before confirmation
    var updatedAkrabi: Akrabi? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display a loading indicator while waiting for data
        if (akrabi == null) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else {
            CreateAkrabiForm(
                navController = navController,
                title= stringResource(id= R.string.edit_akrabi_form),
                akrabi = akrabi, // Pass existing data to pre-fill the form
                collectionSites = collectionSites, // Get the site names for dropdown
                onSubmit = { tempUpdatedAkrabi ->
                    // Store the updated Akrabi and show the confirmation dialog
                    updatedAkrabi = tempUpdatedAkrabi
                    showConfirmationDialog = true
                },
                onCancel = {
                    navController.navigate("akrabi_list_screen")
                }
            )
        }

        // Show confirmation dialog if the flag is set to true
        if (showConfirmationDialog && updatedAkrabi != null) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text(text = stringResource(id = R.string.confirm_update)) },
                text = { Text(text = stringResource(id = R.string.are_you_sure_save_the_changes)) },
                confirmButton = {
                    Button(onClick = {
                        // Update the Akrabi and navigate back to the list screen
                        viewModel.updateAkrabi(updatedAkrabi!!)
                        showConfirmationDialog = false
                        navController.navigate("akrabi_list_screen")
                    }) {
                        Text(text = stringResource(id = R.string.yes))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        // Dismiss the dialog without updating
                        showConfirmationDialog = false
                    }) {
                        Text(text = stringResource(id = R.string.no))
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp
            )
        }
    }
}