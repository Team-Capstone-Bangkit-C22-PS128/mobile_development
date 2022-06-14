package com.company.capstoneapp.activity.auth

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.company.capstoneapp.ApiConfig
import com.company.capstoneapp.dataclass.DataUser
import com.company.capstoneapp.R
import com.company.capstoneapp.databinding.ActivityRegisterAddPhotoBinding
import com.company.capstoneapp.activity.home.HomeActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RegisterAddPhotoActivity : AppCompatActivity() {

    private var statusAdd: Boolean = false

    private lateinit var filePath: Uri
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mFirebaseInstance: FirebaseDatabase

    private lateinit var binding: ActivityRegisterAddPhotoBinding
    private lateinit var userData: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mFirebaseDatabase = mFirebaseInstance.getReference("User")

        userData = getSharedPreferences("login_session", MODE_PRIVATE)

        binding.apply {

            val fullname = userData.getString("name", "Unknown").toString()
            val firstName = fullname.split(" ")[0]

            tvHello.text = resources.getString(
                R.string.tv_welcome,
                firstName
            )
            
            btnEditFoto.setOnClickListener {
                if (statusAdd) {
                    statusAdd = false
                    btnEditFoto.setImageResource(R.drawable.ic_add)
                    circleImageView.setImageResource(R.drawable.profile_pic)
                } else {
                    ImagePicker.with(this@RegisterAddPhotoActivity)
                        .start()
                    btnSimpan.visibility = View.VISIBLE
                }
            }

            btnLewati.setOnClickListener {
                finishAffinity()
                startActivity(Intent(this@RegisterAddPhotoActivity, HomeActivity::class.java))
            }

            btnSimpan.setOnClickListener {
                val progressDialog = ProgressDialog(this@RegisterAddPhotoActivity)
                progressDialog.setTitle("Uploading....")
                progressDialog.show()

                val ref = storageReference.child("images/" + UUID.randomUUID().toString())
                ref.putFile(filePath)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@RegisterAddPhotoActivity,
                            "Uploaded",
                            Toast.LENGTH_LONG
                        ).show()

                        ref.downloadUrl.addOnSuccessListener {
                            saveToServer(it.toString())
                        }
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(this@RegisterAddPhotoActivity, "Failed", Toast.LENGTH_LONG)
                            .show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress =
                            100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog.setMessage("Uploading: " + progress.toInt() + "%")
                    }
            }
        }
    }

    private fun saveToServer(url: String) {
        val idToken = userData.getString("idToken", "").toString()
        ApiConfig.getApiService("https://identitytoolkit.googleapis.com/v1/").changeProfile(
            getString(R.string.API_KEY),
            idToken,
            photoUrl = url,
        ).enqueue(object : Callback<DataUser>{
            override fun onResponse(call: Call<DataUser>, response: Response<DataUser>) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val dataUser: DataUser = responseBody
                    getSharedPreferences("login_session", MODE_PRIVATE)
                        .edit()
                        .putString("urlPhoto", dataUser.photoUrl)
                        .apply()
                }
                
                finishAffinity()
                startActivity(
                    Intent(
                        this@RegisterAddPhotoActivity,
                        HomeActivity::class.java
                    )
                )
            }

            override fun onFailure(call: Call<DataUser>, t: Throwable) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                statusAdd = true
                filePath = data?.data!!
                Glide.with(this@RegisterAddPhotoActivity)
                    .load(filePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.circleImageView)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}