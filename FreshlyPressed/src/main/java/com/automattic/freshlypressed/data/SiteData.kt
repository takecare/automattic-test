package com.automattic.freshlypressed.data

import com.squareup.moshi.Json

data class SiteData(
    val name: String,
    val description: String,
    @field:Json(name = "subscribers_count") val subscriberCount: Int
)
