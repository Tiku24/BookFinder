package com.example.bookfinder.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bookfinder.data.local.table.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    suspend fun saveBook(bookEntity: BookEntity)

    @Delete
    suspend fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * FROM book_table")
    fun getBookmarks(): Flow<List<BookEntity>>

    @Query("SELECT COALESCE((SELECT isViewed FROM book_table WHERE title = :title LIMIT 1), 0)")
    fun isBookViewed(title: String): Flow<Boolean>

    @Query("SELECT * FROM book_table WHERE title = :title LIMIT 1")
    fun getDetailByTitle(title: String): BookEntity
}