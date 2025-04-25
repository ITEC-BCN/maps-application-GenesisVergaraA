package com.example.mapsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mapsapp.ui.navigation.Destination.MarkerList
import com.example.mapsapp.ui.screens.MapScreen
import com.example.mapsapp.ui.screens.MarkerListScreen

@Composable
fun InternalNavigationWrapper(navController: NavHostController) {
    NavHost(navController, MarkerList) {
        composable<Destination.Map> {
            MapScreen()
        }
        composable<MarkerList> {
            MarkerListScreen()
        }
    }
}