package com.company.capstoneapp.activity.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute
import com.company.capstoneapp.R
import com.company.capstoneapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var database: DatabaseReference
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_white_24)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // konfigurasi gmaps
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setMapStyle()

        // beri penanda lokasi real terkini pengguna
        markUserLocation()

        // beri penanda lokasi dummy terkini pengguna
        val userLocationDummy = LatLng(-0.4791039926444693, 117.18969923573819)
        mMap.addMarker(
            MarkerOptions()
                .position(userLocationDummy)
                .title("Lokasi Dummy Anda")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocationDummy, 18f))

        // dapatkan titik-titik lokasi kuliner di sekitar
        getDataCulinary()
    }

    private fun getDataCulinary() {
        database.child("makanan").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val culinaryName = data.child("name").value.toString()
                    val latitude = data.child("latitude").value.toString().toDouble()
                    val longitude = data.child("longitude").value.toString().toDouble()
                    val location = LatLng(latitude, longitude)

                    // beri penanda kuliner di sekitar lokasi pengguna
                    markCulinaryLocation(culinaryName, location)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LOHE", "WKWKWKW EROR")
            }

        })
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("STYLE_MAPS", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("STYLE_MAPS", "Can't find style. Error: ", exception)
        }
    }

    private fun markUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            var userLocationReal: LatLng
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    userLocationReal = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(userLocationReal)
                            .title("Lokasi Real Anda")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocationReal, 18f))

                    val dest = intent.getParcelableExtra<LatLng>("latlng")
                    if (dest != null){
                        GoogleDirection.withServerKey("AIzaSyD9QXH9sYidCoRSeJRxc7zr_DRbmhO6ESs")
                            .from(userLocationReal)
                            .to(dest!!)
                            .avoid(AvoidType.FERRIES)
                            .avoid(AvoidType.HIGHWAYS)
                            .execute(
                                    onDirectionSuccess = { direction: Direction? ->
                                        if(direction!!.isOK()) {
                                            val route = direction.routeList[0]
                                            val leg = route.legList[0]
                                            val directionPositionList: ArrayList<LatLng> = leg.directionPoint
                                            val polylineOptions = DirectionConverter.createPolyline(
                                                this,
                                                directionPositionList,
                                                5,
                                                resources.getColor(R.color.corn_blue)
                                            )
                                            mMap.addPolyline(polylineOptions)

                                            val distance = leg.distance.text.toString()
                                            val duration = leg.duration.text.toString()

                                            binding.detailInfo.visibility = View.VISIBLE
                                            binding.tvDistance.text = resources.getString(R.string.distance, distance)
                                            binding.tvTime.text = resources.getString(R.string.duration, duration)
                                        } else {
                                            // Do something
                                        }
                                    },
                                    onDirectionFailure = { t: Throwable ->
                                        // Do something
                                    }
                            )
                    }
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun markCulinaryLocation(culinaryName: String, location: LatLng) {
        val markerOptions = MarkerOptions()
            .position(location)
            .title(culinaryName)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_culinary_marker))

        mMap.addMarker(markerOptions)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                markUserLocation()
            }
        }


}