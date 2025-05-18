package com.example.mapsapp.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.data.Marker
import com.example.mapsapp.viewmodels.MapsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    onCreateMarker: (LatLng) -> Unit,
    onMarkerClick: (String) -> Unit,
    modifier: Modifier
) {

    val viewModel = viewModel<MapsViewModel>()

    viewModel.loadMarkers()

    val markers by viewModel.markersList.observeAsState(emptyList<Marker>())

    Column(modifier.fillMaxSize()) {
        val itb = LatLng(41.45347071847102, 2.1862792678029237)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(itb, 17f)
        }
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                onCreateMarker(latLng)
            }
        ) {
            markers.forEach { marker ->
                Marker(
                    state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                    title = marker.title,
                    snippet = marker.description,
                    onClick = {
                        marker.id?.toString()?.let { onMarkerClick(it) }
                        true
                    },
                )
            }
        }
    }
}