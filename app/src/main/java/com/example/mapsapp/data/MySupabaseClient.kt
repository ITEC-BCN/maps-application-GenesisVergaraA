package com.example.mapsapp.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from

class MySupabaseClient() {
    lateinit var client: SupabaseClient
    constructor(supabaseUrl: String, supabaseKey: String): this(){
        client = createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Postgrest)
        }
    }
    suspend fun getAllMarkers(): List<Marker> {
        return client.from("markerdatabase").select().decodeList<Marker>()
    }

    suspend fun getMarker(id: String): Marker{
        return client.from("Student").select {
            filter {
                eq("id", id)
            }
        }.decodeSingle<Marker>()
    }

    suspend fun insertMarker(marker: Marker){
        client.from("Student").insert(marker)
    }
    suspend fun updateMarker(id: String, name: String, mark: Double){
        client.from("Student").update({
            set("name", name)
            set("mark", mark)
        }) { filter { eq("id", id) } }
    }
    suspend fun deleteMarker(id: String){
        client.from("Student").delete{ filter { eq("id", id) } }
    }

}
