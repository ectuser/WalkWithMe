package com.example.walkwithme

import com.example.walkwithme.presenter.map.MapPresenter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*

class MainActivity:
    AppCompatActivity(),
    MapViewInterface {

    private val permissionRequestCode = 1
    private var mapPresenter: MapPresenter? = null

    public override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build()
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Configuration.getInstance().apply {
            userAgentValue = "OBP_Tuto/1.0"
            load(
                applicationContext,
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            )
        }

        requestPermissionsIfNecessary(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        Map.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(
                CustomZoomButtonsController.Visibility.NEVER
            )
            controller.setZoom(12.0)
            controller.setCenter(
                GeoPoint(
                    56.4977,
                    84.9744
                )
            )
        }

        mapPresenter = MapPresenter(this).apply {
            setOnMapTapListener()
            setRotationGestureOverlay()
            setMyLocationOverlay()
        }

        BuildRouteButton.setOnClickListener { mapPresenter?.buildRoute() }
        MyLocationButton.setOnClickListener { mapPresenter?.setMyLocationOverlay() }
        CompassButton.setOnClickListener { mapPresenter?.setDefaultRotation() }
    }

    override fun onResume() {
        super.onResume()
        Map.onResume()
    }

    override fun onPause() {
        super.onPause()
        Map.onPause()
    }

    override fun onDestroy() {
        mapPresenter = null
        super.onDestroy()
    }

    override fun getMap(): MapView {
        return Map
    }

    override fun getMarker(): Marker {
        return Marker(Map)
    }

    override fun getRotationGestureOverlay(): RotationGestureOverlay {
        return RotationGestureOverlay(Map)
    }

    override fun getMyLocationOverlay(): MyLocationNewOverlay {
        return MyLocationNewOverlay(
            GpsMyLocationProvider(this),
            Map
        )
    }

    override fun setMapMultiTouchControls(
        on: Boolean
    ) {
        Map.setMultiTouchControls(on)
    }

    override fun mapInvalidate() {
        Map.invalidate()
    }

    override fun mapAddOverlay(
        overlay: Overlay?
    ) {
        if (overlay != null) {
            Map.overlays.add(overlay)
        }
    }

    override fun mapRemoveOverlay(
        overlay: Overlay?
    ) {
        if (overlay != null) {
            Map.overlays.remove(overlay)
        }
    }

    private fun requestPermissionsIfNecessary(
        permissions: Array<String>
    ) {
        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                permissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>()
        for (i in grantResults.indices) {
            permissionsToRequest.add(permissions[i])
        }

        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                permissionRequestCode
            )
        }
    }

}