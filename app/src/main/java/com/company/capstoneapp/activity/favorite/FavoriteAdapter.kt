package com.company.capstoneapp.activity.favorite

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.company.capstoneapp.R
import com.company.capstoneapp.database.Culinary
import com.company.capstoneapp.databinding.ItemCulinaryRecommendationBinding
import com.company.capstoneapp.activity.detail.DetailActivity
import kotlin.math.floor

class FavoriteAdapter(private val favorites: List<Culinary>, private val context: Context) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemCulinaryRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    inner class FavoriteViewHolder(private val binding: ItemCulinaryRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        val context: Context = itemView.context

        fun bind(culinary: Culinary) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(culinary.link)
                    .into(itemImage)

                itemName.text = culinary.name
                itemRate.text = culinary.rate.toString()

                // TODO: masih ada bug di icon bintang
                val yellowStar = floor(culinary.rate)
                icStar1.apply {
                    if (yellowStar < 1) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                icStar2.apply {
                    if (yellowStar < 2) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                icStar3.apply {
                    if (yellowStar < 3) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                icStar4.apply {
                    if (yellowStar < 4) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                icStar5.apply {
                    if (yellowStar < 5) {
                        setImageResource(R.drawable.ic_star_gray)
                    }
                }

                itemView.setOnClickListener {
                    val detailPage = Intent(context, DetailActivity::class.java)
                    detailPage.putExtra(DetailActivity.EXTRA_ID, culinary.id)
                    context.startActivity(detailPage)
                }
            }
        }
    }

}