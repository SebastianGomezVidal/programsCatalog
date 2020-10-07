package it.polito.mad.lab2.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Half.toFloat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import it.polito.mad.lab2.R


class GpsActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var provider: String

    var latitude:Double? = null
    var longitude:Double? = null


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)

        //Initialize location Manager

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        /* The getSystemService method is used when we want to access one of few Android
        system-level services, in this case to get access to GPS device information */

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        val list = locationManager.getProviders(true)

        Log.e("XXX", list.toString())

        val criteria = Criteria()
        /* It is a class indicating the application criteria for selecting a location provider.
        Providers may be ordered according to accuracy, power usage, ability to report altitude,
        speed, bering, and monetary cost */
        provider = locationManager.getBestProvider(criteria, false)
        /* Returns the name of the provider that best meets the given criteria */
        val location = locationManager.getLastKnownLocation(provider)
        /* Gets the last known location from the given provider, or null if there is no last known location */

        //Initialize the location
        location?.let { onLocationChanged(it) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    @SuppressLint("MissingPermission", "HalfFloat")
    override fun onResume() {
        super.onResume()
        locationManager.requestLocationUpdates(provider, 500, toFloat(1), this)
    }

    //Pause the location manager when app is paused/stopped
    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        Log.e("lat", location.latitude.toString())
        Log.e("lng", location.longitude.toString())

        val resultIntent = Intent(this, MapsActivity::class.java)
        resultIntent.putExtra("latitude", location.latitude)
        resultIntent.putExtra("longitude", location.longitude)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String?) {
        //TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        //TODO("Not yet implemented")
    }
}
