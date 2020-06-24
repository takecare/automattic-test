package com.automattic.freshlypressed.presentation

import androidx.lifecycle.SavedStateHandle
import com.automattic.freshlypressed.domain.PostsRepository

class PostsViewModelFactory(
    private val repository: PostsRepository
) : ViewModelFactory<PostsViewModel> {
    override fun create(handle: SavedStateHandle) = PostsViewModel(handle, repository)
}
