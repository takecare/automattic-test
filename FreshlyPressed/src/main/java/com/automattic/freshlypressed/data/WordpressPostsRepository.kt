package com.automattic.freshlypressed.data

import android.net.Uri
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
import org.json.JSONArray
import java.util.*

class WordpressPostsRepository(
    private val service: PostsService,
    private val mapper: Mapper
) : PostsRepository {

    override fun loadSubscribersCount(url: String): Int {
        return -1
    }

    override fun old_loadPosts() = JSONArray()

    override suspend fun loadPosts(): List<Post> {
        val data = service.getPosts()
        return data.posts.map(mapper::map)
    }
}

interface Mapper {
    // map postdata to postdomain
    fun map(data: PostData): Post
}

class PostMapper(
    val mapDate: (String) -> Date
) : Mapper {
    override fun map(data: PostData) =
        Post(
            title = data.title,
            excerpt = data.excerpt,
            author = data.author.niceName,
            imageUrl = data.imageUrl,
            date = mapDate(data.date),
            authorUrl = data.author.profileUrl,
            uri = Uri.parse(data.url)
        )
}
