package com.automattic.freshlypressed.presentation

import androidx.lifecycle.*
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
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
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>().apply {
        postValue(emptyList())
    }
    val posts: LiveData<List<Post>> get() = _posts

    private val _effect = MutableLiveData<Effect<PostEffects>>()
    val effects: LiveData<Effect<PostEffects>> get() = _effect

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = postsRepository.loadPosts()
            _posts.postValue(posts)
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
