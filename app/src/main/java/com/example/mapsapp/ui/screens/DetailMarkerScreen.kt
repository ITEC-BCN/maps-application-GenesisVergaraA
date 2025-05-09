package com.example.mapsapp.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import com.example.mapsapp.viewmodels.MyViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMarkerScreen(
    markerId: String,
    onBack: () -> Unit,
    onMarkerUpdated: () -> Unit,
    modifier: Modifier
) {
    val viewModel = viewModel<MyViewModel>()
    val context = LocalContext.current

    viewModel.getMarker(markerId)


    val marker by viewModel.selectedMarker.observeAsState()
    val isEditing = remember { mutableStateOf(false) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val titleState = remember(marker) { mutableStateOf(marker?.title ?: "") }
    val descriptionState = remember(marker) { mutableStateOf(marker?.description ?: "") }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri.value != null) {
                val stream = context.contentResolver.openInputStream(imageUri.value!!)
                bitmap.value = BitmapFactory.decodeStream(stream)
            }
        }

    Column(
        modifier.fillMaxSize()
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
        }
        Button(
            onClick = { isEditing.value = !isEditing.value },
            modifier = Modifier.padding(end = 8.dp),
            enabled = marker != null
        ) {
            Text(if (isEditing.value) "Detalles" else "Editar")
        }
        if (marker == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando marcador...")
            }
        } else {
            if (isEditing.value) {
                TextField(
                    value = titleState.value,
                    onValueChange = { titleState.value = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = descriptionState.value,
                    onValueChange = { descriptionState.value = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(onClick = {
                    val uri = createImageUri(context)
                    imageUri.value = uri
                    launcher.launch(uri!!)
                }) {
                    Text("Cambiar Imagen")
                }
            } else {
                Text(text = "Título: ${marker?.title}")
                Text(text = "Descripción: ${marker?.description}")

            }
            AsyncImage(
                model = bitmap.value?.asImageBitmap() ?: marker?.image,
                contentDescription = "Imagen del marcador",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isEditing.value) {
                Button(
                    onClick = {
                        viewModel.updateMarker(
                            id = markerId,
                            title = titleState.value,
                            description = descriptionState.value,
                            image = bitmap.value
                        )
                        isEditing.value = false
                        onMarkerUpdated()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = titleState.value.isNotEmpty()
                ) {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}