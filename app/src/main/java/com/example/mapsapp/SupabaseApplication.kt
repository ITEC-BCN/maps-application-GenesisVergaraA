package com.example.mapsapp

import android.app.Application
import com.example.mapsapp.data.MySupabaseClient
import com.example.mapsapp.data.SupabaseManager

class SupabaseApplication: Application() {
    companion object{
        lateinit var supabase: SupabaseManager
        lateinit var database: MySupabaseClient

    }
    override fun onCreate() {
        super.onCreate()
        supabase = SupabaseManager()
        database = MySupabaseClient()

    }
}
