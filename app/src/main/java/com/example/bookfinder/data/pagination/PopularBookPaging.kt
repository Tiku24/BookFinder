package com.example.bookfinder.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookfinder.data.model.subjectresponse.Work
import com.example.bookfinder.data.repository.Repo
import com.example.bookfinder.data.repository.Result
import javax.inject.Inject

class PopularBookPaging @Inject constructor(private val repo: Repo): PagingSource<Int, Work>() {
    override fun getRefreshKey(state: PagingState<Int, Work>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Work> {
       return try {
           val prev = params.key ?: 0
           val res = repo.getPopularBooks(subject = "fiction", page = prev)
           when(res){
               is Result.Success -> {
                   val data = res.data.works
                   LoadResult.Page(
                       data = data,
                       prevKey = if (prev == 0) null else prev - 1,
                       nextKey = if (data.size < params.loadSize) null else prev + 1
                   )
               }
               is Result.Error -> {
                   LoadResult.Error(Exception(res.message))
               }
           }
        }catch (e: Exception){
            return LoadResult.Error(e)
        }
    }
}