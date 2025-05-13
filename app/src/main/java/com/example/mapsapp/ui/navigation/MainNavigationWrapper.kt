package com.example.mapsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.screens.PermissionsScreen
import androidx.navigation.compose.composable
import com.example.mapsapp.ui.navigation.Destination.Map
import com.example.mapsapp.ui.navigation.Destination.Permissions
import com.example.mapsapp.ui.screens.MyDrawerMenu
import com.example.mapsapp.ui.screens.SignInScreen
import com.example.mapsapp.ui.screens.SignUpScreen


@Composable
fun MainNavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController, Permissions) {
        composable<Permissions> {
            PermissionsScreen() {
                navController.navigate(Destination.SignIn)
            }
        }
        composable<Destination.Drawer> {
            MyDrawerMenu(){
                navController.navigate(Destination.SignIn)
            }
        }
        composable<Destination.SignIn> {
            SignInScreen(
                 { navController.navigate(Destination.SignUp) },
                 { navController.navigate(Destination.Drawer) }
            )
        }
        composable<Destination.SignUp> {
            SignUpScreen() {
                navController.navigate(Destination.SignIn)
            }
        }
    }
}