package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailText(label: String, value: String, bold: Boolean = false) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyLarge
        )
    }
}
