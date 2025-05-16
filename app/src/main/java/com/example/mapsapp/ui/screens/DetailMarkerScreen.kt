package com.example.mapsapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mapsapp.data.Marker
import com.example.mapsapp.viewmodels.MyViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMarkerScreen(
    markerId: String,
    onBack: () -> Unit,
    onMarkerUpdated: () -> Unit,
    modifier: Modifier = Modifier
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
                context.contentResolver.openInputStream(imageUri.value!!)?.use { stream ->
                    bitmap.value = BitmapFactory.decodeStream(stream)
                }
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0XFF000113)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Menu") }
            IconButton(onClick = onBack) { Icon(Icons.Default.Create, contentDescription = "Menu") }

            Button(
                onClick = { isEditing.value = !isEditing.value },
                enabled = marker != null
            ) {
                Text(if (isEditing.value) "Detalles" else "Editar")
            }
        }

        when {
            marker == null -> {
                LoadingIndicator()
            }

            isEditing.value -> {
                EditionMode(
                    titleState = titleState,
                    descriptionState = descriptionState,
                    currentImage = bitmap.value?.asImageBitmap() ?: marker?.image,
                    onChangeImage = {
                        createImageUri(context)?.let { uri ->
                            imageUri.value = uri
                            launcher.launch(uri)
                        }
                    },
                    onSave = {
                        viewModel.updateMarker(
                            id = markerId,
                            title = titleState.value,
                            description = descriptionState.value,
                            image = bitmap.value
                        )
                        isEditing.value = false
                        onMarkerUpdated()
                    },
                    onBack = onBack,
                    isSaveEnabled = titleState.value.isNotEmpty()
                )
            }

            else -> {
                DetailMode(
                    title = marker?.title ?: "",
                    description = marker?.description ?: "",
                    image = marker?.image
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Cargando marcador...", color = Color.White)
    }
}

@Composable
private fun DetailMode(
    title: String,
    description: String,
    image: Any?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = description,
            modifier = Modifier.padding(horizontal = 30.dp),
            color = Color.White
        )

        AsyncImage(
            model = image,
            contentDescription = "Imagen del marcador",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun EditionMode(
    titleState: MutableState<String>,
    descriptionState: MutableState<String>,
    currentImage: Any?,
    onChangeImage: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    isSaveEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = titleState.value,
            onValueChange = { titleState.value = it },
            label = { Text("Título", color = Color.White) },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0XFF000113),
                focusedContainerColor = Color(0XFF000113),
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedLabelColor = Color.White,
                cursorColor = Color.White
            )
        )

        TextField(
            value = descriptionState.value,
            onValueChange = { descriptionState.value = it },
            label = { Text("Descripción", color = Color.White) },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0XFF000113),
                focusedContainerColor = Color(0XFF000113),
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedLabelColor = Color.White,
                cursorColor = Color.White
            )
        )

        AsyncImage(
            model = currentImage,
            contentDescription = "Current marker image",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Button(
            onClick = onChangeImage,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Cambiar imagen")
        }

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                enabled = isSaveEnabled
            ) {
                Text("Guardar")
            }
        }
    }
}

