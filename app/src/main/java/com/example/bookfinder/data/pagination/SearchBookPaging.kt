package com.example.bookfinder.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookfinder.data.model.searchresponse.Doc
import com.example.bookfinder.data.model.searchresponse.SearchResponse
import com.example.bookfinder.data.repository.Repo
import com.example.bookfinder.data.repository.Result

const val STARTING_PAGE_INDEX = 1

class SearchBookPaging(private val repo: Repo, val query: String): PagingSource<Int, Doc>() {
    override fun getRefreshKey(state: PagingState<Int, Doc>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Doc> {
        val currentPage = params.key ?: STARTING_PAGE_INDEX
        if (query.isBlank()) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        return try {
            val result = repo.searchBooks(query, currentPage)

            when(result){
                is Result.Success -> {
                    val data = result.data.docs
                    val endOfPaginationReached = data.isEmpty()

                    LoadResult.Page(
                        data = data,
                        prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1,
                        nextKey = if (endOfPaginationReached) null else currentPage + 1
                    )
                }
                is Result.Error -> {
                    LoadResult.Error(Exception(result.message))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}