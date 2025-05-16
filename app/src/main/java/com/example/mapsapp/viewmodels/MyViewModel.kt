package com.example.mapsapp.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.SupabaseApplication
import com.example.mapsapp.data.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MyViewModel() : ViewModel() {
    val supabase = SupabaseApplication.supabase
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
                val marker = supabase.getMarker(id)
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
            val databaseMarker = supabase.getAllMarkers()
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
        image: Bitmap?,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageUrl = image?.let { bitmap ->
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                supabase.uploadImage(stream.toByteArray())
            }
            val marker = Marker(
                title = title,
                description = description,
                latitude = lat,
                longitude = longitude,
                image = imageUrl,
            )
            supabase.insertMarker(marker)
            loadMarkers()

        }
    }

    fun updateMarker(id: String, title: String, description: String, image: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentImageUrl = selectedMarker.value!!.image
            val newImageUrl = image?.let { bitmap ->
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                supabase.uploadImage(stream.toByteArray())
            } ?: currentImageUrl

            if (newImageUrl != currentImageUrl && currentImageUrl != null) {
               supabase.deleteImage(currentImageUrl)
            }

            supabase.updateMarker(id, title, description, newImageUrl)
            loadMarkers()
        }
    }

    fun deleteMarker(id: String, image: String) {
        CoroutineScope(Dispatchers.IO).launch {
            supabase.deleteImage(image)
            supabase.deleteMarker(id)
            loadMarkers()
        }
    }

}
