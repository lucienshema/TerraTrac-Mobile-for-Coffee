package com.example.cafetrac.ui.screens.akrabis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import com.example.cafetrac.database.models.Akrabi
import org.technoserve.cafetrac.ui.components.SkeletonAkrabiItem
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@Composable
fun AkrabiListScreen(
    akrabis: List<Akrabi>?,
    isLoading: Boolean, // Add a flag to indicate loading state
    onViewDetails: (Akrabi) -> Unit,
    onEdit: (Akrabi) -> Unit,
    onDelete: (Akrabi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Show Skeleton UI when loading
        if (isLoading) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(5) { // Show a fixed number of skeleton items
                    SkeletonAkrabiItem()
                }
            }
        } else if (akrabis != null && akrabis.isNotEmpty()) {
            // Akrabi List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(akrabis.size) { index ->
                    val akrabi = akrabis[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onViewDetails(akrabi) },
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${stringResource(id= R.string.akrabi_number)}: ${akrabi.akrabiNumber}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${stringResource(id= R.string.akrabi_name)}: ${akrabi.akrabiName}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${stringResource(id= R.string.site_name)}: ${akrabi.siteName}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Row {
                                IconButton(onClick = { onEdit(akrabi) }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = { onDelete(akrabi) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Handle the empty state if no akrabis are available
            Text(
                text = stringResource(id = R.string.no_results_found),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}