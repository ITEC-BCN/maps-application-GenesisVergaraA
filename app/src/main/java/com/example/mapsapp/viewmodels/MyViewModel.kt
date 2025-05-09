package com.example.mapsapp.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MyViewModel : ViewModel() {
    val database = MyApp.database

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading = _isLoading

    private val _markersList = MutableLiveData<List<Marker>>(emptyList<Marker>())
    val markersList = _markersList
    private val _selectedMarker = MutableLiveData<Marker?>(null)
    val selectedMarker: LiveData<Marker?> = _selectedMarker

    init {
        loadMarkers()
    }


    fun getMarker(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val marker = database.getMarker(id)
                withContext(Dispatchers.Main) {
                    _selectedMarker.value = marker
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _selectedMarker.value = null
                }
            }
        }
    }

    fun loadMarkers() {
        CoroutineScope(Dispatchers.IO).launch {
            val databaseMarker = database.getAllMarkers()
            withContext(Dispatchers.Main) {
                _markersList.value = databaseMarker
                _isLoading.value = false
            }
        }
    }

    fun addMarker(
        title: String,
        description: String,
        lat: Double,
        longitude: Double,
        image: Bitmap?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageUrl = image?.let { bitmap ->
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                database.uploadImage(stream.toByteArray())
            }
            val marker = Marker(
                title = title,
                description = description,
                latitude = lat,
                longitude = longitude,
                image = imageUrl
            )
            database.insertMarker(marker)
            loadMarkers()

        }
    }

    fun updateMarker(id: String, title: String, description: String, image: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentImageUrl = selectedMarker.value!!.image
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
