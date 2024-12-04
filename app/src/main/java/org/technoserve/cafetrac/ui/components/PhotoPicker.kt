package org.technoserve.cafetrac.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import org.technoserve.cafetraorg.technoserve.cafetrac.R


@Composable
fun PhotoPicker(
    photoUri: Uri?,
    onPickPhotoClick: () -> Unit,
    onRemovePhotoClick: () -> Unit
) {
    Column {
        // Display the image if it's selected
        if (photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = "Selected Photo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Crop
            )
            // Button to remove the photo
            IconButton(
                onClick = { onRemovePhotoClick() },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Photo")
            }
        }

        val displayText = photoUri?.toString()?.let {  stringResource(R.string.photo_selected) } ?: stringResource(
            R.string.no_photo_selected)
        OutlinedTextField(
            value = displayText,
            onValueChange = {}, // No-op since the field is read-only
            label = { Text("Photo") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { onPickPhotoClick()}) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Pick Photo")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPickPhotoClick() } // Allow clicking anywhere on the text field to pick a photo
        )
    }
}
