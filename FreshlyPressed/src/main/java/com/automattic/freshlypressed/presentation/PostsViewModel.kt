package com.automattic.freshlypressed.presentation

import android.net.Uri
import androidx.lifecycle.*
import com.automattic.freshlypressed.data.Result
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
import com.automattic.freshlypressed.domain.Site
import com.automattic.freshlypressed.domain.SiteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Effect<T>(
    private var isConsumed: Boolean = false,
    private val payload: T
) {
    constructor(payload: T) : this(false, payload)

    fun get() =
        if (isConsumed) {
            null
        } else {
            isConsumed = true
            payload
        }

    fun consume(block: (T) -> Unit) {
        get()?.let(block)
    }
}

sealed class PostEffects {
    data class NavigateToPost(val url: String) : PostEffects()
}

class PostsViewModel(
    private val handle: SavedStateHandle,
    private val postsRepository: PostsRepository,
    private val siteRepository: SiteRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>().apply {
        postValue(emptyList())
    }
    val posts: LiveData<List<Post>> get() = _posts

    private val _effect = MutableLiveData<Effect<PostEffects>>()
    val effects: LiveData<Effect<PostEffects>> get() = _effect

    fun loadData() {
        viewModelScope.launch(dispatcher) {
            val result = postsRepository.loadPosts()
            if (result is Result.Success) {
                _posts.postValue(result.content)
            } else {
                // TODO @RUI
            }
        }
    }

    fun loadCount(post: Post) {
        val url = Uri.parse(post.authorUrl).host
        if (post.hasSubscriberCount() || url == null) return

        viewModelScope.launch(dispatcher) {
            val result = siteRepository.getSite(url)
            if (result is Result.Success) {
                val site = result.content
                _posts.value
                    ?.map { item -> if (item == post) item.copy(subscriberCount = site.subscriberCount) else item }
                    ?.let { _posts.postValue(it) }
            } else {
                // TODO @RUI
            }
        }
    }

    fun postClicked(post: Post) {
        val navigation = PostEffects.NavigateToPost(post.uri.toString())
        _effect.postValue(Effect(navigation))
    }
}

interface ViewModelFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}
