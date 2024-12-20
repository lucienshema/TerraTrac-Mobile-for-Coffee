package org.technoserve.cafetrac.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import org.technoserve.cafetraorg.technoserve.cafetrac.R


@Composable
fun ImagePicker(
    onImagePicked: (Uri?) -> Unit
) {
    // State to hold the picked photo URI
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for picking images from the gallery
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        photoUri = uri
        onImagePicked(uri) // Notify the parent composable of the selected image URI
    }

    // Function to initiate the photo picker
    fun pickPhoto() {
        pickImageLauncher.launch("image/*")
    }

    Column(modifier = Modifier
        .fillMaxSize()) {

        // Display the image if one is selected
        if (photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = "Selected Photo",
                modifier = Modifier
                    .size(200.dp) // Increased the size for better visibility
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Button to remove the photo
            IconButton(
                onClick = {
                    photoUri = null
                    onImagePicked(null) // Notify that the photo was removed
                }
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Photo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OutlinedTextField acting as the photo selection button
        OutlinedTextField(
            value = stringResource(R.string.select_photo),
            onValueChange = {}, // No-op since the field is read-only
            label = { Text(text= stringResource(R.string.photo)) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { pickPhoto() }) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Pick Photo")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { pickPhoto() } // Allow clicking anywhere on the text field to pick a photo
        )
    }
}