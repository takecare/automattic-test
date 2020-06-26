package com.automattic.freshlypressed.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.automattic.freshlypressed.domain.Result
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
import com.automattic.freshlypressed.domain.SiteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostsViewModel(
    private val handle: SavedStateHandle,
    private val postsRepository: PostsRepository,
    private val siteRepository: SiteRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>().apply { postValue(emptyList()) }
    val posts: LiveData<List<Post>> get() = _posts

    private val _position = MutableLiveData<Int>().apply { postValue(0) }
    val position: LiveData<Int> get() = _position

    private val _effect = MutableLiveData<Effect<PostEffects>>()
    val effects: LiveData<Effect<PostEffects>> get() = _effect

    fun loadData() {
        viewModelScope.launch(dispatcher) {
            val result = postsRepository.loadPosts()
            if (result is Result.Success) {
                _posts.postValue(result.content)
                _position.postValue(handle.get("position") ?: 0)
            } else {
                _effect.postValue(PostEffects.NetworkError.asEffect())
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
                _effect.postValue(PostEffects.NetworkError.asEffect())
            }
        }
    }

    fun postClicked(post: Post) {
        val navigation = PostEffects.NavigateToPost(post.uri.toString())
        _effect.postValue(Effect(navigation))
    }

    fun save(position: Int) {
        handle.set("position", position)
        Log.d("RUI", "save> ${handle.get("position") ?: -1}")
    }
}

sealed class PostEffects {
    data class NavigateToPost(val url: String) : PostEffects()
    object NetworkError : PostEffects()

    fun asEffect() = Effect(this)
}
