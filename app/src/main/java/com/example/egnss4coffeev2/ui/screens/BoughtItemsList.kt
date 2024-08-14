package com.example.egnss4coffeev2.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import com.example.egnss4coffeev2.database.BuyThroughAkrabi
import com.example.egnss4coffeev2.database.DirectBuy
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
    onApply: () -> Unit,
    onClear: () -> Unit // New parameter for clearing the date selection
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

        Row {
            Button(onClick = onApply, modifier = Modifier.weight(1f)) {
                Text("Apply")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onClear, modifier = Modifier.weight(1f)) {
                Text("Clear")
            }
        }

        // Start Date Picker Dialog
        if (showStartDatePicker) {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    onStartDateChange("${year}-${month + 1}-${dayOfMonth}")
                    showStartDatePicker = false
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
                    showEndDatePicker = false
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
    onItemClick: (BuyThroughAkrabi) -> Unit,
    navController: NavController
) {
    val boughtItems by farmViewModel.boughtItems.collectAsStateWithLifecycle(initialValue = emptyList())

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Bought Items Available Through Akrabi",
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
                },
                onClear = {
                    startDate = ""
                    endDate = ""
                    // Optionally refresh the list with no filter
                    farmViewModel.filterBoughtItems("", "")
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

        // Floating Action Button (FAB)
        FloatingActionButton(
            onClick = {
                navController.navigate("buy_through_akrabi/add")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Buy Through Akrabi")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemsListDirectBuy(
    farmViewModel: FarmViewModel,
    onItemClick: (DirectBuy) -> Unit,
    navController: NavController
) {
    val boughtItemsDirectBuy by farmViewModel.boughtItemsDirectBuy.collectAsStateWithLifecycle(initialValue = emptyList())

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DirectBuy?>(null) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Confirm Delete")
            },
            text = {
                Text(text = "Are you sure you want to delete this item? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { farmViewModel.deleteBoughtItemDirectBuy(it) }
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Bought Items through Direct Buy",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            DateRangePicker(
                startDate = startDate,
                endDate = endDate,
                onStartDateChange = { startDate = it },
                onEndDateChange = { endDate = it },
                onApply = {
                    farmViewModel.filterBoughtItemsDirectBuy(startDate, endDate)
                },
                onClear = {
                    startDate = "2024-01-01"
                    endDate = "2024-12-31"
                    // Optionally refresh the list with no filter
                    farmViewModel.filterBoughtItemsDirectBuy("2024-01-01", "2024-12-31")
                }
            )

            if (boughtItemsDirectBuy.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items bought yet")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(boughtItemsDirectBuy) { item ->
                        BoughtItemCardDirectBuy(
                            item = item,
                            onClick = { onItemClick(item) },
                            onEditClick = {
                                navController.navigate("direct_buy/edit/${item.id}")
                            },
                            onDeleteClick = {
                                itemToDelete = item
                                showDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Floating Action Button (FAB)
        FloatingActionButton(
            onClick = {
                navController.navigate("direct_buy/add")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Direct Buy")
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemCardDirectBuy(
    item: DirectBuy,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
                text = "Akrabi: ${item.farmerName}",
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
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
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


@Composable
fun BoughtItemDetailScreenDirectBuy(
    itemId: Long,
    farmViewModel: FarmViewModel,
    onNavigateBack: () -> Unit
) {
    // Fetch the specific item using the itemId
    val item by farmViewModel.getBoughtItemDirectBuyById(itemId).collectAsStateWithLifecycle(initialValue = null)

    item?.let { buyThroughAkrabi ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item Details", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Date: ${buyThroughAkrabi.date}")
            Text("Time: ${buyThroughAkrabi.time}")
            Text("Location: ${buyThroughAkrabi.location}")
            Text("Site Name: ${buyThroughAkrabi.siteName}")
            Text("Akrabi Name: ${buyThroughAkrabi.farmerName}")
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditDirectBuyForm(
    itemId: Long,
    farmViewModel: FarmViewModel,
    navController: NavController
) {
    // Fetch the item data based on the itemId
    val item by farmViewModel.getBoughtItemDirectBuyById(itemId).collectAsStateWithLifecycle(null)

    var farmerName by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }


    LaunchedEffect(item) {
        item?.let {
            farmerName = it.farmerName
            paid = it.paid.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Edit Direct Buy", style = MaterialTheme.typography.headlineMedium)

        TextField(
            value = farmerName,
            onValueChange = { farmerName = it },
            label = { Text("Farmer Name") }
        )

        TextField(
            value = paid,
            onValueChange = { paid = it },
            label = { Text("Paid") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )


        Button(
            onClick = {
                if (item != null) {
                    val updatedItem = item!!.copy(
                        farmerName = farmerName,
                        paid = paid.toDouble(),
                    )
                    farmViewModel.updateDirectBuy(updatedItem)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
