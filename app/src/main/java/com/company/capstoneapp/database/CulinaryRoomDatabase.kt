package com.company.capstoneapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Culinary::class], version = 1)
abstract class CulinaryRoomDatabase : RoomDatabase() {
    abstract fun CulinaryDao() : CulinaryDao

    companion object {
        @Volatile
        private var INSTANCE: CulinaryRoomDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): CulinaryRoomDatabase {
            if (INSTANCE == null) {
                synchronized(CulinaryRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CulinaryRoomDatabase::class.java, "culinary_database")
                        .build()
                }
            }
            return INSTANCE as CulinaryRoomDatabase
        }
    }
}