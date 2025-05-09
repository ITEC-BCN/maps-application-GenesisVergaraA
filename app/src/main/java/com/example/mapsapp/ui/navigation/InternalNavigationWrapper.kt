package com.example.mapsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.mapsapp.ui.screens.CreateMarkerScreen
import com.example.mapsapp.ui.screens.DetailMarkerScreen
import com.example.mapsapp.ui.screens.MapScreen
import com.example.mapsapp.ui.screens.MarkerListScreen
import com.google.android.gms.maps.model.LatLng

@Composable
fun InternalNavigationWrapper(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, Destination.Map) {
        composable<Destination.Map> {
            MapScreen(
                onCreateMarker = { latLng ->
                    navController.navigate(Destination.CreateMarker(latLng.latitude, latLng.longitude))
                },
                onShowList = {
                    navController.navigate(Destination.MarkerList)
                },
                onMarkerClick = { markerId ->
                    navController.navigate(Destination.DetailMarker(markerId))
                },
            modifier)
        }

        composable<Destination.CreateMarker> { backStackEntry ->
            val lat = backStackEntry.toRoute<Destination.CreateMarker>().latitude
            val lng = backStackEntry.toRoute<Destination.CreateMarker>().longitude
            val coordenadas = LatLng(lat, lng)
            CreateMarkerScreen(
                coordenadas = coordenadas,
                onBack = { navController.popBackStack() },
                onMarkerCreated = { navController.popBackStack() },
                modifier
            )
        }

        composable<Destination.MarkerList> {
            MarkerListScreen(
                onBack = { navController.popBackStack() },
                onMarkerClick = { markerId ->
                    navController.navigate(Destination.DetailMarker(markerId))
                },
                modifier
            )
        }

        composable<Destination.DetailMarker> { backStackEntry ->
            val destination = backStackEntry.toRoute<Destination.DetailMarker>()
            DetailMarkerScreen(
                markerId = destination.id,
                onBack = { navController.popBackStack() },
                onMarkerUpdated = { navController.popBackStack() },
                modifier)
        }
    }
}