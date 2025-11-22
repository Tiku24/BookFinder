package com.example.bookfinder.data.model.searchresponse

data class Doc(
    val author_key: List<String?> = emptyList(),
    val author_name: List<String?> = emptyList(),
    val cover_edition_key: String? = null,
    val cover_i: Int? = null,
    val ebook_access: String? = null,
    val edition_count: Int? = null,
    val first_publish_year: Int? = null,
    val has_fulltext: Boolean? = null,
    val ia: List<String?> ? = emptyList(),
    val ia_collection_s: String? = null,
    val id_librivox: List<String?> = emptyList(),
    val id_project_gutenberg: List<String?> = emptyList(),
    val id_standard_ebooks: List<String?> = emptyList(),
    val key: String? = null,
    val language: List<String?> = emptyList(),
    val lending_edition_s: String? = null,
    val lending_identifier_s: String? = null,
    val public_scan_b: Boolean? = null,
    val subtitle: String? = null,
    val title: String? = null
)