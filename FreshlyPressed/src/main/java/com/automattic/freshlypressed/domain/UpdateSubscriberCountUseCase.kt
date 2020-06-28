package com.automattic.freshlypressed.domain

import javax.inject.Inject

class UpdateSubscriberCountUseCase @Inject constructor(
    private val siteRepository: SiteRepository
) {

    suspend fun execute(post: Post, posts: List<Post>?, success: (List<Post>) -> Unit, failure: (Throwable) -> Unit) {
        if (post.hasSubscriberCount() || posts == null || post.authorHost.isBlank()) return

        val result = siteRepository.getSite(post.authorHost)
        when (result) {
            is Result.Success -> {
                val site = result.content
                val updatedPosts = posts.map { item -> if (item == post) item.copy(subscriberCount = site.subscriberCount) else item }
                success(updatedPosts)
            }
            is Result.Error -> failure(result.error)
        }
    }
}
