package com.example.testexample.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE folder_name IN (:folderName)")
    suspend fun getFolderList(folderName: String): List<User>

    @Insert
    suspend fun insertUser(user: User)

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun updateUser(user: User)
}