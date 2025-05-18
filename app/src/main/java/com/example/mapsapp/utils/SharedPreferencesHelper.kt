package com.example.mapsapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

    //Guardar configuraciones
    fun saveAuthData(accessToken: String, refreshToken: String) {
        sharedPreferences.edit {
            putString("access_token", accessToken).putString("refresh_token", refreshToken)
        }
    }

    //Obtener token de acceso
    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)

    //Obtener token de actualización
    fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token", null)

    //Limpiar configuraciones
    fun clear() {
        sharedPreferences.edit { clear() }
    }


}
