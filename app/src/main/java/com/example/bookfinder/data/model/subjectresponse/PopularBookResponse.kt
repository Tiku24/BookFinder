package com.example.bookfinder.data.model.subjectresponse

data class PopularBookResponse(
    val authors: List<Author>,
    val ebook_count: Int,
    val key: String,
    val languages: List<Language>,
    val name: String,
    val people: List<People>,
    val places: List<Place>,
    val publishers: List<Publisher>,
    val publishing_history: List<List<Int>>,
    val solr_query: String,
    val subject_type: String,
    val subjects: List<Subject>,
    val times: List<Time>,
    val work_count: Int,
    val works: List<Work>
)