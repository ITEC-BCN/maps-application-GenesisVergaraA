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
            MapScreen()
        }
        composable<MarkerList> {
            MarkerListScreen()
        }
        composable<Destination.DetailMarker> { backStackEntry ->
            val detallScreen = backStackEntry.toRoute<Destination.DetailMarker>()
            DetailMarkerScreen(detallScreen.id) { navController.popBackStack() }
        }
        composable<Destination.CreateMarker> { backStackEntry ->
            val createScreen = backStackEntry.toRoute<Destination.CreateMarker>()
            CreateMarkerScreen(createScreen.coordenadas) {
                navController.popBackStack()
            }
        }
    }
}