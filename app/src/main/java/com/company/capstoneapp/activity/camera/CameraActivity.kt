package com.company.capstoneapp.activity.camera

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.company.capstoneapp.ApiConfig
import com.company.capstoneapp.activity.home.HomeActivity
import com.company.capstoneapp.createFile
import com.company.capstoneapp.databinding.ActivityCameraBinding
import com.company.capstoneapp.dataclass.DetectionResponse
import com.company.capstoneapp.reduceFileImage
import com.company.capstoneapp.showLoading
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var getFile: File
    private lateinit var userData: SharedPreferences

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val startResult = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userData = getSharedPreferences("login_session", MODE_PRIVATE)

        binding.apply {
            captureImage.setOnClickListener { takePhoto() }
            ivBack.setOnClickListener{
                startActivity(Intent(this@CameraActivity, HomeActivity::class.java))
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun takePhoto() {
        showLoading(true, this@CameraActivity)
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Mengambil gambar...",
                        Toast.LENGTH_SHORT
                    ).show()
                    startResult.putExtra("picture", photoFile)
                    startResult.putExtra(
                        "isBackCamera",
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )

                    val result = BitmapFactory.decodeFile(photoFile.path)

                    val file = reduceFileImage(result, photoFile)
                    getFile = file
                    uploadImage()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.viewFinder.surfaceProvider) }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun uploadImage() {

        Toast.makeText(
            this@CameraActivity,
            "Tunggu ya, Kami lagi mendeteksi makanan...",
            Toast.LENGTH_LONG
        ).show()

        val file = getFile
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestImageFile
        )

        val idTokenMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "id_token",
            userData.getString("idToken", null).toString()
        )

        ApiConfig.getApiService("https://capstone-project-351416.et.r.appspot.com/")
            .uploadImage(imageMultipart, idTokenMultipart)
            .enqueue(object : Callback<DetectionResponse> {
                override fun onResponse(
                    call: Call<DetectionResponse>,
                    response: Response<DetectionResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            showLoading(false, this@CameraActivity)
                            Toast.makeText(
                                this@CameraActivity,
                                "Yey, makanan berhasil dideteksi",
                                Toast.LENGTH_SHORT 
                            ).show()
                            startResult.putExtra("food", responseBody.makanan)
                            setResult(ResultCameraActivity.CAMERA_X_RESULT, startResult)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@CameraActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DetectionResponse>, t: Throwable) {
                    showLoading(false, this@CameraActivity)
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
}