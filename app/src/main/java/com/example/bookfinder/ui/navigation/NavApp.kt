package com.example.bookfinder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bookfinder.MainViewModel
import com.example.bookfinder.ui.screen.LocalDetailScreenUI
import com.example.bookfinder.ui.screen.DetailScreenUI
import com.example.bookfinder.ui.screen.HomeScreenUI
import com.example.bookfinder.ui.screen.PopularScreenUI
import com.example.bookfinder.ui.screen.SearchScreenUI

@Composable
fun NavApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = HomeScreen, modifier = modifier) {
        composable<HomeScreen>{
            HomeScreenUI(viewModel = viewModel, navController = navController)
        }
        composable<SearchScreen>{
            SearchScreenUI(navController = navController)
        }
        composable<PopularScreen> {
            PopularScreenUI(viewModel = viewModel, navController = navController)
        }
        composable<DetailScreen> {
            val title = it.toRoute<DetailScreen>().tittle
            DetailScreenUI(title = title, viewModel = viewModel, navController = navController)
        }
        composable<LocalDetailScreen> {
            val title = it.toRoute<LocalDetailScreen>().tittle
            LocalDetailScreenUI(title = title, viewModel = viewModel, navController = navController)
        }
    }
}