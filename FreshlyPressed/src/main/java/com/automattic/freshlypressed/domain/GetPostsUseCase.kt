package com.automattic.freshlypressed.domain

import javax.inject.Inject

typealias GetPostsSuccessCallback = (List<Post>) -> Unit
typealias GetPostsErrorCallback = (Throwable) -> Unit

class GetPostsUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {

    suspend fun execute(success: GetPostsSuccessCallback, failure: GetPostsErrorCallback) {
        val result = postsRepository.loadPosts()
        when (result) {
            is Result.Success -> success(result.content)
            is Result.Error -> failure(result.error)
        }
    }
}
