package com.example.mapsapp

import android.app.Application
import com.example.mapsapp.data.MySupabaseClient

class MyApp: Application() {
    companion object {
        lateinit var database: MySupabaseClient
    }
    override fun onCreate() {
        super.onCreate()
        database = MySupabaseClient(
            supabaseUrl = "https://sqjmuecwuusztxfmgxyr.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNxam11ZWN3dXVzenR4Zm1neHlyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU5MjA4MDEsImV4cCI6MjA2MTQ5NjgwMX0.YUoTKfE8s0dtgsPMNy0ebPEpsf0JfybJDJit9e_TypA"
        )
    }
}
