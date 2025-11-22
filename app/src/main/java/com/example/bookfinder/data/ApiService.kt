package com.example.bookfinder.data

import com.example.bookfinder.data.model.searchresponse.SearchResponse
import com.example.bookfinder.data.model.subjectresponse.PopularBookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    //https://openlibrary.org/search.json?q=education&page=10

    @GET("/search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int? = null
    ): Response<SearchResponse>

    @GET("/subjects/{subject}.json")
    suspend fun getBooksBySubject(
        @Path("subject") subject: String,
        @Query("page") page: Int? = null,
        @Query("details") details: Boolean = true
    ): Response<PopularBookResponse>

    @GET("/search.json")
    suspend fun getBookByTittle(
        @Query("title") title: String
    ): Response<SearchResponse>

}