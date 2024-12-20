package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(gender: String, onGenderSelected: (String) -> Unit) {
    // Options for gender dropdown
    val genderOptions = listOf("Male", "Female", "Other")

    // State to manage the expanded state of the dropdown
    var expanded by remember { mutableStateOf(false) }

    // State to track the selected gender
    var selectedGender by remember { mutableStateOf(gender) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = { /* Do nothing here, selection happens via dropdown */ },
            label = { Text("Gender") },
            readOnly = true, // Ensure the text field is read-only, dropdown handles selection
            modifier = Modifier
                .menuAnchor() // Anchor the dropdown to the text field
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        // The dropdown menu that shows the gender options
        ExposedDropdownMenu(
            expanded = expanded,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            onDismissRequest = { expanded = false }
        ) {
            genderOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    onClick = {
                        selectedGender = option
                        onGenderSelected(option)
                        expanded = false // Close the dropdown menu
                    }
                )
            }
        }
    }
}