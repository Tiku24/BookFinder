package com.example.bookfinder.data.model.searchresponse

data class SearchResponse(
    val docs: List<Doc>,
    val documentation_url: String,
    val numFound: Int,
    val numFoundExact: Boolean,
    val num_found: Int,
    val q: String,
    val start: Int
)