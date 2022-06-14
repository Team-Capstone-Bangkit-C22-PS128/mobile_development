package com.company.capstoneapp.dataclass

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Culinary(
    val description: String? = null,
    val halal: Boolean? = null,
    val id: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val link: String? = null,
    val name: String? = null,
    val rate: Double? = null
){
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}