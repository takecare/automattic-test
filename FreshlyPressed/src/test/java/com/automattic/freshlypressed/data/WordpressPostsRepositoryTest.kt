package com.automattic.freshlypressed.data

import com.automattic.freshlypressed.Fixtures
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.IOException

internal class WordpressPostsRepositoryTest {

    private val service: PostsService = mockk()
    private val mapper: PostMapper = mockk()

    private val sut = WordpressPostsRepository(service, mapper)

    @Test
    fun `when requesting posts from the repository, is requests posts from the service`() {
        val mappedPosts = Fixtures.postList
        coEvery { service.getPosts(any()) } returns Fixtures.posts
        every { mapper.map(any()) } returns mappedPosts[0]

        runBlocking { sut.loadPosts() }

        coVerify { service.getPosts(any()) }
    }

    @Test
    fun `given service returns posts, when requesting posts from the repository, it maps them with the post mapper`() {
        val mappedPosts = Fixtures.postList
        coEvery { service.getPosts(any()) } returns Fixtures.posts
        every { mapper.map(any()) } returns mappedPosts[0]

        runBlocking { sut.loadPosts() }

        coVerify(exactly = mappedPosts.size) { mapper.map(any()) }
    }

    @Test
    fun `given service returns posts, when requesting posts from the repository, it returns a successful result`() {
        val mappedPosts = Fixtures.postList
        coEvery { service.getPosts(any()) } returns Fixtures.posts
        every { mapper.map(any()) } returns mappedPosts[0]

        val response = runBlocking { sut.loadPosts() }

        assertThat(response is Result.Success).isTrue()
    }

    @Test
    fun `given service throws exception, when requesting posts from the repository, it returns an error result`() {
        coEvery { service.getPosts(any()) } throws IOException("boom!")

        val response = runBlocking { sut.loadPosts() }

        assertThat(response).isInstanceOf(Result.Error::class.java)
    }
}
