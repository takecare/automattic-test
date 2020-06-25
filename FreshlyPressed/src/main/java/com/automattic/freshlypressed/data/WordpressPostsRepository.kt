package com.automattic.freshlypressed.data

import android.net.Uri
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
import com.automattic.freshlypressed.domain.Result
import org.json.JSONArray
import java.io.IOException
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

    override suspend fun loadPosts(): Result<List<Post>> {
        return try {
            val data = service.getPosts().posts.map(mapper::map)
            Result.Success(data)
        } catch (exception: IOException) {
            Result.Error(exception)
        }
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
            authorUrl = data.author.url,
            uri = Uri.parse(data.url),
            subscriberCount = Int.MIN_VALUE
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
