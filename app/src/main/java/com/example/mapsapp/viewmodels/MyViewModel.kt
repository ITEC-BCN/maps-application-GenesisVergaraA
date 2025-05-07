package com.example.mapsapp.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class MyViewModel : ViewModel() {
    val database = MyApp.database

    private val _markers = mutableStateOf<List<Marker>>(emptyList())
    private val _markerMark = MutableLiveData<String>()
    val markerMark = _markerMark
    private val _markersList = MutableLiveData<List<Marker>>()
    val markersList = _markersList

    private val _selectedMarker = mutableStateOf<Marker?>(null)
    val selectedMarker = _selectedMarker.value

    private val _currentPosition = mutableStateOf<LatLng?>(null)
    val currentPosition: LatLng? = _currentPosition.value


    init {
        loadMarkers()
    }

    fun loadMarkers() {
        CoroutineScope(Dispatchers.IO).launch {
            _markers.clear()
            _markers.addAll(MyApp.database.getAllMarkers())
        }
    }

    fun setCurrentPosition(latLng: LatLng) {
        _currentPosition.value = latLng
    }

    fun selectMarker(marker: Marker) {
        _selectedMarker.value = marker
    }

    fun addMarker(title: String, description: String, image: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageUrl = image?.let { bitmap ->
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                database.uploadImage(stream.toByteArray())
            }

            currentPosition?.let { latLng ->
                val marker = Marker(
                    title = title,
                    description = description,
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    image = imageUrl
                )
                database.insertMarker(marker)
                loadMarkers()
            }
        }
    }

    fun updateMarker(id: String, title: String, description: String, image: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentImageUrl = selectedMarker?.image
            val newImageUrl = image?.let { bitmap ->
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                database.uploadImage(stream.toByteArray())
            } ?: currentImageUrl

            if (newImageUrl != currentImageUrl && currentImageUrl != null) {
                database.deleteImage(currentImageUrl)
            }

            database.updateMarker(id, title, description, newImageUrl)
            loadMarkers()
        }
    }

    fun deleteMarker(id: String, image: String) {
        CoroutineScope(Dispatchers.IO).launch {
            database.deleteImage(image)
            database.deleteMarker(id)
            loadMarkers()
        }
    }

}
