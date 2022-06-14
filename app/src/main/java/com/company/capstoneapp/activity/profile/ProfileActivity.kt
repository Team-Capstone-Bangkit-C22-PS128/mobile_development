package com.company.capstoneapp.activity.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.company.capstoneapp.R
import com.company.capstoneapp.databinding.ActivityProfileBinding
import com.company.capstoneapp.activity.auth.LoginActivity
import com.company.capstoneapp.activity.favorite.FavoriteActivity
import com.company.capstoneapp.activity.camera.ResultCameraActivity
import com.company.capstoneapp.activity.home.HomeActivity


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userData: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userData = getSharedPreferences("login_session", MODE_PRIVATE)

        binding.apply {

            tvUsername.text = userData.getString("name", "Unknown")
            tvEmail.text = userData.getString("email", "Unknown")
            Glide.with(this@ProfileActivity)
                .load(userData.getString("urlPhoto", null))
                .into(ivAvatar)

            tvFavorite.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, FavoriteActivity::class.java))
            }

            tvChangeName.setOnClickListener{
                startActivity(Intent(this@ProfileActivity, ChangeProfileActivity::class.java))
            }

            tvChangePassword.setOnClickListener{
                startActivity(Intent(this@ProfileActivity, ChangePasswordActivity::class.java))
            }

            btnSignout.setOnClickListener {
                userData.edit().clear().apply()
                finishAffinity()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            }

            navbar.selectedItemId = R.id.profile_menu
            navbar.menu.findItem(R.id.profile_menu).setIcon(R.drawable.ic_profile_active)
            navbar.menu.findItem(R.id.home_menu).setOnMenuItemClickListener {
                finishAffinity()
                startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
                return@setOnMenuItemClickListener false
            }
            floatingActionButton.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, ResultCameraActivity::class.java))
            }

            tvAbout.setOnClickListener {
                Toast.makeText(this@ProfileActivity, "WORK IN PROGRESS", Toast.LENGTH_LONG).show()
            }

            tvHelp.setOnClickListener {
                Toast.makeText(this@ProfileActivity, "WORK IN PROGRESS", Toast.LENGTH_LONG).show()
            }
        }
    }

}
