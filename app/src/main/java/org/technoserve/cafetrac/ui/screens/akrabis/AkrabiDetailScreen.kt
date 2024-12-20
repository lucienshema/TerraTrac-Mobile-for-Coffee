package org.technoserve.cafetrac.ui.screens.akrabis

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import org.technoserve.cafetrac.database.models.Akrabi

import org.technoserve.cafetrac.ui.components.DetailText

import org.technoserve.cafetrac.viewmodels.AkrabiViewModel
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AkrabiDetailScreen(
    akrabi: Akrabi,
    navController: NavHostController,
    akrabiViewModel: AkrabiViewModel,
    onBack: () -> Unit // Callback for navigation back
) {
    var showDialog by remember { mutableStateOf(false) } // State to show/hide delete confirmation dialog
    var itemToDelete by remember { mutableStateOf<Akrabi?>(null) } // State to hold the item to delete

    fun deleteAkrabi(akrabi: Akrabi, akrabiViewModel: AkrabiViewModel) {
        akrabiViewModel.deleteAkrabi(akrabi)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.akrabi_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 64.dp), // Adjust the top padding here to move the Card closer to the header
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally within Column
            ) {
                // Display the image if available (non-null and non-empty)
                if (akrabi.photoUri?.isNotBlank() == true) {
                    Image(
                        painter = rememberAsyncImagePainter(akrabi.photoUri),
                        contentDescription = stringResource(R.string.bought_item_image_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
                // Card for item details
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // Padding for horizontal spacing
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailText(label = stringResource(R.string.akrabi_number), value = akrabi.akrabiNumber)
                        DetailText(label = stringResource(R.string.akrabi_name), value = akrabi.akrabiName)
                        DetailText(label = stringResource(R.string.site_name), value = akrabi.siteName)
                        DetailText(label = stringResource(R.string.age), value = akrabi.age.toString())
                        DetailText(label = stringResource(R.string.gender), value = akrabi.gender)
                        DetailText(label = stringResource(R.string.woreda), value = akrabi.woreda)
                        DetailText(label = stringResource(R.string.kebele), value = akrabi.kebele)
                        DetailText(label = stringResource(R.string.gov_id_number), value = akrabi.govtIdNumber)
                        DetailText(label = stringResource(R.string.phone), value = akrabi.phone)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Row for icons
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally) // Center icons horizontally
                ) {
                    IconButton(
                        onClick = {
                            // Navigate to edit screen
                            navController.navigate("edit_akrabi_form/${akrabi.id}")
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Space between icons
                    IconButton(
                        onClick = {
                            // Trigger the confirmation dialog
                            itemToDelete = akrabi
                            showDialog = true
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = stringResource(id = R.string.confirm)) },
                text = { Text(text = stringResource(id = R.string.are_you_sure)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            itemToDelete?.let { deleteAkrabi(it, akrabiViewModel) } // Perform the delete action
                            showDialog = false // Close the dialog
                            onBack() // Navigate back after deletion
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp
            )
        }
    }
}
