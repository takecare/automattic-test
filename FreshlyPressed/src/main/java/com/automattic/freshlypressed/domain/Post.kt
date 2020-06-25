package com.automattic.freshlypressed.domain

import android.net.Uri
import java.util.*

data class Post constructor(
    val title: String,
    val excerpt: String,
    val author: String,
    val imageUrl: String,
    val date: Date,
    val authorUrl: String,
    val uri: Uri,
    val subscriberCount: Int
) {
    fun hasSubscriberCount() = subscriberCount != Int.MIN_VALUE
}
