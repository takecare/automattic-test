package com.automattic.freshlypressed.presentation

import androidx.lifecycle.SavedStateHandle
import com.automattic.freshlypressed.domain.PostsRepository
import com.automattic.freshlypressed.domain.SiteRepository

class PostsViewModelFactory(
    private val postsRepository: PostsRepository,
    private val siteRepository: SiteRepository
) : ViewModelFactory<PostsViewModel> {
    override fun create(handle: SavedStateHandle) = PostsViewModel(handle, postsRepository, siteRepository)
}
