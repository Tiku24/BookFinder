package com.example.bookfinder.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.bookfinder.MainViewModel
import com.example.bookfinder.data.model.searchresponse.Doc
import com.example.bookfinder.data.model.subjectresponse.Work
import com.example.bookfinder.ui.navigation.DetailScreen

@Composable
fun PopularScreenUI(viewModel: MainViewModel,navController: NavController) {
    val lazyPagingItems: LazyPagingItems<Work> = viewModel.popularBookPager.collectAsLazyPagingItems()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        items(lazyPagingItems.itemCount, key = { index ->
            lazyPagingItems.peek(index)?.key ?: index
        }){ index ->
            val book = lazyPagingItems[index]
            if (book != null) {
                BookList(Doc(
                    title = book.title,
                    author_name = book.authors.map { it?.name },
                    first_publish_year = book.first_publish_year,
                    key = book.key,
                    cover_i = book.cover_id,
                ), onClick = {
                    navController.navigate(DetailScreen(tittle = book.title!!))
                })
            }
        }

        lazyPagingItems.apply {
            when{
                loadState.refresh is LoadState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                loadState.prepend is LoadState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Not Found")
                        }
                    }
                }
            }
        }
    }
}