package com.company.capstoneapp.dataclass

import com.google.gson.annotations.SerializedName

data class DataUser(

	@field:SerializedName("expiresIn")
	val expiresIn: String,

	@field:SerializedName("displayName")
	val displayName: String,

	@field:SerializedName("idToken")
	val idToken: String,

	@field:SerializedName("localId")
	val localId: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("refreshToken")
	val refreshToken: String,

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("profilePicture")
	val profilePicture: String,

	@field:SerializedName("refresh_token")
	val refresh_Token: String,

	@field:SerializedName("id_token")
	val id_Token: String,
)
