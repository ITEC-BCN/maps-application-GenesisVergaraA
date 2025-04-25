package com.example.mapsapp.ui.navigation

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    object Map : Destination()

    @Serializable
    object MarkerList : Destination()

    /*@Serializable
    data class CreateMarker(val coordenadas: Long) : Destination()

    @Serializable
    data class DetailMarker(val id: String): Destination()
*/
    @Serializable
    object Permissions: Destination()

    @Serializable
    object Drawer: Destination()
}
