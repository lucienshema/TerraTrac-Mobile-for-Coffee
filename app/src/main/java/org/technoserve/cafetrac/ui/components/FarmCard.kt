package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cafetrac.database.models.Farm
import com.example.cafetrac.ui.screens.farms.formatInput
import org.technoserve.cafetraorg.technoserve.cafetrac.R


/**
 * A card displaying a farm's information.
 *
 * @param farm The Farm object to display.
 * @param onCardClick A callback to be invoked when the card is clicked.
 * @param onDeleteClick A callback to be invoked when the delete icon is clicked.
 */

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
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect(farm.id) }
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
                        text = "${stringResource(id = R.string.size)}: ${formatInput(farm.size.toString())} ${
                            stringResource(
                                id = R.string.ha
                            )
                        }",
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
//                    IconButton(
//                        onClick = onDeleteClick,
//                        modifier = Modifier
//                            .padding(end = 2.dp)
//                            .size(24.dp) // Reduced padding and set explicit size
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Delete,
//                            contentDescription = stringResource(id = R.string.delete),
//                            tint = Color.Red
//                        )
//                    }
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
