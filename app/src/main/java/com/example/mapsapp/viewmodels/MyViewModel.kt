package com.example.mapsapp.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel : ViewModel() {
    val database = MyApp.database

    private val _markers = mutableStateListOf<Marker>()
    val markers: List<Marker> = _markers
    private val _markerName = MutableLiveData<String>()
    val markerName = _markerName
    private val _markerMark = MutableLiveData<String>()
    val markerMark = _markerMark
    private val _markersList = MutableLiveData<List<Marker>>()
    val markersList = _markersList

    private val _selectedMarker = mutableStateOf<Marker?>(null)

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

    fun addMarker(title: String, description: String, imageUri: String) {
        CoroutineScope(Dispatchers.IO).launch {
            currentPosition?.let { latLng ->
                val marker = Marker(
                    title = title,
                    description = description,
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    image = imageUri
                )
                database.insertMarker(marker)
                loadMarkers()
            }
        }
    }

    fun updateMarker(id: String, title: String, description: String) {
        CoroutineScope(Dispatchers.IO).launch {
            database.updateMarker(id, title, description)
            loadMarkers()
        }
    }

    fun deleteMarker(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
           database.deleteMarker(id)
            loadMarkers()
        }
    }

}
