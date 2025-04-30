package com.example.mapsapp.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from


class MySupabaseClient() {
    lateinit var client: SupabaseClient
    lateinit var storage: SessionSource.Storage

    constructor(supabaseUrl: String, supabaseKey: String): this() {
        client = createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Postgrest)
            install(Storage)
        }
        storage = client.storage
    }

    suspend fun getAllMarkers(): List<Marker> {
        return client.from("markers").select().decodeList<Marker>()
    }

    suspend fun getMarker(id: String): Marker {
        return client.from("markers").select {
            filter { eq("id", id) }
        }.decodeSingle()
    }

    suspend fun insertMarker(marker: Marker) {
        client.from("markers").insert(marker)
    }

    suspend fun updateMarker(id: String, title: String, description: String) {
        client.from("markers").update({
            set("title", title)
            set("description", description)
        }) { filter { eq("id", id) } }
    }

    suspend fun deleteMarker(id: String) {
        client.from("markers").delete { filter { eq("id", id) } }
    }

    suspend fun uploadImage(fileName: String, inputStream: InputStream): String {
        storage.from("marker_images").upload(fileName, inputStream)
        return storage.from("marker_images").publicUrl(fileName)
    }
}
