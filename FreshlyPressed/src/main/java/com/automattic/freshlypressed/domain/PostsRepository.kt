package com.automattic.freshlypressed.domain

import org.json.JSONArray

interface PostsRepository {

    fun loadSubscribersCount(url: String): Int

    fun old_loadPosts(): JSONArray

    suspend fun loadPosts(): Result<List<Post>>
}
