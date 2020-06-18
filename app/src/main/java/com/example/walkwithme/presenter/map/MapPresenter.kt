package com.example.walkwithme.presenter.map

import android.content.Context
import android.location.LocationManager
import android.os.SystemClock
import android.widget.Toast
import com.example.walkwithme.MapViewInterface
import com.example.walkwithme.R
import com.example.walkwithme.model.Algorithms
import org.osmdroid.bonuspack.routing.MapQuestRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class MapPresenter(private var mapInterface: MapViewInterface) {
    private val context: Context = mapInterface as Context
    private val map: MapView? = mapInterface.map
    private var wayPoints = ArrayList<GeoPoint>()

    fun setMarker(latitude: Double, longitude: Double) {
        val startPoint = GeoPoint(latitude, longitude)
        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = context.resources.getDrawable(R.drawable.marker)

        map!!.overlays.add(startMarker)
        map.invalidate()
    }

    fun onMapTapListener() {
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                setMarker(p.latitude, p.longitude)
                wayPoints.add(p)
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        map!!.overlays.add(MapEventsOverlay(mReceive))
    }

    fun buildRoute() {
        val roadManager: RoadManager = MapQuestRoadManager("sudOFI4elaABURi9uNTp74tdaN3scVcb")
        roadManager.addRequestOption("routeType=pedestrian")

        val distance = Array(wayPoints.size) { Array(wayPoints.size) { 0.0 } }

        for (i in 0 until wayPoints.size - 1) {
            for (j in i + 1 until wayPoints.size) {
                val roadFragment = roadManager.getRoad(arrayListOf(wayPoints[i], wayPoints[j]))
                distance[i][j] = roadFragment.mLength
                distance[j][i] = roadFragment.mLength
            }
        }

        val path = Algorithms.runGenetic(distance, 10.0)
        val newWayPoints = ArrayList<GeoPoint>()

        for (i in path) {
            newWayPoints.add(wayPoints[i])
        }

        val road = roadManager.getRoad(newWayPoints)
        val roadOverlay = RoadManager.buildRoadOverlay(road)
        map!!.overlays.add(roadOverlay)

        map.invalidate()
    }

    fun addRotation() {
        val mRotationGestureOverlay: RotationGestureOverlay = RotationGestureOverlay(context, map)
        mRotationGestureOverlay.isEnabled = true
        map!!.setMultiTouchControls(true)
        map.overlays.add(mRotationGestureOverlay)
    }

    fun getMyLocation(context: Context) {
        val mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
        val mapController = map!!.controller
        mMyLocationOverlay.disableMyLocation()
        mMyLocationOverlay.disableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
//        mMyLocationOverlay.runOnFirstFix {
//            runOnUiThread {
//                mapController.animateTo(mMyLocationOverlay.myLocation)
//                mapController.setZoom(18)
//            }
//        }
        map.overlays.add(mMyLocationOverlay)

//                val prov = GpsMyLocationProvider(context)
//        prov.addLocationSource(LocationManager.NETWORK_PROVIDER)
//        val locationOverlay = MyLocationNewOverlay(prov, map)
//        locationOverlay.enableMyLocation()
//        map.overlayManager.add(locationOverlay)
// hello world
    }

    private fun initMyLocationNewOverlay(ctx: Context) {
        // Another way to get location but less perspective

//        val provider = GpsMyLocationProvider(ctx)
//        provider.addLocationSource(LocationManager.NETWORK_PROVIDER)
//        myLocationOverlay = MyLocationNewOverlay(provider, map)
//        myLocationOverlay.enableMyLocation()
//        map!!.overlays.add(myLocationOverlay)
    }

    fun setDefaultRotation() {
        map?.controller?.animateTo(map.mapCenter, map.zoomLevelDouble, 1000, 0f)
    }
}