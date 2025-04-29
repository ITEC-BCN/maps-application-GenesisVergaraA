package com.example.mapsapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Marker(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
)
