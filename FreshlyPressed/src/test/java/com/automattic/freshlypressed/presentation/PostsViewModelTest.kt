package com.automattic.freshlypressed.presentation

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.*
import com.automattic.freshlypressed.Fixtures
import com.automattic.freshlypressed.domain.PostsRepository
import com.automattic.freshlypressed.domain.Result
import com.automattic.freshlypressed.domain.SiteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.*
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class PostsViewModelTest {

    companion object {
        private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
        private val scope: TestCoroutineScope = TestCoroutineScope(dispatcher)

        @JvmField
        @RegisterExtension
        val instantExecutorExtension = InstantExecutorExtension(dispatcher, scope)
    }

    private val handle = SavedStateHandle()
    private val postsRepository: PostsRepository = mockk()
    private val siteRepository: SiteRepository = mockk()
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main

    private val sut = PostsViewModel(handle, postsRepository, siteRepository, dispatcher)

    @Test
    internal fun `viewmodel emits posts it fetches from the repository`() = scope.runBlockingTest {
        val values = sut.posts.toList()
        coEvery { postsRepository.loadPosts() } returns Result.Success(Fixtures.postList)

        sut.loadData()

        assertThat(values).hasSize(1)
    }

    @Test
    internal fun `viewmodel emits error when it fails to fetch posts from the repository`() = scope.runBlockingTest {
        val values = sut.effects.toList()
        coEvery { postsRepository.loadPosts() } returns Result.Error(Throwable("boom!"))

        sut.loadData()

        assertThat(values).hasSize(1)
        assertThat(values[0]).isEqualTo(PostEffects.NetworkError.asEffect())
    }

    @Test
    internal fun `viewmodel emits posts with count it fetches from the repository`() = scope.runBlockingTest {
        val values = sut.posts.toList()
        coEvery { postsRepository.loadPosts() } returns Result.Success(Fixtures.postList)
        coEvery { siteRepository.getSite(any()) } returns Result.Success(Fixtures.site)

        sut.loadData()
        sut.loadCount(Fixtures.postList[0])

        assertThat(values).hasSize(2)
        assertThat(values.flatten().filter { it.subscriberCount == Fixtures.site.subscriberCount }).hasSize(1)
    }

    @Test
    internal fun `viewmodel emits error when it fails to fetch site from the repository`() = scope.runBlockingTest {
        val values = sut.effects.toList()
        coEvery { siteRepository.getSite(any()) } returns Result.Error(Throwable("boom!"))

        sut.loadCount(Fixtures.postList[0])

        assertThat(values).hasSize(1)
        assertThat(values[0]).isEqualTo(PostEffects.NetworkError.asEffect())
    }
}

fun <T> LiveData<T>.toList(): List<T> = mutableListOf<T>().also { observeForever { item -> it.add(item) } }

@ExperimentalCoroutinesApi
class InstantExecutorExtension(
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher(),
    private val scope: TestCoroutineScope = TestCoroutineScope(dispatcher)
) : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(extensionContext: ExtensionContext) {
        Dispatchers.setMain(object : CoroutineDispatcher() {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                dispatcher.dispatch(context, block)
            }
        })

        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread() = true
        })
    }

    override fun afterEach(context: ExtensionContext?) {
        dispatcher.cleanupTestCoroutines()
        scope.cleanupTestCoroutines()
        Dispatchers.resetMain()
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    fun runBlocking(block: suspend TestCoroutineScope.() -> Unit) = scope.runBlockingTest(block)
}
