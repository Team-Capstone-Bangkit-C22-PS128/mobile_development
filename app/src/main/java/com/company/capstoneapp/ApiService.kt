package com.company.capstoneapp

import com.company.capstoneapp.dataclass.DataUser
import com.company.capstoneapp.dataclass.DetectionResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST("./accounts:signUp")//karna ada semicolon(:) jadi pake (./)
    fun register(
        @Query("key") query: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("returnSecureToken") bool: Boolean = true,
    ): Call<DataUser>

    @FormUrlEncoded
    @POST("./accounts:signInWithPassword")//karna ada semicolon(:) jadi pake (./)
    fun login(
        @Query("key") query: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("returnSecureToken") bool: Boolean = true,
    ): Call<DataUser>

    @FormUrlEncoded
    @POST("./accounts:update")//karna ada semicolon(:) jadi pake (./)
    fun changeProfile(
        @Query("key") query: String,
        @Field("idToken") idToken: String?,
        @Field("displayName") displayName: String? = null,
        @Field("password") password: String? = null,
        @Field("photoUrl") photoUrl: String? = null,
        @Field("returnSecureToken") bool: Boolean = true,
    ): Call<DataUser>

    @FormUrlEncoded
    @POST("token")
    fun refreshIdToken(
        @Query("key") query: String,
        @Field("refresh_token") displayName: String,
        @Field("grant_type") idToken: String = "refresh_token",
    ): Call<DataUser>

    @Multipart
    @POST("predict")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part idToken: MultipartBody.Part
    ): Call<DetectionResponse>
}