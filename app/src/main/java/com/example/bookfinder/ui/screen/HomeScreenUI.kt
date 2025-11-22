package com.example.bookfinder.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.bookfinder.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreenUI(viewModel: MainViewModel,navController: NavController) {
    val listOfTabs = listOf("Search Books", "Popular Books")
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { listOfTabs.size }, initialPage = 0)

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            listOfTabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when(page) {
                0 -> SearchScreenUI(navController = navController)
                1 -> PopularScreenUI(viewModel, navController = navController)
            }
        }
    }
}