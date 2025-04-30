package com.example.mapsapp.ui.navigation

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

sealed class Destination {

    @Serializable
    object Map : Destination()

    @Serializable
    object MarkerList : Destination()

    @Serializable
    data class CreateMarker(
        @Contextual
        val coordenadas: LatLng
    ) : Destination()

    @Serializable
    data class DetailMarker(val id: String) : Destination()

    @Serializable
    object Permissions : Destination()

    @Serializable
    object Drawer : Destination()
}
