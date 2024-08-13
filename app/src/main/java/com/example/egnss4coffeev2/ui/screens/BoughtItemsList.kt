package com.example.egnss4coffeev2.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.egnss4coffeev2.database.BuyThroughAkrabi
import com.example.egnss4coffeev2.database.FarmViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateRangePicker(
    startDate: String,
    endDate: String,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onApply: () -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Date Range", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        // Start Date Picker
        OutlinedTextField(
            value = startDate,
            onValueChange = { /* Disable manual input */ },
            label = { Text("Start Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showStartDatePicker = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // End Date Picker
        OutlinedTextField(
            value = endDate,
            onValueChange = { /* Disable manual input */ },
            label = { Text("End Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showEndDatePicker = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onApply) {
            Text("Apply")
        }

        // Start Date Picker Dialog
        if (showStartDatePicker) {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    onStartDateChange("${year}-${month + 1}-${dayOfMonth}")
                },
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            ).apply {
                setOnDismissListener { showStartDatePicker = false }
            }.show()
        }

        // End Date Picker Dialog
        if (showEndDatePicker) {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    onEndDateChange("${year}-${month + 1}-${dayOfMonth}")
                },
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            ).apply {
                setOnDismissListener { showEndDatePicker = false }
            }.show()
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemsList(
    farmViewModel: FarmViewModel,
    onItemClick: (BuyThroughAkrabi) -> Unit
) {
    val boughtItems by farmViewModel.boughtItems.collectAsStateWithLifecycle(initialValue = emptyList())

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Bought Items",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        DateRangePicker(
            startDate = startDate,
            endDate = endDate,
            onStartDateChange = { startDate = it },
            onEndDateChange = { endDate = it },
            onApply = {
                farmViewModel.filterBoughtItems(startDate, endDate)
            }
        )

        if (boughtItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No items bought yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(boughtItems) { item ->
                    BoughtItemCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoughtItemCard(
    item: BuyThroughAkrabi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Date: ${item.date.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Time: ${item.time}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Site: ${item.siteName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Akrabi: ${item.akrabiName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Cherry Sold: ${item.cherrySold} kg",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Price per kg: $${item.pricePerKg}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total Paid: $${item.paid}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BoughtItemDetailScreen(
    itemId: Long,
    farmViewModel: FarmViewModel,
    onNavigateBack: () -> Unit
) {
    // Fetch the specific item using the itemId
    val item by farmViewModel.getBoughtItemById(itemId).collectAsStateWithLifecycle(initialValue = null)

    item?.let { buyThroughAkrabi ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item Details", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Date: ${buyThroughAkrabi.date}")
            Text("Time: ${buyThroughAkrabi.time}")
            Text("Location: ${buyThroughAkrabi.location}")
            Text("Site Name: ${buyThroughAkrabi.siteName}")
            Text("Akrabi Name: ${buyThroughAkrabi.akrabiName}")
            Text("Cherry Sold: ${buyThroughAkrabi.cherrySold} kg")
            Text("Price per kg: $${buyThroughAkrabi.pricePerKg}")
            Text("Total Paid: $${buyThroughAkrabi.paid}")

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateBack) {
                Text("Back to List")
            }
        }
    } ?: Text("Loading...")
}