package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.technoserve.cafetrac.database.models.CollectionSite

import org.technoserve.cafetrac.ui.composes.UpdateCollectionDialog
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetraorg.technoserve.cafetrac.R

/**
 * A site card in the FarmList screen. Displays the site name, number of farms, and a button to edit or delete the site.
 *
 * @param site The site to display
 * @param onCardClick A callback function to be called when the card is clicked
 * @param totalFarms The total number of farms in the site
 * @param farmsWithIncompleteData The number of farms with incomplete data
 * @param onDeleteClick
 * @param farmViewModel
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun siteCard(
    site: CollectionSite,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onCardClick: () -> Unit,
    totalFarms: Int,
    farmsWithIncompleteData: Int,
    farmViewModel: FarmViewModel,
) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        UpdateCollectionDialog(
            site = site,
            showDialog = showDialog,
            farmViewModel = farmViewModel,
        )
    }
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth()
                .padding(2.dp),
            onClick = { onCardClick() },
        ) {
            Column(
                modifier = Modifier
                    .background(backgroundColor)
                    .padding(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(backgroundColor)
                        .padding(2.dp)
                        .fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onCheckedChange,
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = if (isDarkTheme) Color.Green else Color.Blue,
                            uncheckedColor = if (isDarkTheme) Color.Gray else Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f) // Adjusted weight
                            .padding(start = 2.dp) // Reduced padding between checkbox and text
                    ) {
                        Text(
                            text = site.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = textColor
                            ),
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                        Text(
                            text = "${stringResource(id = R.string.agent_name)}: ${site.agentName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = textColor),
                            modifier = Modifier.padding(bottom = 1.dp),
                        )
//                        Text(
//                            text = "${stringResource(id = R.string.village)}: ${site.village}",
//                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
//                        )
//                        Text(
//                            text = "${stringResource(id = R.string.district)}: ${site.district}",
//                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
//                        )
                        if (site.phoneNumber.isNotEmpty()) {
                            Text(
                                text = "${stringResource(id = R.string.phone_number)}: ${site.phoneNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
                            )
                        }
//                        if (site.email.isNotEmpty()) {
//                            Text(
//                                text = "${stringResource(id = R.string.email)}: ${site.email}",
//                                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
//                            )
//                        }

                        Text(
                            text = stringResource(
                                id = R.string.total_farms,
                                totalFarms
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                        )

                        Text(
                            text = stringResource(
                                id = R.string.total_farms_with_incomplete_data,
                                farmsWithIncompleteData
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Blue
                            ),
                        )
                    }
                    IconButton(
                        onClick = {
                            showDialog.value = true
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Site Details",
                            tint = iconColor,
                        )
                    }
                }
            }
        }
    }
}
