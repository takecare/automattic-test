package com.automattic.freshlypressed.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// https://public-api.wordpress.com/rest/v1.1/sites/discover.wordpress.com/posts?number=1&meta=site&fields=author,date,URL,title,excerpt,featured_image,meta

interface PostsService {

    @GET("discover.wordpress.com/posts?number=1&meta=site&fields=author,date,URL,title,excerpt,featured_image,meta")
    suspend fun getPosts(@Query("number") number: Int = 10): Posts

    companion object {
        fun createService(okHttpClient: OkHttpClient) =
            Retrofit.Builder()
                .baseUrl("https://public-api.wordpress.com/rest/v1.1/sites/")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(PostsService::class.java)
    }
}
