package org.technoserve.cafetrac.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.technoserve.cafetraorg.technoserve.cafetrac.R

import java.time.LocalDate


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
        Text(text= stringResource(id = R.string.select_date_range), style = MaterialTheme.typography.labelLarge)

        Spacer(modifier = Modifier.height(8.dp))
        // Row for Start Date and End Date Pickers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { /* Disable manual input */ },
                label = { Text(text= stringResource(id = R.string.start_date), fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp), // Adds space between the two text fields
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { /* Disable manual input */ },
                label = { Text(text= stringResource(id = R.string.end_date),fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Row for Apply and Clear Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = onApply,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp), // Adds space between the buttons
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Reduces button size
                enabled = startDate.isNotBlank() && endDate.isNotBlank()
            ) {
                Text(text= stringResource(id = R.string.apply))
            }

            Button(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Reduces button size
                enabled = startDate.isNotBlank() && endDate.isNotBlank()
            ) {
                Text(text= stringResource(id = R.string.clear))
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
