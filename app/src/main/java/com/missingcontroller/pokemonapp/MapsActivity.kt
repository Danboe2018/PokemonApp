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
        loadPokemon()
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)

        var MyThread = MyThread()
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

    var oldLocation: Location? = null

    inner class MyThread : Thread {
        constructor() : super() {
            oldLocation = Location("Start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0

        }

        override fun run() {
            while (true) {
                try {
                    if (oldLocation!!.distanceTo(location) == 0f) {
                        continue
                    }

                    oldLocation = location

                    runOnUiThread {
                        Log.wtf(
                            "GPS",
                            "Location: ${location!!.latitude} and ${location!!.longitude}"
                        )
                        mMap.clear()

                        // show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet(" here is my location ")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        // show pokemon

                        for (i in 0 until listPokemon.size) {
                            var newPokemon = listPokemon[i]
                            if (newPokemon.IsCatch == false) {
                                val pokemonLoc = LatLng(
                                    newPokemon.location!!.latitude,
                                    newPokemon.location!!.longitude
                                )
                                mMap!!.addMarker(
                                    MarkerOptions()
                                        .position(pokemonLoc)
                                        .title(newPokemon.name!!)
                                        .snippet(newPokemon.des!! + ", power:" + newPokemon!!.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!))
                                )


                                if (location!!.distanceTo(newPokemon.location) < 2) {
                                    newPokemon.IsCatch = true
                                    listPokemon[i] = newPokemon
                                    playerPower += newPokemon.power!!
                                    Toast.makeText(
                                        applicationContext,
                                        "You catch new pokemon your new power is " + playerPower,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }

                    Thread.sleep(1000)
                } catch (ex: Exception) {

                }
            }
        }
    }

    var playerPower = 0.0
    var listPokemon = ArrayList<Pokemon>()

    fun loadPokemon() {
        listPokemon.add(
            Pokemon(
                R.drawable.charmander,
                "Charmander",
                "Charmander living in japan",
                55.0,
                37.7789994893035,
                -122.401846647263
            )
        )
        listPokemon.add(
            Pokemon(
                R.drawable.bulbasaur,
                "Bulbasaur", "Bulbasaur living in usa", 90.5, 37.7949568502667, -122.410494089127
            )
        )
        listPokemon.add(
            Pokemon(
                R.drawable.squirtle,
                "Squirtle", "Squirtle living in iraq", 33.5, 37.7816621152613, -122.41225361824
            )
        )

    }
}
