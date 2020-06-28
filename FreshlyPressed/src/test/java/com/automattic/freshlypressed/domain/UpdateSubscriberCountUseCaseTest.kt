package com.automattic.freshlypressed.domain

import com.automattic.freshlypressed.AsyncTest
import com.automattic.freshlypressed.Fixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class UpdateSubscriberCountUseCaseTest : AsyncTest() {

    private val siteRepository: SiteRepository = mockk()

    private val sut = UpdateSubscriberCountUseCase(siteRepository)

    @Test
    internal fun `when executing the use case it relies on the respository`() = testScope.runBlockingTest {
        coEvery { siteRepository.getSite(any()) } returns Result.Success(Fixtures.site)

        sut.execute(Fixtures.postList[0], Fixtures.postList, {}, {})

        coVerify { siteRepository.getSite(any()) }
    }

    @Test
    internal fun `when usecase executed successfully it calls success callback`() = testScope.runBlockingTest {
        coEvery { siteRepository.getSite(any()) } returns Result.Success(Fixtures.site)
        var successCalled = false
        var failureCalled = false

        sut.execute(Fixtures.postList[0], Fixtures.postList, { successCalled = true }, { failureCalled = true })

        assertTrue(successCalled)
        assertFalse(failureCalled)
    }

    @Test
    internal fun `usecase updates post with subscriber count`() = testScope.runBlockingTest {
        var posts: List<Post> = emptyList()
        coEvery { siteRepository.getSite(any()) } returns Result.Success(Fixtures.site)

        sut.execute(Fixtures.postList[0], Fixtures.postList, { posts = it }, {})

        assertThat(posts.filter { it.subscriberCount == Fixtures.site.subscriberCount }).hasSize(1)
    }

    @Test
    internal fun `when usecase executed unsuccessfully it calls failure callback`() = testScope.runBlockingTest {
        coEvery { siteRepository.getSite(any()) } returns Result.Error(Throwable("boom!"))
        var successCalled = false
        var failureCalled = false

        sut.execute(Fixtures.postList[0], Fixtures.postList, { successCalled = true }, { failureCalled = true })

        assertFalse(successCalled)
        assertTrue(failureCalled)
    }
}
