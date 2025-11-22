package com.example.bookfinder.data.local.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_table")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUrl: String,
    val title: String,
    val author: String,
    val language: String,
    val year: String,
    val isViewed: Boolean = false
)
