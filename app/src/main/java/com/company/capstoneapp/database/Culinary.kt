package com.company.capstoneapp.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Culinary(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "link")
    val link: String,

    @ColumnInfo(name = "rate")
    val rate: Double
) : Parcelable
