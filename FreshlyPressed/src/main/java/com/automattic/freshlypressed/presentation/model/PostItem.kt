package com.automattic.freshlypressed.presentation.model

import android.net.Uri
import java.util.*

sealed class Post {
    data class Header(
        val title: String,
        val excerpt: String,
        val author: String,
        val imageUrl: String,
        val date: Date,
        val authorUrl: String,
        val uri: Uri
    )
    data class Item(
        val title: String,
        val excerpt: String,
        val author: String,
        val imageUrl: String,
        val date: Date,
        val authorUrl: String,
        val uri: Uri
    )
}
