package com.example.bookfinder.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookfinder.MainViewModel
import com.example.bookfinder.R
import com.example.bookfinder.data.local.table.BookEntity
import com.example.bookfinder.data.model.searchresponse.Doc

@Composable
fun LocalDetailScreenUI(title: String, viewModel: MainViewModel, navController: NavController) {
    val state by viewModel.localBookDetail.collectAsState()
    LaunchedEffect(title) {
        viewModel.getBookDetailByTitle(title)
    }
    if (state == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            TopSection(
                imageUrl = state?.imageUrl ?: "",
                onBackClicked = { navController.popBackStack() }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp)
                    .padding(horizontal = 24.dp)
            ) {Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    DetailRow(
                        icon = Icons.Default.Person,
                        label = "AUTHOR",
                        value = state?.author ?: "N/A"
                    )
                    Divider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.Gray.copy(alpha = 0.2f))
                    DetailRow(
                        icon = Icons.Default.Book,
                        label = "TITLE",
                        value = state?.title ?: "N/A"
                    )
                    Divider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.Gray.copy(alpha = 0.2f))
                    DetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "FIRST PUBLISHED",
                        value = state?.year ?: "N/A"
                    )
                    Divider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.Gray.copy(alpha = 0.2f))
                    DetailRow(
                        icon = Icons.Default.Language,
                        label = "LANGUAGE",
                        value = state?.language ?: "N/A"
                    )
                }
            }
            }
        }
    }
}

@Composable
private fun TopSection(imageUrl: String, onBackClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF86C5D8),
                        Color(0xFFC5E3EE)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF86C5D8)
            )
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3F2F7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF86C5D8),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DetailScreenUI(title: String, viewModel: MainViewModel, navController: NavController) {
    val state by viewModel.bookDetailState.collectAsState()

    LaunchedEffect(title) {
        viewModel.getBookByTitle(title)
    }

    when (val currentState = state) {
        is MainViewModel.SearchState.Loading, MainViewModel.SearchState.Idle -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is MainViewModel.SearchState.Success -> {
            currentState.books.docs.firstOrNull()?.let { book ->
                LaunchedEffect(book.title) {
                    viewModel.saveBook(BookEntity(
                        title = book.title ?: "N/A",
                        author = book.author_name.firstOrNull() ?: "N/A",
                        language = book.language.firstOrNull() ?: "N/A",
                        year = book.first_publish_year.toString(),
                        imageUrl = book.cover_i?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" } ?: "N/A"
                    ))
                }
                DetailScreenContent(book = book, onBackClicked = { navController.popBackStack() })
            } ?: Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F3ED)),
                contentAlignment = Alignment.Center
            ) {
                Text("Book not found.")
            }
        }

        is MainViewModel.SearchState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.error), contentDescription = "Error", modifier = Modifier.size(150.dp))
                    Text(text = currentState.message)
                    Button(onClick = {
                        viewModel.getBookByTitle(title)
                    }) {
                        Text(text = "Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailScreenContent(book: Doc, onBackClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageUrl = "https://covers.openlibrary.org/b/id/${book.cover_i}-L.jpg"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Blurred Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent),
                            startY = 0f,
                            endY = 400f
                        )
                    )
            )

            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF86C5D8)
                )
            }
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = book.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(250.dp)
                .offset(y = (-150).dp)
                .clip(RoundedCornerShape(12.dp))
        )


        Text(
            text = book.title ?: "No Title",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(y = (-130).dp)
                .padding(horizontal = 24.dp)
        )

        Text(
            text = book.author_name?.firstOrNull() ?: "Unknown Author",
            fontSize = 18.sp,
            modifier = Modifier.offset(y = (-130).dp)
        )

        Text(book.language.firstOrNull()?.uppercase() ?: "N/A",fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            modifier = Modifier.offset(y = (-130).dp))
        Text(
            text = book.first_publish_year?.toString() ?: "",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.offset(y = (-130).dp)
        )
    }
}