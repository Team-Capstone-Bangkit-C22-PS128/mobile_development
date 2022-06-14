package com.company.capstoneapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CulinaryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(culinary: Culinary)

    @Delete
    fun delete(culinary: Culinary)

    @Query("SELECT * from culinary ORDER BY id ASC")
    fun getAllFavorite(): LiveData<List<Culinary>>

    @Query("SELECT EXISTS(SELECT * FROM culinary WHERE id = :id)")
    fun isFavorite(id: String) : Boolean
}