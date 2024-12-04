package org.technoserve.cafetrac.ui.screens.directbuy

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.technoserve.cafetrac.database.models.BuyThroughAkrabi
import org.technoserve.cafetrac.ui.components.DetailText
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BoughtItemDetailScreen(buyThroughAkrabi: BuyThroughAkrabi, onBack: () -> Unit, navController: NavHostController,
                           farmViewModel: FarmViewModel
) {

    var showDialog by remember { mutableStateOf(false) } // State to show/hide delete confirmation dialog
    var itemToDelete by remember { mutableStateOf<BuyThroughAkrabi?>(null) } // State to hold the item to delete

    fun deleteBoughtItem(buyThroughAkrabi: BuyThroughAkrabi, farmViewModel: FarmViewModel) {
        farmViewModel.deleteBoughtItemBuyThroughAkrabi(buyThroughAkrabi)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.item_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
        ) {

            // Display the image if available
            buyThroughAkrabi.photoUri?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription =  stringResource(R.string.bought_item_image_description),
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
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailText(label = stringResource(R.string.date), value = buyThroughAkrabi.date.toString())
                    DetailText(label = stringResource(R.string.time), value = buyThroughAkrabi.time)
                    DetailText(label = stringResource(R.string.location), value = buyThroughAkrabi.location)
                    DetailText(label = stringResource(R.string.akrabi_name), value = buyThroughAkrabi.akrabiName)
                    DetailText(label = stringResource(R.string.site_name), value = buyThroughAkrabi.siteName)
                    DetailText(label = stringResource(R.string.cherry_sold), value = "${buyThroughAkrabi.cherrySold}")
                    DetailText(label = stringResource(R.string.price_per_kg), value = "$${buyThroughAkrabi.pricePerKg}")
                    DetailText(label = stringResource(R.string.total_paid), value = "$${buyThroughAkrabi.paid}", bold = true)
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
                            navController.navigate("buy_through_akrabi/edit/${buyThroughAkrabi.id}")
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
                            itemToDelete = buyThroughAkrabi
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
                            itemToDelete?.let { deleteBoughtItem(it, farmViewModel ) } // Perform the delete action
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
                containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
                tonalElevation = 6.dp // Adds a subtle shadow for better UX
            )
        }
    }
}