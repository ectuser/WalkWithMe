package com.example.walkwithme

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.bonuspack.routing.MapQuestRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import java.util.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var map: MapView? = null
    private lateinit var myLocationOverlay : MyLocationNewOverlay

    private var wayPoints = ArrayList<GeoPoint>();
    private lateinit var lastMarker : Marker;

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        val ctx = applicationContext
        Configuration.getInstance()
            .load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map

        setContentView(R.layout.activity_main)
        map = findViewById<View>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)
        requestPermissionsIfNecessary(
            arrayOf( // if you need to show the current location, uncomment the line below
                // Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )


        onMapTapListener()
        addRotation()
        getMyLocation(this)
        buildRouteButton.setOnClickListener {buildThreePointsRoute()}

    }

    private fun initMyLocationNewOverlay(ctx : Context) {
        val provider = GpsMyLocationProvider(ctx)
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER)
        myLocationOverlay = MyLocationNewOverlay(provider, map)
        myLocationOverlay.enableMyLocation()
        map!!.overlays.add(myLocationOverlay)
    }

    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        map!!.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    public override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map!!.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest =
            ArrayList<String>()
        for (i in grantResults.indices) {
            permissionsToRequest.add(permissions[i])
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun setMarker(latitude : Double, longtitude : Double){
        val startPoint = GeoPoint(latitude, longtitude)
        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map!!.overlays.add(startMarker)

        lastMarker = startMarker
    }

    private fun onMapTapListener(){
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                Toast.makeText(
                    baseContext,
                    p.latitude.toString() + " - " + p.longitude,
                    Toast.LENGTH_LONG
                ).show()
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

    private fun addRotation(){
        var mRotationGestureOverlay: RotationGestureOverlay = RotationGestureOverlay(this, map)
        mRotationGestureOverlay.isEnabled = true
        map!!.setMultiTouchControls(true)
        map!!.overlays.add(mRotationGestureOverlay)
    }

    private fun getMyLocation(context: Context){
        val mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
        val mapController = map!!.controller
        mMyLocationOverlay.disableMyLocation()
        mMyLocationOverlay.disableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                mapController.animateTo(mMyLocationOverlay.myLocation)
                mapController.setZoom(18)
            }
        }
        map!!.overlays.add(mMyLocationOverlay)

        //        val prov = GpsMyLocationProvider(this)
//        prov.addLocationSource(LocationManager.NETWORK_PROVIDER)
//        val locationOverlay = MyLocationNewOverlay(prov, map)
//        locationOverlay.enableMyLocation()
//        map!!.overlayManager.add(locationOverlay)
    }

    private fun buildThreePointsRoute(){
        val roadManager: RoadManager = MapQuestRoadManager("sudOFI4elaABURi9uNTp74tdaN3scVcb")
        roadManager.addRequestOption("routeType=fastest")
        try{
            val road = roadManager.getRoad(wayPoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            map!!.overlays.add(roadOverlay)

            lastMarker.title = road.mLength.toString()
        }
        catch(error : Exception){
//            Log.d("MyTagTag", error.message);
        }
    }




    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest =
            ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }
}