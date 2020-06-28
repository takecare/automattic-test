package com.automattic.freshlypressed.domain

import com.automattic.freshlypressed.AsyncTest
import com.automattic.freshlypressed.Fixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class GetPostsUseCaseTest : AsyncTest() {

    private val postsRepository: PostsRepository = mockk()

    private val sut = GetPostsUseCase(postsRepository)

    @Test
    internal fun `when executing the use case it relies on the respository`() = testScope.runBlockingTest {
        coEvery { postsRepository.loadPosts() } returns Result.Success(Fixtures.postList)

        sut.execute({}, {})

        coVerify { postsRepository.loadPosts() }
    }

    @Test
    internal fun `when usecase executed successfully it calls success callback`() = testScope.runBlockingTest {
        coEvery { postsRepository.loadPosts() } returns Result.Success(Fixtures.postList)
        var successCalled = false
        var failureCalled = false

        sut.execute(
            { successCalled = true },
            { failureCalled = true }
        )

        assertTrue(successCalled)
        assertFalse(failureCalled)
    }

    @Test
    internal fun `when usecase executed unsuccessfully it calls failure callback`() = testScope.runBlockingTest {
        coEvery { postsRepository.loadPosts() } returns Result.Error(Throwable("boom!"))
        var successCalled = false
        var failureCalled = false

        sut.execute(
            { successCalled = true },
            { failureCalled = true }
        )

        assertFalse(successCalled)
        assertTrue(failureCalled)
    }
}
