package com.example.mapsapp.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mapsapp.utils.Loading
import com.example.mapsapp.utils.textFieldColors
import com.example.mapsapp.viewmodels.MapsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMarkerScreen(
    markerId: String,
    onBack: () -> Unit,
    onMarkerUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<MapsViewModel>()
    val context = LocalContext.current

    viewModel.getMarker(markerId)


    val marker by viewModel.selectedMarker.observeAsState()
    val isEditing = remember { mutableStateOf(false) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val showImageOptions = remember { mutableStateOf(false) }

    val titleState = remember(marker) { mutableStateOf(marker?.title ?: "") }
    val descriptionState = remember(marker) { mutableStateOf(marker?.description ?: "") }

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
            .background(Color(0XFF000113)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 20.dp, top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)

                )
            }
            IconButton(onClick = { isEditing.value = !isEditing.value }) {
                Icon(
                    if (isEditing.value) Icons.Default.Info else Icons.Default.Create,
                    contentDescription = if (isEditing.value) "Detalles" else "Editar",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        when {
            marker == null -> Loading("Cargando marcador...")
            isEditing.value -> EditionMode(
                titleState = titleState,
                descriptionState = descriptionState,
                currentImage = bitmap.value,
                onChangeImage = { showImageOptions.value = true },
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
                isSaveEnabled = titleState.value.isNotEmpty()
            )

            else -> DetailMode(
                title = marker?.title ?: "",
                description = marker?.description ?: "",
                imageUrl = marker?.image?.toString(),
            )
        }
    }

    if (showImageOptions.value) {
        ImageDialog(
            onDismiss = { showImageOptions.value = false },
            onTakePhoto = {
                createImageUri(context)?.let { uri ->
                    imageUri.value = uri
                    cameraLauncher.launch(uri)
                }
            },
            onPickFromGallery = {
                galleryLauncher.launch("image/*")
            }
        )
    }
}

@Composable
fun DetailMode(
    title: String,
    description: String,
    imageUrl: String?
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

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            when {
                imageUrl != null -> {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagen del marcador",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Sin imagen",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("No disponible, sube una", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun EditionMode(
    titleState: MutableState<String>,
    descriptionState: MutableState<String>,
    currentImage: Bitmap?,
    onChangeImage: () -> Unit,
    onSave: () -> Unit,
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
            colors = textFieldColors()
        )

        TextField(
            value = descriptionState.value,
            onValueChange = { descriptionState.value = it },
            label = { Text("Descripción", color = Color.White) },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = textFieldColors()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onChangeImage),
            contentAlignment = Alignment.Center
        ) {
            if (currentImage != null) {
                Image(
                    bitmap = currentImage.asImageBitmap(),
                    contentDescription = "Imagen actual",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Añadir imagen",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )

                    Text("Haz clic para añadir una imagen", color = Color.White)
                }
            }
        }
        Spacer(Modifier.fillMaxHeight(0.3f))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = isSaveEnabled
        ) {
            Text("Guardar cambios")
        }
    }
}


@Composable
fun ImageDialog(
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Seleccionar imagen") },
        text = {
            Column {
                Button(
                    onClick = {
                        onTakePhoto()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tomar foto")
                }
                Spacer(modifier = Modifier.fillMaxHeight(0.02f))
                Button(
                    onClick = {
                        onPickFromGallery()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar de la galería")
                }
            }
        }
    )
}