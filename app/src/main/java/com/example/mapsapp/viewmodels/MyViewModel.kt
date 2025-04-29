package com.example.mapsapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel : ViewModel() {
    val database = MyApp.database
    private val _markerName = MutableLiveData<String>()
    val markerName = _markerName
    private val _markerMark = MutableLiveData<String>()
    val markerMark = _markerMark
    private val _markersList = MutableLiveData<List<Marker>>()
    val markersList = _markersList
    private var _selectedMarker: Marker? = null


    fun insertNewMarker(id: Int, name: String, latitude: Double, longitude: Double) {
        val newMarker = Marker(id, name, latitude, longitude)
        CoroutineScope(Dispatchers.IO).launch {
            database.insertMarker(newMarker)
            getAllMarkers()
        }
    }

    fun getAllMarkers() {
        CoroutineScope(Dispatchers.IO).launch {
            val databaseStudents = database.getAllMarkers()
            withContext(Dispatchers.Main) {
                _markersList.value = databaseStudents
            }
        }
    }

    fun updateStudent(id: String, name: String, mark: String) {
        CoroutineScope(Dispatchers.IO).launch {
            database.updateMarker(id, name, mark.toDouble())
        }
    }

    fun getStudent(id: String) {
        if (_selectedMarker == null) {
            CoroutineScope(Dispatchers.IO).launch {
                val marker = database.getMarker(id)
                withContext(Dispatchers.Main) {
                    _selectedMarker = marker
                    _markerName.value = marker.name
                    _markerMark.value = marker.mark.toString()
                }
            }
        }
    }

    fun deleteStudent(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            database.deleteMarker(id)
            getAllMarkers()
        }
    }


}
