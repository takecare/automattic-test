package com.automattic.freshlypressed.domain

import java.util.*

data class Post(
    val title: String,
    val excerpt: String,
    val author: String,
    val imageUrl: String,
    val date: Date,
    val authorHost: String,
    val uri: String,
    val subscriberCount: Int = Int.MIN_VALUE
) {
    fun hasSubscriberCount() = subscriberCount != Int.MIN_VALUE
}
