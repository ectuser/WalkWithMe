package com.example.walkwithme

import android.content.Context
import android.content.res.Resources
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.ArrayList

interface MapViewInterface {
    var map: MapView?

    fun onMapTapListener()
    fun addRotation()
    fun getMyLocation(context: Context)
}