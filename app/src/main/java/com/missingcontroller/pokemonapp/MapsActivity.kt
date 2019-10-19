package com.missingcontroller.pokemonapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
    }

    val ACCESSLOCATION = 123

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESSLOCATION
                )
                return
            }
        }
    }

    fun getUserLocation() {
        Toast.makeText(this, "User location access on", Toast.LENGTH_LONG).show()

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var myLocation = MyLocationListener()

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3,3f, myLocation)

        var MyThread=MyThread()
        MyThread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "We cannot access to your location", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    var location: Location? = null

    // Get user location

    inner class MyLocationListener : LocationListener {

        constructor() {
            Log.wtf("GPS", "Constructor")
            location = Location("Start")
            location!!.longitude = 0.0
            location!!.latitude = 0.0
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Log.wtf("GPS", "onStatusChanged: $provider")
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            Log.wtf("GPS", "onProviderEnabled: $provider")
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            Log.wtf("GPS", "onProviderDisabled: $provider")
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onLocationChanged(p0: Location?) {
            Log.wtf("GPS", "Location: $p0")
            location = p0
        }

    }

    inner class MyThread:Thread {
        constructor():super(){

        }

        override fun run(){
            while (true){
                try {
                    runOnUiThread {
                        Log.wtf("GPS", "Location: ${location!!.latitude} and ${location!!.longitude}")
                        mMap.clear()
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet(" here is my location ")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))
                    }

                    Thread.sleep(1000)
                } catch (ex:Exception){

                }
            }
        }
    }
}
