package it.polito.mad.lab2.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.*
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.AsyncTaskLoader
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Tasks
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import it.polito.mad.lab2.R
import java.io.IOException
import it.polito.mad.lab2.activities.GpsActivity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var searchView: SearchView
    private var currentMarker: Marker? = null

    private var action: Int? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    lateinit var url:String
    private var place1: Marker? = null
    private var place2: Marker? = null

    private var GPSCODE: Int = 900


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //Checking if the map is consulted by a show or edit fragment
        val intent: Intent = intent
        action = intent.getIntExtra("action", 0)
        latitude = intent.getDoubleExtra("latitude", 1000.0)
        longitude = intent.getDoubleExtra("longitude", 1000.0)

        Log.e("XXX", action.toString())
        Log.e("XXX", latitude.toString())
        Log.e("XXX", longitude.toString())
        //Referencing Google's map functionality

        //Reference the searchview box object
        searchView = findViewById(R.id.mapLocation)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        //Differentiating between Edit and Show mode
        if (action == 2 || action == 3) searchView.visibility = View.GONE
        else searchView.visibility = View.VISIBLE

        //Listen to data typed into the searchview box object
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = searchView.query.toString()
                var addressList: List<Address>? = null

                if (location != "") {
                    val geocoder = Geocoder(applicationContext)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return false
                    }
                    val address: Address = addressList?.get(0)!!
                    val latLng = LatLng(address.latitude, address.longitude)

                    latitude = address.latitude
                    longitude = address.longitude

                    currentMarker?.remove()
                    currentMarker =
                        map.addMarker(MarkerOptions().position(latLng).title(location))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        //Plot the map on screen
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Turin, Italia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        //Setting up options of the map
        map = googleMap
        // Zoom in/out with buttons
        map.uiSettings.isZoomControlsEnabled = true
        // Zoom in/out with fingers
        map.getUiSettings().setZoomGesturesEnabled(true)
        //Enabling User Current location
        map.isMyLocationEnabled


        if (action == 3) {

            Log.e("TRACE1", latitude.toString())
            Log.e("TRACE2", longitude.toString())

            val intent = Intent(this, GpsActivity::class.java)
            startActivityForResult(intent, GPSCODE)

            val latLng = LatLng(latitude!!, longitude!!)
            place1 = map.addMarker(MarkerOptions().position(latLng).title("Place1"))

        } else {
            if (latitude!! in -90.0..90.0 && longitude!! in -180.0..180.0) {
                val locationA = LatLng(latitude!!, longitude!!)
                Log.e("HERE", "HERE")
                currentMarker = map.addMarker(MarkerOptions().position(locationA))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationA, 10F))
            } else {
                // Add a marker in Turin and move the camera there
                val turin = LatLng(45.0703393, 7.686864)
                currentMarker = map.addMarker(MarkerOptions().position(turin).title("Marker in Turin"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(turin, 10F))
            }

            //Waiting for the User to pick a position by clicking on the marker
            map.setOnMarkerClickListener { marker: Marker ->
                if (marker == currentMarker) {
                    val resultIntent = Intent()
                    resultIntent.putExtra("latitude", latitude!!)
                    resultIntent.putExtra("longitude", longitude!!)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                false
            }
        }
    }

    private fun getUrl(origin: LatLng, destination: LatLng, directionMode: String): String {

        //Origin Route
        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude

        //Destination Route
        val strDest = "destination=" + destination.latitude + "," + destination.longitude

        //Mode
        val mode = "mode=" + directionMode

        //Building the parameters to the web service
        val parameters = strOrigin + "&" + strDest + "&" + mode

        //Output format
        val output = "json"

        //Building the Url to the web services
        val url = "https://maps.googleapis.com/maps/api/directions/" + output +
                "?" + parameters + "&key" + getString(R.string.google_api_key)
        return url
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetDirection(val url: String) : AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body().string()
            Log.e("DATA", data)
            val result = ArrayList<List<LatLng>>()
            try {

                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
                val path = ArrayList<LatLng>()

                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    val startlatLng = LatLng(
                        respObj.routes[0].legs[0].steps[i].start_Location.lat.toDouble(),
                        respObj.routes[0].legs[0].steps[i].start_Location.lng.toDouble()
                    )
                    path.add(startlatLng)

                    val endlatLng = LatLng(
                        respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble(),
                        respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble()
                    )
                    path.add(endlatLng)
                }
                result.add(path)
            } catch (e: Exception) {
                Log.e("RASTRO", "4444")
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>?) {
            val lineoption = PolylineOptions()

            for (i in result!!.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }

            map.addPolyline(lineoption)
        }
    }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == GPSCODE) {
                if (resultCode == Activity.RESULT_OK) {
                    val latLng = LatLng(
                        data?.getDoubleExtra("latitude", 0.0)!!,
                        data.getDoubleExtra("longitude", 0.0)
                    )
                            place2 = map.addMarker(MarkerOptions().position(latLng).title("Place2"))
                            val line:PolylineOptions = PolylineOptions().add(place1!!.position, place2!!.position).
                            width(10F).color(Color.BLUE)

                            map.addPolyline(line)

                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 2F))

                            /* This methods display the real path but a paid gmail account
                            is need it for this
                            place2 = map.addMarker(MarkerOptions().position(latLng).title("Place2"))
                            url = getUrl(place1!!.position, place2!!.position, "driving")
                            GetDirection(url).execute()*/
                }
            }
        }

    }
