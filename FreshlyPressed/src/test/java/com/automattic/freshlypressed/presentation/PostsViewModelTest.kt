package com.automattic.freshlypressed.presentation

import androidx.lifecycle.*
import com.automattic.freshlypressed.AsyncTest
import com.automattic.freshlypressed.Fixtures
import com.automattic.freshlypressed.domain.*
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class PostsViewModelTest : AsyncTest() {

    private val handle = SavedStateHandle()
    private val getPostsUseCase: GetPostsUseCase = mockk()
    private val updateSubscriberCountUseCase: UpdateSubscriberCountUseCase = mockk()
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main

    private val sut = PostsViewModel(handle, getPostsUseCase, updateSubscriberCountUseCase, dispatcher)

    @Test
    internal fun `viewmodel emits posts it fetches from the usecase`() {
        val values = sut.posts.toList()
        val slot = slot<GetPostsSuccessCallback>()
        coEvery { getPostsUseCase.execute(capture(slot), any()) } coAnswers {
            slot.captured.invoke(Fixtures.postList)
        }

        sut.loadData()

        assertThat(values).hasSize(1)
    }

    @Test
    internal fun `viewmodel emits error when it fails to fetch posts from the usecase`() {
        val values = sut.effects.toList()
        val slot = slot<GetPostsErrorCallback>()
        coEvery { getPostsUseCase.execute(any(), capture(slot)) } coAnswers {
            slot.captured.invoke(Throwable("boom!"))
        }

        sut.loadData()

        assertThat(values).hasSize(1)
        assertThat(values[0]).isEqualTo(PostEffects.NetworkError.asEffect())
    }

    @Test
    internal fun `viewmodel emits posts with count it fetches from the usecase`() {
        val values = sut.posts.toList()
        val postListSuccessSlot = slot<GetPostsSuccessCallback>()
        coEvery { getPostsUseCase.execute(capture(postListSuccessSlot), any()) } coAnswers {
            postListSuccessSlot.captured.invoke(Fixtures.postList)
        }
        val updateCountSuccessSlot = slot<UpdateCountSuccessCallback>()
        coEvery { updateSubscriberCountUseCase.execute(any(), any(), capture(updateCountSuccessSlot), any()) } coAnswers {
            updateCountSuccessSlot.captured.invoke(Fixtures.postList)
        }

        sut.loadData()
        sut.loadCount(Fixtures.postList[0])

        assertThat(values).hasSize(2)
    }

    @Test
    internal fun `viewmodel emits error when it fails to fetch site via the usecase`() {
        val values = sut.effects.toList()
        val postListSuccessSlot = slot<GetPostsSuccessCallback>()
        coEvery { getPostsUseCase.execute(capture(postListSuccessSlot), any()) } coAnswers {
            postListSuccessSlot.captured.invoke(Fixtures.postList)
        }
        val updateCountFailureSlot = slot<UpdateCountErrorCallback>()
        coEvery { updateSubscriberCountUseCase.execute(any(), any(), any(), capture(updateCountFailureSlot)) } coAnswers {
            updateCountFailureSlot.captured.invoke(Throwable("boom!"))
        }

        sut.loadCount(Fixtures.postList[0])

        assertThat(values).hasSize(1)
        assertThat(values[0]).isEqualTo(PostEffects.NetworkError.asEffect())
    }
}

fun <T> LiveData<T>.toList(): List<T> = mutableListOf<T>().also { observeForever { item -> it.add(item) } }

