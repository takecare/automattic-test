package com.automattic.freshlypressed.data

import android.net.Uri
import java.util.*

data class PostData(
    val title: String,
    val excerpt: String,
    val author: String,
    val imageUrl: String,
    val date: String,
    val authorUrl: String,
    val uri: String
)
