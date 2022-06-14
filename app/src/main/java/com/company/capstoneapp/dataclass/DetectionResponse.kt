package com.company.capstoneapp.dataclass

import com.google.gson.annotations.SerializedName

data class DetectionResponse(

	@field:SerializedName("status")
	val status: Boolean,

	@field:SerializedName("makanan")
	val makanan: String
)
