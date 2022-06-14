package com.company.capstoneapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.company.capstoneapp.database.Culinary
import com.company.capstoneapp.database.CulinaryDao
import com.company.capstoneapp.database.CulinaryRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CulinaryRepository(application: Application) {
    private val mCulinaryDao: CulinaryDao

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = CulinaryRoomDatabase.getDatabase(application)
        mCulinaryDao = db.CulinaryDao()
    }

    fun getAllFavorite(): LiveData<List<Culinary>> = mCulinaryDao.getAllFavorite()

    fun isFavorite(culinaryId: String) : Boolean = mCulinaryDao.isFavorite(culinaryId)

    fun insert(culinary: Culinary) {
        executorService.execute { mCulinaryDao.insert(culinary) }
    }

    fun delete(culinary: Culinary) {
        executorService.execute { mCulinaryDao.delete(culinary) }
    }
}