package com.example.bookfinder.data.model.subjectresponse

data class Work(
    val authors: List<AuthorX?> = emptyList(),
    val availability: Availability?=null,
    val cover_edition_key: String?=null,
    val cover_id: Int?=null,
    val edition_count: Int?=null,
    val first_publish_year: Int?=null,
    val has_fulltext: Boolean?=null,
    val ia: String?=null,
    val ia_collection: List<String?> = emptyList(),
    val key: String?=null,
    val lending_edition: String?=null,
    val lending_identifier: String?=null,
    val printdisabled: Boolean?=null,
    val public_scan: Boolean?=null,
    val subject: List<String?> = emptyList(),
    val title: String?=null
)