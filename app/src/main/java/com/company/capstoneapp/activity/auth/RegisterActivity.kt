package com.company.capstoneapp.activity.auth

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.company.capstoneapp.*
import com.company.capstoneapp.databinding.ActivityRegisterBinding
import com.company.capstoneapp.dataclass.DataUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val tvsignin = binding.tvSignin
        val text = "Sudah punya akun? Masuk!"
        spannable(
            tvsignin,
            text,
            18,
            24,
            this@RegisterActivity,
            Intent(this@RegisterActivity, LoginActivity::class.java)
        )

        binding.apply {
            btnSignup.setOnClickListener {
                if (etPassword.error != null || etEmail.error != null || etUsername.error != null || etVerifyPassword.error != null) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please fill the field with the right data",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else if (etPassword.text.toString() != etVerifyPassword.text.toString()) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Your Password doesn't match",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                } else {
                    showLoading(true, this@RegisterActivity)
                    val email = etEmail.text.toString().trim()
                    val password = etPassword.text.toString().trim()
                    val name = etUsername.text.toString()
                    handleSignUpResult(email, password, name)
                }
            }
        }
    }

    private fun handleSignUpResult(email: String, password: String, name: String) {
        ApiConfig.getApiService("https://identitytoolkit.googleapis.com/v1/").register(
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
                            .putString("password", password)
                            .putString("idToken", dataUser.idToken)
                            .apply()
                    }
                    ApiConfig.getApiService("https://identitytoolkit.googleapis.com/v1/").changeProfile(
                        getString(R.string.API_KEY),
                        response.body()?.idToken,
                        displayName = name,
                    ).enqueue(object : Callback<DataUser>{
                        override fun onResponse(
                            call: Call<DataUser>,
                            response: Response<DataUser>
                        ) {
                            getSharedPreferences("login_session", MODE_PRIVATE)
                                .edit()
                                .putString("name", name)
                                .apply()
                            showLoading(false, this@RegisterActivity)
                            Toast.makeText(
                                this@RegisterActivity,
                                "Berhasil daftar dengan email: " + response.body()?.email,
                                Toast.LENGTH_LONG
                            ).show()
                            finishAffinity()
                            startActivity(Intent(this@RegisterActivity, RegisterAddPhotoActivity::class.java)
                            )
                        }

                        override fun onFailure(call: Call<DataUser>, t: Throwable) {
                        }

                    })

                }else{
                    showLoading(false, this@RegisterActivity)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email yang anda masukkan telah terdaftar",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<DataUser>, t: Throwable) {
                Log.d(ContentValues.TAG, "ERROR :", t)
            }
        })
    }
}
