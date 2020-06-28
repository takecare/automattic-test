package com.automattic.freshlypressed.domain

interface PostsRepository {
    suspend fun loadPosts(): Result<List<Post>>
}
