package com.automattic.freshlypressed.domain

import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {

    suspend fun execute(success: (List<Post>) -> Unit, failure: (Throwable) -> Unit) {
        val result = postsRepository.loadPosts()
        when (result) {
            is Result.Success -> success(result.content)
            is Result.Error -> failure(result.error)
        }
    }
}
