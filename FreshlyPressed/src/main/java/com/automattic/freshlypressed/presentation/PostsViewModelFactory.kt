package com.automattic.freshlypressed.presentation

import androidx.lifecycle.SavedStateHandle
import com.automattic.freshlypressed.domain.GetPostsUseCase
import com.automattic.freshlypressed.domain.UpdateSubscriberCountUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PostsViewModelFactory @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val updateSubscriberCountUseCase: UpdateSubscriberCountUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModelFactory<PostsViewModel> {
    override fun create(handle: SavedStateHandle) = PostsViewModel(handle, getPostsUseCase, updateSubscriberCountUseCase, dispatcher)
}
