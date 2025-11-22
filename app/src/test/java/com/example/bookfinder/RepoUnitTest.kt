package com.example.bookfinder

import com.example.bookfinder.data.ApiService
import com.example.bookfinder.data.local.dao.BookDao
import com.example.bookfinder.data.model.searchresponse.Doc
import com.example.bookfinder.data.model.searchresponse.SearchResponse
import com.example.bookfinder.data.model.subjectresponse.AuthorX
import com.example.bookfinder.data.model.subjectresponse.PopularBookResponse
import com.example.bookfinder.data.model.subjectresponse.Work
import com.example.bookfinder.data.repository.Repo
import com.example.bookfinder.data.repository.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RepoUnitTest {
    private val apiService = mockk<ApiService>()
    private val bookDao = mockk<BookDao>()
    private lateinit var repo: Repo

    @Before
    fun setUp() {
        repo = Repo(apiService,bookDao)
    }

    @Test
    fun `fetch search books from api service`() = runBlocking {
        val fakeBookList = listOf<Doc>(
            Doc(
                title = "Book 1",
                author_name = listOf("Author 1"),
                first_publish_year = 2000,
                cover_i = 123456
            ),
            Doc(
                title = "Book 2",
                author_name = listOf("Author 2"),
                first_publish_year = 1992,
                cover_i = 123535
            )
        )
        val fakeResponse = SearchResponse(docs = fakeBookList, numFound = 2, numFoundExact = true, q = "test", documentation_url = "", num_found = 2, start = 1)
        val fakeApiResponse = Response.success(fakeResponse)

        coEvery { apiService.searchBooks(query = "John", page = 1) } returns fakeApiResponse
        val result = repo.searchBooks(query = "John", page = 1)
        Assert.assertTrue(result is Result.Success)
        val successData = (result as Result.Success).data
        Assert.assertEquals(2, successData.docs.size)
        Assert.assertEquals("Book 1", successData.docs[0].title)
    }

    @Test
    fun `fetch popular books from api service`() = runBlocking {
        val fakePopularList = listOf<Work>(Work(
            title = "Book 1",
            authors = listOf(AuthorX(key = "key1", name = "Author 1")),
            first_publish_year = 2000,
            cover_id = 123456
        ),Work(
            title = "Book 2",
            authors = listOf(AuthorX(key = "key2", name = "Author 2")),
            first_publish_year = 2100,
            cover_id = 1238765
        ))
        val fakeResponse = PopularBookResponse(
            works = fakePopularList,
            authors = listOf(),
            ebook_count = 1,
            key = "",
            languages = listOf(),
            name = "",
            people = listOf(),
            places = listOf(),
            publishers = listOf(),
            publishing_history = listOf(),
            solr_query = "",
            subject_type = "",
            subjects = listOf(),
            times = listOf(),
            work_count = 1
        )
        val fakeApiResponse = Response.success(fakeResponse)

        coEvery { apiService.getBooksBySubject("subject", page = 2) } returns fakeApiResponse
        val result = repo.getPopularBooks("subject", page = 2)
        Assert.assertTrue(result is Result.Success)
        val successData = (result as Result.Success).data
        Assert.assertEquals(2, successData.works.size)
        Assert.assertEquals("2000", successData.works[0].first_publish_year.toString())
    }
}
