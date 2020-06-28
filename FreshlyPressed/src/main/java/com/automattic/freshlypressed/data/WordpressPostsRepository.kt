package com.automattic.freshlypressed.data

import android.net.Uri
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.PostsRepository
import com.automattic.freshlypressed.domain.Result
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class WordpressPostsRepository @Inject constructor(
    private val service: PostsService,
    private val mapper: PostMapper
) : PostsRepository {

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

class PostMapperImpl @Inject constructor(
    private val dateMapper: DateMapper,
    private val hostExtractor: HostExtractor
) : PostMapper {
    override fun map(data: PostData) =
        Post(
            title = data.title,
            excerpt = data.excerpt,
            author = data.author.niceName,
            imageUrl = data.imageUrl,
            date = dateMapper.map(data.date),
            authorHost = hostExtractor.extract(data.author.url),
            uri = data.url
        )
}

interface DateMapper {
    fun map(date: String): Date
}

class DateMapperImpl @Inject constructor(
    private val dateFormat: DateFormat
) : DateMapper {
    override fun map(date: String): Date = dateFormat.parse(date) ?: Date(0)
}

interface HostExtractor {
    fun  extract(url: String): String
}

class HostExtractorImpl @Inject constructor() : HostExtractor {
    override fun extract(url: String) = Uri.parse(url).host ?: ""
}
