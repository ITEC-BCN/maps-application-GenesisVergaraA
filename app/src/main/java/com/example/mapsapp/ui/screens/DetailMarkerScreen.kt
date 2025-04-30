package com.example.mapsapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.viewmodels.MyViewModel


@Composable
fun DetailMarkerScreen(
    markerId: String,
    onBack: () -> Unit,
    onMarkerUpdated: () -> Unit
) {
    val marker = remember { mutableStateOf<Marker?>(null) }
    val scope = rememberCoroutineScope()

    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }

    LaunchedEffect(markerId) {
        scope.launch {
            marker.value = MyApp.database.getMarker(markerId)
            title.value = marker.value?.title ?: ""
            description.value = marker.value?.description ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text("Editar Marcador") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
            }
        )

        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        marker.value?.imageUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                scope.launch {
                    MyApp.database.updateMarker(
                        id = markerId,
                        title = title.value,
                        description = description.value
                    )
                    onMarkerUpdated()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }
    }
}

