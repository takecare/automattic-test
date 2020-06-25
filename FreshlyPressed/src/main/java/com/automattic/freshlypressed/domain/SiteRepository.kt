package com.automattic.freshlypressed.domain

import com.automattic.freshlypressed.data.Result

interface SiteRepository {
    suspend fun getSite(url: String): Result<Site>
}
