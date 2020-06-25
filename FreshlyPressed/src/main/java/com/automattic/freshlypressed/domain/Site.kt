package com.automattic.freshlypressed.domain

import com.squareup.moshi.Json

data class Site(
    val name: String,
    val description: String,
    val subscriberCount: Int
)
