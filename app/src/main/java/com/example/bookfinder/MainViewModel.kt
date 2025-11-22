package com.example.bookfinder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookfinder.data.local.table.BookEntity
import com.example.bookfinder.data.model.searchresponse.Doc
import com.example.bookfinder.data.model.searchresponse.SearchResponse
import com.example.bookfinder.data.model.subjectresponse.PopularBookResponse
import com.example.bookfinder.data.model.subjectresponse.Work
import com.example.bookfinder.data.pagination.PopularBookPaging
import com.example.bookfinder.data.repository.Repo
import com.example.bookfinder.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.addAll

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: Repo): ViewModel() {

    private val _bookDetailState = MutableStateFlow<SearchState>(SearchState.Idle)
    val bookDetailState = _bookDetailState.asStateFlow()

    private val _uiState: MutableStateFlow<LocalState> = MutableStateFlow(LocalState.Loading)
    val uiState: StateFlow<LocalState> = _uiState.asStateFlow()

    private val _localDetail = MutableStateFlow<LocalDetailState>(LocalDetailState.Loading)
    val localDetail = _localDetail.asStateFlow()

    val getSavedBook = MutableStateFlow<List<BookEntity>>(emptyList())
    private val _localBookDetail = MutableStateFlow<BookEntity?>(null)
    val localBookDetail = _localBookDetail.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    fun onQueryChange(query: String) {
        _query.value = query
    }

    init {
        getSavedBook()
        getLocalCachedBooks()
    }


    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val bookPagingDataFLow: Flow<PagingData<Doc>> = _query
        .debounce(500L)
        .flatMapLatest { query ->
            if (query.isNotBlank()){
                repo.getBookSearchPage(query)
            }else{
                emptyFlow()
            }
        }
        .cachedIn(viewModelScope)

    val popularBookPager = Pager(
        config = PagingConfig(pageSize = 1, prefetchDistance = 2),
        pagingSourceFactory = { PopularBookPaging(repo) }
    ).flow.cachedIn(viewModelScope)

    fun getBookByTitle(title: String){
        viewModelScope.launch {
            val res = repo.getBookByTittle(title)
            when(res){
                is Result.Success ->{
                    _bookDetailState.value = SearchState.Success(res.data)
                    Log.d("title", "getBookByTitle: ${res.data}")
                }
                is Result.Error ->{
                    _bookDetailState.value = SearchState.Error(res.message)
                    Log.d("title", "getBookByTitle: ${res.message}")
                }
            }
        }
    }

    fun saveBook(bookEntity: BookEntity){
        viewModelScope.launch(Dispatchers.IO) {
            val isAlreadyViewed = repo.isBookVisited(bookEntity.title).first()

            if (!isAlreadyViewed){
                val book = BookEntity(
                    title = bookEntity.title,
                    author = bookEntity.author,
                    year = bookEntity.year,
                    language = bookEntity.language,
                    isViewed = true,
                    imageUrl = bookEntity.imageUrl
                )
                repo.saveBook(book)
            }
        }
    }

    fun getSavedBook() = viewModelScope.launch(Dispatchers.IO) {
        repo.getSavedBooks().collectLatest{
            getSavedBook.value = it
            Log.d("saved", "getSavedBook: $it")
        }
    }

    fun getLocalCachedBooks(){
        viewModelScope.launch {
            repo.getSavedBooks()
                .map { book->
                    if (book.isEmpty())
                        LocalState.Empty
                    else
                        LocalState.Content(book)
                }
                .onStart {
                    delay(1000)
                    emit(LocalState.Loading) }
                .catch { throwable->
                    emit(LocalState.Error(throwable.message ?: "Failed to load books"))
                }
                .collect { newSate -> _uiState.value = newSate }
        }
    }

    fun deleteBook(bookEntity: BookEntity) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteBook(bookEntity)
    }

    fun getBookDetailByTitle(title: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getBookDetailByTittle(title)
                .map { book ->
                    if (book == null)
                        LocalDetailState.Empty
                    else
                        LocalDetailState.Content(book)
                }
                .onStart { delay(500)
                    emit(LocalDetailState.Loading) }
                .catch { throwable ->
                    emit(LocalDetailState.Error(throwable.message ?: "Failed to load details"))
                }
                .collect { newSate -> _localDetail.value = newSate
                    Log.d("localDetail", "getBookByTitles: $newSate")
                }
        }
    }


    sealed class SearchState{
        object Idle: SearchState()
        object Loading: SearchState()
        data class Success(val books: SearchResponse): SearchState()
        data class Error(val message: String): SearchState()
    }

    sealed class PopularState{
        object Idle: PopularState()
        object Loading: PopularState()
        data class Success(val books: PopularBookResponse): PopularState()
        data class Error(val message: String): PopularState()
    }

    sealed class LocalState{
        data object Loading : LocalState()
        data class Content(val books: List<BookEntity>) : LocalState()
        data object Empty : LocalState()
        data class Error(val message: String) : LocalState()
    }

    sealed class LocalDetailState{
        data object Loading : LocalDetailState()
        data class Content(val books: BookEntity) : LocalDetailState()
        data object Empty : LocalDetailState()
        data class Error(val message: String) : LocalDetailState()
    }
}