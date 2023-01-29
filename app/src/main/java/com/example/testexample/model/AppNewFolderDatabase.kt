package com.example.testexample.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserFolder::class], version = 1)
abstract class AppNewFolderDatabase : RoomDatabase() {

    abstract fun userFolderDao(): UserFolderDao

    companion object{
        fun buildDatabase(context: Context): AppNewFolderDatabase{
            return Room.databaseBuilder(
                context.applicationContext,
                AppNewFolderDatabase::class.java, "database-new-folder-name"
            ).fallbackToDestructiveMigration().build()
        }
    }
}