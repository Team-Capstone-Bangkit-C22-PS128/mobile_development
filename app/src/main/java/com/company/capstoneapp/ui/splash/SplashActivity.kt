package com.company.capstoneapp.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.company.capstoneapp.ApiConfig
import com.company.capstoneapp.DataUser
import com.company.capstoneapp.R
import com.company.capstoneapp.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private lateinit var userData: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        userData = getSharedPreferences("login_session", MODE_PRIVATE)
        val refreshToken = userData.getString("refreshToken", null).toString()

        Handler(Looper.getMainLooper()).postDelayed({

            if (refreshToken == "null")
            {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
            else if (refreshToken != "null")
            {
                ApiConfig.getApiService("https://securetoken.googleapis.com/v1/")
                        .refreshIdToken(
                            getString(R.string.API_KEY),
                            refreshToken
                        ).enqueue(object : Callback<DataUser>{
                            override fun onResponse(call: Call<DataUser>, response: Response<DataUser>) {
                                if (response.isSuccessful) {
                                    val responseBody = response.body()

                                    if (responseBody != null) {
                                        val dataUser: DataUser = responseBody
                                        getSharedPreferences("login_session", MODE_PRIVATE)
                                            .edit()
                                            .putString("refreshToken", dataUser.refreshToken)
                                            .putString("idToken", dataUser.idToken)
                                            .apply()
                                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<DataUser>, t: Throwable) {
                                // code ...
                            }
                        })
            }

        }, 3000)
    }
}