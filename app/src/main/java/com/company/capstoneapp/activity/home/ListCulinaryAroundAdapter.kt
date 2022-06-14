package com.company.capstoneapp.activity.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.company.capstoneapp.R
import com.company.capstoneapp.databinding.ItemCulinaryAroundBinding
import com.company.capstoneapp.dataclass.Culinary
import com.company.capstoneapp.activity.detail.DetailActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlin.math.floor

class ListCulinaryAroundAdapter(options: FirebaseRecyclerOptions<Culinary>) : FirebaseRecyclerAdapter<Culinary, ListCulinaryAroundAdapter.ListViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_culinary_around, parent, false)
        val binding = ItemCulinaryAroundBinding.bind(view)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int, model: Culinary) {
        holder.bind(model, position)
    }

    inner class ListViewHolder(private val binding: ItemCulinaryAroundBinding) : RecyclerView.ViewHolder(binding.root) {
        val context: Context = itemView.context

        fun bind(item: Culinary, position: Int) {
            binding.itemName.text = item.name
            binding.itemRate.text = item.rate.toString()

            Glide.with(itemView.context)
                .load(item.link)
                .into(binding.itemImage)

            // TODO: masih ada bug di icon bintang
            val yellowStar = floor(item.rate!!)
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

            itemView.setOnClickListener {
                val detailPage = Intent(context, DetailActivity::class.java)
                detailPage.putExtra(DetailActivity.EXTRA_ID, item.id)
                context.startActivity(detailPage)
            }

            if (position == 0) { // jika item pertama, beri margin left
                val params = binding.cardView.layoutParams as RecyclerView.LayoutParams
                params.marginStart = 45
                binding.cardView.layoutParams = params
            }

        }
    }

}