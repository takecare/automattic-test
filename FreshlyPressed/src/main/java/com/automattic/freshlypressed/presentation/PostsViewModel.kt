package com.automattic.freshlypressed.presentation

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.automattic.freshlypressed.domain.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class PostsViewModel @ViewModelInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val getPostsUseCase: GetPostsUseCase,
    private val updateSubscriberCountUseCase: UpdateSubscriberCountUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _effect = MutableLiveData<Effect<PostEffects>>()
    val effects: LiveData<Effect<PostEffects>> get() = _effect

    fun loadData() {
        viewModelScope.launch(dispatcher) {
            getPostsUseCase.execute(
                { _posts.postValue(it) },
                { _effect.postValue(PostEffects.NetworkError.asEffect()) }
            )
        }
    }

    fun loadCount(post: Post) {
        val posts = _posts.value
        viewModelScope.launch(dispatcher) {
            updateSubscriberCountUseCase.execute(
                post,
                posts,
                { _posts.postValue(it) },
                { _effect.postValue(PostEffects.NetworkError.asEffect()) })
        }
    }

    fun postClicked(post: Post) {
        val navigation = PostEffects.NavigateToPost(post.uri)
        _effect.postValue(Effect(navigation))
    }
}

sealed class PostEffects {
    data class NavigateToPost(val url: String) : PostEffects()
    object NetworkError : PostEffects()

    fun asEffect() = Effect(this)
}
