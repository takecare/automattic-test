package com.automattic.freshlypressed.data

import android.net.Uri
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
import org.json.JSONArray
import java.util.*

class WordpressPostsRepository(
    private val service: PostsService
) : PostsRepository {

    override fun loadSubscribersCount(url: String): Int {
        return -1
    }

    override fun old_loadPosts() = JSONArray()

    override suspend fun loadPosts(): List<Post> {
        val data = service.getPosts()

        return data.map { Post(
            title = it.title,
            excerpt = it.excerpt,
            author = it.author,
            imageUrl = it.imageUrl,
            date = Date(),
            authorUrl = it.authorUrl,
            uri = Uri.parse(it.uri)
        ) }
    }
}
