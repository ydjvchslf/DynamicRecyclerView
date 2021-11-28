package com.example.dynamicrecyclerview.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class Memo (

    @PrimaryKey(autoGenerate = true)
    var id: Int?,

    @ColumnInfo(name = "title")
    var title: String?,

    @ColumnInfo(name = "content")
    var content: String?

)