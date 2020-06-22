package com.automattic.freshlypressed.domain

import org.json.JSONArray

interface PostsRepository {

    fun loadSubscribersCount(url: String): Int

    fun loadPosts(): JSONArray // TODO should be a model @RUI
}
