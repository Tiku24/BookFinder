package com.example.bookfinder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookfinder.data.local.dao.BookDao
import com.example.bookfinder.data.local.table.BookEntity

@Database(entities = [BookEntity::class], version = 2, exportSchema = false)
abstract class BookDatabase: RoomDatabase() {
    abstract fun bookDao(): BookDao
}