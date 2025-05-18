package com.example.mapsapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.material3.Icon
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.utils.SharedPreferencesHelper
import com.example.mapsapp.utils.textFieldColors
import com.example.mapsapp.viewmodels.AuthViewModel
import com.example.mapsapp.viewmodels.AuthViewModelFactory
import com.example.mapsapp.viewmodels.MapsViewModel
import com.google.android.gms.maps.model.LatLng
import java.io.File

@Composable
fun CreateMarkerScreen(
    coordenadas: LatLng,
    onBack: () -> Unit,
    onMarkerCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<MapsViewModel>()
    val context = LocalContext.current
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val showImageDialog = remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri.value != null) {
            context.contentResolver.openInputStream(imageUri.value!!)?.use { stream ->
                bitmap.value = BitmapFactory.decodeStream(stream)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri.value = uri
            context.contentResolver.openInputStream(uri)?.use { stream ->
                bitmap.value = BitmapFactory.decodeStream(stream)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Título", color = Color.White) },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Descripción", color = Color.White) },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.fillMaxHeight(0.08f))

        //Añadir una imagen
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent)
                .clickable { showImageDialog.value = true },
            contentAlignment = Alignment.Center
        ) {
            if (bitmap.value != null) {
                Image(
                    bitmap = bitmap.value!!.asImageBitmap(),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir imagen",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Haz clic para añadir una imagen",
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.3f))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    viewModel.addMarker(
                        title = title.value,
                        description = description.value,
                        lat = coordenadas.latitude,
                        longitude = coordenadas.longitude,
                        image = bitmap.value
                    )
                    onMarkerCreated()
                },
                enabled = title.value.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }

    // Diálogo para seleccionar imagen
    if (showImageDialog.value) {
        AlertDialog(
            onDismissRequest = { showImageDialog.value = false },
            title = { Text("Seleccionar imagen") },
            text = {
                Column {
                    Button(
                        onClick = {
                            createImageUri(context)?.let { uri ->
                                imageUri.value = uri
                                cameraLauncher.launch(uri)
                            }
                            showImageDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tomar foto")
                    }

                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))

                    Button(
                        onClick = {
                            galleryLauncher.launch("image/*")
                            showImageDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Elegir de galería")
                    }
                }
            },
            confirmButton = {}
        )
    }
}


fun createImageUri(context: Context): Uri? {
    val file = File.createTempFile("temp_image_", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}