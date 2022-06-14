package com.company.capstoneapp.activity.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.company.capstoneapp.R
import com.company.capstoneapp.database.Culinary
import com.company.capstoneapp.databinding.ActivityDetailBinding
import com.company.capstoneapp.viewmodel.CulinaryViewModel
import com.company.capstoneapp.viewmodel.ViewModelFactory
import com.company.capstoneapp.activity.maps.MapsActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.floor

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var database: DatabaseReference
    private lateinit var culinaryViewModel: CulinaryViewModel
    private var culinary: Culinary? = null
    private var isFavorite: Boolean = false
    private lateinit var latlng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        culinaryViewModel = obtainViewModel(this)

        isFavorite = false

        binding.btnLike.setOnClickListener(this)

        binding.btnBack.setOnClickListener(this)

        val culinaryId = intent.getStringExtra(EXTRA_ID).toString()

        if (culinaryId.isNotEmpty()) {
            getCulinary(culinaryId)
        }

        binding.btnMaps.setOnClickListener(this)
    }

    private fun getCulinary(culinaryId: String) {
        database = Firebase.database.reference.child("makanan")

        Thread(Runnable {
            isFavorite = culinaryViewModel.isFavorite(culinaryId)
        }).start()

        database.child(culinaryId).get().addOnSuccessListener {

            if (it.exists()) {

                // variables
                val link = it.child("link").value.toString()
                val name = it.child("name").value.toString()
                val rate = it.child("rate").value.toString()
                val description = it.child("description").value.toString().replace("\\n", "\n")

                // set image
                Glide.with(this)
                    .load(link)
                    .into(binding.itemImage)

                // set logo halal
                if (it.child("halal").value == false) {
                    binding.itemHalal.setImageResource(R.drawable.ic_nonhalal)
                }

                // set attribute
                binding.itemName.text = name
                binding.itemRate.text = rate
                binding.itemDescription.text = description

                // set icon star
                var yellowStar = rate.toDouble()
                yellowStar = floor(yellowStar)

                binding.icStar1.apply {
                    if (yellowStar < 1) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                binding.icStar2.apply {
                    if (yellowStar < 2) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                binding.icStar3.apply {
                    if (yellowStar < 3) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                binding.icStar4.apply {
                    if (yellowStar < 4) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                binding.icStar5.apply {
                    if (yellowStar < 5) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                val lat = it.child("latitude").value.toString().toDouble()
                val lng = it.child("longitude").value.toString().toDouble()
                latlng = LatLng(lat, lng)

                // set data on culinary variable
                culinary = Culinary(
                    id = culinaryId,
                    name = name,
                    link = link,
                    rate = rate.toDouble()
                )

                // set button favorite
                isFavoriteCulinary()

            } else {
                Toast.makeText(this@DetailActivity, "Data kuliner tidak ditemukan", Toast.LENGTH_LONG).show()
            }

        }.addOnFailureListener {
            Log.e("FIREBASE_GAGAL", it.toString())

        }
    }
private fun isFavoriteCulinary() {
        // set button favorite if isFavorite = true
        if (isFavorite) {
            binding.btnLike.apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(this@DetailActivity, R.color.like_red)
                binding.btnLike.setImageResource(R.drawable.ic_favorite_checked)
            }
        }
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_like -> {
                if (culinary != null) {
                    isFavorite = !isFavorite

                    binding.btnLike.apply {
                        if (isFavorite) {
                            culinaryViewModel.insert(culinary!!)
                            backgroundTintList = ContextCompat.getColorStateList(this@DetailActivity, R.color.like_red)
                            binding.btnLike.setImageResource(R.drawable.ic_favorite_checked)
                            Toast.makeText(this@DetailActivity, "Disukai", Toast.LENGTH_SHORT).show()
                        } else {
                            culinaryViewModel.delete(culinary!!)
                            backgroundTintList = ContextCompat.getColorStateList(this@DetailActivity, R.color.like_gray)
                            binding.btnLike.setImageResource(R.drawable.ic_favorite_uncheck)
                            Toast.makeText(this@DetailActivity, "Batal Suka", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
R.id.btn_maps -> {
                Log.v("GANTENG", latlng.toString())
                val intent = Intent(this@DetailActivity, MapsActivity::class.java)
                intent.putExtra("latlng", latlng)
                startActivity(intent)
            }
        }
    }
private fun obtainViewModel(activity: AppCompatActivity): CulinaryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[CulinaryViewModel::class.java]
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}
