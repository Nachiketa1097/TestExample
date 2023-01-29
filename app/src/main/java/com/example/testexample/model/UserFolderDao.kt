package com.example.testexample.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserFolderDao {

    @Query("SELECT * FROM UserFolder")
    suspend fun getNewFolder(): List<UserFolder>

    @Insert
    suspend fun insertNewFolder(userFolder: UserFolder)

    @Delete
    suspend fun deleteNewFolder(userFolder: UserFolder)
}