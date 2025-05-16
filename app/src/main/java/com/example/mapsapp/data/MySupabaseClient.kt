package com.example.mapsapp.data

import android.util.Log
import androidx.compose.runtime.clearCompositionErrors
import io.github.jan.supabase.SupabaseClient
import com.example.mapsapp.BuildConfig
import com.example.mapsapp.utils.AuthState
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MySupabaseClient {
    lateinit var client: SupabaseClient
    lateinit var storage: Storage

    private val supabaseUrl = BuildConfig.SUPABASE_URL
    private val supabaseKey = BuildConfig.SUPABASE_KEY

    constructor() {
        client = createSupabaseClient(supabaseUrl = supabaseUrl, supabaseKey = supabaseKey) {
            install(Postgrest)
            install(Storage)
            install(Auth) {
                autoLoadFromStorage = true
            }
        }
        storage = client.storage

    }

    suspend fun getAllMarkers(): List<Marker> {
        return client.from("markerdatabase").select().decodeList<Marker>()
    }

    suspend fun getMarker(id: String): Marker {
        return client.from("markerdatabase").select {
            filter { eq("id", id) }
        }.decodeSingle()
    }

    suspend fun insertMarker(marker: Marker) {
        Log.d("insertar", "Llego a insertar")
        client.from("markerdatabase").insert(marker)
    }

    suspend fun updateMarker(id: String, title: String, description: String, newImageUrl: String?) {
        client.from("markerdatabase").update({
            set("title", title)
            set("description", description)
            set("image", newImageUrl)
        }) { filter { eq("id", id) } }
    }

    suspend fun deleteMarker(id: String) {
        client.from("markerdatabase").delete { filter { eq("id", id) } }
    }

    suspend fun uploadImage(imageFile: ByteArray): String {
        val fechaHoraActual = LocalDateTime.now()
        val formato = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val imageName = storage.from("images")
            .upload(path = "image_${fechaHoraActual.format(formato)}.png", data = imageFile)
        return buildImageUrl(imageFileName = imageName.path)
    }

    suspend fun deleteImage(imageName: String) {
        val imgName =
            imageName.removePrefix("https://aobflzinjcljzqpxpcxs.supabase.co/storage/v1/object/public/images/")
        client.storage.from("images").delete(imgName)
    }


    fun buildImageUrl(imageFileName: String) =
        "${this.supabaseUrl}/storage/v1/object/public/images/${imageFileName}"


    suspend fun signUpWithEmail(emailValue: String, passwordValue: String): AuthState {
        try {
            client.auth.signUpWith(Email) {
                email = emailValue
                password = passwordValue
            }
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage)
        }
    }


    suspend fun signInWithEmail(emailValue: String, passwordValue: String): AuthState {
        try {
            client.auth.signInWith(Email) {
                email = emailValue
                password = passwordValue
            }
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage)
        }
    }

    fun retrieveCurrentSession(): UserSession?{
        val session = client.auth.currentSessionOrNull()
        return session
    }

    fun refreshSession(): AuthState {
        try {
           client.auth.currentSessionOrNull()
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage)
        }
    }
}
