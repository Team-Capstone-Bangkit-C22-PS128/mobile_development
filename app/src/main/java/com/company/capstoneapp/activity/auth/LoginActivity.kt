package com.company.capstoneapp.activity.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.company.capstoneapp.*
import com.company.capstoneapp.databinding.ActivityLoginBinding
import com.company.capstoneapp.activity.home.HomeActivity
import com.company.capstoneapp.dataclass.DataUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userData: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userData = getSharedPreferences("login_session", MODE_PRIVATE)
        if (userData.getString("name", null) != null) {
            finishAffinity()
            startActivity(Intent(this, HomeActivity::class.java))
        }


        binding.apply {
            //sign in with our service
            btnSignin.setOnClickListener {
                if (etPassword.error != null || etEmail.error != null) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please fill the field with the right data",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    showLoading(true, this@LoginActivity)
                    handleSignInResult(
                        etEmail.text.toString().trim(),
                        etPassword.text.toString().trim()
                    )
                }
            }


        }

        val tvsignup = binding.tvSignup
        val text = "Belum punya akun? Daftar!"
        spannable(
            tvsignup,
            text,
            18,
            25,
            this@LoginActivity,
            Intent(this@LoginActivity, RegisterActivity::class.java)
        )


    }

    private fun handleSignInResult(email: String, password: String) {
        ApiConfig.getApiService("https://identitytoolkit.googleapis.com/v1/").login(
            getString(R.string.API_KEY),
            email,
            password
        ).enqueue(object : Callback<DataUser> {
            override fun onResponse(
                call: Call<DataUser>,
                response: Response<DataUser>
            ) {
                if (response.isSuccessful) {

                    val responseBody = response.body()

                    if (responseBody != null) {
                        val dataUser: DataUser = responseBody
                        getSharedPreferences("login_session", MODE_PRIVATE)
                            .edit()
                            .putString("localId", dataUser.localId)
                            .putString("email", dataUser.email)
                            .putString("name", dataUser.displayName)
                            .putString("password", password)
                            .putString("idToken", dataUser.idToken)
                            .putString("urlPhoto", dataUser.profilePicture)
                            .putString("lastLocation", "")
                            .putString("refreshToken", dataUser.refreshToken)
                            .apply()
                        showLoading(false, this@LoginActivity)
                        finishAffinity()
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                HomeActivity::class.java
                            )
                        )
                        Toast.makeText(
                            this@LoginActivity,
                            "Hello " + dataUser.displayName,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }else{
                    showLoading(false, this@LoginActivity)
                    Toast.makeText(
                        this@LoginActivity,
                        "Email atau password yang anda masukkan salah",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<DataUser>, t: Throwable) {
                Log.d(TAG, "ERROR :", t)
            }
        })
    }
}
