package com.example.testexample.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "note_title") val noteTitle: String?,
    @ColumnInfo(name = "note") val note: String?,
    @ColumnInfo(name = "characters") val characters: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "folder_name") var folderName: String = "default",

)
