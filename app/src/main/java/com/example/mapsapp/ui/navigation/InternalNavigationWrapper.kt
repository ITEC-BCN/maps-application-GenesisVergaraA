package com.example.mapsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.mapsapp.ui.navigation.Destination.MarkerList
import com.example.mapsapp.ui.screens.CreateMarkerScreen
import com.example.mapsapp.ui.screens.DetailMarkerScreen
import com.example.mapsapp.ui.screens.MapScreen
import com.example.mapsapp.ui.screens.MarkerListScreen

@Composable
fun InternalNavigationWrapper(navController: NavHostController) {
    NavHost(navController, Destination.Map) {
        composable<Destination.Map> {
            MapScreen(
                onCreateMarker = { latLng ->
                    navController.navigate(Destination.CreateMarker(latLng))
                },
                onShowList = {
                    navController.navigate(Destination.MarkerList)
                },
                onMarkerClick = { markerId ->
                    navController.navigate(Destination.DetailMarker(markerId))
                }
            )
        }

        composable<Destination.CreateMarker> { backStackEntry ->
            val destination = backStackEntry.toRoute<Destination.CreateMarker>()
            CreateMarkerScreen(
                coordinates = destination.coordenadas,
                onBack = { navController.popBackStack() },
                onMarkerCreated = { navController.popBackStack() }
            )
        }

        composable<Destination.MarkerList> {
            MarkerListScreen(
                onBack = { navController.popBackStack() },
                onMarkerClick = { markerId ->
                    navController.navigate(Destination.DetailMarker(markerId))
                }
            )
        }

        composable<Destination.DetailMarker> { backStackEntry ->
            val destination = backStackEntry.toRoute<Destination.DetailMarker>()
            DetailMarkerScreen(
                markerId = destination.id,
                onBack = { navController.popBackStack() },
                onMarkerUpdated = { navController.popBackStack() }
            )
        }
    }
}