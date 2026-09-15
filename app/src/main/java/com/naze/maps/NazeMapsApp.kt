package com.naze.maps

import android.app.Application
import org.maplibre.android.MapLibre

class NazeMapsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Init MapLibre once, app-wide. No API key or payment method required.
        MapLibre.getInstance(this)
    }
}
