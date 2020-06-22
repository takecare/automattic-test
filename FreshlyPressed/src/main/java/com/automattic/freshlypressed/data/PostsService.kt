package com.automattic.freshlypressed.data

import retrofit2.http.GET
import retrofit2.http.Query

interface PostsService {

    @GET("discover.wordpress.com/posts")
    suspend fun getPosts(@Query("number") number: Int = 10): Posts
}
