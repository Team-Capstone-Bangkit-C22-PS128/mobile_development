package com.company.capstoneapp.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.company.capstoneapp.database.Culinary
import com.company.capstoneapp.repository.CulinaryRepository

class CulinaryViewModel(application: Application) : ViewModel() {
    private val mCulinaryRepository: CulinaryRepository = CulinaryRepository(application)

    fun getAllFavorite() = mCulinaryRepository.getAllFavorite()

    fun isFavorite(culinaryId: String) : Boolean {
        return mCulinaryRepository.isFavorite(culinaryId)
    }

    fun insert(culinary: Culinary) {
        mCulinaryRepository.insert(culinary)
    }

    fun delete(culinary: Culinary) {
        mCulinaryRepository.delete(culinary)
    }
}