package com.company.capstoneapp.activity.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.company.capstoneapp.ApiConfig
import com.company.capstoneapp.dataclass.DataUser
import com.company.capstoneapp.R
import com.company.capstoneapp.databinding.ActivityChangeProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ChangeProfileActivity : AppCompatActivity() {
    private var statusAdd: Boolean = false

    private lateinit var filePath: Uri
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mFirebaseInstance: FirebaseDatabase

    private lateinit var binding: ActivityChangeProfileBinding
    private lateinit var userData: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mFirebaseDatabase = mFirebaseInstance.getReference("User")

        userData = getSharedPreferences("login_session", MODE_PRIVATE)

        binding.apply {

            val toolbar = toolbar
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_white_24)

            Glide.with(this@ChangeProfileActivity)
                .load(userData.getString("urlPhoto", null))
                .into(ivAvatar)

            btnSave.setOnClickListener {
                val pass = etPassword.text.toString().trim()
                val name = etUsername.text.toString()
                if (etUsername.error != null && etPassword.error != null) {
                    Toast.makeText(
                        this@ChangeProfileActivity,
                        "Please fill the field with the right data",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else if (pass != userData.getString("password", null)) {
                    Toast.makeText(
                        this@ChangeProfileActivity,
                        "Wrong Password",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    if (name != "" && statusAdd) {
                        handleChangeName(name)
                    } else if (statusAdd && name == "") {
                        handleOnAdd()
                    } else if (name != "" && !statusAdd) {
                        handleChangeName(name)
                    }
                }
            }

            btnEditFoto.setOnClickListener {
                ImagePicker.with(this@ChangeProfileActivity)
                    .start()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                statusAdd = true
                filePath = data?.data!!
                Glide.with(this@ChangeProfileActivity)
                    .load(filePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.ivAvatar)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                statusAdd = false
            }
        }
    }

    private fun handleChangeName(name: String) {
        ApiConfig.getApiService("https://identitytoolkit.googleapis.com/v1/")
            .changeProfile(
                getString(R.string.API_KEY),
                userData.getString("idToken", null),
                displayName = name
            ).enqueue(object : Callback<DataUser> {
                override fun onResponse(
                    call: Call<DataUser>,
                    response: Response<DataUser>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val dataUser: DataUser = responseBody
                            if (statusAdd) {
                                handleOnAdd()
                            } else {
                                saveToServer(dataUser.photoUrl)
                            }
                        }

                        Toast.makeText(
                            this@ChangeProfileActivity,
                            "Ubah nama berhasil",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(
                            this@ChangeProfileActivity,
                            "Terjadi error, silahkan coba lagi nanti",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DataUser>, t: Throwable) {
                }

            })
    }

    private fun handleOnAdd() {
        val progressDialog = ProgressDialog(this@ChangeProfileActivity)
        progressDialog.setTitle("Uploading....")
        progressDialog.show()

        val ref = storageReference.child("images/" + UUID.randomUUID().toString())
        ref.putFile(filePath)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this@ChangeProfileActivity,
                    "Uploaded",
                    Toast.LENGTH_LONG
                ).show()

                ref.downloadUrl.addOnSuccessListener {
                    saveToServer(it.toString())
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this@ChangeProfileActivity, "Failed", Toast.LENGTH_LONG)
                    .show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress =
                    100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Uploading: " + progress.toInt() + "%")
            }
    }

    private fun saveToServer(url: String) {
        val idToken = userData.getString("idToken", "").toString()
        ApiConfig.getApiService("https://identitytoolkit.googleapis.com/v1/").changeProfile(
            getString(R.string.API_KEY),
            idToken,
            photoUrl = url,
        ).enqueue(object : Callback<DataUser> {
            override fun onResponse(call: Call<DataUser>, response: Response<DataUser>) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val dataUser: DataUser = responseBody
                    getSharedPreferences("login_session", MODE_PRIVATE)
                        .edit()
                        .putString("name", dataUser.displayName)
                        .putString("urlPhoto", dataUser.photoUrl)
                        .apply()
                    finishAffinity()
                    startActivity(
                        Intent(
                            this@ChangeProfileActivity, ProfileActivity::class.java
                        )
                    )
                }
            }

            override fun onFailure(call: Call<DataUser>, t: Throwable) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}