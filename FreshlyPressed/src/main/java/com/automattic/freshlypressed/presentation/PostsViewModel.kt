package com.automattic.freshlypressed.presentation

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.automattic.freshlypressed.data.PostsApi
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PostsViewModel(
    private val handle: SavedStateHandle,
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>().apply {
        postValue(emptyList())
    }
    val posts: LiveData<List<Post>> get() = _posts

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = postsRepository.loadPosts()
            _posts.postValue(posts)
        }
    }
}

interface ViewModelFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}

class PostsViewModelFactory(
    private val repository: PostsRepository
) : ViewModelFactory<PostsViewModel> {
    override fun create(handle: SavedStateHandle) = PostsViewModel(handle, repository)
}

class GenericSavedStateViewModelFactory<T : ViewModel>(
    private val viewModelFactory: ViewModelFactory<T>,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            viewModelFactory.create(handle)
        } else {
            throw IllegalStateException("")
        } as T
    }
}
