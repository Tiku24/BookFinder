package com.example.bookfinder.ui.navigation

import kotlinx.serialization.Serializable

interface Routes {
}

@Serializable
object HomeScreen : Routes

@Serializable
object PopularScreen : Routes

@Serializable
object SearchScreen : Routes

@Serializable
data class DetailScreen(val tittle: String) : Routes

@Serializable
data class LocalDetailScreen(val tittle: String) : Routes