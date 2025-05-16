package com.example.mapsapp

import android.app.Application
import com.example.mapsapp.data.MySupabaseClient

class SupabaseApplication: Application() {
    companion object{
        lateinit var supabase: MySupabaseClient

    }
    override fun onCreate() {
        super.onCreate()
        supabase = MySupabaseClient()

    }
}
