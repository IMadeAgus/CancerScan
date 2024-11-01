package com.dicoding.asclepius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class HistoryEntity (
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    val id: Int,

    @field:ColumnInfo(name = "label")
    val label: String,

    @field:ColumnInfo(name = "presentase")
    val presentase:Float,

    @field:ColumnInfo(name = "iamge")
    val image:ByteArray
)