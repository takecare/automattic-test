package com.automattic.freshlypressed.data

import com.squareup.moshi.Json

data class PostData(
    val title: String,
    val excerpt: String,
    val author: AuthorData,
    @field:Json(name = "featured_image") val imageUrl: String,
    val date: String,
    @field:Json(name = "URL") val url: String
)

data class AuthorData(
    @field:Json(name = "first_name") val firstName: String,
    @field:Json(name = "last_name") val lastName: String,
    @field:Json(name = "nice_name") val niceName: String,
    @field:Json(name = "profile_URL") val profileUrl: String
)
