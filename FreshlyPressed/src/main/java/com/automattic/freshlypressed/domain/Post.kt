package com.automattic.freshlypressed.domain

import android.net.Uri
import java.util.*

// TODO most basic model for now @RUI
data class Post(
    val title: String,
    val excerpt: String,
    val author: String,
    val imageUrl: String,
    val date: Date,
    val authorUrl: String,
    val uri: Uri
)
