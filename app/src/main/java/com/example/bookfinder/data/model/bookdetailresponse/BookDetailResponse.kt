package com.example.bookfinder.data.model.bookdetailresponse

data class BookDetailResponse(
    val docs: List<Doc>,
    val documentation_url: String,
    val numFound: Int,
    val numFoundExact: Boolean,
    val num_found: Int,
    val q: String,
    val start: Int
)