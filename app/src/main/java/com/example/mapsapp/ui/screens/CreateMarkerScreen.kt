package com.example.mapsapp.ui.screens

import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import com.google.android.gms.maps.model.LatLng
import java.io.File

@Composable
fun CreateMarkerScreen(
    coordinates: LatLng,
    onBack: () -> Unit,
    onMarkerCreated: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri.value != null) {
            val stream = context.contentResolver.openInputStream(imageUri.value!!)
            bitmap.value = BitmapFactory.decodeStream(stream)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Título") }
        )

        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Descripción") }
        )

        Button(
            onClick = {
                val uri = createImageUri(context)
                imageUri.value = uri
                cameraLauncher.launch(uri)
            }
        ) {
            Text("Tomar foto")
        }

        bitmap.value?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    scope.launch {
                        val imageUrl = imageUri.value?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { stream ->
                                MyApp.database.uploadImage(
                                    "marker_${System.currentTimeMillis()}.jpg",
                                    stream
                                )
                            }
                        }

                        MyApp.database.insertMarker(
                            Marker(
                                title = title.value,
                                description = description.value,
                                latitude = coordinates.latitude,
                                longitude = coordinates.longitude,
                                imageUrl = imageUrl
                            )
                        )
                        onMarkerCreated()
                    }
                },
                enabled = title.value.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}