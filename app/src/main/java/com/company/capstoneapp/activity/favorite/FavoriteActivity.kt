package com.company.capstoneapp.activity.favorite

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.capstoneapp.R
import com.company.capstoneapp.database.Culinary
import com.company.capstoneapp.databinding.ActivityFavoriteBinding
import com.company.capstoneapp.viewmodel.CulinaryViewModel
import com.company.capstoneapp.viewmodel.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var culinaryViewModel: CulinaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_white_24)

        culinaryViewModel = obtainViewModel(this)

        culinaryViewModel.getAllFavorite().observe(this) { result ->
            if (result.isNotEmpty()) {
                setListFavorite(result)
            } else {
                binding.rvFavorite.visibility = View.INVISIBLE
                binding.tvNoAvailable.visibility = View.VISIBLE
            }
        }
    }

    private fun setListFavorite(favorites: List<Culinary>) {
        val favoriteAdapter = FavoriteAdapter(favorites, this)
        binding.rvFavorite.apply {
            layoutManager = if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 2)
            } else {
                LinearLayoutManager(context)
            }
            setHasFixedSize(true)
            adapter = favoriteAdapter
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): CulinaryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[CulinaryViewModel::class.java]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}