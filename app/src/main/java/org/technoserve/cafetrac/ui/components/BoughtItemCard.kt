package org.technoserve.cafetrac.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.technoserve.cafetrac.database.models.BuyThroughAkrabi
import org.technoserve.cafetraorg.technoserve.cafetrac.R


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemCard(
    item: BuyThroughAkrabi,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp), // Added padding around the card for better spacing
        onClick = onClick,
        elevation = CardDefaults.cardElevation(4.dp) // Optional: add subtle elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Adjusted padding to make it more compact
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column( modifier = Modifier.weight(1f)) {
                Text(
                    text = item.akrabiName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stringResource(id= R.string.site_name)}: ${item.siteName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stringResource(id= R.string.cherry_sold)}: ${item.cherrySold}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stringResource(id= R.string.total_paid)}: ${item.paid}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.padding(end = 0.dp)
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp) ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = (-8).dp) ) {
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