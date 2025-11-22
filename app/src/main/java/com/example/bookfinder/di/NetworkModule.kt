package com.example.bookfinder.di

import android.app.Application
import androidx.room.Room
import com.example.bookfinder.data.ApiService
import com.example.bookfinder.data.local.dao.BookDao
import com.example.bookfinder.data.local.database.BookDatabase
import com.example.bookfinder.data.repository.Repo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://openlibrary.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepo(apiService: ApiService,bookDao: BookDao): Repo{
        return Repo(apiService, bookDao = bookDao)
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application): BookDatabase{
        return Room
            .databaseBuilder(application, BookDatabase::class.java, "book_database")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(bookDatabase: BookDatabase) = bookDatabase.bookDao()
}