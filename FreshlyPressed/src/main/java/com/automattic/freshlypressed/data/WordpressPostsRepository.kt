package com.automattic.freshlypressed.data

import android.net.Uri
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
import org.json.JSONArray
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WordpressPostsRepository(
    private val service: PostsService,
    private val mapper: PostMapper
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

interface PostMapper {
    fun map(data: PostData): Post
}

class PostMapperImpl(
    private val dateMapper: DateMapper
) : PostMapper {
    override fun map(data: PostData) =
        Post(
            title = data.title,
            excerpt = data.excerpt,
            author = data.author.niceName,
            imageUrl = data.imageUrl,
            date = dateMapper.map(data.date),
            authorUrl = data.author.profileUrl,
            uri = Uri.parse(data.url)
        )
}

interface DateMapper {
    fun map(date: String): Date
}

class DateMapperImpl(
    private val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
) : DateMapper {
    override fun map(date: String): Date = dateFormat.parse(date)?: Date(0)
}
