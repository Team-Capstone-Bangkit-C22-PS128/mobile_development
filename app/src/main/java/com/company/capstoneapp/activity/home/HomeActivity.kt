package com.company.capstoneapp.activity.home

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.company.capstoneapp.R
import com.company.capstoneapp.databinding.ActivityHomeBinding
import com.company.capstoneapp.dataclass.Culinary
import com.company.capstoneapp.activity.camera.ResultCameraActivity
import com.company.capstoneapp.activity.maps.MapsActivity
import com.company.capstoneapp.activity.profile.ProfileActivity
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userInfo: SharedPreferences
    private lateinit var database: DatabaseReference
    private lateinit var culinaryAroundAdapter: ListCulinaryAroundAdapter
    private lateinit var culinaryRecommendationAdapter: ListCulinaryRecommendationAdapter
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            setupHome()
        }
    }

    private fun setupHome() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        database = Firebase.database.reference
        userInfo = getSharedPreferences("login_session", MODE_PRIVATE)

        getUserLocation()

        binding.apply {
            val fullname = userInfo.getString("name", "Unknown").toString()
            val firstName = fullname.split(" ")[0]

            tvUsername.text =
                resources.getString(
                    R.string.name_placeholder,
                    firstName
                )

            Glide.with(this@HomeActivity)
                .load(
                    userInfo.getString(
                        "urlPhoto",
                        null
                    )
                )
                .into(ivAvatar)

            btnMaps.setOnClickListener {
                startActivity(Intent(this@HomeActivity, MapsActivity::class.java))
            }

            floatingActionButton.setOnClickListener {
                startActivity(Intent(this@HomeActivity, ResultCameraActivity::class.java))
            }

            // recyclerview makanan disekitar
            val managerCulinaryAroundAdapter = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            managerCulinaryAroundAdapter.stackFromEnd = false
            rvCulinaryAround.layoutManager = managerCulinaryAroundAdapter
            rvCulinaryAround.itemAnimator = null

            val optionsCulinaryAroundAdapter = FirebaseRecyclerOptions.Builder<Culinary>()
                .setQuery(database.child("makanan").limitToFirst(6), Culinary::class.java)
                .build()

            culinaryAroundAdapter = ListCulinaryAroundAdapter(optionsCulinaryAroundAdapter)
            rvCulinaryAround.adapter = culinaryAroundAdapter

            // recyclerview makanan rekomendasi
            val managerCulinaryRecommendationAdapter = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
            managerCulinaryRecommendationAdapter.stackFromEnd = false
            rvRecommendation.layoutManager = managerCulinaryRecommendationAdapter
            rvRecommendation.itemAnimator = null

            val optionsCulinaryRecommendationAdapter = FirebaseRecyclerOptions.Builder<Culinary>()
                .setQuery(database.child("makanan").orderByChild("rate").startAt(4.0), Culinary::class.java)
                .build()

            culinaryRecommendationAdapter = ListCulinaryRecommendationAdapter(optionsCulinaryRecommendationAdapter)
            rvRecommendation.adapter = culinaryRecommendationAdapter

            navbar.selectedItemId = R.id.home_menu
            navbar.menu.findItem(R.id.home_menu).setIcon(R.drawable.ic_home_active)
            navbar.menu.findItem(R.id.profile_menu).setOnMenuItemClickListener {
                startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                return@setOnMenuItemClickListener false
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        culinaryAroundAdapter.startListening()
        culinaryRecommendationAdapter.startListening()
    }

    public override fun onPause() {
        super.onPause()
        culinaryAroundAdapter.stopListening()
        culinaryRecommendationAdapter.stopListening()
    }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location

                    // get Address
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val city: String = addresses[0].subAdminArea
                            val state: String = addresses[0].adminArea

                            getSharedPreferences("login_session", MODE_PRIVATE)
                                .edit()
                                .putString("lastLocation", "$city, $state")
                                .apply()

                            binding.tvAddress.text = userInfo.getString("lastLocation", "-").toString()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }


        return
    }
}
