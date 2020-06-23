package com.example.walkwithme.presenter.map

import android.content.Context
import com.example.walkwithme.MapViewInterface
import com.example.walkwithme.R
import com.example.walkwithme.model.Algorithms
import com.google.android.gms.maps.CameraUpdateFactory
import org.osmdroid.bonuspack.routing.MapQuestRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class MapPresenter(private var mapInterface: MapViewInterface) {
    private val context: Context = mapInterface as Context
    private val map: MapView? = mapInterface.map
    private var wayPoints = ArrayList<GeoPoint>()
    private lateinit var roadOverlay: Polyline

    fun setMarker(latitude: Double, longitude: Double, index: Int) {
        val point = GeoPoint(latitude, longitude)
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = context.resources.getDrawable(R.drawable.marker)
        marker.isDraggable = true

        marker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {
            }

            override fun onMarkerDragStart(marker: Marker) {
            }

            override fun onMarkerDragEnd(marker: Marker) {
                wayPoints[index] = marker.position
            }
        })

        map!!.overlays.add(marker)
        map.invalidate()
    }

    fun onMapTapListener() {
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                if (wayPoints.size < 2) {
                    setMarker(p.latitude, p.longitude, wayPoints.size)
                    wayPoints.add(p)
                }

                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        map!!.overlays.add(MapEventsOverlay(mReceive))
    }

    fun buildRoute() {
        if (::roadOverlay.isInitialized) {
            map!!.overlays.remove(roadOverlay)
        }

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
        roadOverlay = RoadManager.buildRoadOverlay(road)
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