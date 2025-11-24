package com.example.bookfinder.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.bookfinder.MainViewModel
import com.example.bookfinder.R
import com.example.bookfinder.data.model.searchresponse.Doc
import com.example.bookfinder.ui.navigation.DetailScreen
import com.example.bookfinder.ui.navigation.LocalDetailScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

@Composable
fun SearchScreenUI(viewModel: MainViewModel = hiltViewModel(),navController: NavController) {
    val query by viewModel.query.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.localEvent.collectLatest {
            when (it) {
                is MainViewModel.LocalEvent.OnBookDelete -> {
                    scope.launch {
                        snackState.showSnackbar("Removed")
                    }
                }
                is MainViewModel.LocalEvent.ShowError -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val lazyPagingItems: LazyPagingItems<Doc> = viewModel.bookPagingDataFLow.collectAsLazyPagingItems()
    Box{
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp), verticalArrangement = Arrangement.Top) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.onQueryChange(it) },
            placeholder = { Text("Search by title, author...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            shape = CircleShape,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

            if (query.isBlank()){
                    when(val state = uiState){
                        is MainViewModel.LocalState.Empty ->{
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("No Recently Viewed Books Yet")
                                }
                            }
                        }
                        is MainViewModel.LocalState.Content -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                item {
                                    Text("Recently Viewed Books")
                                }
                                items(state.books.size, key = { state.books[it].id }){ index ->
                                    val book = state.books[index]
                                    BookList(Doc(
                                        title = book.title,
                                        author_name = listOf(book.author),
                                        first_publish_year = book.year.toIntOrNull()
                                    ), onClick = {
                                        navController.navigate(LocalDetailScreen(tittle = book.title))
                                    }, imageUrl = book.imageUrl, showDeleteButton = true, onDelete = {
                                        viewModel.deleteBook(book)
                                    })
                                }
                            }
                        }
                        is MainViewModel.LocalState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(state.message)
                                }
                            }
                        }
                        is MainViewModel.LocalState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
            }else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                items(lazyPagingItems.itemCount, key = { index ->
                    lazyPagingItems.peek(index)?.key ?: index
                }) { index ->
                    val book = lazyPagingItems[index]
                    if (book != null) {
                        BookList(books = book, onClick = {
                            navController.navigate(DetailScreen(tittle = book.title!!))
                        })
                    }
                }

                lazyPagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        loadState.isIdle -> {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No Search Results")
                                }
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Not Found")
                                }
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Not Found")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    SnackbarHost(hostState = snackState){
        Snackbar(modifier = Modifier.height(25.dp),containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Text(
                text = "Book removed.", style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Normal), modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } }
}


@Composable
fun BookList(books: Doc, imageUrl: String?=null, showDeleteButton: Boolean = false, onDelete: () -> Unit = {}, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick.invoke() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if(imageUrl.isNullOrEmpty()) "https://covers.openlibrary.org/b/id/${books.cover_i}-M.jpg" else imageUrl ,
                contentDescription = books.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(90.dp)
                    .width(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = painterResource(id = R.drawable.file),
                error = painterResource(R.drawable.file)
            )
            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = books.title ?: "No title available",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = books.author_name.firstOrNull() ?: "Unknown Author",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))

                Text(
                    text = books.first_publish_year?.toString() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (showDeleteButton){
                IconButton(onClick =  onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}