package com.example.bookfinder.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookfinder.data.ApiService
import com.example.bookfinder.data.local.dao.BookDao
import com.example.bookfinder.data.local.table.BookEntity
import com.example.bookfinder.data.model.searchresponse.Doc
import com.example.bookfinder.data.model.searchresponse.SearchResponse
import com.example.bookfinder.data.model.subjectresponse.PopularBookResponse
import com.example.bookfinder.data.model.subjectresponse.Work
import com.example.bookfinder.data.pagination.PopularBookPaging
import com.example.bookfinder.data.pagination.SearchBookPaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class Repo @Inject constructor(private val apiService: ApiService,private val bookDao: BookDao) {

    suspend fun searchBooks(query: String,page: Int): Result<SearchResponse> {
        return try {
            val res = apiService.searchBooks(query,page)
            if (res.isSuccessful){
                Result.Success(res.body()!!)
            }else{
                Log.d("TAG", "searchBooks: ${res.message()}")
                Result.Error(res.message(),null,res.body())
            }
        }catch (e: Exception){
            Log.d("TAG", "searchBooks: ${e.message}")
            Result.Error( "Something went wrong try again", Exception(e.message))
        }
    }

    suspend fun getPopularBooks(subject: String, page: Int): Result<PopularBookResponse> {
        return try {
            val res = apiService.getBooksBySubject(subject, page = page)
            if (res.isSuccessful){
                Result.Success(res.body()!!)
            }else{
                Result.Error("Something went wrong",null,res.body())
            }
        } catch (e: Exception){
            Result.Error("Something went wrong try again", Exception(e.message))
        }
    }

    suspend fun getBookByTittle(title: String): Result<SearchResponse> {
        return try {
            val res = apiService.getBookByTittle(title)
            if (res.isSuccessful){
                Result.Success(res.body()!!)
            }else{
                Result.Error("Something went wrong",null,res.body())
            }
        } catch (e: Exception){
            Result.Error("Something went wrong try again", Exception(e.message))
        }
    }

    fun getBookSearchPage(query: String): Flow<PagingData<Doc>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchBookPaging(this, query = query) }
        ).flow
    }

    suspend fun saveBook(bookEntity: BookEntity) = bookDao.saveBook(bookEntity = bookEntity)
    suspend fun deleteBook(bookEntity: BookEntity) = bookDao.deleteBook(bookEntity = bookEntity)
    fun getSavedBooks() = bookDao.getBookmarks()
    fun isBookVisited(title: String) = bookDao.isBookViewed(title)

    fun getBookDetailByTittle(title: String): Flow<BookEntity?> {
        return flow {
            emit(bookDao.getDetailByTitle(title))
        }
    }
}

sealed class Result<T>() {
    data class Success<T>(val data: T): Result<T>()
    data class Error<T>(
        val message:String, val throwable: Throwable? = null,val data: T? = null
    ): Result<T>()
}